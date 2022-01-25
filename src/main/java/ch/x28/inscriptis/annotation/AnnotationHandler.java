package ch.x28.inscriptis.annotation;

import java.util.Arrays;
import java.util.List;
import ch.x28.inscriptis.HtmlElement;

public class AnnotationHandler {

  private String attr;
  private String matchTag;
  private String matchValue;
  private List<String> annotations;

  public AnnotationHandler(List<String> annotations,
                           String attr,
                           String matchTag,
                           String matchValue) {
    this.annotations = annotations;
    this.attr = attr;
    this.matchTag = matchTag;
    this.matchValue = matchValue;

  }

  public void apply(String attributeValue, HtmlElement htmlElement) {
    List<String> a = Arrays.asList(attributeValue.split(""));
    if ( (matchTag != null && !matchTag.equals(htmlElement.getTag()))
        || (matchValue != null && !a.contains(matchValue)) ) {
      return;
    }
    htmlElement.getAnnotation().addAll(annotations);

  }

  @Override
  public String toString() {
    return "AnnotationHandler[attr=" + attr +
        ", matchTag=" + matchTag +
        ", matchValue=" + matchValue +
        ", annotations=(" + annotations + ")]";
  }
}
