package us.myles.ViaVersion.protocols.protocol1_14to1_13_2.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.Position;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.ListTag;
import us.myles.viaversion.libs.opennbt.tag.builtin.Tag;

public class PlayerPackets {
  public static void register(Protocol protocol) {
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_13.OPEN_SIGN_EDITOR, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION, Type.POSITION1_14);
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_14.QUERY_BLOCK_NBT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.POSITION1_14, Type.POSITION);
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_14.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item item = (Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM);
                    InventoryPackets.toServer(item);
                    if (Via.getConfig().isTruncate1_14Books()) {
                      if (item == null)
                        return; 
                      CompoundTag tag = item.getTag();
                      if (tag == null)
                        return; 
                      Tag pages = tag.get("pages");
                      if (!(pages instanceof ListTag))
                        return; 
                      ListTag listTag = (ListTag)pages;
                      if (listTag.size() <= 50)
                        return; 
                      listTag.setValue(listTag.getValue().subList(0, 50));
                    } 
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_14.PLAYER_DIGGING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.POSITION1_14, Type.POSITION);
            map(Type.BYTE);
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_14.RECIPE_BOOK_DATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (type == 0) {
                      wrapper.passthrough(Type.STRING);
                    } else if (type == 1) {
                      wrapper.passthrough(Type.BOOLEAN);
                      wrapper.passthrough(Type.BOOLEAN);
                      wrapper.passthrough(Type.BOOLEAN);
                      wrapper.passthrough(Type.BOOLEAN);
                      wrapper.read(Type.BOOLEAN);
                      wrapper.read(Type.BOOLEAN);
                      wrapper.read(Type.BOOLEAN);
                      wrapper.read(Type.BOOLEAN);
                    } 
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_14.UPDATE_COMMAND_BLOCK, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14, Type.POSITION);
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_14.UPDATE_STRUCTURE_BLOCK, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14, Type.POSITION);
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_14.UPDATE_SIGN, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION1_14, Type.POSITION);
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_14.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    Position position = (Position)wrapper.read(Type.POSITION1_14);
                    int face = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    float x = ((Float)wrapper.read((Type)Type.FLOAT)).floatValue();
                    float y = ((Float)wrapper.read((Type)Type.FLOAT)).floatValue();
                    float z = ((Float)wrapper.read((Type)Type.FLOAT)).floatValue();
                    wrapper.read(Type.BOOLEAN);
                    wrapper.write(Type.POSITION, position);
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(face));
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(hand));
                    wrapper.write((Type)Type.FLOAT, Float.valueOf(x));
                    wrapper.write((Type)Type.FLOAT, Float.valueOf(y));
                    wrapper.write((Type)Type.FLOAT, Float.valueOf(z));
                  }
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14to1_13_2\packets\PlayerPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */