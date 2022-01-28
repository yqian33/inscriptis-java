package ch.x28.inscriptis.models;

import static org.assertj.core.api.Assertions.assertThat;

import ch.x28.inscriptis.HtmlElement;
import ch.x28.inscriptis.HtmlProperties;
import org.junit.jupiter.api.Test;

public class CanvasTest {

  @Test
  public void test() {

    {
      HtmlElement htmlElement = new HtmlElement();
      assertThat(getText(htmlElement)).isEqualTo("firstEhre sei Gott!last");

      htmlElement.setDisplay(HtmlProperties.Display.BLOCK);
      htmlElement.setMarginBefore(1);
      htmlElement.setMarginAfter(2);
      assertThat(getText(htmlElement)).isEqualTo("first\n\nEhre sei Gott!\n\n\nlast");

      htmlElement.setListBullet("* ");
      assertThat(getText(htmlElement)).isEqualTo("first\n\n* Ehre sei Gott!\n\n\nlast");

      htmlElement.setPadding(3);
      assertThat(getText(htmlElement)).isEqualTo("first\n\n * Ehre sei Gott!\n\n\nlast");

      htmlElement.setPrefix(">>");
      htmlElement.setSuffix("<<");
      assertThat(getText(htmlElement)).isEqualTo("first\n\n * >>Ehre sei Gott!<<\n\n\nlast");



    }

  }

  private static String getText(HtmlElement htmlElement) {
    Canvas canvas = new Canvas();
    htmlElement.setCanvas(canvas);

    new HtmlElement().setCanvas(canvas).write("first");

    canvas.openTag(htmlElement);

    htmlElement.write("Ehre sei Gott!");
    canvas.closeTag(htmlElement);

    new HtmlElement().setCanvas(canvas).write("last");

    canvas.flushInline();

    return String.join("\n", canvas.getBlocks());

  }
}
