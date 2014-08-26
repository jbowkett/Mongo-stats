package info.bowkett.mongostats.tasks;

import info.bowkett.mongostats.RegionDAO;
import info.bowkett.mongostats.RegionMeanGrowthDeviation;

import java.util.List;

/**
 * Created by jbowkett on 24/08/2014.
 *
 * Class to implement the following task:
 *
 * "Based on the average rate of annual change in population for each region,
 *  calculate and show the top two deviations from that average."
 */
public class Task3 implements Task {
  private final RegionDAO regionDao;

  public Task3(RegionDAO regionDao) {
    this.regionDao = regionDao;
  }

  /**
   * Uses the region dao from the constructor to first update all the regions in
   * the DB with each region's average growth over all the years for that region.
   *
   * The DAO is then used to retrieve the top 2 years for each region that have
   * the greatest absolute deviation from the mean population growth for the
   * region.
   */
  @Override
  public void demonstrate() {
    final int recordsUpdated = regionDao.setAverageGrowthForEachRegion();
    System.out.println(recordsUpdated + " documents updated with average population growth");

    final List<RegionMeanGrowthDeviation> regionMeanGrowthDeviations = regionDao.getMeanGrowthDeviationForEachRegion(2);
    printRegionMeanDeviationGrowth(regionMeanGrowthDeviations);
    System.out.println("Task 3 complete.");
  }

  /**
   * Private method to print all the region statistics.
   * This is extracted into a separate method so as not clutter the
   * intent of the demonstrate() method.
   * @param regionMeanGrowthDeviations
   */
  private void printRegionMeanDeviationGrowth(List<RegionMeanGrowthDeviation> regionMeanGrowthDeviations) {
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
