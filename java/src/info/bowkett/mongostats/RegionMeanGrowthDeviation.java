package info.bowkett.mongostats;

/**
 * Created by jbowkett on 26/08/2014.
 */
public class RegionMeanGrowthDeviation {
  private final String country;
  private final String region;
  private final double averageGrowthForRegion;
  private final int year;
  private final int growthForYear;
  private final double deviationFromMean;

  public RegionMeanGrowthDeviation(String country, String region, double averageGrowthForRegion, int year, int growthForYear, double deviationFromMean) {
    this.country = country;
    this.region = region;
    this.averageGrowthForRegion = averageGrowthForRegion;
    this.year = year;
    this.growthForYear = growthForYear;
    this.deviationFromMean = deviationFromMean;
  }

  public String getCountry() {
    return country;
  }

  public String getRegion() {
    return region;
  }

  public double getAverageGrowthForRegion() {
    return averageGrowthForRegion;
  }

  public int getYear() {
    return year;
  }

  public int getGrowthForYear() {
    return growthForYear;
  }

  public double getDeviationFromMean() {
    return deviationFromMean;
  }
}
