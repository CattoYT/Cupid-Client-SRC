package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.blockconnections;

public class PumpkinConnectionHandler extends AbstractStempConnectionHandler {
  static ConnectionData.ConnectorInitAction init() {
    return (new PumpkinConnectionHandler("minecraft:pumpkin_stem[age=7]")).getInitAction("minecraft:carved_pumpkin", "minecraft:attached_pumpkin_stem");
  }
  
  public PumpkinConnectionHandler(String baseStateId) {
    super(baseStateId);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\blockconnections\PumpkinConnectionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */