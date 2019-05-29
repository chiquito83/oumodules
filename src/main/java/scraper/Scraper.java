package scraper;

import model.Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scraper {

  private final static int TIMEOUT = 5*60*60*1000;

  private static Logger logger = LogManager.getLogger(Scraper.class);



  private Set<Module> modules;
  private boolean success;

  public Set<Module> scrape() {

    success = false;

    try {
      Document document = Jsoup.connect("http://www.open.ac.uk/courses/modules").timeout(TIMEOUT).get();
      Elements course_list = document.getElementsByClass("int-grid7");


      modules = new HashSet<Module>();

      int numberOfModulesOnCoursePage = course_list.size();

      logger.info("Found " + numberOfModulesOnCoursePage + " modules. Getting details...");

      for (Element e : course_list) {



        Element link = e.select("a").first();

        String name = link.text();
        String sUri = link.attr("href");

        String path[] = sUri.split("/");
        String code = path[path.length-1].toUpperCase();

        URI uri = null;
        try {
          uri = new URI(sUri);
        } catch (URISyntaxException e1) {
          e1.printStackTrace();
        }


        Module module = new Module(name, code, uri);
        addDetails(module);

        if (module.validate()) {
          modules.add(module);
          logger.info("Added another module. Progress: " + modules.size() + "/" + course_list.size());
        }



      }

      if (numberOfModulesOnCoursePage == modules.size()) {
        success = true;
        logger.info("All ok. Number of modules: " + modules.size() );
      }
      else if (numberOfModulesOnCoursePage > modules.size()) {
        logger.info("Some modules missing(" + modules.size() + "/" + numberOfModulesOnCoursePage + ")");
      }
      else {
        logger.info("Somehow we have more modules than we found on the" +
        "main page. Downloaded " + modules.size() + " while on main page lists only " + numberOfModulesOnCoursePage);
      }


    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    return modules;
  }

  public boolean isSuccess() {
    return success;
  }

  private void addDetails(Module module) throws URISyntaxException, IOException {
    URI link = new URI("http://www.open.ac.uk" + module.getUri());

    Document document = Jsoup.connect(link.toString()).timeout(TIMEOUT).get();

    Element desc = document.getElementsByClass("int-row").get(2);
    module.setDescription(desc.text());
    module.setLink(link);


    Element moduleDetails = document.select("dl").first();
    Elements rows = moduleDetails.select("dt");
    Elements rows2 = moduleDetails.select("dd");
    int credits = Integer.parseInt(rows2.get(1).text()); // credits
    module.setCredits(credits);
    Element table = moduleDetails.select("table").first();

    Elements tableRows = table.select("tr");


    Elements countries = tableRows.get(0).select("td");
    Elements levels = tableRows.get(1).select("td");

    Map<String, String> levelsByCountry = new HashMap<String, String>();

    for (int i = 0; i < countries.size(); i++) {
      levelsByCountry.put(countries.get(i).text(), levels.get(i).text());
    }



    module.setLevel(levelsByCountry.get("OU"));
    module.setScqfLevel(levelsByCountry.get("SCQF"));
    module.setFheqLevel(levelsByCountry.get("FHEQ"));










  }

}
