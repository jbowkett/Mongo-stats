package info.bowkett.mongostats;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * CommandLine Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Aug 22, 2014</pre>
 */
public class CommandLineTest {

  private CommandLine commandLine;

  @Before
  public void before() throws Exception {
    commandLine = new CommandLine();
  }

  /**
   * Method: parse(String[] args)
   */
  @Test
  public void testParseHelpOnlyCmdLineSwitch() throws Exception {
    final String[] args = {"-help"};
    final Map<String, String> parsedArgs = commandLine.parse(args);
    assertTrue(parsedArgs.containsKey("-help"));
  }

  @Test
  public void testParseHelpWithSomethingElseCmdLineSwitch() throws Exception {
    final String[] args = {"-help", "something_else"};
    final Map<String, String> parsedArgs = commandLine.parse(args);
    assertTrue(parsedArgs.containsKey("-help"));
    assertFalse(parsedArgs.containsKey("something_else"));
  }

  @Test
  public void testParseValidCmdLineSwitch() throws Exception {
    final String[] args = {"-DB", "database", "-P", "1433"};
    final Map<String, String> parsedArgs = commandLine.parse(args);
    assertEquals("1433", parsedArgs.get("-P"));
  }
}
