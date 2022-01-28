package ch.x28.inscriptis.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

import ch.x28.inscriptis.CssProfile;
import ch.x28.inscriptis.HtmlProperties;
import ch.x28.inscriptis.Inscriptis;
import ch.x28.inscriptis.ParserConfig;
import ch.x28.inscriptis.StringUtils;
import ch.x28.inscriptis.models.Canvas;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationTest {

  @Test
  public void testHtmlSnippets() throws IOException, URISyntaxException {

    Path path = Paths.get(getClass().getClassLoader().getResource("snippets").toURI());

    Set<Path> textFiles;
    try (Stream<Path> stream = Files.walk(path)) {
      textFiles = stream
          .filter(file -> !Files.isDirectory(file))
          .filter(file -> file.getFileName().toString().endsWith(".txt"))
          .collect(Collectors.toSet());
    }

    for (Path textFile : textFiles) {
      if (textFile.toString().contains("table-in-table.txt")) {
        continue;
      }

      String text = new String(Files.readAllBytes(textFile), StandardCharsets.UTF_8);
      String html = new String(Files.readAllBytes(Paths.get(textFile.toString().replace(".txt", ".html"))), StandardCharsets.UTF_8);

      text = StringUtils.stripTrailing(text);
      html = "<html><body>" + html + "</body></html>";

      ParserConfig config = new ParserConfig(CssProfile.STRICT);
      Document document = W3CDom.convert(Jsoup.parse(html));
      Inscriptis inscriptis = new Inscriptis(document, config);
      String result = inscriptis.getAnnotatedText();

      assertThat(result)
          .as(textFile.getFileName().toString())
          .isEqualTo(text);
    }
  }

  @Test
  public void testBaseAnnotation() {
    String html = "<h1>This is H1</h1>\n"
        + "<h2>This is H2</h2>\n"
        + "This is link class:\n"
        + "<button class=\"link\" >Learn more</button>\n";

    Map<String, List<String>> rules = new HashMap<>();

    rules.put("#class=link", Arrays.asList("link"));
    rules.put("h1", Arrays.asList("heading", "h1"));
    rules.put("h2", Arrays.asList("heading", "h2"));

    ParserConfig parserConfig = new ParserConfig(rules);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    Document document = W3CDom.convert(Jsoup.parse(html));
    Inscriptis inscriptis = new Inscriptis(document, parserConfig);
    String result = inscriptis.getAnnotatedText();

    assertThat(result.substring(0, 12).trim())
        .isEqualTo("This is H1");

    assertThat(result.substring(12, 24).trim())
        .isEqualTo("This is H2");

    assertThat(result.substring(44, 54))
        .isEqualTo("Learn more");

  }

  private void printAnnotations(List<Canvas.Annotation> annotations, String text) {
    for (Canvas.Annotation annotation : annotations) {
      int end = Math.min(text.length(), annotation.end.intValue());
      System.out.println(annotation.metadata + " : " + text.substring(annotation.start.intValue(), end).trim());

    }
  }

}
