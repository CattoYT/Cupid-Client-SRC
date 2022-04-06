package us.myles.ViaVersion.api.protocol;

import com.google.common.base.Preconditions;

public class VersionRange {
  private final String baseVersion;
  
  private final int rangeFrom;
  
  private final int rangeTo;
  
  public VersionRange(String baseVersion, int rangeFrom, int rangeTo) {
    Preconditions.checkNotNull(baseVersion);
    Preconditions.checkArgument((rangeFrom >= 0));
    Preconditions.checkArgument((rangeTo > rangeFrom));
    this.baseVersion = baseVersion;
    this.rangeFrom = rangeFrom;
    this.rangeTo = rangeTo;
  }
  
  public String getBaseVersion() {
    return this.baseVersion;
  }
  
  public int getRangeFrom() {
    return this.rangeFrom;
  }
  
  public int getRangeTo() {
    return this.rangeTo;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\protocol\VersionRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */