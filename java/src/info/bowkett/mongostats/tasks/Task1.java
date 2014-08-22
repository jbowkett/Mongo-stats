package info.bowkett.mongostats.tasks;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import info.bowkett.mongostats.CommandLine;
import info.bowkett.mongostats.Inserter;
import info.bowkett.mongostats.Region;
import info.bowkett.mongostats.RegionsFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by jbowkett on 20/08/2014.
 */
public class Task1 {

  private static final String PORT_SWITCH = "-pt";
  private static final String DB_SWITCH = "-d";
  private static final String UNAME_SWITCH = "-u";
  private static final String PASS_SWITCH = "-pw";
  private static final String HELP_SWITCH = "-help";
  private static final String HOST_SWITCH = "-h";
  private final String fileName;
  private final MongoClient mongoClient;
  private final String dbName;

  public Task1(String fileName, MongoClient client, String dbName) {
    this.fileName = fileName;
    mongoClient = client;
    this.dbName = dbName;
  }

  public void demonstrate() throws FileNotFoundException {
    System.out.println("Loading file...");
    final FileReader inputFile = new FileReader(fileName);
    final BufferedReader reader = new BufferedReader(inputFile);
    final RegionsFactory factory = new RegionsFactory();
    final Collection<Region> regions = factory.createFromStream(reader.lines());
    System.out.println("File parsed.  Loading into DB...");
    final Inserter inserter = new Inserter(mongoClient, dbName);
    inserter.insertAll(regions.stream());
    System.out.println("All documents loaded into DB.  Task complete.");
  }

  public static void main(String[] args) {
    final CommandLine cmdLine = new CommandLine();
    final Map<String, String> parsed = cmdLine.parse(args);
    if (valid(parsed)) {
      try {
        final String fileName = "input-data/data.txt";
        final String mongoUrl = mongoUrlFrom(parsed);
        System.out.println("Connecting to : " + mongoUrl);
        final MongoClientURI uri  = new MongoClientURI(mongoUrl);
        final MongoClient client = new MongoClient(uri);
        final Task1 task1 = new Task1(fileName, client, parsed.get(DB_SWITCH));
        task1.demonstrate();
      }
      catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      catch (UnknownHostException e) {
        e.printStackTrace();
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
