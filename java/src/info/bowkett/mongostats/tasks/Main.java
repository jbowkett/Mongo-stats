package info.bowkett.mongostats.tasks;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import info.bowkett.mongostats.*;

import java.net.UnknownHostException;
import java.util.AbstractSequentialList;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by jbowkett on 22/08/2014.
 */
public class Main {

  private static final String TASK_SWITCH = "-t";
  private static final String HOST_SWITCH = "-h";
  private static final String PORT_SWITCH = "-pt";
  private static final String DB_SWITCH = "-d";
  private static final String UNAME_SWITCH = "-u";
  private static final String PASS_SWITCH = "-pw";
  private static final String HELP_SWITCH = "-help";

  public static void main(String[] args) {
    final CommandLine cmdLine = new CommandLine();
    final Map<String, String> parsed = cmdLine.parse(args);
    if (valid(parsed)) {
      MongoClient client = null;
      try {
        final String mongoUrl = mongoUrlFrom(parsed);
        System.out.println("Connecting to : " + mongoUrl);
        final MongoClientURI uri  = new MongoClientURI(mongoUrl);
        client = new MongoClient(uri);
        final AbstractSequentialList<Task> tasks = getTasks(parsed, client);
        tasks.forEach(task -> task.demonstrate());
      }
      catch (UnknownHostException e) {
        e.printStackTrace();
      }
      finally {
        if(client != null) client.close();
      }
    }
    else {
      usage();
      System.exit(-1);
    }
  }

  public static AbstractSequentialList<Task> getTasks(Map<String, String> parsed, MongoClient client) {
    final AbstractSequentialList<Task> tasks = new LinkedList<>();
    final String taskName = parsed.get(TASK_SWITCH);
    final RegionDAO regionDao = new RegionDAO(client, parsed.get(DB_SWITCH), new RegionCodec());
    if(taskName.matches("1|ALL")){
      final String fileName = "input-data/data.txt";
      tasks.add(new Task1(fileName, regionDao, new RegionsFactory()));
    }
    if(taskName.matches("2|ALL")) {
      tasks.add(new Task2(regionDao));
    }
    if(taskName.matches("3|ALL")) {
      tasks.add(null);
    }
    return tasks;
  }

  private static String mongoUrlFrom(Map<String, String> parsed) {
    final String credentials = parsed.get(UNAME_SWITCH) != null ?
        parsed.get(UNAME_SWITCH)+":" + parsed.get(PASS_SWITCH) + "@":"";
    return  "mongodb://" +
        credentials +
        parsed.get(HOST_SWITCH) + ":" +
        parsed.get(PORT_SWITCH) + "/" +
        parsed.get(DB_SWITCH);
  }

  private static void usage() {
    System.out.println("java info.bowkett.mongostats.tasks.Task1 " +
        TASK_SWITCH + " <1|2|3|ALL> " + HOST_SWITCH + " <host> " +
        PORT_SWITCH + " <port> " + DB_SWITCH + " <database> [" +
        UNAME_SWITCH + " <username> " + PASS_SWITCH + " <password>]");
    System.out.println("username and password are optional, but if one is specified, both must be specified");
  }

  private static boolean valid(Map<String, String> parsed) {
    final boolean taskSpecified = parsed.get(TASK_SWITCH) != null;
    final boolean hostSpecified = parsed.get(HOST_SWITCH) != null;
    final boolean portSpecified = parsed.get(PORT_SWITCH) != null;
    final boolean dbSpecified   = parsed.get(DB_SWITCH)  != null;
    final boolean credentialsSuppliedCorrectly = parsed.get(UNAME_SWITCH) != null &&
        parsed.get(PASS_SWITCH) != null;
    final boolean credentialsNotSuppliedAtAll = !parsed.containsKey(UNAME_SWITCH) &&
        !parsed.containsKey(PASS_SWITCH);
    final boolean helpSupplied = parsed.containsKey(HELP_SWITCH);
    return !helpSupplied &&
        taskSpecified &&
        hostSpecified &&
        portSpecified &&
        dbSpecified &&
        (credentialsSuppliedCorrectly || credentialsNotSuppliedAtAll);
  }
}
