package info.bowkett.mongostats;

import com.mongodb.*;

import java.util.ArrayList;
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

  public void insertAll(Stream<Region> allToAdd){
    allToAdd.forEach(region -> insert(region));
  }

  public void insert(Region r){
    final DBObject mapped = codec.toDBObject(r);
    collection.insert(mapped);
  }

  public List<Region> allRegions() {
    final DBCursor cursor = collection.find();
    final List<Region> regions = new ArrayList<>();
    while (cursor.hasNext()){
      final DBObject dbObject = cursor.next();
      regions.add(codec.toRegion(dbObject));
    }
    return regions;
  }
}
