import com.google.gson.Gson;
import model.Module;
import model.ServerStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import scraper.Scraper;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.*;

import static spark.Spark.*;



public class Main {

  private static Set<Module> modules;

  private final static String BASE_PATH = "/";

  private static ServerStatus serverStatus = ServerStatus.getInstance();




  private static Logger logger = LogManager.getLogger(Main.class);

  private static final SessionFactory sessionFactory = buildSessionFactory();

  private static SessionFactory buildSessionFactory() {
    final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

    return new MetadataSources(registry).buildMetadata().buildSessionFactory();
  }

  public static void main(String[] args) {





    staticFileLocation("/public");






    get(BASE_PATH, (request, response) -> new ModelAndView(null, "index.hbs"), new HandlebarsTemplateEngine());
    get(BASE_PATH+"status", (request, response) -> new ModelAndView(serverStatus.getModel(), "status.hbs"), new HandlebarsTemplateEngine());

    get(BASE_PATH+"v1/modules", "application/json",
            (request, response) -> new Gson().toJson(getAllModules()));

    get(BASE_PATH+"v1/modules/:code", "application/json",
            (request, response) -> new Gson().toJson(getModuleByCode(request.params(":code"))) );



    modules = new HashSet<>();



    logger.info("Started the app.");

    TimerTask scraperTask = new TimerTask() {
      @Override
      public void run() {

        try {
          updateModules();
        }
        catch (Exception e) {
          logger.error("Could not update the modules.");
        }

      }
    };

    Timer timer = new Timer("Timer");

    long delay = 1000L * 15L;
    long period = 1000L * 60L * 60L * 24L; //every day
    timer.scheduleAtFixedRate(scraperTask, delay, period);
    logger.info("Scheduled the web scraper.");

    serverStatus.setNumberOfModulesInDatabase(getAllModules().size());




  }

  private static void updateModules() {
    serverStatus.setStatus(ServerStatus.Status.UPDATING);
    Scraper scraper = new Scraper();
    Set<Module> updatedModules = scraper.scrape();

    modules.addAll(updatedModules);
    saveModulesToDatabase(updatedModules);
    serverStatus.setNumberOfModulesInDatabase(getAllModules().size());

    serverStatus.setStatus(ServerStatus.Status.IDLE);
    serverStatus.update();

  }

  private static void saveModulesToDatabase(Collection<Module> modules) {
    logger.info("Saving to database...");
    Session session = sessionFactory.openSession();
    session.beginTransaction();
    for (Module m : modules) {
      session.saveOrUpdate(m);
    }
    session.getTransaction().commit();



    session.close();
    logger.info("Saved.");

  }





  private static Module getModuleByCode(String code) {
    Session session = sessionFactory.openSession();
    Module module = session.get(Module.class, code);
    System.out.println(module);
    serverStatus.incrementRequestCount();
    return module;
  }

  private static List<Module> getAllModules() {
    Session session = sessionFactory.openSession();
    serverStatus.incrementRequestCount();


    return session.createQuery("FROM Module").list();
  }


}
