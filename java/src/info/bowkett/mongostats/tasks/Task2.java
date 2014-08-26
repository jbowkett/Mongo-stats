package info.bowkett.mongostats.tasks;

import info.bowkett.mongostats.Region;
import info.bowkett.mongostats.RegionDAO;

import java.util.List;

/**
 * Created by jbowkett on 24/08/2014.
 * Class to implement the following task:
 * "Calculate and show the two regions which have largest recent annual growth in population"
 */
public class Task2 implements Task {
  private final RegionDAO regionDao;

  public Task2(RegionDAO regionDao) {
    this.regionDao = regionDao;
  }

  /**
   * Uses the region dao from the constructor to get the two regions with the
   * largest growth for 2012
   */
  @Override
  public void demonstrate() {
    final int topNumberOfRegions = 2;
    final int year = 2012;
    final List<Region> regions = regionDao.getLargestGrowth(topNumberOfRegions, year);
    System.out.println("Top "+topNumberOfRegions+" regions in order of greatest" +
        " growth for " + year + ":");
    for (int i = 0; i < regions.size(); i++) {
      final Region region = regions.get(i);
      System.out.println((i+1)+" - " + region.getRegion()+", "+region.getCountry());
    }
    System.out.println("Task 2 complete.");
  }
}
