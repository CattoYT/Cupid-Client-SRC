package us.myles.ViaVersion.api.minecraft;

public interface BlockChangeRecord {
  byte getSectionX();
  
  byte getSectionY();
  
  byte getSectionZ();
  
  short getY(int paramInt);
  
  @Deprecated
  default short getY() {
    return getY(-1);
  }
  
  int getBlockId();
  
  void setBlockId(int paramInt);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\minecraft\BlockChangeRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */