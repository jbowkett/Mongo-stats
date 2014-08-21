package info.bowkett.mongostats;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jbowkett on 21/08/2014.
 */
public class RegionFactoryTest {

  private RegionsFactory factory;

  @Before
  public void setup() {
    factory = new RegionsFactory();
  }

  @Test
  public void testCreateFromLine(){
    final Region region = factory.createFromLine("2012,England,London,49141671");
    assertNotNull(region);
    assertEquals("England", region.getCountry());
    assertEquals("London", region.getRegion());
    assertEquals(49141671, region.getPopulationFor(2012));
  }

  @Test
  public void testCreateFromStreamWithOneLine(){
    final String [] lines = {"2012,England,London,49141671"};
    final Region region = extractRegionAtIndex(lines, 0);
    assertEquals("England", region.getCountry());
    assertEquals("London", region.getRegion());
    assertEquals(49141671, region.getPopulationFor(2012));
  }

  @Test
  public void testCreateFromStreamWithTwoLinesForSameRegion(){
    final String [] lines = {"2012,England,London,49141671", "2011,England,London,49140671"};
    final Region region = extractRegionAtIndex(lines, 0);
    assertEquals("England", region.getCountry());
    assertEquals("London", region.getRegion());
    assertEquals(49140671, region.getPopulationFor(2011));
    assertEquals(49141671, region.getPopulationFor(2012));
  }

  @Test
  public void testCreateFromStreamWithTwoLinesForTwoDifferentRegions(){
    final String [] lines = {
        "2012,England,London,49141671",
        "2011,England,London,49140671",
        "2012,Northern Ireland,Belfast,1685529",
        "2011,Northern Ireland,Belfast,1685427"
    };

    final Collection<Region> regions = factory.createFromStream(Arrays.stream(lines));
    assertEquals(2, regions.size());
    final Region london  = regions.stream().filter(region -> region.getRegion().equals("London")).findFirst().get();
    final Region belfast = regions.stream().filter(region -> region.getRegion().equals("Belfast")).findFirst().get();

    assertEquals("England", london.getCountry());
    assertEquals("London", london.getRegion());
    assertEquals(49140671, london.getPopulationFor(2011));
    assertEquals(49141671, london.getPopulationFor(2012));

    assertEquals("Northern Ireland", belfast.getCountry());
    assertEquals("Belfast", belfast.getRegion());
    assertEquals(1685427, belfast.getPopulationFor(2011));
    assertEquals(1685529, belfast.getPopulationFor(2012));
  }

  public Region extractRegionAtIndex(String[] lines, int index) {
    final Region[] regionsAry = extractRegions(lines);
    return regionsAry[index];
  }

  public Region[] extractRegions(String[] lines) {
    final Collection<Region> regions = factory.createFromStream(Arrays.stream(lines));
    final Region[] regionsAry = new Region[regions.size()];
    regions.toArray(regionsAry);
    return regionsAry;
  }


}
