package info.bowkett.mongostats.tasks;

import info.bowkett.mongostats.RegionDAO;
import info.bowkett.mongostats.RegionMeanGrowthDeviation;

import java.util.List;

/**
 * Created by jbowkett on 24/08/2014.
 */
public class Task3 implements Task {
  private final RegionDAO regionDao;

  public Task3(RegionDAO regionDao) {
    this.regionDao = regionDao;
  }

  @Override
  public void demonstrate() {
    final int recordsUpdated = regionDao.setAverageGrowthForEachRegion();
    System.out.println(recordsUpdated + " documents updated with average population growth");

    final List<RegionMeanGrowthDeviation> regionMeanGrowthDeviations = regionDao.printDeviationGrowthForEachRegion(2);
    printRegionMeanDeviationGrowth(regionMeanGrowthDeviations);
    System.out.println("Task 3 complete.");
  }

  public void printRegionMeanDeviationGrowth(List<RegionMeanGrowthDeviation> regionMeanGrowthDeviations) {
    for (RegionMeanGrowthDeviation regionMeanGrowthDeviation : regionMeanGrowthDeviations) {
      final StringBuilder msg = new StringBuilder();
      msg.append("In ").append(regionMeanGrowthDeviation.getYear())
          .append(", in ").append(regionMeanGrowthDeviation.getRegion())
          .append(", ")
          .append(regionMeanGrowthDeviation.getCountry())
          .append(" the population grew by ")
          .append(regionMeanGrowthDeviation.getGrowthForYear())
          .append(" which is an absolute deviation of ")
          .append(regionMeanGrowthDeviation.getDeviationFromMean())
          .append(" from the arithmetic mean population for the region for all years of ")
          .append(regionMeanGrowthDeviation.getAverageGrowthForRegion());
      System.out.println(msg);
    }
  }
}
