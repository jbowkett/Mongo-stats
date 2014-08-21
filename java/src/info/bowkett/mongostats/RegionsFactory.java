package info.bowkett.mongostats;

import java.util.stream.Stream;

/**
 * Created by jbowkett on 20/08/2014.
 */
public class RegionsFactory {

  public void createFromStream(Stream<String> lines) {
    lines.forEach(line -> createFromLine(line));
  }


  /**
   * Creates from an input line of the form :  2012,England,London,49141671
   * @param line 2012,England,London,49141671
   * @return
   */
  public Region createFromLine(String line) {
    final String[] elements = line.split("\\,");
    final String year = elements[0];
    final String country = elements[1];
    final String region = elements[2];
    final String population = elements[3];
    final Region toReturn = new Region(country, region);
    toReturn.insertPopulation(Integer.parseInt(year), Integer.parseInt(population));
    return toReturn;
  }
}
