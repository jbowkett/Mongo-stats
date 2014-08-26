package info.bowkett.mongostats;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbowkett on 24/08/2014.
 *
 * Class to encode and decode regions to and from Mongo document representations.
 */
public class RegionCodec {

  /* document keys  */
  private static final String COUNTRY = "country";
  private static final String REGION = "region";
  private static final String YEAR = "year";
  private static final String POPULATION = "population";
  private static final String POPULATIONS = "populations";
  private static final String GROWTH = "growth";

  /**
   * Encodes to db object from a Region
   * @param r
   * @return
   */
  protected DBObject toDBObject(Region r) {
    final DBObject mapped = new BasicDBObject();
    mapped.put(COUNTRY, r.getCountry());
    mapped.put(REGION, r.getRegion());
    final List<BasicDBObject> populations = new ArrayList<>();
    r.populationEntries().forEach(entry -> {
      final BasicDBObject population = new BasicDBObject();
      final Integer year = entry.getKey();
      population.put(YEAR, year);
      population.put(POPULATION, entry.getValue());
      population.put(GROWTH, r.getPopulationChangeFor(year));
      populations.add(population);
    });
    mapped.put(POPULATIONS, populations);
    return mapped;
  }

  /**
   * Encodes to a Region from a Mongo DB object
   * @param dbObject
   * @return
   */
  protected Region toRegion(DBObject dbObject) {
    final Region region = new Region(dbObject.get(COUNTRY).toString(),
                                     dbObject.get(REGION).toString());
    final BasicDBList populations = (BasicDBList)dbObject.get(POPULATIONS);
    populations.forEach(population -> {
      final DBObject castedPopulation = (DBObject) population;
      final int year = (Integer) castedPopulation.get(YEAR);
      final int pop = (Integer) castedPopulation.get(POPULATION);
      region.insertPopulation(year, pop);
    });
    return region;
  }
}
