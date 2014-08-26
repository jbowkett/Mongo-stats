package info.bowkett.mongostats;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jbowkett on 22/08/2014.
 */
public class CommandLine {

  /**
   * Parses the string array of command line arguments of the form:
   * ["-T", "3"]
   * into a map of the form:
   * {"-T" => "3"}
   * @param args
   * @return map of switches to values
   */
  public Map<String, String> parse(String[] args) {
    final Map<String, String> parsed = new HashMap<>();
    int index = 0;
    while (index < args.length) {
      final String possibleSwitch = args[index];
      if (possibleSwitch.startsWith("-")) {
        final int nextIndex = index + 1;
        final String value = nextIndex >= args.length ? null : args[nextIndex];
        parsed.put(possibleSwitch, value);
        index += 2;
      }
    }
    return parsed;
  }
}
