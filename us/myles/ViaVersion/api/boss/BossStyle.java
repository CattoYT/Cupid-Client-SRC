package us.myles.ViaVersion.api.boss;

public enum BossStyle {
  SOLID(0),
  SEGMENTED_6(1),
  SEGMENTED_10(2),
  SEGMENTED_12(3),
  SEGMENTED_20(4);
  
  private final int id;
  
  BossStyle(int id) {
    this.id = id;
  }
  
  public int getId() {
    return this.id;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\boss\BossStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */