package ch.x28.inscriptis;

import static org.assertj.core.api.Assertions.assertThat;

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
  public void testTable() {
    HtmlProperties.VerticalAlignment a = HtmlProperties.VerticalAlignment.valueOf("TOP");
    Map<String, List<String>> rules = new HashMap<>();
    rules.put("ul", Arrays.asList("heading", "h1"));
    rules.put("h2", Arrays.asList("heading", "h2"));
    rules.put("b", Arrays.asList("emphasis"));
    rules.put("div#class=toc", Arrays.asList("table-of-contents"));
    rules.put("#class=FactBox", Arrays.asList("fact-box"));
    rules.put("#cite", Arrays.asList("citation"));
    ParserConfig parserConfig = new ParserConfig(rules);
    parserConfig.setDisplayImages(true);

    String html = "<html><body><table>\n"
        + "<tr>\n"
        + "  <td width=\"134\" valign=\"top\" class=\"nav\"><ul id=\"navi\"><li class=\"act1\"><a"
        + " href=\"aktuell/aktuell.html\" onfocus=\"blurLink(this);\" "
        + "class=\"FactBox\">aktuell</a></li><li class=\"pas1\"><a href=\"aktuell/projekte.html\" "
        + "onfocus=\"blurLink(this);\" class=\"pas1\">projekte</a></li><li class=\"pas1\"><a "
        + "href=\"aktuell/zu-verkaufen.html\" onfocus=\"blurLink(this);\" class=\"pas1\">zu "
        + "verkaufen</a></li><li class=\"pas1\"><a href=\"aktuell/offene-stelle.html\" "
        + "onfocus=\"blurLink(this);\" class=\"pas1\">offene stelle</a></li></ul></td>\n"
        + "                                        <td width=\"743\" valign=\"top\" "
        + "class=\"con\"><!--TYPO3SEARCH_begin--><div class=\"cnb\"><a id=\"c49\"></a><div "
        + "class=\"csc-textpic csc-textpic-center csc-textpic-below "
        + "csc-textpic-equalheight\"><div class=\"csc-textpic-text\"><p>An der Gewerbeausstellung"
        + " vom 1.-3.September sind wir nicht persönlich anwesend. \n"
        + "                                        </p>\n"
        + "</tr>\n"
        + "</table>\n"
        + "</body></html>";

    Document document = W3CDom.convert(Jsoup.parse(html));
    Inscriptis inscriptis = new Inscriptis(document, parserConfig);
    String result = inscriptis.getText();
    String n = inscriptis.getAnnotatedText();
    System.out.println(n);
    System.out.println(inscriptis.getAnnotations());
    assertThat(n).isEqualTo(result);
  }

  @Test
  public void test() {
    HtmlElement h1 = new HtmlElement();
    HtmlElement h2 = h1.clone();
    h2.setTag("h2");
    assertThat(h2.getTag()).isEqualTo(h1.getTag());
    String res = StringUtils.repeat("\n", 0);
    assertThat(res).isEqualTo("");
  }

  @Test
  public void testHtmls() throws IOException, URISyntaxException {

    // given
    Path path = Paths.get(getClass().getClassLoader().getResource("html").toURI());

    Set<Path> textFiles;
    try (Stream<Path> stream = Files.walk(path)) {
      textFiles = stream
          .filter(file -> !Files.isDirectory(file))
          .filter(file -> file.getFileName().toString().endsWith(".txt"))
          .collect(Collectors.toSet());
    }
    System.out.println("Total test file:" + textFiles.size());
    int count = 0;

    List<String> bl = Arrays.asList(
        "/Users/yqian/playground/inscriptis-java/target/test-classes/html/stackoverflow.txt"
    );

    Map<String, List<String>> rules = new HashMap<>();

    // rules.put("code", Arrays.asList("code"));
    rules.put("#itemprop=dateCreated", Arrays.asList("creation-date"));
    rules.put("#class=user-details", Arrays.asList("user"));
    rules.put("#class=reputation-score", Arrays.asList("reputation"));
    rules.put("#class=comment-date", Arrays.asList("comment-date"));
    rules.put("#class=comment-copy", Arrays.asList("comment-comment"));

    ParserConfig parserConfig = new ParserConfig(rules);
    parserConfig.setDisplayImages(true);

    for (Path textFile : textFiles) {
      if (!bl.contains(textFile.toString())) {
        continue;
      }
      System.out.println("test file:" + textFile.toString());
      String text = new String(Files.readAllBytes(textFile), StandardCharsets.UTF_8);
      text = StringUtils.stripTrailing(text);

      String html = new String(Files.readAllBytes(Paths.get(textFile.toString().replace(".txt", ".html"))), StandardCharsets.UTF_8);
      html = "<html><body>" + html + "</body></html>";

      // System.out.println("html:" + html);
      Document document = W3CDom.convert(Jsoup.parse(html));
      Inscriptis inscriptis = new Inscriptis(document, parserConfig);
      String result = inscriptis.getAnnotatedText();
      count += 1;

      printAnnotations(inscriptis.getAnnotations(), result);
      System.out.println("text:" + result);

      // then
      assertThat(result)
          .as(textFile.getFileName().toString())
          .isEqualTo("");
    }
  }

  @Test
  public void testHtmlSnippets() throws IOException, URISyntaxException {

    // given
    Path path = Paths.get(getClass().getClassLoader().getResource("snippets").toURI());

    Set<Path> textFiles;
    try (Stream<Path> stream = Files.walk(path)) {
      textFiles = stream
          .filter(file -> !Files.isDirectory(file))
          .filter(file -> file.getFileName().toString().endsWith(".txt"))
          .collect(Collectors.toSet());
    }
    System.out.println("Total test file:" + textFiles.size());
    int count = 0;

    List<String> bl = Arrays.asList(
        "/Users/yqian/playground/inscriptis-java/target/test-classes/snippets/pre.txt",
        "/Users/yqian/playground/inscriptis-java/target/test-classes/snippets/nested-table.txt",
        "/Users/yqian/playground/inscriptis-java/target/test-classes/snippets/invalid-table.txt",
        "/Users/yqian/playground/inscriptis-java/target/test-classes/snippets/table-in-table.txt",
        "/Users/yqian/playground/inscriptis-java/target/test-classes/snippets/table-itemize.txt"
    );

    for (Path textFile : textFiles) {
      if (bl.contains(textFile.toString())) {
        continue;
      }
      System.out.println("Test file:" + textFile.toString());
      String text = new String(Files.readAllBytes(textFile), StandardCharsets.UTF_8);
      String html = new String(Files.readAllBytes(Paths.get(textFile.toString().replace(".txt", ".html"))), StandardCharsets.UTF_8);

      text = StringUtils.stripTrailing(text);
      html = "<html><body>" + html + "</body></html>";

      // when
      ParserConfig config = new ParserConfig(CssProfile.STRICT);
      Document document = W3CDom.convert(Jsoup.parse(html));
      Inscriptis inscriptis = new Inscriptis(document, config);
      String result = inscriptis.getAnnotatedText();
      count += 1;
      // then
      assertThat(result)
          .as(textFile.getFileName().toString())
          .isEqualTo(text);
    }
  }

  @Test
  public void baseTest() {
    String html = "<h1>This is H1</h1>\n"
        + "<h2>This is H2</h2>\n"
        + "This is link class:\n"
        + "<button class=\"link\" >Learn more</button>\n";

    Map<String, List<String>> rules = new HashMap<>();

    // rules.put("code", Arrays.asList("code"));
    rules.put("#class=link", Arrays.asList("link"));
    rules.put("h1", Arrays.asList("heading", "h1"));
    rules.put("h2", Arrays.asList("heading", "h2"));

    ParserConfig parserConfig = new ParserConfig(rules);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    Document document = W3CDom.convert(Jsoup.parse(html));
    Inscriptis inscriptis = new Inscriptis(document, parserConfig);
    String result = inscriptis.getAnnotatedText();
    System.out.println(result);
    System.out.println(inscriptis.getAnnotations());
    printAnnotations(inscriptis.getAnnotations(), result);

    assertThat(result)
        .isEqualTo("");

  }

  private void printAnnotations(List<Canvas.Annotation> annotations, String text) {
    for (Canvas.Annotation annotation : annotations) {
      int end = Math.min(text.length(), annotation.end.intValue());
      System.out.println(annotation.metadata + ": " +  text.substring(annotation.start.intValue(), end));
    }
  }
}
