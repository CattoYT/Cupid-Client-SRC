package paulscode.sound.libraries;

import java.util.LinkedList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import paulscode.sound.Channel;
import paulscode.sound.SoundBuffer;

public class ChannelJavaSound extends Channel {
  public Clip clip = null;
  
  SoundBuffer soundBuffer;
  
  public SourceDataLine sourceDataLine = null;
  
  private List<SoundBuffer> streamBuffers;
  
  private int processed = 0;
  
  private Mixer myMixer = null;
  
  private AudioFormat myFormat = null;
  
  private FloatControl gainControl = null;
  
  private FloatControl panControl = null;
  
  private FloatControl sampleRateControl = null;
  
  private float initialGain = 0.0F;
  
  private float initialSampleRate = 0.0F;
  
  private boolean toLoop = false;
  
  public ChannelJavaSound(int type, Mixer mixer) {
    super(type);
    this.libraryType = LibraryJavaSound.class;
    this.myMixer = mixer;
    this.clip = null;
    this.sourceDataLine = null;
    this.streamBuffers = new LinkedList<SoundBuffer>();
  }
  
  public void cleanup() {
    if (this.streamBuffers != null) {
      SoundBuffer buf = null;
      while (!this.streamBuffers.isEmpty()) {
        buf = this.streamBuffers.remove(0);
        buf.cleanup();
        buf = null;
      } 
      this.streamBuffers.clear();
    } 
    this.clip = null;
    this.soundBuffer = null;
    this.sourceDataLine = null;
    this.streamBuffers.clear();
    this.myMixer = null;
    this.myFormat = null;
    this.streamBuffers = null;
    super.cleanup();
  }
  
  public void newMixer(Mixer m) {
    if (this.myMixer != m) {
      try {
        if (this.clip != null) {
          this.clip.close();
        } else if (this.sourceDataLine != null) {
          this.sourceDataLine.close();
        } 
      } catch (SecurityException e) {}
      this.myMixer = m;
      if (this.attachedSource != null)
        if (this.channelType == 0 && this.soundBuffer != null) {
          attachBuffer(this.soundBuffer);
        } else if (this.myFormat != null) {
          resetStream(this.myFormat);
        }  
    } 
  }
  
  public boolean attachBuffer(SoundBuffer buffer) {
    if (errorCheck((this.channelType != 0), "Buffers may only be attached to non-streaming sources"))
      return false; 
    if (errorCheck((this.myMixer == null), "Mixer null in method 'attachBuffer'"))
      return false; 
    if (errorCheck((buffer == null), "Buffer null in method 'attachBuffer'"))
      return false; 
    if (errorCheck((buffer.audioData == null), "Buffer missing audio data in method 'attachBuffer'"))
      return false; 
    if (errorCheck((buffer.audioFormat == null), "Buffer missing format information in method 'attachBuffer'"))
      return false; 
    DataLine.Info lineInfo = new DataLine.Info(Clip.class, buffer.audioFormat);
    if (errorCheck(!AudioSystem.isLineSupported(lineInfo), "Line not supported in method 'attachBuffer'"))
      return false; 
    Clip newClip = null;
    try {
      newClip = (Clip)this.myMixer.getLine(lineInfo);
    } catch (Exception e) {
      errorMessage("Unable to create clip in method 'attachBuffer'");
      printStackTrace(e);
      return false;
    } 
    if (errorCheck((newClip == null), "New clip null in method 'attachBuffer'"))
      return false; 
    if (this.clip != null) {
      this.clip.stop();
      this.clip.flush();
      this.clip.close();
    } 
    this.clip = newClip;
    this.soundBuffer = buffer;
    this.myFormat = buffer.audioFormat;
    newClip = null;
    try {
      this.clip.open(this.myFormat, buffer.audioData, 0, buffer.audioData.length);
    } catch (Exception e) {
      errorMessage("Unable to attach buffer to clip in method 'attachBuffer'");
      printStackTrace(e);
      return false;
    } 
    resetControls();
    return true;
  }
  
  public void setAudioFormat(AudioFormat audioFormat) {
    resetStream(audioFormat);
    if (this.attachedSource != null && this.attachedSource.rawDataStream && this.attachedSource.active() && this.sourceDataLine != null)
      this.sourceDataLine.start(); 
  }
  
  public boolean resetStream(AudioFormat format) {
    if (errorCheck((this.myMixer == null), "Mixer null in method 'resetStream'"))
      return false; 
    if (errorCheck((format == null), "AudioFormat null in method 'resetStream'"))
      return false; 
    DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, format);
    if (errorCheck(!AudioSystem.isLineSupported(lineInfo), "Line not supported in method 'resetStream'"))
      return false; 
    SourceDataLine newSourceDataLine = null;
    try {
      newSourceDataLine = (SourceDataLine)this.myMixer.getLine(lineInfo);
    } catch (Exception e) {
      errorMessage("Unable to create a SourceDataLine in method 'resetStream'");
      printStackTrace(e);
      return false;
    } 
    if (errorCheck((newSourceDataLine == null), "New SourceDataLine null in method 'resetStream'"))
      return false; 
    this.streamBuffers.clear();
    this.processed = 0;
    if (this.sourceDataLine != null) {
      this.sourceDataLine.stop();
      this.sourceDataLine.flush();
      this.sourceDataLine.close();
    } 
    this.sourceDataLine = newSourceDataLine;
    this.myFormat = format;
    newSourceDataLine = null;
    try {
      this.sourceDataLine.open(this.myFormat);
    } catch (Exception e) {
      errorMessage("Unable to open the new SourceDataLine in method 'resetStream'");
      printStackTrace(e);
      return false;
    } 
    resetControls();
    return true;
  }
  
  private void resetControls() {
    switch (this.channelType) {
      case 0:
        try {
          if (!this.clip.isControlSupported(FloatControl.Type.PAN)) {
            this.panControl = null;
          } else {
            this.panControl = (FloatControl)this.clip.getControl(FloatControl.Type.PAN);
          } 
        } catch (IllegalArgumentException iae) {
          this.panControl = null;
        } 
        try {
          if (!this.clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            this.gainControl = null;
            this.initialGain = 0.0F;
          } else {
            this.gainControl = (FloatControl)this.clip.getControl(FloatControl.Type.MASTER_GAIN);
            this.initialGain = this.gainControl.getValue();
          } 
        } catch (IllegalArgumentException iae) {
          this.gainControl = null;
          this.initialGain = 0.0F;
        } 
        try {
          if (!this.clip.isControlSupported(FloatControl.Type.SAMPLE_RATE)) {
            this.sampleRateControl = null;
            this.initialSampleRate = 0.0F;
          } else {
            this.sampleRateControl = (FloatControl)this.clip.getControl(FloatControl.Type.SAMPLE_RATE);
            this.initialSampleRate = this.sampleRateControl.getValue();
          } 
        } catch (IllegalArgumentException iae) {
          this.sampleRateControl = null;
          this.initialSampleRate = 0.0F;
        } 
        return;
      case 1:
        try {
          if (!this.sourceDataLine.isControlSupported(FloatControl.Type.PAN)) {
            this.panControl = null;
          } else {
            this.panControl = (FloatControl)this.sourceDataLine.getControl(FloatControl.Type.PAN);
          } 
        } catch (IllegalArgumentException iae) {
          this.panControl = null;
        } 
        try {
          if (!this.sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            this.gainControl = null;
            this.initialGain = 0.0F;
          } else {
            this.gainControl = (FloatControl)this.sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            this.initialGain = this.gainControl.getValue();
          } 
        } catch (IllegalArgumentException iae) {
          this.gainControl = null;
          this.initialGain = 0.0F;
        } 
        try {
          if (!this.sourceDataLine.isControlSupported(FloatControl.Type.SAMPLE_RATE)) {
            this.sampleRateControl = null;
            this.initialSampleRate = 0.0F;
          } else {
            this.sampleRateControl = (FloatControl)this.sourceDataLine.getControl(FloatControl.Type.SAMPLE_RATE);
            this.initialSampleRate = this.sampleRateControl.getValue();
          } 
        } catch (IllegalArgumentException iae) {
          this.sampleRateControl = null;
          this.initialSampleRate = 0.0F;
        } 
        return;
    } 
    errorMessage("Unrecognized channel type in method 'resetControls'");
    this.panControl = null;
    this.gainControl = null;
    this.sampleRateControl = null;
  }
  
  public void setLooping(boolean value) {
    this.toLoop = value;
  }
  
  public void setPan(float p) {
    if (this.panControl == null)
      return; 
    float pan = p;
    if (pan < -1.0F)
      pan = -1.0F; 
    if (pan > 1.0F)
      pan = 1.0F; 
    this.panControl.setValue(pan);
  }
  
  public void setGain(float g) {
    if (this.gainControl == null)
      return; 
    float gain = g;
    if (gain < 0.0F)
      gain = 0.0F; 
    if (gain > 1.0F)
      gain = 1.0F; 
    double minimumDB = this.gainControl.getMinimum();
    double maximumDB = this.initialGain;
    double ampGainDB = 0.5D * maximumDB - minimumDB;
    double cste = Math.log(10.0D) / 20.0D;
    float valueDB = (float)(minimumDB + 1.0D / cste * Math.log(1.0D + (Math.exp(cste * ampGainDB) - 1.0D) * gain));
    this.gainControl.setValue(valueDB);
  }
  
  public void setPitch(float p) {
    if (this.sampleRateControl == null)
      return; 
    float sampleRate = p;
    if (sampleRate < 0.5F)
      sampleRate = 0.5F; 
    if (sampleRate > 2.0F)
      sampleRate = 2.0F; 
    sampleRate *= this.initialSampleRate;
    this.sampleRateControl.setValue(sampleRate);
  }
  
  public boolean preLoadBuffers(LinkedList<byte[]> bufferList) {
    if (errorCheck((this.channelType != 1), "Buffers may only be queued for streaming sources."))
      return false; 
    if (errorCheck((this.sourceDataLine == null), "SourceDataLine null in method 'preLoadBuffers'."))
      return false; 
    this.sourceDataLine.start();
    if (bufferList.isEmpty())
      return true; 
    byte[] preLoad = bufferList.remove(0);
    if (errorCheck((preLoad == null), "Missing sound-bytes in method 'preLoadBuffers'."))
      return false; 
    while (!bufferList.isEmpty())
      this.streamBuffers.add(new SoundBuffer(bufferList.remove(0), this.myFormat)); 
    this.sourceDataLine.write(preLoad, 0, preLoad.length);
    this.processed = 0;
    return true;
  }
  
  public boolean queueBuffer(byte[] buffer) {
    if (errorCheck((this.channelType != 1), "Buffers may only be queued for streaming sources."))
      return false; 
    if (errorCheck((this.sourceDataLine == null), "SourceDataLine null in method 'queueBuffer'."))
      return false; 
    if (errorCheck((this.myFormat == null), "AudioFormat null in method 'queueBuffer'"))
      return false; 
    this.streamBuffers.add(new SoundBuffer(buffer, this.myFormat));
    processBuffer();
    this.processed = 0;
    return true;
  }
  
  public boolean processBuffer() {
    if (errorCheck((this.channelType != 1), "Buffers are only processed for streaming sources."))
      return false; 
    if (errorCheck((this.sourceDataLine == null), "SourceDataLine null in method 'processBuffer'."))
      return false; 
    if (this.streamBuffers == null || this.streamBuffers.isEmpty())
      return false; 
    SoundBuffer nextBuffer = this.streamBuffers.remove(0);
    this.sourceDataLine.write(nextBuffer.audioData, 0, nextBuffer.audioData.length);
    if (!this.sourceDataLine.isActive())
      this.sourceDataLine.start(); 
    nextBuffer.cleanup();
    nextBuffer = null;
    return true;
  }
  
  public int feedRawAudioData(byte[] buffer) {
    if (errorCheck((this.channelType != 1), "Raw audio data can only be processed by streaming sources."))
      return -1; 
    if (errorCheck((this.streamBuffers == null), "StreamBuffers queue null in method 'feedRawAudioData'."))
      return -1; 
    this.streamBuffers.add(new SoundBuffer(buffer, this.myFormat));
    return buffersProcessed();
  }
  
  public int buffersProcessed() {
    this.processed = 0;
    if (errorCheck((this.channelType != 1), "Buffers may only be queued for streaming sources.")) {
      if (this.streamBuffers != null)
        this.streamBuffers.clear(); 
      return 0;
    } 
    if (this.sourceDataLine == null) {
      if (this.streamBuffers != null)
        this.streamBuffers.clear(); 
      return 0;
    } 
    if (this.sourceDataLine.available() > 0)
      this.processed = 1; 
    return this.processed;
  }
  
  public void flush() {
    if (this.channelType != 1)
      return; 
    if (errorCheck((this.sourceDataLine == null), "SourceDataLine null in method 'flush'."))
      return; 
    this.sourceDataLine.stop();
    this.sourceDataLine.flush();
    this.sourceDataLine.drain();
    this.streamBuffers.clear();
    this.processed = 0;
  }
  
  public void close() {
    switch (this.channelType) {
      case 0:
        if (this.clip != null) {
          this.clip.stop();
          this.clip.flush();
          this.clip.close();
        } 
        break;
      case 1:
        if (this.sourceDataLine != null) {
          flush();
          this.sourceDataLine.close();
        } 
        break;
    } 
  }
  
  public void play() {
    switch (this.channelType) {
      case 0:
        if (this.clip != null) {
          if (this.toLoop) {
            this.clip.stop();
            this.clip.loop(-1);
            break;
          } 
          this.clip.stop();
          this.clip.start();
        } 
        break;
      case 1:
        if (this.sourceDataLine != null)
          this.sourceDataLine.start(); 
        break;
    } 
  }
  
  public void pause() {
    switch (this.channelType) {
      case 0:
        if (this.clip != null)
          this.clip.stop(); 
        break;
      case 1:
        if (this.sourceDataLine != null)
          this.sourceDataLine.stop(); 
        break;
    } 
  }
  
  public void stop() {
    switch (this.channelType) {
      case 0:
        if (this.clip != null) {
          this.clip.stop();
          this.clip.setFramePosition(0);
        } 
        break;
      case 1:
        if (this.sourceDataLine != null)
          this.sourceDataLine.stop(); 
        break;
    } 
  }
  
  public void rewind() {
    switch (this.channelType) {
      case 0:
        if (this.clip != null) {
          boolean rePlay = this.clip.isRunning();
          this.clip.stop();
          this.clip.setFramePosition(0);
          if (rePlay) {
            if (this.toLoop) {
              this.clip.loop(-1);
              break;
            } 
            this.clip.start();
          } 
        } 
        break;
    } 
  }
  
  public float millisecondsPlayed() {
    switch (this.channelType) {
      case 0:
        if (this.clip == null)
          return -1.0F; 
        return (float)this.clip.getMicrosecondPosition() / 1000.0F;
      case 1:
        if (this.sourceDataLine == null)
          return -1.0F; 
        return (float)this.sourceDataLine.getMicrosecondPosition() / 1000.0F;
    } 
    return -1.0F;
  }
  
  public boolean playing() {
    switch (this.channelType) {
      case 0:
        if (this.clip == null)
          return false; 
        return this.clip.isActive();
      case 1:
        if (this.sourceDataLine == null)
          return false; 
        return this.sourceDataLine.isActive();
    } 
    return false;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\paulscode\sound\libraries\ChannelJavaSound.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */