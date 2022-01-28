package ch.x28.inscriptis.models;

import static org.assertj.core.api.Assertions.assertThat;

import ch.x28.inscriptis.HtmlProperties;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TableCellCanvasTest {

  @Test
  public void testHeight() {
    TableCellCanvas cell = new TableCellCanvas(
        HtmlProperties.HorizontalAlignment.LEFT,
        HtmlProperties.VerticalAlignment.TOP);
    {
      cell.addBlock("hallo");
      cell.getNormalizedBlocks();
      Integer height = String.join("\n", cell.getBlocks()).split("\n").length;
      assertThat(cell.getHeight()).isEqualTo(height);
    }

    {
      cell.setBlocks(Arrays.asList("hallo", "echo"));
      cell.getNormalizedBlocks();
      assertThat(cell.getHeight()).isEqualTo(2);
    }

    {
      cell.setBlocks(Arrays.asList("hallo\necho"));
      cell.getNormalizedBlocks();
      assertThat(cell.getHeight()).isEqualTo(2);
    }

    {
      // different from python version due to Python split behaviour
      cell.setBlocks(Arrays.asList("hallo\necho", "Ehre sei Gott", "Jump\n&\nRun!\n\n\n"));
      cell.getNormalizedBlocks();
      Integer height = String.join("\n", cell.getBlocks()).split("\n").length;

      assertThat(cell.getHeight()).isEqualTo(6);
      assertThat(height).isEqualTo(6);
    }

  }

  @Test
  public void testWidth() {

    TableCellCanvas cell = new TableCellCanvas(
        HtmlProperties.HorizontalAlignment.LEFT,
        HtmlProperties.VerticalAlignment.TOP);

    {
      cell.setBlocks(Arrays.asList("hallo"));
      cell.getNormalizedBlocks();
      assertThat(cell.getWidth()).isEqualTo(cell.getBlocks().get(0).length());
    }

    {
      cell.setBlocks(Arrays.asList("hallo\necho", "Ehre sei Gott", "Jump\n&\nRun!\n\n\n"));
      cell.getNormalizedBlocks();
      assertThat(cell.getWidth()).isEqualTo("Ehre sei Gott".length());
    }

    {
      cell.setWidth(95);
      cell.getNormalizedBlocks();
      assertThat(cell.getWidth()).isEqualTo(95);
    }

  }

  @Test
  public void testTableFormatting() {

    TableCellCanvas cell = new TableCellCanvas(
        HtmlProperties.HorizontalAlignment.LEFT,
        HtmlProperties.VerticalAlignment.TOP);

    { // left alignment
      cell.setBlocks(Arrays.asList("Ehre sei Gott!"));
      cell.setWidth(16);
      assertThat(cell.getBlocks().get(0)).isEqualTo("Ehre sei Gott!  ");
    }

    {
      cell = new TableCellCanvas(
          HtmlProperties.HorizontalAlignment.RIGHT,
          HtmlProperties.VerticalAlignment.TOP);

      cell.setBlocks(Arrays.asList("Ehre sei Gott!"));
      cell.setWidth(16);
      assertThat(cell.getBlocks().get(0)).isEqualTo("  Ehre sei Gott!");
    }

  }

  @Test
  public void testTableVerticalFormatting() {

    TableCellCanvas cell = new TableCellCanvas(
        HtmlProperties.HorizontalAlignment.LEFT,
        HtmlProperties.VerticalAlignment.TOP);

    { // left alignment
      cell.setBlocks(Arrays.asList("Ehre sei Gott!"));
      cell.setWidth(16);
      cell.setHeight(4);
      assertThat(cell.getBlocks().size()).isEqualTo(4);
      assertThat(cell.getBlocks().get(0)).isEqualTo("Ehre sei Gott!  ");
      assertThat(cell.getBlocks().get(1)).isEqualTo("");
      assertThat(cell.getBlocks().get(2)).isEqualTo("");
      assertThat(cell.getBlocks().get(3)).isEqualTo("");

    }

    {
      cell = new TableCellCanvas(
          HtmlProperties.HorizontalAlignment.LEFT,
          HtmlProperties.VerticalAlignment.BOTTOM);

      cell.setBlocks(Arrays.asList("Ehre sei Gott!"));
      cell.setWidth(16);
      cell.setHeight(4);
      assertThat(cell.getBlocks().size()).isEqualTo(4);
      assertThat(cell.getBlocks().get(3)).isEqualTo("Ehre sei Gott!  ");
      assertThat(cell.getBlocks().get(1)).isEqualTo("");
      assertThat(cell.getBlocks().get(2)).isEqualTo("");
      assertThat(cell.getBlocks().get(0)).isEqualTo("");
    }

    {
      cell = new TableCellCanvas(
          HtmlProperties.HorizontalAlignment.LEFT,
          HtmlProperties.VerticalAlignment.MIDDLE);

      cell.setBlocks(Arrays.asList("Ehre sei Gott!"));
      cell.setWidth(16);
      cell.setHeight(4);
      assertThat(cell.getBlocks().size()).isEqualTo(4);
      assertThat(cell.getBlocks().get(1)).isEqualTo("Ehre sei Gott!  ");
      assertThat(cell.getBlocks().get(3)).isEqualTo("");
      assertThat(cell.getBlocks().get(2)).isEqualTo("");
      assertThat(cell.getBlocks().get(0)).isEqualTo("");
    }

  }
}
