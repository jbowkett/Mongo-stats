package info.bowkett.mongostats.tasks;

import info.bowkett.mongostats.Region;
import info.bowkett.mongostats.RegionDAO;

import java.util.List;

/**
 * Created by jbowkett on 24/08/2014.
 */
public class Task2 implements Task {
  private final RegionDAO regionDao;

  public Task2(RegionDAO regionDao) {
    this.regionDao = regionDao;
  }

  @Override
  public void demonstrate() {
    final List<Region> regions = regionDao.allRegions();
    for (Region region : regions) {
      regionDao.updatePopulationChange(region);
    }
  }
}
