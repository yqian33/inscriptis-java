package ch.x28.inscriptis;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class Block extends Line {

  private boolean collapsableWhitespace = true;

  private Prefix prefix;

  public Block(Long index, Prefix prefix) {
    this.setIndex(index);
    this.prefix = prefix;
  }


  public Prefix getPrefix() {
    return prefix;
  }

  public void setPrefix(Prefix prefix) {
    this.prefix = prefix;
  }

  public Block newBlock() {
    this.prefix.setConsumed(false);
    return new Block(this.getIndex()+1, this.prefix);
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
        // System.out.println("line obj:" + this.prefixObj);
        // System.out.println("prefix consumed:" + prefixObj.consumed);
        resText = prefix.first() + resText;
      }
      // TODO:  unescape text
      resText = StringEscapeUtils.unescapeCsv(resText);
      resText = StringEscapeUtils.unescapeHtml(resText);
      addContent(resText);
      Long newIndex = getIndex() + resText.length();
      setIndex(newIndex);
    }
  }

  public void mergePreText(String text) {
    String ntext = text.replace("\n", "\n"+ prefix.rest());
    String resText = prefix.first() + ntext;
    // TODO:  unescape text
    resText = StringEscapeUtils.unescapeCsv(resText);
    resText = StringEscapeUtils.unescapeHtml(resText);
    addContent(resText);
    Long newIndex = getIndex() + resText.length();
    setIndex(newIndex);
    collapsableWhitespace = false;
  }

}
