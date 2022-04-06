package paulscode.sound.libraries;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;

public class SourceLWJGLOpenAL extends Source {
  private ChannelLWJGLOpenAL channelOpenAL = (ChannelLWJGLOpenAL)this.channel;
  
  private IntBuffer myBuffer;
  
  private FloatBuffer listenerPosition;
  
  private FloatBuffer sourcePosition;
  
  private FloatBuffer sourceVelocity;
  
  public SourceLWJGLOpenAL(FloatBuffer listenerPosition, IntBuffer myBuffer, boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary) {
    super(priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
    if (this.codec != null)
      this.codec.reverseByteOrder(true); 
    this.listenerPosition = listenerPosition;
    this.myBuffer = myBuffer;
    this.libraryType = LibraryLWJGLOpenAL.class;
    this.pitch = 1.0F;
    resetALInformation();
  }
  
  public SourceLWJGLOpenAL(FloatBuffer listenerPosition, IntBuffer myBuffer, Source old, SoundBuffer soundBuffer) {
    super(old, soundBuffer);
    if (this.codec != null)
      this.codec.reverseByteOrder(true); 
    this.listenerPosition = listenerPosition;
    this.myBuffer = myBuffer;
    this.libraryType = LibraryLWJGLOpenAL.class;
    this.pitch = 1.0F;
    resetALInformation();
  }
  
  public SourceLWJGLOpenAL(FloatBuffer listenerPosition, AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll) {
    super(audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll);
    this.listenerPosition = listenerPosition;
    this.libraryType = LibraryLWJGLOpenAL.class;
    this.pitch = 1.0F;
    resetALInformation();
  }
  
  public void cleanup() {
    super.cleanup();
  }
  
  public void changeSource(FloatBuffer listenerPosition, IntBuffer myBuffer, boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, SoundBuffer soundBuffer, float x, float y, float z, int attModel, float distOrRoll, boolean temporary) {
    changeSource(priority, toStream, toLoop, sourcename, filenameURL, soundBuffer, x, y, z, attModel, distOrRoll, temporary);
    this.listenerPosition = listenerPosition;
    this.myBuffer = myBuffer;
    this.pitch = 1.0F;
    resetALInformation();
  }
  
  public boolean incrementSoundSequence() {
    if (!this.toStream) {
      errorMessage("Method 'incrementSoundSequence' may only be used for streaming sources.");
      return false;
    } 
    synchronized (this.soundSequenceLock) {
      if (this.soundSequenceQueue != null && this.soundSequenceQueue.size() > 0) {
        this.filenameURL = this.soundSequenceQueue.remove(0);
        if (this.codec != null)
          this.codec.cleanup(); 
        this.codec = SoundSystemConfig.getCodec(this.filenameURL.getFilename());
        if (this.codec != null) {
          this.codec.reverseByteOrder(true);
          if (this.codec.getAudioFormat() == null)
            this.codec.initialize(this.filenameURL.getURL()); 
          AudioFormat audioFormat = this.codec.getAudioFormat();
          if (audioFormat == null) {
            errorMessage("Audio Format null in method 'incrementSoundSequence'");
            return false;
          } 
          int soundFormat = 0;
          if (audioFormat.getChannels() == 1) {
            if (audioFormat.getSampleSizeInBits() == 8) {
              soundFormat = 4352;
            } else if (audioFormat.getSampleSizeInBits() == 16) {
              soundFormat = 4353;
            } else {
              errorMessage("Illegal sample size in method 'incrementSoundSequence'");
              return false;
            } 
          } else if (audioFormat.getChannels() == 2) {
            if (audioFormat.getSampleSizeInBits() == 8) {
              soundFormat = 4354;
            } else if (audioFormat.getSampleSizeInBits() == 16) {
              soundFormat = 4355;
            } else {
              errorMessage("Illegal sample size in method 'incrementSoundSequence'");
              return false;
            } 
          } else {
            errorMessage("Audio data neither mono nor stereo in method 'incrementSoundSequence'");
            return false;
          } 
          this.channelOpenAL.setFormat(soundFormat, (int)audioFormat.getSampleRate());
          this.preLoad = true;
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public void listenerMoved() {
    positionChanged();
  }
  
  public void setPosition(float x, float y, float z) {
    super.setPosition(x, y, z);
    if (this.sourcePosition == null) {
      resetALInformation();
    } else {
      positionChanged();
    } 
    this.sourcePosition.put(0, x);
    this.sourcePosition.put(1, y);
    this.sourcePosition.put(2, z);
    if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
      AL10.alSource(this.channelOpenAL.ALSource.get(0), 4100, this.sourcePosition);
      checkALError();
    } 
  }
  
  public void positionChanged() {
    calculateDistance();
    calculateGain();
    if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
      AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4106, this.gain * this.sourceVolume * Math.abs(this.fadeOutGain) * this.fadeInGain);
      checkALError();
    } 
    checkPitch();
  }
  
  private void checkPitch() {
    if (this.channel != null && this.channel.attachedSource == this && LibraryLWJGLOpenAL.alPitchSupported() && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
      AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4099, this.pitch);
      checkALError();
    } 
  }
  
  public void setLooping(boolean lp) {
    super.setLooping(lp);
    if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
      if (lp) {
        AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 1);
      } else {
        AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 0);
      } 
      checkALError();
    } 
  }
  
  public void setAttenuation(int model) {
    super.setAttenuation(model);
    if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
      if (model == 1) {
        AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, this.distOrRoll);
      } else {
        AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, 0.0F);
      } 
      checkALError();
    } 
  }
  
  public void setDistOrRoll(float dr) {
    super.setDistOrRoll(dr);
    if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
      if (this.attModel == 1) {
        AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, dr);
      } else {
        AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, 0.0F);
      } 
      checkALError();
    } 
  }
  
  public void setVelocity(float x, float y, float z) {
    super.setVelocity(x, y, z);
    this.sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { x, y, z });
    this.sourceVelocity.flip();
    if (this.channel != null && this.channel.attachedSource == this && this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
      AL10.alSource(this.channelOpenAL.ALSource.get(0), 4102, this.sourceVelocity);
      checkALError();
    } 
  }
  
  public void setPitch(float value) {
    super.setPitch(value);
    checkPitch();
  }
  
  public void play(Channel c) {
    if (!active()) {
      if (this.toLoop)
        this.toPlay = true; 
      return;
    } 
    if (c == null) {
      errorMessage("Unable to play source, because channel was null");
      return;
    } 
    boolean newChannel = (this.channel != c);
    if (this.channel != null && this.channel.attachedSource != this)
      newChannel = true; 
    boolean wasPaused = paused();
    super.play(c);
    this.channelOpenAL = (ChannelLWJGLOpenAL)this.channel;
    if (newChannel) {
      setPosition(this.position.x, this.position.y, this.position.z);
      checkPitch();
      if (this.channelOpenAL != null && this.channelOpenAL.ALSource != null) {
        if (LibraryLWJGLOpenAL.alPitchSupported()) {
          AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4099, this.pitch);
          checkALError();
        } 
        AL10.alSource(this.channelOpenAL.ALSource.get(0), 4100, this.sourcePosition);
        checkALError();
        AL10.alSource(this.channelOpenAL.ALSource.get(0), 4102, this.sourceVelocity);
        checkALError();
        if (this.attModel == 1) {
          AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, this.distOrRoll);
        } else {
          AL10.alSourcef(this.channelOpenAL.ALSource.get(0), 4129, 0.0F);
        } 
        checkALError();
        if (this.toLoop && !this.toStream) {
          AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 1);
        } else {
          AL10.alSourcei(this.channelOpenAL.ALSource.get(0), 4103, 0);
        } 
        checkALError();
      } 
      if (!this.toStream) {
        if (this.myBuffer == null) {
          errorMessage("No sound buffer to play");
          return;
        } 
        this.channelOpenAL.attachBuffer(this.myBuffer);
      } 
    } 
    if (!playing()) {
      if (this.toStream && !wasPaused) {
        if (this.codec == null) {
          errorMessage("Decoder null in method 'play'");
          return;
        } 
        if (this.codec.getAudioFormat() == null)
          this.codec.initialize(this.filenameURL.getURL()); 
        AudioFormat audioFormat = this.codec.getAudioFormat();
        if (audioFormat == null) {
          errorMessage("Audio Format null in method 'play'");
          return;
        } 
        int soundFormat = 0;
        if (audioFormat.getChannels() == 1) {
          if (audioFormat.getSampleSizeInBits() == 8) {
            soundFormat = 4352;
          } else if (audioFormat.getSampleSizeInBits() == 16) {
            soundFormat = 4353;
          } else {
            errorMessage("Illegal sample size in method 'play'");
            return;
          } 
        } else if (audioFormat.getChannels() == 2) {
          if (audioFormat.getSampleSizeInBits() == 8) {
            soundFormat = 4354;
          } else if (audioFormat.getSampleSizeInBits() == 16) {
            soundFormat = 4355;
          } else {
            errorMessage("Illegal sample size in method 'play'");
            return;
          } 
        } else {
          errorMessage("Audio data neither mono nor stereo in method 'play'");
          return;
        } 
        this.channelOpenAL.setFormat(soundFormat, (int)audioFormat.getSampleRate());
        this.preLoad = true;
      } 
      this.channel.play();
      if (this.pitch != 1.0F)
        checkPitch(); 
    } 
  }
  
  public boolean preLoad() {
    if (this.codec == null)
      return false; 
    this.codec.initialize(this.filenameURL.getURL());
    LinkedList<byte[]> preLoadBuffers = (LinkedList)new LinkedList<byte>();
    for (int i = 0; i < SoundSystemConfig.getNumberStreamingBuffers(); i++) {
      this.soundBuffer = this.codec.read();
      if (this.soundBuffer == null || this.soundBuffer.audioData == null)
        break; 
      preLoadBuffers.add(this.soundBuffer.audioData);
    } 
    positionChanged();
    this.channel.preLoadBuffers(preLoadBuffers);
    this.preLoad = false;
    return true;
  }
  
  private void resetALInformation() {
    this.sourcePosition = BufferUtils.createFloatBuffer(3).put(new float[] { this.position.x, this.position.y, this.position.z });
    this.sourceVelocity = BufferUtils.createFloatBuffer(3).put(new float[] { this.velocity.x, this.velocity.y, this.velocity.z });
    this.sourcePosition.flip();
    this.sourceVelocity.flip();
    positionChanged();
  }
  
  private void calculateDistance() {
    if (this.listenerPosition != null) {
      double dX = (this.position.x - this.listenerPosition.get(0));
      double dY = (this.position.y - this.listenerPosition.get(1));
      double dZ = (this.position.z - this.listenerPosition.get(2));
      this.distanceFromListener = (float)Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    } 
  }
  
  private void calculateGain() {
    if (this.attModel == 2) {
      if (this.distanceFromListener <= 0.0F) {
        this.gain = 1.0F;
      } else if (this.distanceFromListener >= this.distOrRoll) {
        this.gain = 0.0F;
      } else {
        this.gain = 1.0F - this.distanceFromListener / this.distOrRoll;
      } 
      if (this.gain > 1.0F)
        this.gain = 1.0F; 
      if (this.gain < 0.0F)
        this.gain = 0.0F; 
    } else {
      this.gain = 1.0F;
    } 
  }
  
  private boolean checkALError() {
    switch (AL10.alGetError()) {
      case 0:
        return false;
      case 40961:
        errorMessage("Invalid name parameter.");
        return true;
      case 40962:
        errorMessage("Invalid parameter.");
        return true;
      case 40963:
        errorMessage("Invalid enumerated parameter value.");
        return true;
      case 40964:
        errorMessage("Illegal call.");
        return true;
      case 40965:
        errorMessage("Unable to allocate memory.");
        return true;
    } 
    errorMessage("An unrecognized error occurred.");
    return true;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\paulscode\sound\libraries\SourceLWJGLOpenAL.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */