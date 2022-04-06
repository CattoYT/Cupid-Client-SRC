package us.myles.ViaVersion.api.boss;

public enum BossColor {
  PINK(0),
  BLUE(1),
  RED(2),
  GREEN(3),
  YELLOW(4),
  PURPLE(5),
  WHITE(6);
  
  private final int id;
  
  BossColor(int id) {
    this.id = id;
  }
  
  public int getId() {
    return this.id;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\boss\BossColor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */