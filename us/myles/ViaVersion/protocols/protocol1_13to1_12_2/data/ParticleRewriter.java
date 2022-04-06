package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.Particle;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.packets.InventoryPackets;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.packets.WorldPackets;

public class ParticleRewriter {
  private static final List<NewParticle> particles = new ArrayList<>();
  
  static {
    add(34);
    add(19);
    add(18);
    add(21);
    add(4);
    add(43);
    add(22);
    add(42);
    add(42);
    add(6);
    add(14);
    add(37);
    add(30);
    add(12);
    add(26);
    add(17);
    add(0);
    add(44);
    add(10);
    add(9);
    add(1);
    add(24);
    add(32);
    add(33);
    add(35);
    add(15);
    add(23);
    add(31);
    add(-1);
    add(5);
    add(11, reddustHandler());
    add(29);
    add(34);
    add(28);
    add(25);
    add(2);
    add(27, iconcrackHandler());
    add(3, blockHandler());
    add(3, blockHandler());
    add(36);
    add(-1);
    add(13);
    add(8);
    add(16);
    add(7);
    add(40);
    add(20, blockHandler());
    add(41);
    add(38);
  }
  
  public static Particle rewriteParticle(int particleId, Integer[] data) {
    if (particleId >= particles.size()) {
      Via.getPlatform().getLogger().severe("Failed to transform particles with id " + particleId + " and data " + Arrays.toString((Object[])data));
      return null;
    } 
    NewParticle rewrite = particles.get(particleId);
    return rewrite.handle(new Particle(rewrite.getId()), data);
  }
  
  private static void add(int newId) {
    particles.add(new NewParticle(newId, null));
  }
  
  private static void add(int newId, ParticleDataHandler dataHandler) {
    particles.add(new NewParticle(newId, dataHandler));
  }
  
  private static ParticleDataHandler reddustHandler() {
    return new ParticleDataHandler() {
        public Particle handler(Particle particle, Integer[] data) {
          particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(ParticleRewriter.randomBool() ? 1.0F : 0.0F)));
          particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(0.0F)));
          particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(ParticleRewriter.randomBool() ? 1.0F : 0.0F)));
          particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(1.0F)));
          return particle;
        }
      };
  }
  
  private static boolean randomBool() {
    return ThreadLocalRandom.current().nextBoolean();
  }
  
  private static ParticleDataHandler iconcrackHandler() {
    return new ParticleDataHandler() {
        public Particle handler(Particle particle, Integer[] data) {
          Item item;
          if (data.length == 1) {
            item = new Item(data[0].intValue(), (byte)1, (short)0, null);
          } else if (data.length == 2) {
            item = new Item(data[0].intValue(), (byte)1, data[1].shortValue(), null);
          } else {
            return particle;
          } 
          InventoryPackets.toClient(item);
          particle.getArguments().add(new Particle.ParticleData(Type.FLAT_ITEM, item));
          return particle;
        }
      };
  }
  
  private static ParticleDataHandler blockHandler() {
    return new ParticleDataHandler() {
        public Particle handler(Particle particle, Integer[] data) {
          int value = data[0].intValue();
          int combined = (value & 0xFFF) << 4 | value >> 12 & 0xF;
          int newId = WorldPackets.toNewId(combined);
          particle.getArguments().add(new Particle.ParticleData((Type)Type.VAR_INT, Integer.valueOf(newId)));
          return particle;
        }
      };
  }
  
  static interface ParticleDataHandler {
    Particle handler(Particle param1Particle, Integer[] param1ArrayOfInteger);
  }
  
  private static class NewParticle {
    private final int id;
    
    private final ParticleRewriter.ParticleDataHandler handler;
    
    public NewParticle(int id, ParticleRewriter.ParticleDataHandler handler) {
      this.id = id;
      this.handler = handler;
    }
    
    public Particle handle(Particle particle, Integer[] data) {
      if (this.handler != null)
        return this.handler.handler(particle, data); 
      return particle;
    }
    
    public int getId() {
      return this.id;
    }
    
    public ParticleRewriter.ParticleDataHandler getHandler() {
      return this.handler;
    }
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\data\ParticleRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */