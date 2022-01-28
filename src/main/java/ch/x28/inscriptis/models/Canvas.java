package ch.x28.inscriptis.models;

import ch.x28.inscriptis.HtmlElement;
import ch.x28.inscriptis.HtmlProperties;
import ch.x28.inscriptis.Prefix;
import ch.x28.inscriptis.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Canvas {

  public static class Annotation {
    public Long start;
    public Long end;
    public String metadata;

    public Annotation(Long start, Long end, String metadata) {
      this.start = start;
      this.end = end;
      this.metadata = metadata;
    }

    @Override
    public String toString() {
      return "Annotation[start=" + start +
          ", end=" + end +
          ", metadata=(" + metadata + ")]";
    }
  }

  private int margin = 1000; // margin to the previous block
  public Block currentBlock = new Block(0L, new Prefix());
  public List<String> blocks = new ArrayList<>();
  private List<Annotation> annotations = new ArrayList<>();
  private Map<HtmlElement, Long> openAnnotations = new HashMap<>();

  List<String> addBlock(String content) {
    // System.out.println("Add block: " + content + ":" + this.blocks);
    this.blocks.add(content);
    // System.out.println("Added block " + this.blocks);
    return blocks;
  }

  @Override
  public String toString() {
    return "Canvas[margin=" + margin +
        ", currentBlock=" + currentBlock +
        ", annotations=(" + annotations + ")]";
  }

  public String getText() {
    flushInline();
    return String.join("\n", blocks);
  }

  public void openTag(HtmlElement tag) {

    if (tag.getAnnotation() != null && tag.getAnnotation().size() != 0) {
      this.openAnnotations.put(tag, currentBlock.getIndex());
    }

    if (tag.getDisplay() == HtmlProperties.Display.BLOCK) {
      openBlock(tag);
    }
  }

  public void openBlock(HtmlElement tag) {

    if (!flushInline() && tag.getListBullet() != null && !tag.getListBullet().isEmpty()) {
      writeUnconsumedBullet();
    }

    currentBlock.getPrefixObj().registerPrefix(tag.getPadding(), tag.getListBullet());

    int requiredMargin = Math.max(tag.getPreviousMarginAfter(), tag.getMarginBefore());
    if (requiredMargin > margin) {
      int requiredNewlines = requiredMargin - margin;
      currentBlock.setIndex(currentBlock.getIndex() + requiredNewlines);
      String newLines = StringUtils.repeat("\n", requiredNewlines-1);
      addBlock(newLines);
      margin = requiredMargin;
    }
  }


  public void closeTag(HtmlElement tag) {
    if (tag.getDisplay() == HtmlProperties.Display.BLOCK) {
      if (!flushInline() && tag.getListBullet()!= null) {
        writeUnconsumedBullet();
      }
      currentBlock.getPrefixObj().removeLastPrefix();
      closeBlock(tag);
    }

    if (openAnnotations.containsKey(tag)) {
      Long start_idx = openAnnotations.get(tag);
      openAnnotations.remove(tag);
      if (start_idx.equals(currentBlock.getIndex())) {
        return;
      }
      for(String annotation: tag.getAnnotation()) {
        annotations.add(new Annotation(start_idx, currentBlock.getIndex(), annotation));
      }
    }
  }

  public void closeBlock(HtmlElement tag) {
    if (tag.getMarginAfter() > margin) {
      int requiredNewlines = tag.getMarginAfter() - margin;
      currentBlock.setIndex(currentBlock.getIndex() + requiredNewlines);
      addBlock(StringUtils.repeat("\n", requiredNewlines-1));
      margin = tag.getMarginAfter();
    }
  }

  public boolean flushInline() {
    String currentContent = currentBlock.getText();
    if (!StringUtils.isEmpty(currentContent)) {
      addBlock(currentContent);
      currentBlock = currentBlock.newBlock();
      margin = 0;
      return true;
    }
    return false;
  }

  public void writeUnconsumedBullet() {
    String bullet = currentBlock.getPrefixObj().getUnconsumedBullet();
    if (bullet == null || bullet.isEmpty() ) {
      return;
    }
    addBlock(bullet);
    currentBlock.setIndex(currentBlock.getIndex() + bullet.length());
    currentBlock = currentBlock.newBlock();
    margin = 0;
  }

  public void write(HtmlElement tag, String text, HtmlProperties.WhiteSpace whiteSpace) {
    if (whiteSpace != null) {
      currentBlock.merge(text, whiteSpace);
    } else {
      currentBlock.merge(text, tag.getWhitespace());
    }
  }

  public void writeNewLine() {
    if (!flushInline()) {
      addBlock("");
      currentBlock = currentBlock.newBlock();
    }
  }

  public int getLeftMargin() {
    return currentBlock.getPrefixObj().getCurrentPadding();
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public List<String> getBlocks() {
    return blocks;
  }

  public void setBlocks(List<String> blocks) {
    this.blocks = blocks;
  }
}
