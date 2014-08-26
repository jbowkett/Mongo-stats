package info.bowkett.mongostats.tasks;

import info.bowkett.mongostats.RegionDAO;
import info.bowkett.mongostats.Region;
import info.bowkett.mongostats.RegionsFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;

/**
 * Created by jbowkett on 20/08/2014.
 * Class to implement the following task:
 * "Insert the source data provided into a MongoDB instance."
 */
public class Task1 implements Task {

  private final String fileName;
  private final RegionDAO regionDao;
  private RegionsFactory regionsFactory;

  public Task1(String fileName, RegionDAO regionDao, RegionsFactory regionsFactory) {
    this.fileName = fileName;
    this.regionDao = regionDao;
    this.regionsFactory = regionsFactory;
  }

  /**
   * Loads the input file name given in the constructor,
   * uses the regions factory from the constructor to parse each line in turn
   * and then inserts each document using the Region DAO
   */
  @Override
  public void demonstrate() {
    try {
      System.out.println("Loading file...");
      final FileReader inputFile = new FileReader(fileName);
      final BufferedReader reader = new BufferedReader(inputFile);
      final Collection<Region> regions = regionsFactory.createFromStream(reader.lines());
      System.out.println("File parsed.  Loading into DB...");
      regionDao.insertAll(regions.stream());
      System.out.println("All documents loaded into DB.  Task 1 complete.");
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
