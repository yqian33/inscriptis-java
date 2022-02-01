package ch.x28.inscriptis.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.x28.inscriptis.Inscriptis;
import ch.x28.inscriptis.ParserConfig;
import ch.x28.inscriptis.StringUtils;
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

public class DescriptionAnnotationTest {

  private ParserConfig getWikipediaConfig() {

    Map<String, List<String>> rules = new HashMap<>();

    rules.put("h1", Arrays.asList("heading"));
    rules.put("h2", Arrays.asList("heading"));
    rules.put("h3", Arrays.asList("subheading"));
    rules.put("h4", Arrays.asList("subheading"));
    rules.put("h5", Arrays.asList("subheading"));
    rules.put("i", Arrays.asList("emphasis"));
    rules.put("b", Arrays.asList("bold"));
    rules.put("table", Arrays.asList("table"));
    rules.put("th", Arrays.asList("tableheading"));
    rules.put("a", Arrays.asList("link"));

    ParserConfig parserConfig = new ParserConfig(rules);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);
    return parserConfig;
  }

  private ParserConfig getWikipediaConfig2() {

    Map<String, List<String>> rules = new HashMap<>();

    // rules.put("a#title", Arrays.asList("entity"));
    // rules.put("a#class=new", Arrays.asList("missing entity"));
    rules.put("#class=reference", Arrays.asList("citation"));

    ParserConfig parserConfig = new ParserConfig(rules);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);
    return parserConfig;
  }

  @Test
  public void testHtmls() throws IOException, URISyntaxException {
    Path path = Paths.get(getClass().getClassLoader().getResource("html").toURI());

    Set<Path> textFiles;
    try (Stream<Path> stream = Files.walk(path)) {
      textFiles = stream
          .filter(file -> !Files.isDirectory(file))
          .filter(file -> file.getFileName().toString().endsWith(".txt"))
          .collect(Collectors.toSet());
    }

    List<String> bl = Arrays.asList(
        "/Users/yqian/playground/inscriptis-java/target/test-classes/html/stackoverflow.txt"
        //"/Users/yqian/playground/inscriptis-java/target/test-classes/html/fhgr.txt"
        //"/Users/yqian/playground/inscriptis-java/target/test-classes/html/wikipedia.txt"
        //"/Users/yqian/playground/inscriptis-java/target/test-classes/html/wayfair.txt"
    );

    Map<String, List<String>> stackOverFlowRules = new HashMap<>();
    stackOverFlowRules.put("code", Arrays.asList("code"));
    stackOverFlowRules.put("#itemprop=dateCreated", Arrays.asList("creation-date"));
    stackOverFlowRules.put("#class=user-details", Arrays.asList("user"));
    stackOverFlowRules.put("#class=reputation-score", Arrays.asList("reputation"));
    stackOverFlowRules.put("#class=comment-date", Arrays.asList("comment-date"));
    stackOverFlowRules.put("#class=comment-copy", Arrays.asList("comment-comment"));

    // fghr rules
    Map<String, List<String>> fghrRule = new HashMap<>();
    fghrRule.put("div#class=toc", Arrays.asList("table-of-contents"));
    fghrRule.put("#class=FactBox", Arrays.asList("fact-box"));
    fghrRule.put("#cite", Arrays.asList("citation"));
    fghrRule.put("h1", Arrays.asList("heading", "h1"));
    fghrRule.put("h2", Arrays.asList("heading", "h2"));
    fghrRule.put("b", Arrays.asList("emphasis"));
    fghrRule.put("#id=specAndDescript", Arrays.asList("specAndDescript"));
    fghrRule.put("#class=ProductOverviewInformation-description", Arrays.asList("description"));


    ParserConfig parserConfig = new ParserConfig(stackOverFlowRules);
    parserConfig.setDisplayImages(true);
    parserConfig.setDisplayLinks(true);

    for (Path textFile : textFiles) {
      if (!bl.contains(textFile.toString())) {
        continue;
      }
      System.out.println("test file:" + textFile.toString());
      String text = new String(Files.readAllBytes(textFile), StandardCharsets.UTF_8);
      text = StringUtils.stripTrailing(text);

      String html = new String(Files.readAllBytes(Paths.get(textFile.toString().replace(".txt", ".html"))), StandardCharsets.UTF_8);
      html = "<html><body>" + html + "</body></html>";

      Document document = W3CDom.convert(Jsoup.parse(html));
      Inscriptis inscriptis = new Inscriptis(document, parserConfig);
      String result = inscriptis.getAnnotatedText();

      // then
      assertThat(result)
          .as(textFile.getFileName().toString())
          .isEqualTo("");
    }
  }
}
