package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data;

import java.util.Optional;

public enum SoundSource {
  MASTER("master", 0),
  MUSIC("music", 1),
  RECORD("record", 2),
  WEATHER("weather", 3),
  BLOCK("block", 4),
  HOSTILE("hostile", 5),
  NEUTRAL("neutral", 6),
  PLAYER("player", 7),
  AMBIENT("ambient", 8),
  VOICE("voice", 9);
  
  private final String name;
  
  private final int id;
  
  SoundSource(String name, int id) {
    this.name = name;
    this.id = id;
  }
  
  public static Optional<SoundSource> findBySource(String source) {
    for (SoundSource item : values()) {
      if (item.name.equalsIgnoreCase(source))
        return Optional.of(item); 
    } 
    return Optional.empty();
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getId() {
    return this.id;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\data\SoundSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */