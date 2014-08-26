package info.bowkett.mongostats;

import com.mongodb.*;

import java.util.*;
import java.util.stream.Stream;


/**
 * Created by jbowkett on 20/08/2014.
 *
 * Data Access Object for create, read and updating Regions within a MongoDB
 * store
 */
public class RegionDAO {
  private static final String COLLECTION_NAME = "regions";
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

  /**

   * Queries the DB and selects the regions in order of population growth from
   * largest to smallest.  Effectively executes the following aggregation query:

   db.regions_3.aggregate([
  { $unwind : "$populations" },
  { $match : {"populations.year":2012} },
  { $sort  : {"populations.growth":-1} },
  { $limit : 2}
  ]);

   *
   * @param topNumberOfRegions - the maximum number of regions to return
   * @param year - the year for which to calculate the population growth
   * @return
   */
  public List<Region> getLargestGrowth(int topNumberOfRegions, int year) {
    final DBObject unwind = unwindPopulation();
    final DBObject match  = new BasicDBObject("$match", new BasicDBObject("populations.year", year));
    final DBObject sort   = new BasicDBObject("$sort",  new BasicDBObject("populations.growth", -1));
    final DBObject limit  = new BasicDBObject("$limit", topNumberOfRegions);

    final List<DBObject> pipeline = Arrays.asList(unwind, match, sort, limit);
    final AggregationOutput output = collection.aggregate(pipeline);

    List<Region> toReturn = new ArrayList<>();
    for (DBObject result : output.results()) {
      final Region e = new Region((String) result.get("country"),
                                  (String) result.get("region"));
      // could do a projection here instead
      final DBObject populations = (DBObject) result.get("populations");

      e.insertPopulation((Integer) populations.get("year"),
          (Integer) populations.get("population"));
      toReturn.add(e);
    }
    return toReturn;
  }

  /*

db.regions_3.aggregate([
    { $unwind : "$populations" },
    { $match : {"populations.year": {$gt : 2008} } },
    {
      $project : {
        _id : 1,
        year : "$populations.year",
        growth : "$populations.growth"
      }
    },
    {
        $group :{
          _id : "$_id",
          avg_growth : { $avg: "$growth" }
        }
    }
])
   */


  /**
   * Update operation to set the average (arithmetic mean) amount of growth for
   * each region.
   * Applies the result of the following query:
   *
     db.regions_3.aggregate([
       { $unwind : "$populations" },
       { $match : {"populations.year": {$gt : 2008} } },
       {
         $project : {
           _id : 1,
           year : "$populations.year",
           growth : "$populations.growth"
         }
       },
       {
           $group :{
             _id : "$_id",
             avg_growth : { $avg: "$growth" }
           }
       }
   ])
   *
   * @return the number of documents updated
   */
  public int setAverageGrowthForEachRegion() {
    final DBObject unwind = unwindPopulation();
    // exclude 2008 as there is no population growth stat for the first year
    // - this would skew the average
    final DBObject match   = excludeYear(2008);
    final DBObject project = new BasicDBObject("$project",new BasicDBObject("_id", 1).append("year", "$populations.year").append("growth", "$populations.growth"));
    final DBObject group   = new BasicDBObject("$group",  new BasicDBObject("_id", "$_id").append("avg_growth", new BasicDBObject("$avg", "$growth")));

    final List<DBObject> pipeline = Arrays.asList(unwind, match, project, group);
    final AggregationOutput output = collection.aggregate(pipeline);
    final BulkWriteOperation builder = collection.initializeOrderedBulkOperation();
    int recordCount = 0;
    for (DBObject result : output.results()) {
      final Object id = result.get("_id");
      final double averageGrowth = (double) result.get("avg_growth");
      builder.find(new BasicDBObject("_id", id)).updateOne(new BasicDBObject("$set", new BasicDBObject("avg_growth", averageGrowth)));
      recordCount++;
    }
    if (recordCount > 0) {
      final BulkWriteResult result = builder.execute();
      return result.getMatchedCount();
    }
    return 0;
  }


  /**
   * For each region, gets the years and their absolute distance from the
   * mean growth for that region.  Effectively runs the following query:
   db.regions_3.aggregate([
       { $unwind : "$populations" },
       { $match : {"populations.year" : {$gt:2008} }  },
       {
         $project : {
           _id : {
             country : "$country",
             region : "$region"
           },
           year : "$populations.year",
           avg_growth : "$avg_growth",
           deviation : { $subtract : ["$avg_growth", "$populations.growth"] }
         }
       },
       {
         $project : {
           _id : "$_id",
           year : "$year",
           avg_growth : "$avg_growth",
           absolute_deviation : {
              //had to lookup how to do absolute
              $cond: [
                { $lt: ['$deviation', 0] },
                { $subtract: [0, '$deviation'] },
                '$deviation'
              ]
           }
         }
       },
       { $sort:{_id :1, absolute_deviation:-1}  }
   ])
   * @param limit - the number of year records for each region to return
   * @return an ordered list containing ordered by region and then by their
   * absolute deviation from the mean
   */
  public AbstractSequentialList<RegionMeanGrowthDeviation> getMeanGrowthDeviationForEachRegion(int limit) {
    final DBObject unwind = unwindPopulation();
    // exclude 2008 as there is no population growth stat for the first year
    final DBObject match   = excludeYear(2008);
    final DBObject deviation_projection = new BasicDBObject("$project",
         new BasicDBObject("_id",
             new BasicDBObject("country", "$country").append("region","$region")
         ).append("year", "$populations.year").append("avg_growth", "$avg_growth").append("annual_growth", "$populations.growth")
             .append("deviation", new BasicDBObject("$subtract", new String []{"$avg_growth", "$populations.growth"}))
    );
    final DBObject abs_deviation = new BasicDBObject("$project",
         new BasicDBObject("_id","$_id").append("year", "$year").append("avg_growth", "$avg_growth").append("annual_growth", "$annual_growth")
             .append("absolute_deviation",
                 new BasicDBObject("$cond", new Object []{
                     new BasicDBObject("$lt", new Object[] {"$deviation", 0}),
                     new BasicDBObject("$subtract", new Object [] {0, "$deviation"}),
                     "$deviation"
                 }))
    );
    final DBObject sort   = new BasicDBObject("$sort",  new BasicDBObject("_id", 1).append("absolute_deviation", -1));
    final List<DBObject> pipeline = Arrays.asList(unwind, match, deviation_projection, abs_deviation, sort);
    final AggregationOutput output = collection.aggregate(pipeline);

    final AbstractSequentialList<RegionMeanGrowthDeviation> toReturn = new LinkedList<>();
    int regionCount = 0;
    String previousRegion = null;
    for (DBObject result : output.results()) {
      final DBObject id = (DBObject) result.get("_id");
      final String country = (String) id.get("country");
      final String region = (String) id.get("region");
      final double averageGrowthForRegion = (double) result.get("avg_growth");
      final int year = (Integer) result.get("year");
      final int growthForYear = (Integer) result.get("annual_growth");
      final double deviationFromMean = (Double) result.get("absolute_deviation");
      if(newRegion(previousRegion, region)){
        regionCount = 0;
        previousRegion = region;
      }
      if(regionCount++ < limit){
        toReturn.add(new RegionMeanGrowthDeviation(country, region, averageGrowthForRegion, year, growthForYear, deviationFromMean));
      }
    }
    return toReturn;
  }

  /**
   * utility method to make conditionals read more (Uncle Bob) "cleanly"
   * @param previousRegion
   * @param region
   * @return
   */
  private boolean newRegion(String previousRegion, String region) {
    return previousRegion == null || !previousRegion.equals(region);
  }

  private BasicDBObject excludeYear(int year) {
    return new BasicDBObject("$match",  new BasicDBObject("populations.year", new BasicDBObject("$gt", year)));
  }

  private DBObject unwindPopulation() {
    return new BasicDBObject("$unwind", "$populations");
  }
}
