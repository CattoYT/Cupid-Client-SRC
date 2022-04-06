package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data;

public class StatisticData {
  private final int categoryId;
  
  private final int newId;
  
  private final int value;
  
  public StatisticData(int categoryId, int newId, int value) {
    this.categoryId = categoryId;
    this.newId = newId;
    this.value = value;
  }
  
  public int getCategoryId() {
    return this.categoryId;
  }
  
  public int getNewId() {
    return this.newId;
  }
  
  public int getValue() {
    return this.value;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\data\StatisticData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */