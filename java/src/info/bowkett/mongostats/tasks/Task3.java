package info.bowkett.mongostats.tasks;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBCollection;
import info.bowkett.mongostats.Region;
import info.bowkett.mongostats.RegionDAO;

import java.util.List;

/**
 * Created by jbowkett on 24/08/2014.
 */
public class Task3 implements Task {
  private final RegionDAO regionDao;

  public Task3(RegionDAO regionDao) {
    this.regionDao = regionDao;
  }

  @Override
  public void demonstrate() {

    final int recordsUpdated = regionDao.setAverageGrowthForEachRegion();
    System.out.println(recordsUpdated + "records updated with average growth");

    System.out.println("Task 3 complete.");
  }
}
