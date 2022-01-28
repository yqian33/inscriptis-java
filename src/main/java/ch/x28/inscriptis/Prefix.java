package ch.x28.inscriptis;

import java.util.List;
import java.util.ArrayList;

public class Prefix {

  private int currentPadding = 0;
  private List<Integer> paddings = new ArrayList<>();
  private List<String> bullets = new ArrayList<>();
  private boolean consumed = false;

  public void registerPrefix(int paddingInline, String bullet) {
    currentPadding += paddingInline;
    paddings.add(paddingInline);
    if (bullet != null) {
      bullets.add(bullet);
    } else {
      bullets.add("");
    }
  }

  private String popNextBullet() {
    Integer nextBulletIdx = null;

    for (int i = bullets.size()-1; i >=0; i--) {
      if (!StringUtils.isEmpty(bullets.get(i))) {
        nextBulletIdx = i;
        break;
      }
    }

    if (nextBulletIdx == null) {
      return "";
    }

    String bullet = bullets.get(nextBulletIdx);
    bullets.set(nextBulletIdx, "");
    return bullet;
  }

  public String getUnconsumedBullet() {
    if (consumed) {
      return "";
    }
    String bullet = popNextBullet();
    if (bullet == null || bullet.isEmpty()) {
      return "";
    }
    int padding = currentPadding - paddings.get(paddings.size()-1);
    String res = StringUtils.repeat(" ", padding - bullet.length());
    return res + bullet;
  }

  public void removeLastPrefix() {
    int paddingSize = paddings.size();
    if (paddingSize > 0) {
      int p = paddings.get(paddingSize-1);
      currentPadding -= p;
      paddings.remove(paddingSize-1);
    }
    if (bullets.size() > 0) {
      bullets.remove(bullets.size()-1);
    }
  }

  public String first() {
    if (consumed) {
      return "";
    }
    consumed = true;
    String bullet = popNextBullet();
    String res = StringUtils.repeat(" ", currentPadding - bullet.length());
    return res + bullet;
  }

  public String rest() {
    return StringUtils.repeat(" ", currentPadding);
  }

  public int getCurrentPadding() {
    return currentPadding;
  }

  public void setCurrentPadding(int current_padding) {
    this.currentPadding = current_padding;
  }

  public List<Integer> getPaddings() {
    return paddings;
  }

  public void setPaddings(List<Integer> paddings) {
    this.paddings = paddings;
  }

  public List<String> getBullets() {
    return bullets;
  }

  public void setBullets(List<String> bullets) {
    this.bullets = bullets;
  }

  public boolean isConsumed() {
    return consumed;
  }

  public void setConsumed(boolean consumed) {
    this.consumed = consumed;
  }
}
