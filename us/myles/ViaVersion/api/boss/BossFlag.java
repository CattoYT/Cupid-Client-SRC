package us.myles.ViaVersion.api.boss;

public enum BossFlag {
  DARKEN_SKY(1),
  PLAY_BOSS_MUSIC(2);
  
  private final int id;
  
  BossFlag(int id) {
    this.id = id;
  }
  
  public int getId() {
    return this.id;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\boss\BossFlag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */