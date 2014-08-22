package info.bowkett.mongostats.tasks;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import info.bowkett.mongostats.CommandLine;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;
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
        final String fileName = "input-data/data.txt";
        final String mongoUrl = mongoUrlFrom(parsed);
        System.out.println("Connecting to : " + mongoUrl);
        final MongoClientURI uri  = new MongoClientURI(mongoUrl);
        client = new MongoClient(uri);
        final Task1 task1 = new Task1(fileName, client, parsed.get(DB_SWITCH));
        task1.demonstrate();
      }
      catch (FileNotFoundException e) {
        e.printStackTrace();
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
        HOST_SWITCH + " <host> " + PORT_SWITCH + " <port> " +
        DB_SWITCH + " <database> [" + UNAME_SWITCH + " <username> " + PASS_SWITCH + " <password>]");
    System.out.println("username and password are optional, but if one is specified, both must be specified");
  }

  private static boolean valid(Map<String, String> parsed) {
    final boolean dbArgsCorrect = parsed.get(HOST_SWITCH) != null &&
        parsed.get(PORT_SWITCH) != null &&
        parsed.get(DB_SWITCH)   != null;
    final boolean credentialsSuppliedCorrectly = parsed.get(UNAME_SWITCH) != null &&
        parsed.get(PASS_SWITCH) != null;
    final boolean credentialsNotSuppliedAtAll = !parsed.containsKey(UNAME_SWITCH) &&
        !parsed.containsKey(PASS_SWITCH);
    final boolean helpSupplied = parsed.containsKey(HELP_SWITCH);
    return !helpSupplied &&
        dbArgsCorrect &&
        (credentialsSuppliedCorrectly || credentialsNotSuppliedAtAll);
  }


}
