package us.myles.ViaVersion.protocols.protocol1_13_2to1_13_1.types;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.Particle;

public class Particle1_13_2Type extends Type<Particle> {
  public Particle1_13_2Type() {
    super("Particle", Particle.class);
  }
  
  public void write(ByteBuf buffer, Particle object) throws Exception {
    Type.VAR_INT.writePrimitive(buffer, object.getId());
    for (Particle.ParticleData data : object.getArguments())
      data.getType().write(buffer, data.getValue()); 
  }
  
  public Particle read(ByteBuf buffer) throws Exception {
    int type = Type.VAR_INT.readPrimitive(buffer);
    Particle particle = new Particle(type);
    switch (type) {
      case 3:
      case 20:
        particle.getArguments().add(new Particle.ParticleData((Type)Type.VAR_INT, Integer.valueOf(Type.VAR_INT.readPrimitive(buffer))));
        break;
      case 11:
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buffer))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buffer))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buffer))));
        particle.getArguments().add(new Particle.ParticleData((Type)Type.FLOAT, Float.valueOf(Type.FLOAT.readPrimitive(buffer))));
        break;
      case 27:
        particle.getArguments().add(new Particle.ParticleData(Type.FLAT_VAR_INT_ITEM, Type.FLAT_VAR_INT_ITEM.read(buffer)));
        break;
    } 
    return particle;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13_2to1_13_1\types\Particle1_13_2Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */