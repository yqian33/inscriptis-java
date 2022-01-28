package ch.x28.inscriptis.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.x28.inscriptis.CssProfile;
import ch.x28.inscriptis.Inscriptis;
import ch.x28.inscriptis.ParserConfig;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;


public class AnnotatedTextTest {

  @Test
  public void testList() {

    ParserConfig parserConfig = new ParserConfig(CssProfile.STRICT);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    String html;
    Document document;
    Inscriptis inscriptis;
    String result;

    {

      html = "<body>Thomas<div>Anna <b>läuft</b> weit weg.</div>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Thomas\nAnna läuft weit weg.");
    }

    {
      html = "<body>Thomas <ul><li>  <div><div>Anton</div></div>Maria</ul></body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Thomas\n  * Anton\n    Maria");
    }

    {
      html = "<body>Thomas <ul><li> a  <div>Anton</div>Maria</ul></body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Thomas\n  * a\n    Anton\n    Maria");
    }

    {
      html = "<body>Thomas <ul><li><div>Anton</div>Maria</ul></body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Thomas\n  * Anton\n    Maria");
    }
  }

  @Test
  public void testMarginHandling() {

    ParserConfig parserConfig = new ParserConfig(CssProfile.STRICT);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    String html;
    Document document;
    Inscriptis inscriptis;
    String result;

    {

      html = "<body>Hallo\n"
          + "                     <div style=\"margin-top: 1em; margin-bottom: 1em\">Echo\n"
          + "                         <div style=\"margin-top: 2em\">Mecho</div>\n"
          + "                     </div>\n"
          + "                     sei Gott\n"
          + "               </body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Hallo\n\nEcho\n\n\nMecho\n\nsei Gott");
    }

    {

      html = "<body>Hallo\n"
          + "                     <div style=\"margin-top: 1em; margin-bottom: 1em\">Echo</div>\n"
          + "                         <div style=\"margin-top: 2em\">Mecho</div>\n"
          + "                     sei Gott\n"
          + "               </body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Hallo\n\nEcho\n\n\nMecho\nsei Gott");
    }

    {

      html = "<body>Hallo\n"
          + "                     <div style=\"margin-top: 1em; margin-bottom: 1em\">\n"
          + "                         <div style=\"margin-top: 2em\">Ehre</div>\n"
          + "                    </div>\n"
          + "                    sei Gott\n"
          + "               </body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Hallo\n\n\nEhre\n\nsei Gott");
    }
  }

  @Test
  public void testMarginBeforeStart() {
    ParserConfig parserConfig = new ParserConfig(CssProfile.STRICT);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    String html;
    Document document;
    Inscriptis inscriptis;
    String result;

    {

      html = "<html><body><br>first</p></body></html>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("\nfirst");
    }

    {

      html = "<html><body>first<p>"
          + "second</p></body></html>";

      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("first\n\nsecond");
    }
  }

  @Test
  public void testWhiteSpaceHandling() {
    ParserConfig parserConfig = new ParserConfig(CssProfile.STRICT);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    String html;
    Document document;
    Inscriptis inscriptis;
    String result;

    {
      html = "<body><span style=\"white-space: normal\"><i>1</i>2\n3</span></body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("12 3");
    }


    {
      html = "<body><span style=\"white-space: nowrap\"><i>1</i>2\n3</span></body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("12 3");
    }


    {
      html = "<body><span style=\"white-space: pre\"><i>1</i>2\n3</span></body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("12\n3");
    }

    {
      html = "<body><span style=\"white-space: pre-line\"><i>1</i>2\n3</span></body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("12\n3");
    }

    {
      html = "<body><span style=\"white-space: pre-wrap\"><i>1</i>2\n3</span></body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("12\n3");
    }
  }

  @Test
  public void testBorderLineCases() {
    ParserConfig parserConfig = new ParserConfig(CssProfile.STRICT);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    String html;
    Document document;
    Inscriptis inscriptis;
    String result;

    {
      html = "<body>Hallo<span style=\"white-space: pre\">echo</span> versus";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Halloecho versus");
    }

    {
      html = "<body>Hallo<span style=\"white-space: pre\"> echo</span> versus";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Hallo echo versus");
    }

    {
      html = "<body>Hallo <span style=\"white-space: pre\">echo</span> versus";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Hallo echo versus");
    }

    {
      html = "<body>Hallo   <span style=\"white-space: pre\">   echo</span> versus";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Hallo    echo versus");
    }
  }

  @Test
  public void testTail() {
    ParserConfig parserConfig = new ParserConfig(CssProfile.STRICT);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    String html;
    Document document;
    Inscriptis inscriptis;
    String result;

    {
      html = "<body>Hi<span style=\"white-space: pre\"> 1   3 </span> versus 1   3";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Hi 1   3  versus 1 3");
    }
  }

  @Test
  public void testTableCellSeparator() {
    ParserConfig parserConfig = new ParserConfig(CssProfile.STRICT);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    String html;
    Document document;
    Inscriptis inscriptis;
    String result;

    {
      html = "<html><body><table><tr><td>Hallo<br>Eins</td><td>Echo<br>Zwei</td></tr></table></html>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Hallo  Echo\nEins   Zwei");
    }

    {
      html = "<html><body><table><tr><td>Hallo<br>Eins</td><td>Echo<br>Zwei</td></tr></table></html>";
      document = W3CDom.convert(Jsoup.parse(html));
      parserConfig.setTableCellSeparator("\t");
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result)
          .isEqualTo("Hallo\tEcho\nEins \tZwei");
    }

  }

  @Test
  public void testA() {
    ParserConfig parserConfig = new ParserConfig(CssProfile.STRICT);
    parserConfig.setDisplayImages(false);
    parserConfig.setDisplayLinks(false);

    String html;
    Document document;
    Inscriptis inscriptis;
    String result;

    {
      html = "<html><body><a href=\"first\">first</a><a href=\"second\">second</a></body></html>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result).isEqualTo("firstsecond");
    }

    {
      html = "<html><body><a href=\"first\">first</a>\n<a href=\"second\">second</a></body></html>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result).isEqualTo("first second");
    }

    {
      html = "<html><body><a href=\"first\">first</a><a href=\"second\">second</a></body></html>";
      document = W3CDom.convert(Jsoup.parse(html));
      parserConfig.setDisplayLinks(true);
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result).isEqualTo("[first](first)[second](second)");
    }
  }

  @Test
  public void testBrokenTable() {
    ParserConfig parserConfig = new ParserConfig(CssProfile.STRICT);
    parserConfig.setDisplayImages(false);
    parserConfig.setDisplayLinks(false);

    String html;
    Document document;
    Inscriptis inscriptis;
    String result;

    {
      // two lines (i.e. missing </td> before the <tr> and before the </table>
      html = "<body>hallo<table>"
          + "<tr><td>1<td>2"
          + "<tr><td>3<td>4"
          + "</table>echo</body>";
      document = W3CDom.convert(Jsoup.parse(html));
      inscriptis = new Inscriptis(document, parserConfig);
      result = inscriptis.getAnnotatedText();

      assertThat(result).isEqualTo("hallo\n1  2\n3  4\n\necho");
    }

  }
}
