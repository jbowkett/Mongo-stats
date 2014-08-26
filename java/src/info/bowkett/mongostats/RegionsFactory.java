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

  /**
   * Creates regions from a stream.  If called with a parallel stream, this
   * method should still work.  Internally it makes use of a concurrent map
   * as its api for only adding new values if previously absent makes the code
   * easier to read, and has the added benefit of allowing for later
   * multi-threading if required.
   * @param lines a Java 8 stream of lines
   * @return a collection of regions from the stream of lines.
   */
  public Collection<Region> createFromStream(Stream<String> lines) {
    final ConcurrentMap<String, Region> regions = new ConcurrentHashMap<>();
    lines.forEach(line -> {
      final Region incoming = createFromLine(line);
      //create initial value, ensures thread safety
      regions.putIfAbsent(incoming.getRegion(), incoming);
      //merge the stats together into the pre-existing region
      //if there is already a value for the region:
      regions.computeIfPresent(incoming.getRegion(), (key,existingRegion) -> {
        final Set<Map.Entry<Integer, Integer>> populationEntries = incoming.populationEntries();
        populationEntries.forEach(entry -> {
          final int year = entry.getKey();
          final int population = entry.getValue();
          existingRegion.insertPopulation(year, population);
        });
        return existingRegion;
      });
    });
    return regions.values();
  }


  /**
   * Creates a Region from an input line of the form :  2012,England,London,49141671
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
