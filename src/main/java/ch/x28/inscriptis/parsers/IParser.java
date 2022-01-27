package ch.x28.inscriptis.parsers;

import ch.x28.inscriptis.HtmlElement;

public interface IParser {
  void apply(String attr, HtmlElement htmlElement);
}
