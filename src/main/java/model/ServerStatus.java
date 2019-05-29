package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class ServerStatus {

  private static ServerStatus INSTANCE;

  private Status status;
  private int numberOfModulesInDatabase;
  private int requestsSinceStart;
  private LocalDateTime startTime;
  private LocalDateTime lastUpdated;

  private ServerStatus() {
    status = Status.IDLE;
    numberOfModulesInDatabase = 0;
    requestsSinceStart = 0;
    startTime = LocalDateTime.now();
    lastUpdated = LocalDateTime.now();
  }



  public synchronized static ServerStatus getInstance() {
    if(INSTANCE == null) {
      INSTANCE = new ServerStatus();
    }

    return INSTANCE;

  }

  public Map<String, String> getModel() {
    Map<String, String> model = new HashMap<>();

    model.put("status", getCurrentStatus());
    model.put("number_of_modules", getNumberOfModulesInDatabase()+"");
    model.put("requests", getRequestsSinceStart()+"");
    model.put("days", getUptime()+"");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    model.put("last_updated", getLastUpdated().format(formatter));


    return model;
  }

  public void update() {
    lastUpdated = LocalDateTime.now();
  }

  public void setNumberOfModulesInDatabase(int numberOfModulesInDatabase) {
    this.numberOfModulesInDatabase = numberOfModulesInDatabase;
  }

  public void incrementRequestCount() {
    requestsSinceStart++;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public long getUptime() {
    long days = ChronoUnit.DAYS.between(startTime, LocalDateTime.now());
    return days;
  }

  public String getCurrentStatus() {
    return status.toString();
  }

  public int getNumberOfModulesInDatabase() {
    return numberOfModulesInDatabase;
  }

  public int getRequestsSinceStart() {
    return requestsSinceStart;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public enum Status {
    IDLE, UPDATING
  }

  @Override
  public String toString() {
    return "ServerStatus{" +
            "status=" + status +
            ", numberOfModulesInDatabase=" + numberOfModulesInDatabase +
            ", requestsSinceStart=" + requestsSinceStart +
            ", uptime=" + getUptime() +
            '}';
  }
}
