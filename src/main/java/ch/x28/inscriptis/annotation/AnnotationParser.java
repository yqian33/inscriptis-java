package ch.x28.inscriptis.annotation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import ch.x28.inscriptis.HtmlElement;
import ch.x28.inscriptis.parsers.IParser;
import org.apache.commons.lang.StringUtils;

public class AnnotationParser implements IParser {

  private String attr;
  private String matchTag;
  private String matchValue;
  private List<String> annotations;

  public AnnotationParser(List<String> annotations,
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
    if (!StringUtils.isEmpty(matchTag) && !matchTag.equals(htmlElement.getTag())) {
      return;
    }

    if (!StringUtils.isEmpty(matchValue) && !attributeValue.contains(matchValue)) {
      return;
    }
    if (htmlElement.getAnnotation() == null) {
      htmlElement.setAnnotation(new HashSet<>());
    }
    htmlElement.getAnnotation().addAll(annotations);

  }

  @Override
  public String toString() {
    return "AnnotationParser[attr=" + attr +
        ", matchTag=" + matchTag +
        ", matchValue=" + matchValue +
        ", annotations=" + annotations + "]";
  }

  public String getAttr() {
    return attr;
  }

  public void setAttr(String attr) {
    this.attr = attr;
  }

  public List<String> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<String> annotations) {
    this.annotations = annotations;
  }

  public String getMatchTag() {
    return matchTag;
  }

  public String getMatchValue() {
    return matchValue;
  }
}
