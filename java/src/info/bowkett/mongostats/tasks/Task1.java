package info.bowkett.mongostats.tasks;

import com.mongodb.MongoClient;
import info.bowkett.mongostats.Inserter;
import info.bowkett.mongostats.Region;
import info.bowkett.mongostats.RegionsFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;

/**
 * Created by jbowkett on 20/08/2014.
 */
public class Task1 {

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
    System.out.println("All documents loaded into DB.  Task 1 complete.");
  }
}
