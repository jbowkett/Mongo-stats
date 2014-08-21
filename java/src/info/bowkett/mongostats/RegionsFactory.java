package info.bowkett.mongostats;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * Created by jbowkett on 20/08/2014.
 */
public class RegionsFactory {

  public Collection<Region> createFromStream(Stream<String> lines) {
    final ConcurrentMap<String, Region> regions = new ConcurrentHashMap<>();
    lines.forEach(line -> {
      final Region incoming = createFromLine(line);
      //do merge if already a value for the region:
      regions.computeIfPresent(incoming.getRegion(), (key,existingRegion) -> {
        final Set<Map.Entry<Integer, Integer>> populationEntries = incoming.populationEntries();
        populationEntries.forEach(entry -> {
          final int year = entry.getKey();
          final int population = entry.getValue();
          existingRegion.insertPopulation(year, population);
        });
        return existingRegion;
      });
      regions.putIfAbsent(incoming.getRegion(), incoming);
    });
    return regions.values();
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
