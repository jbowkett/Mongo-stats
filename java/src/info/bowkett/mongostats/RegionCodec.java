package info.bowkett.mongostats;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbowkett on 24/08/2014.
 */
public class RegionCodec {

  private static final String COUNTRY = "country";
  private static final String REGION = "region";
  private static final String YEAR = "year";
  private static final String POPULATION = "population";
  private static final String POPULATIONS = "populations";

  protected DBObject toDBObject(Region r) {
    final DBObject mapped = new BasicDBObject();
    mapped.put(COUNTRY, r.getCountry());
    mapped.put(REGION, r.getRegion());
    final List<BasicDBObject> populations = new ArrayList<>();
    r.populationEntries().forEach(entry -> {
      final BasicDBObject population = new BasicDBObject();
      population.put(YEAR, entry.getKey());
      population.put(POPULATION, entry.getValue());
      populations.add(population);
    });
    mapped.put(POPULATIONS, populations);
    return mapped;
  }

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
