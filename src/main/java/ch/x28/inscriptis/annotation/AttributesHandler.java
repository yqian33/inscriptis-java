package ch.x28.inscriptis.annotation;

import ch.x28.inscriptis.HtmlElement;
import ch.x28.inscriptis.parsers.IParser;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributesHandler {

  private Map<String, List<IParser>> attributesMap = new HashMap<>();

  public Map<String, List<IParser>> getAttributesMap() {
    return attributesMap;
  }

  public void addToAttributesMap(String attr, IParser parser) {
    if (attributesMap.containsKey(attr)) {
      attributesMap.get(attr).add(parser);
    } else {
      attributesMap.put(attr, Arrays.asList(parser));
    }
  }

  public void mergeAttributesMap(List<AnnotationParser> annotationHandlers) {
    for (AnnotationParser handler : annotationHandlers) {
      if (attributesMap.containsKey(handler.getAttr())) {
        attributesMap.get(handler.getAttr()).add(handler);
      } else {
        List<IParser> parserList = new ArrayList<>();
        parserList.add(handler);
        attributesMap.put(handler.getAttr(), parserList);
      }
    }

  }

  public HtmlElement applyHandlers(NamedNodeMap namedNodeMap, HtmlElement htmlElement) {
    int numAttrs = namedNodeMap.getLength();
    for (int i = 0; i < numAttrs; i++){
      String attrName = namedNodeMap.item(i).getNodeName();
      String attrValue = namedNodeMap.item(i).getNodeValue();
      if (!attributesMap.containsKey(attrName)) {
        continue;
      }
      for (IParser parser : attributesMap.get(attrName)) {
        parser.apply(attrValue, htmlElement);
      }
    }
    return htmlElement;
  }
}

