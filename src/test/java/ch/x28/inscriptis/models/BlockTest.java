package ch.x28.inscriptis.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class BlockTest {

  @Test
  public void testMergeNormalTextCollapsableWithWhitespaces() {
    {
      Block block = new Block(0L, new Prefix());
      block.mergeNormalText("Hallo");
      assertThat(block.getContent())
          .isEqualTo("Hallo");
      assertThat((block.isCollapsableWhitespace())).isEqualTo(false);
    }

    {
      Block block = new Block(0L, new Prefix());
      block.mergeNormalText(" Hallo ");
      assertThat(block.getContent())
          .isEqualTo("Hallo ");
      assertThat((block.isCollapsableWhitespace())).isEqualTo(true);
    }

    {
      Block block = new Block(0L, new Prefix());
      block.mergeNormalText("");
      assertThat(block.getContent())
          .isEqualTo("");
      assertThat((block.isCollapsableWhitespace())).isEqualTo(true);
    }

    {
      Block block = new Block(0L, new Prefix());
      block.mergeNormalText(" ");
      assertThat(block.getContent())
          .isEqualTo("");
      assertThat((block.isCollapsableWhitespace())).isEqualTo(true);
    }

    {
      Block block = new Block(0L, new Prefix());
      block.mergeNormalText("  ");
      assertThat(block.getContent())
          .isEqualTo("");
      assertThat((block.isCollapsableWhitespace())).isEqualTo(true);
    }

  }

  @Test
  public void testMergeNormalTextNonCollapsableWithWhitespaces() {
    {
      Block block = new Block(0L, new Prefix());
      block.setCollapsableWhitespace(false);
      block.mergeNormalText("Hallo");
      assertThat(block.getContent())
          .isEqualTo("Hallo");
      assertThat((block.isCollapsableWhitespace())).isEqualTo(false);
    }

    {
      Block block = new Block(0L, new Prefix());
      block.setCollapsableWhitespace(false);
      block.mergeNormalText(" Hallo ");
      assertThat(block.getContent())
          .isEqualTo(" Hallo ");
      assertThat((block.isCollapsableWhitespace())).isEqualTo(true);
    }

    {
      Block block = new Block(0L, new Prefix());
      block.setCollapsableWhitespace(false);
      block.mergeNormalText("");
      assertThat(block.getContent())
          .isEqualTo("");
      assertThat((block.isCollapsableWhitespace())).isEqualTo(false);
    }

    {
      Block block = new Block(0L, new Prefix());
      block.setCollapsableWhitespace(false);
      block.mergeNormalText(" ");
      assertThat(block.getContent())
          .isEqualTo(" ");
      assertThat((block.isCollapsableWhitespace())).isEqualTo(true);
    }
  }
}
