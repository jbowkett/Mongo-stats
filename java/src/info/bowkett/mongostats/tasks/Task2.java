package info.bowkett.mongostats.tasks;

import info.bowkett.mongostats.Region;
import info.bowkett.mongostats.RegionDAO;

import java.util.List;

/**
 * Created by jbowkett on 24/08/2014.
 */
public class Task2 implements Task {
  private final RegionDAO regionDao;

  public Task2(RegionDAO regionDao) {
    this.regionDao = regionDao;
  }

  @Override
  public void demonstrate() {
    final int topNumberOfRegions = 2;
    final int year = 2012;
    final List<Region> regions = regionDao.largestGrowth(topNumberOfRegions, year);
    System.out.println("Top "+topNumberOfRegions+" regions in order of greatest" +
        " growth for " + year + ":");
    for (int i = 0; i < regions.size(); i++) {
      final Region region = regions.get(i);
      System.out.println((i+1)+" - " + region.getRegion()+", "+region.getCountry());
    }
    System.out.println("Task 2 complete.");
  }
}
