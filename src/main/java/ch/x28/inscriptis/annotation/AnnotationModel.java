package ch.x28.inscriptis.annotation;

import ch.x28.inscriptis.CssProfile;
import ch.x28.inscriptis.HtmlElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationModel {

  private List<AnnotationHandler> cssAttr = new ArrayList<>();
  private CssProfile css;

  public List<AnnotationHandler> getCssAttr() {
    return cssAttr;
  }

  public void setCssAttr(List<AnnotationHandler> cssAttr) {
    this.cssAttr = cssAttr;
  }

  public CssProfile getCss() {
    return css;
  }

  public void setCss(CssProfile css) {
    this.css = css;
  }


  public AnnotationModel(CssProfile cssProfile, Map<String, List<String>> model) {
    Map<String, List<String>> tagToAnnotations = parse(model);
    for (Map.Entry<String, List<String>> kv : tagToAnnotations.entrySet()) {
      String tag = kv.getKey();
      List<String> annotations = kv.getValue();
      if (cssProfile.get(tag) == null) {
        cssProfile.add(tag, new HtmlElement());
      }
      List<String> updatedAnnotations = cssProfile.get(tag).getAnnotation();
      if (updatedAnnotations == null) {
        updatedAnnotations = new ArrayList<>();
      }
      updatedAnnotations.addAll(annotations);
      cssProfile.get(tag).setAnnotation(updatedAnnotations);
      this.css = cssProfile;
    }

  }


  public Map<String, List<String>> parse(Map<String, List<String>> model) {

    Map<String, List<String>> tagToAnnotations = new HashMap<>();

    for (Map.Entry<String, List<String>> kv : model.entrySet()) {
      String key = kv.getKey();
      List<String> annotations = kv.getValue();

      if (key.contains("#")) {
        List<String> tagAttr =  Arrays.asList(key.split("#"));
        String tag = tagAttr.get(0);
        String attr = tagAttr.get(1);
        String value = null;

        if (attr.contains("=")) {
          List<String> attrValue =  Arrays.asList(key.split("="));
          attr = attrValue.get(0);
          value = attrValue.get(1);
        }

        cssAttr.add(new AnnotationHandler(annotations, attr, tag, value));

      } else {
        tagToAnnotations.computeIfAbsent(key, k -> new ArrayList<>())
            .addAll(annotations);
      }
    }

    return tagToAnnotations;
  }
}
