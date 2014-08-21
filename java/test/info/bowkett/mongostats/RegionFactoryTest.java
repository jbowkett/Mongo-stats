package info.bowkett.mongostats;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by jbowkett on 21/08/2014.
 */
public class RegionFactoryTest {

  @Test
  public void testCreateFromStringWithOneLine(){
    final RegionsFactory factory = new RegionsFactory();
    final Region region = factory.createFromLine("2012,England,London,49141671");
    assertNotNull(region);
    assertEquals("England", region.getCountry());
    assertEquals("London", region.getRegion());
    assertEquals(49141671, region.getPopulationFor(2012));
  }
}
