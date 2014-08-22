package info.bowkett.mongostats;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jbowkett on 22/08/2014.
 */
public class CommandLine {

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
