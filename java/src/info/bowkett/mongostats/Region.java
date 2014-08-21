package info.bowkett.mongostats;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jbowkett on 20/08/2014.
 */
public class Region {

  private final String country;
  private final String region;
  private final HashMap<Integer, Integer> yearToPopulationMap = new HashMap<>();;

  public Region(String country, String region) {
    this.country = country;
    this.region = region;
  }

  public void insertPopulation(int year, int population){
    yearToPopulationMap.put(year, population);
  }

  public String getCountry() {
    return country;
  }

  public String getRegion() {
    return region;
  }

  public int getPopulationFor(int year) {
    return yearToPopulationMap.get(year);
  }

  public Set<Map.Entry<Integer, Integer>> populationEntries() {
    return yearToPopulationMap.entrySet();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Region{");
    sb.append("country='").append(country).append('\'');
    sb.append(", region='").append(region).append('\'');
    sb.append(", yearToPopulationMap=").append(yearToPopulationMap);
    sb.append('}');
    return sb.toString();
  }
}
