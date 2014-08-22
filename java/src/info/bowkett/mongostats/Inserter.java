package info.bowkett.mongostats;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.stream.Stream;


/**
 * Created by jbowkett on 20/08/2014.
 */
public class Inserter {
  private static final String COLLECTION_NAME = "regions";
  private final MongoClient mongoClient;
  private final DB db;
  private final DBCollection collection;

  public Inserter(MongoClient mongoClient, String database) {
    this.mongoClient = mongoClient;
    db = mongoClient.getDB(database);
    collection = db.getCollection(COLLECTION_NAME);
  }

  public void insertAll(Stream<Region> allToAdd){
    allToAdd.forEach(region -> insert(region));
  }

  public void insert(Region r){
    final BasicDBObject mapped = map(r);
    collection.insert(mapped);
  }

  protected BasicDBObject map(Region r) {
    BasicDBObject mapped = new BasicDBObject();
    mapped.put("country", r.getCountry());
    mapped.put("region", r.getRegion());
    r.populationEntries().forEach(entry -> mapped.put(entry.getKey()+"_pop", entry.getValue()));
    return mapped;
  }
}
