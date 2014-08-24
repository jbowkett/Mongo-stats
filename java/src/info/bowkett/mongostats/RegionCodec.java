package info.bowkett.mongostats;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jbowkett on 24/08/2014.
 */
public class RegionCodec {

  protected BasicDBObject map(Region r) {
    BasicDBObject mapped = new BasicDBObject();
    mapped.put("country", r.getCountry());
    mapped.put("region", r.getRegion());
    final List<BasicDBObject> populations = new ArrayList<>();
    r.populationEntries().forEach(entry -> {
      final BasicDBObject population = new BasicDBObject();
      population.put("year", entry.getKey());
      population.put("population", entry.getValue());
      populations.add(population);
    });
    mapped.put("populations", populations);
    return mapped;
  }

  protected Region toRegion(DBObject dbObject) {

    return null;
  }

}
