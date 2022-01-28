package ch.x28.inscriptis.models;

import static org.assertj.core.api.Assertions.assertThat;

import ch.x28.inscriptis.Prefix;
import org.junit.jupiter.api.Test;

public class PrefixTest {

  @Test
  public void testSimplePrefix() {

    {
      Prefix prefix = new Prefix();
      prefix.registerPrefix(5, "1. ");

      // Use prefix
      assertThat(prefix.first()).isEqualTo("  1. ");

      // no prefix since already used
      assertThat(prefix.first()).isEqualTo("");

      // prefix used to indent lines separated with newlines
      assertThat(prefix.rest()).isEqualTo("     ");

    }
  }

  @Test
  public void testCombinedPrefix() {

    {
      Prefix prefix = new Prefix();
      prefix.registerPrefix(5, "1. ");
      prefix.registerPrefix(2, "");

      // Use prefix
      assertThat(prefix.first()).isEqualTo("    1. ");

      // no prefix since already used
      assertThat(prefix.first()).isEqualTo("");

      prefix.removeLastPrefix();
      assertThat(prefix.first()).isEqualTo("");

      // final consumption, no prefix left
      prefix.removeLastPrefix();
      assertThat(prefix.first()).isEqualTo("");

      // set to false to start second run
      prefix.setConsumed(false);

      prefix.registerPrefix(5, "2. ");
      prefix.registerPrefix(2, "- ");

      assertThat(prefix.first()).isEqualTo("     - ");
      assertThat(prefix.first()).isEqualTo("");
      assertThat(prefix.rest()).isEqualTo("       ");

      prefix.setConsumed(false);
      prefix.removeLastPrefix();

      assertThat(prefix.first()).isEqualTo("  2. ");
      assertThat(prefix.rest()).isEqualTo("     ");
    }
  }
}
