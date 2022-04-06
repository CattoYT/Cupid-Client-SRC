package us.myles.ViaVersion.api.minecraft;

public enum Environment {
  NORMAL(0),
  NETHER(-1),
  END(1);
  
  private final int id;
  
  Environment(int id) {
    this.id = id;
  }
  
  public int getId() {
    return this.id;
  }
  
  public static Environment getEnvironmentById(int id) {
    switch (id) {
      default:
        return NETHER;
      case 0:
        return NORMAL;
      case 1:
        break;
    } 
    return END;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\minecraft\Environment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */