package us.myles.ViaVersion.protocols.protocol1_9to1_8.packets;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.entities.Entity1_10Types;
import us.myles.ViaVersion.api.entities.EntityType;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.ServerboundPacketType;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.remapper.ValueCreator;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_8.ClientboundPackets1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ItemRewriter;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.PlayerMovementMapper;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.chat.ChatRewriter;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.chat.GameMode;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.CommandBlockProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.MainHandProvider;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.ClientChunks;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import us.myles.viaversion.libs.gson.JsonObject;

public class PlayerPackets {
  public static void register(Protocol1_9To1_8 protocol) {
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    try {
                      JsonObject obj = (JsonObject)wrapper.get(Type.COMPONENT, 0);
                      ChatRewriter.toClient(obj, wrapper.user());
                    } catch (Exception e) {
                      e.printStackTrace();
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.TAB_LIST, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.DISCONNECT, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.TITLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (action == 0 || action == 1)
                      Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING)); 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.PLAYER_POSITION, new PacketRemapper() {
          public void registerMap() {
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map(Type.BYTE);
            create(new ValueCreator() {
                  public void write(PacketWrapper wrapper) {
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.TEAMS, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map(Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    byte mode = ((Byte)wrapper.get(Type.BYTE, 0)).byteValue();
                    if (mode == 0 || mode == 2) {
                      wrapper.passthrough(Type.STRING);
                      wrapper.passthrough(Type.STRING);
                      wrapper.passthrough(Type.STRING);
                      wrapper.passthrough(Type.BYTE);
                      wrapper.passthrough(Type.STRING);
                      wrapper.write(Type.STRING, Via.getConfig().isPreventCollision() ? "never" : "");
                      wrapper.passthrough(Type.BYTE);
                    } 
                    if (mode == 0 || mode == 3 || mode == 4) {
                      String[] players = (String[])wrapper.passthrough(Type.STRING_ARRAY);
                      EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                      String myName = wrapper.user().getProtocolInfo().getUsername();
                      String teamName = (String)wrapper.get(Type.STRING, 0);
                      for (String player : players) {
                        if (entityTracker.isAutoTeam() && player.equalsIgnoreCase(myName))
                          if (mode == 4) {
                            wrapper.send(Protocol1_9To1_8.class, true, true);
                            wrapper.cancel();
                            entityTracker.sendTeamPacket(true, true);
                            entityTracker.setCurrentTeam("viaversion");
                          } else {
                            entityTracker.sendTeamPacket(false, true);
                            entityTracker.setCurrentTeam(teamName);
                          }  
                      } 
                    } 
                    if (mode == 1) {
                      EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                      String teamName = (String)wrapper.get(Type.STRING, 0);
                      if (entityTracker.isAutoTeam() && teamName
                        .equals(entityTracker.getCurrentTeam())) {
                        wrapper.send(Protocol1_9To1_8.class, true, true);
                        wrapper.cancel();
                        entityTracker.sendTeamPacket(true, true);
                        entityTracker.setCurrentTeam("viaversion");
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get(Type.INT, 0)).intValue();
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.addEntity(entityId, (EntityType)Entity1_10Types.EntityType.PLAYER);
                    tracker.setClientEntityId(entityId);
                  }
                });
            map(Type.UNSIGNED_BYTE);
            map(Type.BYTE);
            map(Type.UNSIGNED_BYTE);
            map(Type.UNSIGNED_BYTE);
            map(Type.STRING);
            map(Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    tracker.setGameMode(GameMode.getById(((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue()));
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
                    provider.sendPermission(wrapper.user());
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                    if (Via.getConfig().isAutoTeam()) {
                      entityTracker.setAutoTeam(true);
                      wrapper.send(Protocol1_9To1_8.class, true, true);
                      wrapper.cancel();
                      entityTracker.sendTeamPacket(true, true);
                      entityTracker.setCurrentTeam("viaversion");
                    } else {
                      entityTracker.setAutoTeam(false);
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.PLAYER_INFO, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int count = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    for (int i = 0; i < count; i++) {
                      wrapper.passthrough(Type.UUID);
                      if (action == 0) {
                        wrapper.passthrough(Type.STRING);
                        int properties = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                        for (int j = 0; j < properties; j++) {
                          wrapper.passthrough(Type.STRING);
                          wrapper.passthrough(Type.STRING);
                          boolean isSigned = ((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue();
                          if (isSigned)
                            wrapper.passthrough(Type.STRING); 
                        } 
                        wrapper.passthrough((Type)Type.VAR_INT);
                        wrapper.passthrough((Type)Type.VAR_INT);
                        boolean hasDisplayName = ((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue();
                        if (hasDisplayName)
                          Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING)); 
                      } else if (action == 1 || action == 2) {
                        wrapper.passthrough((Type)Type.VAR_INT);
                      } else if (action == 3) {
                        boolean hasDisplayName = ((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue();
                        if (hasDisplayName)
                          Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING)); 
                      } else if (action == 4) {
                      
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String name = (String)wrapper.get(Type.STRING, 0);
                    if (name.equalsIgnoreCase("MC|BOpen")) {
                      wrapper.read(Type.REMAINING_BYTES);
                      wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                    } 
                    if (name.equalsIgnoreCase("MC|TrList")) {
                      wrapper.passthrough(Type.INT);
                      Short size = (Short)wrapper.passthrough(Type.UNSIGNED_BYTE);
                      for (int i = 0; i < size.shortValue(); i++) {
                        Item item1 = (Item)wrapper.passthrough(Type.ITEM);
                        ItemRewriter.toClient(item1);
                        Item item2 = (Item)wrapper.passthrough(Type.ITEM);
                        ItemRewriter.toClient(item2);
                        boolean present = ((Boolean)wrapper.passthrough(Type.BOOLEAN)).booleanValue();
                        if (present) {
                          Item item3 = (Item)wrapper.passthrough(Type.ITEM);
                          ItemRewriter.toClient(item3);
                        } 
                        wrapper.passthrough(Type.BOOLEAN);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.UPDATE_HEALTH, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.FLOAT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    float health = ((Float)wrapper.get((Type)Type.FLOAT, 0)).floatValue();
                    if (health <= 0.0F) {
                      ClientChunks cc = (ClientChunks)wrapper.user().get(ClientChunks.class);
                      cc.getBulkChunks().clear();
                      cc.getLoadedChunks().clear();
                    } 
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map(Type.INT);
            map(Type.UNSIGNED_BYTE);
            map(Type.UNSIGNED_BYTE);
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientChunks cc = (ClientChunks)wrapper.user().get(ClientChunks.class);
                    cc.getBulkChunks().clear();
                    cc.getLoadedChunks().clear();
                    int gamemode = ((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue();
                    ((EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class)).setGameMode(GameMode.getById(gamemode));
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
                    provider.sendPermission(wrapper.user());
                    provider.unloadChunks(wrapper.user());
                  }
                });
          }
        });
    protocol.registerOutgoing((ClientboundPacketType)ClientboundPackets1_8.GAME_EVENT, new PacketRemapper() {
          public void registerMap() {
            map(Type.UNSIGNED_BYTE);
            map((Type)Type.FLOAT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).shortValue() == 3) {
                      int gamemode = ((Float)wrapper.get((Type)Type.FLOAT, 0)).intValue();
                      ((EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class)).setGameMode(GameMode.getById(gamemode));
                    } 
                  }
                });
          }
        });
    protocol.cancelOutgoing((ClientboundPacketType)ClientboundPackets1_8.SET_COMPRESSION);
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.TAB_COMPLETE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map(Type.BOOLEAN, Type.NOTHING);
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.CLIENT_SETTINGS, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map(Type.BYTE);
            map((Type)Type.VAR_INT, Type.BYTE);
            map(Type.BOOLEAN);
            map(Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    if (Via.getConfig().isLeftHandedHandling())
                      if (hand == 0)
                        wrapper.set(Type.UNSIGNED_BYTE, 0, 
                            Short.valueOf((short)(((Short)wrapper.get(Type.UNSIGNED_BYTE, 0)).intValue() | 0x80)));  
                    wrapper.sendToServer(Protocol1_9To1_8.class, true, true);
                    wrapper.cancel();
                    ((MainHandProvider)Via.getManager().getProviders().get(MainHandProvider.class)).setMainHand(wrapper.user(), hand);
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.ANIMATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT, Type.NOTHING);
          }
        });
    protocol.cancelIncoming((ServerboundPacketType)ServerboundPackets1_9.TELEPORT_CONFIRM);
    protocol.cancelIncoming((ServerboundPacketType)ServerboundPackets1_9.VEHICLE_MOVE);
    protocol.cancelIncoming((ServerboundPacketType)ServerboundPackets1_9.STEER_BOAT);
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.PLUGIN_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String name = (String)wrapper.get(Type.STRING, 0);
                    if (name.equalsIgnoreCase("MC|BSign")) {
                      Item item = (Item)wrapper.passthrough(Type.ITEM);
                      if (item != null) {
                        item.setIdentifier(387);
                        ItemRewriter.rewriteBookToServer(item);
                      } 
                    } 
                    if (name.equalsIgnoreCase("MC|AutoCmd")) {
                      wrapper.set(Type.STRING, 0, "MC|AdvCdm");
                      wrapper.write(Type.BYTE, Byte.valueOf((byte)0));
                      wrapper.passthrough(Type.INT);
                      wrapper.passthrough(Type.INT);
                      wrapper.passthrough(Type.INT);
                      wrapper.passthrough(Type.STRING);
                      wrapper.passthrough(Type.BOOLEAN);
                      wrapper.clearInputBuffer();
                    } 
                    if (name.equalsIgnoreCase("MC|AdvCmd"))
                      wrapper.set(Type.STRING, 0, "MC|AdvCdm"); 
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.CLIENT_STATUS, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (action == 2) {
                      EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().get(EntityTracker1_9.class);
                      if (tracker.isBlocking()) {
                        tracker.setSecondHand(null);
                        tracker.setBlocking(false);
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.PLAYER_POSITION, new PacketRemapper() {
          public void registerMap() {
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.BOOLEAN);
            handler((PacketHandler)new PlayerMovementMapper());
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.PLAYER_POSITION_AND_ROTATION, new PacketRemapper() {
          public void registerMap() {
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map(Type.DOUBLE);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map(Type.BOOLEAN);
            handler((PacketHandler)new PlayerMovementMapper());
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.PLAYER_ROTATION, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT);
            map(Type.BOOLEAN);
            handler((PacketHandler)new PlayerMovementMapper());
          }
        });
    protocol.registerIncoming((ServerboundPacketType)ServerboundPackets1_9.PLAYER_MOVEMENT, new PacketRemapper() {
          public void registerMap() {
            map(Type.BOOLEAN);
            handler((PacketHandler)new PlayerMovementMapper());
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\packets\PlayerPackets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */