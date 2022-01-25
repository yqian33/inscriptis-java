package ch.x28.inscriptis;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class Block extends Line {

  private Long index;

  private boolean collapsableWhitespace = true;

  private Prefix prefixObj;

  public Block(Long index, Prefix prefix) {
    this.setIndex(index);
    this.prefixObj = prefix;
  }

  public Prefix getPrefixObj() {
    return prefixObj;
  }

  public Block newBlock() {
    this.prefixObj.setConsumed(false);
    return new Block(this.getIndex()+1, this.prefixObj);
  }

  @Override
  public String getText() {
    if (!collapsableWhitespace) {
      return getContent();
    }
    if (getContent().endsWith(" ")) {
      String content = getContent().substring(0, getContent().length()-1);
      setContent(content);
      setIndex(getIndex()-1);
    }
    return getContent();
  }

  public void merge(String text, HtmlProperties.WhiteSpace whiteSpace) {
    if ( whiteSpace == HtmlProperties.WhiteSpace.PRE) {
      mergePreText(text);
    } else {
      mergeNormalText(text);
    }
  }

  public void mergeNormalText(String text) {
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
      String resText = String.join("", normalizedText);
      if (getContent() == null || getContent().isEmpty()) {
        resText = prefixObj.first() + resText;
      }
      // TODO:  unescape text
      resText = StringEscapeUtils.unescapeCsv(resText);
      resText = StringEscapeUtils.unescapeHtml(resText);
      addContent(resText);
      index += resText.length();
    }
  }

  public void mergePreText(String text) {
    String ntext = text.replace("\n", "\n"+ prefixObj.rest());
    String resText = prefixObj.first() + ntext;
    // TODO:  unescape text
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

}
