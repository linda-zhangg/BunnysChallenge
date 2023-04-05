package nz.ac.vuw.ecs.swen225.gp22.renderer;

import nz.ac.vuw.ecs.swen225.gp22.renderer.sounds.Sound;

import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SoundPlayer class to keep track of ongoing and loaded sounds for playing and
 * stopping.
 *
 * @author Declan Cross 300567901
 */
public class SoundPlayer {
  // loaded map of sound clips
  Map<Sound, Clip> sounds;

  /**
   * SoundPlayer constructor that loads all files.
   */
  public SoundPlayer() {
    // load in all sounds and add to map for retrieval
    sounds = new HashMap<>();
    sounds.putAll(Stream.of(Sound.values()).collect(Collectors.toMap(a -> a, a -> a.clip)));
  }

  /**
   * Returns a sound clip for usage by other functions.
   *
   * @param s sound name to retrieve
   * @return sound clip
   */
  private Clip getSound(Sound s) {
    if (sounds.containsKey(s)) {
      return sounds.get(s);
    } else {
      throw new IllegalArgumentException("Sound " + s + " does not exist.");
    }
  }

  /**
   * Plays a requested sound.
   *
   * @param s name of sound
   */
  public void play(Sound s) {
    // take argument to play specific sound
    Clip curClip = getSound(s);
    curClip.setFramePosition(0);
    curClip.stop();
    curClip.start();
  }

  /**
   * Loops a requested sound with a fade in effect.
   *
   * @param s      name of sound
   * @param maxVol maximum fade in volume
   */
  public void loop(Sound s, int maxVol) {
    // get sound control and set to min volume
    Clip curClip = getSound(s);
    FloatControl gainControl = (FloatControl) curClip.getControl(FloatControl.Type.MASTER_GAIN);
    gainControl.setValue(20f * (float) Math.log10(0.01));
    // set song to loop mode
    curClip.stop();
    curClip.loop(Clip.LOOP_CONTINUOUSLY);
    // loop to gradually fade in sound
    for (double i = 0; i < maxVol; i++) {
      try {
        Thread.sleep(45);
        gainControl.setValue(20f * (float) Math.log10(i / 100));
        curClip.loop(Clip.LOOP_CONTINUOUSLY);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Fades a sound out then stops playing it.
   *
   * @param s        name of sound
   * @param startVol initial volume
   */
  public void fadeOut(Sound s, int startVol) {
    // get sound control
    Clip curClip = getSound(s);
    FloatControl gainControl = (FloatControl) curClip.getControl(FloatControl.Type.MASTER_GAIN);
    // loop to gradually fade out sound
    for (double i = startVol; i > -100; i -= 5) {
      try {
        Thread.sleep(50);
        gainControl.setValue(20f * (float) Math.log10(i / 100));

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    // stop sound
    curClip.stop();
  }

  /**
   * Stops a sound .
   *
   * @param s name of sound
   */
  public void stop(Sound s) {
    getSound(s).stop();
  }

  /**
   * Stops all sounds.
   */
  public void stopAll() {
    // loop through each sound and end
    sounds.values().stream().forEach(clip -> clip.stop());
  }
}