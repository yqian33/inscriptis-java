package ch.x28.inscriptis.models;

import ch.x28.inscriptis.HtmlProperties;
import ch.x28.inscriptis.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TableCellCanvas extends Canvas {

  private HtmlProperties.HorizontalAlignment align;
  private HtmlProperties.VerticalAlignment valign;
  private Integer width;
  private List<Integer> lineWidth;
  private Integer verticalPadding;

  public TableCellCanvas(HtmlProperties.HorizontalAlignment align,
                         HtmlProperties.VerticalAlignment valign) {
    super();
    this.align = align;
    this.valign = valign;
    this.verticalPadding = 0;
  }

  public Integer getHeight() {
    return Math.max(1, this.blocks.size());
  }


  public Integer getWidth() {
    if (this.width != null && this.width > 0) {
      return this.width;
    }
    List<Integer> linesSize = getLines(blocks).stream().map(String::length).collect(Collectors.toList());
    return Collections.max(linesSize);
  }

  private List<String> getLines(List<String> blocks) {
    List<String> result = new ArrayList<>();
    for (String str: blocks) {
      List<String> splitted = Arrays.asList(str.split("\n"));
      result.addAll(splitted);
    }
    return result;
  }

  public Integer getNormalizedBlocks() {
    flushInline();
    this.blocks = getLines(blocks);
    if (this.blocks == null || this.blocks.isEmpty()) {
      this.blocks = Arrays.asList("");
    }
    return this.blocks.size();
  }

  public void setHeight(Integer height) {
    int rows = this.blocks.size();
    if (rows < height) {
      if (this.valign == HtmlProperties.VerticalAlignment.BOTTOM) {
        this.verticalPadding = height - rows;
        List<String> res = StringUtils.repeatAsList("", verticalPadding);
        res.addAll(blocks);
        blocks = res;
      } else if (valign == HtmlProperties.VerticalAlignment.MIDDLE) {
        verticalPadding = (height - rows) / 2;

        List<String> pre = StringUtils.repeatAsList("", verticalPadding);
        List<String> post = StringUtils.repeatAsList("", (height - rows + 1)/2);
        pre.addAll(blocks);
        pre.addAll(post);
        blocks = pre;

      } else {
        List<String> res = StringUtils.repeatAsList("", height - rows);
        blocks.addAll(res);
      }
    }
  }

  public void setWidth(int width) {
    this.lineWidth = this.blocks.stream().map(String::length).collect(Collectors.toList());
    this.width = width;
    List<String> updated = new ArrayList<>();
    for (String str: blocks) {
      String padded = "";
      if (align == HtmlProperties.HorizontalAlignment.RIGHT) {
        padded = StringUtils.padLeft(str, width);
      } else if (align == HtmlProperties.HorizontalAlignment.LEFT) {
        padded = StringUtils.padRight(str, width);
      } else {
        padded = StringUtils.padCenter(str, width);
      }
      updated.add(padded);

    }
    blocks = updated;
  }

  public List<Annotation> getCellAnnotations(int index, int rowWidth) {
    currentBlock.setIndex((long) index);
    if (getAnnotations().isEmpty()) {
      return new ArrayList<>();
    }
    // the easy case - the cell has only one line :)
    if (blocks.size() == 1) {
      List<Annotation> annotations =
          horizontalShift(getAnnotations(), lineWidth.get(0), width, align, index);
      lineWidth.set(0, width);
      return annotations;
    }

    // the more challenging one - multiple cell lines
    List<Integer> lineBreakPos = StringUtils.accumulate(lineWidth);
    List<List<Annotation>> annotationLines = generateList(getBlocks());

    // # assign annotations to the corresponding line
    for (Annotation annotation: getAnnotations()) {
      for (int i = 0; i < lineBreakPos.size(); i ++) {
        int lineBreak = lineBreakPos.get(i);
        if (annotation.start <= lineBreak + i) {
          annotationLines.get(i + verticalPadding).add(annotation);
          break;
        }
      }
    }

    // compute the annotation index based on its line and delta :)
    List<Annotation> result = new ArrayList<>();
    index += verticalPadding; // newlines introduced by the padding
    int len = Math.min(annotationLines.size(), lineWidth.size());
    for (int i = 0; i < len; i ++) {
      List<Annotation> a = horizontalShift(annotationLines.get(i), lineWidth.get(i), width, align, index);
      result.addAll(a);
      index += rowWidth - lineWidth.get(i);
    }
    lineWidth = StringUtils.repeatAsList( width, lineWidth.size());
    return result;
  }

  private static List<List<Annotation>> generateList(List<String> blocks) {
    List<List<Annotation>> result = new ArrayList<>();
    for (int i = 0; i < blocks.size(); i++) {
      result.add(new ArrayList<>());
    }
    return result;
  }

  private List<Annotation> horizontalShift(List<Annotation> annotations,
                                           int contentWidth,
                                           int lineWidth,
                                           HtmlProperties.HorizontalAlignment align,
                                           int shift) {
    int hAlign;
    if (align == HtmlProperties.HorizontalAlignment.LEFT) {
      hAlign = shift;
    } else if (align == HtmlProperties.HorizontalAlignment.RIGHT) {
      hAlign = shift + lineWidth - contentWidth;
    } else {
      hAlign = shift + (lineWidth - contentWidth)/2;
    }

    List<Annotation> shiftedAnnotations = new ArrayList<>();
    for (Annotation annotation: annotations) {
      shiftedAnnotations.add(new Annotation(
          annotation.start + hAlign,
          annotation.end + hAlign,
          annotation.metadata
          ));
    }
    return shiftedAnnotations;
  }
}
