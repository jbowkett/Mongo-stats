
package info.bowkett.mongostats;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.assertEquals;


/**
 * PopulationChange Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Aug 24, 2014</pre>
 */
public class RegionTest {

  @Before
  public void before() throws Exception {
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: updateRegion(Region region)
   */
  @Test
  public void testPopulationChangeWithNoPopulationStats() throws Exception {
    final Region toTestWith = new Region("Test", "Test");
    assertEquals(0, toTestWith.getPopulationChangeFor(2011));
  }

  @Test
  public void testPopulationChangeWithOnlyOnePopulationStat() throws Exception {
    final Region toTestWith = new Region("Test", "Test");
    toTestWith.insertPopulation(2011, 10);
    assertEquals(0, toTestWith.getPopulationChangeFor(2011));
  }

  @Test
  public void testPopulationChangeWithOnlyTwoPopulationStat() throws Exception {
    final Region toTestWith = new Region("Test", "Test");
    toTestWith.insertPopulation(2011, 10);
    toTestWith.insertPopulation(2012, 100);
    assertEquals(90, toTestWith.getPopulationChangeFor(2012));
  }
}
