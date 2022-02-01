package ch.x28.inscriptis.models;

import ch.x28.inscriptis.HtmlProperties;
import ch.x28.inscriptis.Line;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class Block extends Line {

  private Long index;

  private boolean collapsableWhitespace = true;

  private Prefix prefixObj;

  public Block(Long index, Prefix prefix) {
    this.index = index;
    this.prefixObj = prefix;
  }

  Block newBlock() {
    this.prefixObj.setConsumed(false);
    return new Block(index + 1, this.prefixObj);
  }

  @Override
  public String getText() {
    if (!collapsableWhitespace) {
      return getContent();
    }
    if (getContent().endsWith(" ")) {
      String content = getContent().substring(0, getContent().length()-1);
      setContent(content);
      index -= 1;
    }
    return getContent();
  }

  void merge(String text, HtmlProperties.WhiteSpace whiteSpace) {
    if ( whiteSpace == HtmlProperties.WhiteSpace.PRE) {
      mergePreText(text);
    } else {
      mergeNormalText(text);
    }
  }

  void mergeNormalText(String text) {
    List<String> normalizedText = new ArrayList<>();
    for (char ch : text.toCharArray()) {
      if (!Character.isWhitespace(ch)) {
        normalizedText.add(String.valueOf(ch));
        collapsableWhitespace = false;
      }else if (!collapsableWhitespace) {
        normalizedText.add(" ");
        collapsableWhitespace = true;
      }
    }

    if (normalizedText.size() > 0) {
      String prefixFirst = prefixObj.first();
      String resText = String.join("", normalizedText);
      if (getContent() == null || getContent().isEmpty()) {
        resText = prefixFirst + resText;
      }
      resText = StringEscapeUtils.unescapeCsv(resText);
      resText = StringEscapeUtils.unescapeHtml(resText);
      addContent(resText);
      index += resText.length();
    }
  }

  void mergePreText(String text) {
    String ntext = text.replace("\n", "\n"+ prefixObj.rest());
    String prefixFirst = prefixObj.first();
    String resText = prefixFirst + ntext;
    resText = StringEscapeUtils.unescapeCsv(resText);
    resText = StringEscapeUtils.unescapeHtml(resText);
    addContent(resText);
    index += resText.length();
    collapsableWhitespace = false;
  }


  public Long getIndex() {
    return index;
  }

  public void setIndex(Long index) {
    this.index = index;
  }

  public boolean isCollapsableWhitespace() {
    return collapsableWhitespace;
  }

  public void setCollapsableWhitespace(boolean collapsableWhitespace) {
    this.collapsableWhitespace = collapsableWhitespace;
  }

  Prefix getPrefixObj() {
    return prefixObj;
  }
}
