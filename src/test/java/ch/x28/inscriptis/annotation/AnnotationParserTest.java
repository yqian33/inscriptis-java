package ch.x28.inscriptis.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import ch.x28.inscriptis.CssProfile;
import ch.x28.inscriptis.HtmlElement;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationParserTest {

  @Test
  public void testBaseRule() {
    Map<String, List<String>> rule = new HashMap<>();
    rule.put("table#border=1", Arrays.asList("table"));
    rule.put("hr", Arrays.asList("horizontal-line"));

    AnnotationModel model = new AnnotationModel(CssProfile.STRICT, rule);
    Map<String, List<String>> tagToAnnotations = model.parse(rule);
    assertThat(tagToAnnotations.size()).isEqualTo(1);
    assertThat(tagToAnnotations.get("hr").get(0)).isEqualTo("horizontal-line");

    assertThat(model.getCssAttr().size()).isEqualTo(2);
    AnnotationParser parser = model.getCssAttr().get(0);
    assertThat(parser.getMatchTag()).isEqualTo("table");
    assertThat(parser.getMatchValue()).isEqualTo("1");
    assertThat(parser.getAttr()).isEqualTo("border");

    HtmlElement htmlElement = new HtmlElement().setTag("table");
    parser.apply("1", htmlElement);
    assertThat(htmlElement.getAnnotation().contains("table")).isEqualTo(true);

  }

  @Test
  public void testApplyRule() {
    Map<String, List<String>> rule = new HashMap<>();
    rule.put("table#border=1", Arrays.asList("table"));
    rule.put("hr", Arrays.asList("horizontal-line"));
    rule.put("#color=red", Arrays.asList("red"));
    rule.put("#bgcolor", Arrays.asList("bgcolor"));

    AnnotationModel model = new AnnotationModel(CssProfile.STRICT, rule);
    assertThat(model.getCss().get("hr").getAnnotation().contains("horizontal-line")).isEqualTo(true);

    AttributesHandler handler = new AttributesHandler();
    handler.mergeAttributesMap(model.getCssAttr());
    assertThat(handler.getAttributesMap().get("border").get(0).toString())
        .isEqualTo("AnnotationParser[attr=border, matchTag=table, matchValue=1, annotations=[table]]");

    assertThat(handler.getAttributesMap().get("color").get(0).toString())
        .isEqualTo("AnnotationParser[attr=color, matchTag=, matchValue=red, annotations=[red]]");

    assertThat(handler.getAttributesMap().get("bgcolor").get(0).toString())
        .isEqualTo("AnnotationParser[attr=bgcolor, matchTag=, matchValue=null, annotations=[bgcolor]]");

  }
}
