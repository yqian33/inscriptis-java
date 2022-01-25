package ch.x28.inscriptis;

import java.util.List;
import java.util.ArrayList;

public class Prefix {

  public int currentPadding = 0;
  public List<Integer> paddings = new ArrayList();
  public List<String> bullets = new ArrayList();
  public boolean consumed = false;

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

  public void registerPrefix(int paddingInline, String bullet) {
    currentPadding += paddingInline;
    paddings.add(paddingInline);
    if (bullet != null) {
      bullets.add(bullet);
    }
    System.out.println("padding: " + currentPadding);
  }

  public String popNextBullet() {
    int next_bullet_idx = 1;
    for (int i = bullets.size()-1; i >=0; i--) {
      if (bullets.get(i) == null) {
        continue;
      }
      next_bullet_idx = bullets.size() - i;
    }
    next_bullet_idx -= 1;
    System.out.println("bullets: " + bullets + " " + next_bullet_idx);

    if (next_bullet_idx == 0) {
      return "";
    }
    String bullet = bullets.get(next_bullet_idx);
    bullets.set(next_bullet_idx, "");
    System.out.println("now bullets: " + bullets);
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

}
