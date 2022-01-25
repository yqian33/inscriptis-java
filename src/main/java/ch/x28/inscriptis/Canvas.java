package ch.x28.inscriptis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Canvas {

  public static class Annotation {
    Long start;
    Long end;
    String metadata;

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

  private Long margin = 1000L; // margin to the previous block
  public Line currentBlock = new Line(0L, new Prefix());
  public List<String> blocks = new ArrayList<>();
  private List<Annotation> annotations = new ArrayList<>();
  private Map<HtmlElement, Long> openAnnotations = new HashMap<>();

  public Long getMargin() {
    return margin;
  }

  public void setMargin(Long margin) {
    this.margin = margin;
  }

  public Line getCurrentBlock() {
    return currentBlock;
  }

  public void setCurrentBlock(Line currentBlock) {
    this.currentBlock = currentBlock;
  }

  public List<String> getBlocks() {
    return blocks;
  }

  public void setBlocks(List<String> blocks) {
    this.blocks = blocks;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
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
    if (!flushInline() && tag.getListBullet() != null) {
        writeUnconsumedBullet();
    }
    System.out.println("tag:" + tag);
    currentBlock.getPrefixObj().registerPrefix(tag.getPadding(), tag.getListBullet());

    int requiredMargin = Math.max(tag.getPreviousMarginAfter(), tag.getMarginBefore());
    if (requiredMargin > margin) {
      Long requiredNewlines = requiredMargin - margin;
      currentBlock.setIndex(currentBlock.getIndex() + requiredNewlines);
      blocks.add(StringUtils.generateStr("\n", requiredNewlines.intValue()-1));
      margin = (long) requiredMargin;
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
      Long requiredNewlines = tag.getMarginAfter() - margin;
      currentBlock.setIndex(currentBlock.getIndex() + requiredNewlines);
      blocks.add(StringUtils.generateStr("\n", requiredNewlines.intValue()-1));
      margin = (long) tag.getMarginAfter();
    }
  }

  public boolean flushInline() {
    System.out.println("current block: "+ currentBlock.getContentText());
    if (!StringUtils.isEmpty(currentBlock.getContentText())) {
      blocks.add(currentBlock.getContentText());
      currentBlock = currentBlock.newLine();
      margin = 0L;
      return true;
    }
    return false;
  }

  public void writeUnconsumedBullet() {
    String bullet = currentBlock.getPrefixObj().getUnconsumedBullet();
    if (bullet == null || bullet.isEmpty() ) {
      return;
    }
    blocks.add(bullet);
    currentBlock.setIndex(currentBlock.getIndex() + bullet.length());
    currentBlock = currentBlock.newLine();
    margin = 0L;
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
      blocks.add("");
      currentBlock = currentBlock.newLine();
    }
  }

  public int getLeftMargin() {
    return currentBlock.getPrefixObj().getCurrent_padding();
  }
}
