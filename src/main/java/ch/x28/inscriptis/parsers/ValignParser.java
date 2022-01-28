package ch.x28.inscriptis.parsers;

import ch.x28.inscriptis.HtmlElement;
import ch.x28.inscriptis.HtmlProperties;

public class ValignParser implements IParser {

  @Override
  public void apply(String attr, HtmlElement htmlElement) {
    htmlElement.setValign(HtmlProperties.VerticalAlignment.valueOf(attr.toUpperCase()));
  }
}
