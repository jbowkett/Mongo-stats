package info.bowkett.mongostats;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


/**
 * Created by jbowkett on 20/08/2014.
 */
public class RegionDAO {
  private static final String COLLECTION_NAME = "regions_3";
  private final MongoClient mongoClient;
  private final RegionCodec codec;
  private final DB db;
  private final DBCollection collection;

  public RegionDAO(MongoClient mongoClient, String database, RegionCodec codec) {
    this.mongoClient = mongoClient;
    this.codec = codec;
    db = mongoClient.getDB(database);
    collection = db.getCollection(COLLECTION_NAME);
  }

  public void insertAll(Stream<Region> allToAdd) {
    allToAdd.forEach(region -> insert(region));
  }

  public void insert(Region r) {
    final DBObject mapped = codec.toDBObject(r);
    collection.insert(mapped);
  }

  public List<Region> allRegions() {
    final DBCursor cursor = collection.find();
    final List<Region> regions = new ArrayList<>();
    while (cursor.hasNext()) {
      final DBObject dbObject = cursor.next();
      regions.add(codec.toRegion(dbObject));
    }
    return regions;
  }

  /*
  db.regions.aggregate([
      { $unwind : "$populations" },
      { $match : {"populations.year":2012} },
      {
        $project : {
          _id : {
            country : "$country",
            region : "$region"
          },
          year : "$populations.year",
          population : "$populations.population",
          growth : "$populations.growth"
        }
      },
      { $sort  : {"growth":-1} },
      { $limit : 2}
  ])
   */
  public List<Region> largestGrowth(int topNumberOfRegions, int year) {
    final DBObject unwind = new BasicDBObject("$unwind", "$populations");
    final DBObject match = new BasicDBObject("$match", new BasicDBObject("populations.year", year));
    final DBObject sort = new BasicDBObject("$sort", new BasicDBObject("populations.growth", -1));
    final DBObject limit = new BasicDBObject("$limit", topNumberOfRegions);

    final List<DBObject> pipeline = Arrays.asList(unwind, match, sort, limit);
    final AggregationOutput output = collection.aggregate(pipeline);

    List<Region> toReturn = new ArrayList<>();
    for (DBObject result : output.results()) {
      final Region e = new Region((String)result.get("country"),
                                  (String)result.get("region"));
      // could do a projection here instead
      final DBObject populations = (DBObject) result.get("populations");

      e.insertPopulation((Integer)populations.get("year"),
                         (Integer)populations.get("population"));
      toReturn.add(e);
    }
    return toReturn;
  }
}
