package nz.ac.vuw.ecs.swen225.gp22.renderer.sounds;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


/**
 * Sound loader enum for local wav file.
 *
 * @author Declan Cross 300567901
 */
public enum Sound {
  /** Level music. */
  Eightbitsong,
  /** default sound effect for interactions. */
  Interact,
  /** Door unlock sound */
  Door,
  /** Escape sound */
  Escape,
  /** Footstep in grass sound */
  Footstep,
  /** Portal teleport sound */
  Portal,
  /** Sign interaction sound */
  Sign,
  /** Treasure collect sound */
  Treasure;

  /** Loaded sound clip for playing. */
  public final Clip clip;

  /**
   * Constructor that loads the sound of class.
   */
  Sound() {
    clip = loadSound(this.name());
  }

  /**
   * Loads a sound clip.
   *
   * @param name of sound to load
   * @return opened sound clip for playing
   */
  private static Clip loadSound(String name) {
    URL soundPath = Sound.class.getResource(name + ".wav");
    try {
      // try load sound clip and open
      AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundPath);
      Clip c = AudioSystem.getClip();
      c.open(audioIn);
      return c;
    } catch (Exception e) {
      // throw error if sound does not exist
      System.out.println("Sound does not exist: " + name + ".wav");
      e.printStackTrace();
      throw new Error(e);
    }
  }

}
