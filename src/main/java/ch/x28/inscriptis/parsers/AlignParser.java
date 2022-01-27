package ch.x28.inscriptis.parsers;

import ch.x28.inscriptis.HtmlElement;
import ch.x28.inscriptis.HtmlProperties;

public class AlignParser implements IParser {

  @Override
  public void apply(String attr, HtmlElement htmlElement) {
    htmlElement.setAlign(HtmlProperties.HorizontalAlignment.valueOf(attr.toUpperCase()));
  }
}
