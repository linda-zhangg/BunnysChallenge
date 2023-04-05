package nz.ac.vuw.ecs.swen225.gp22.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nz.ac.vuw.ecs.swen225.gp22.domain.*;
import nz.ac.vuw.ecs.swen225.gp22.renderer.sounds.Sound;


/**
 * Class that observes subjects in domain.
 *
 * @author Declan Cross 300567901
 */
public class EventHandler {
  // active listeners and sound player variables
  final List<Subject> activeListeners = new ArrayList<>();
  final SoundPlayer sounds;
  // create a mapping of different object interactions to sounds
  final Map<Object, Object> soundMaps = Map.of(
		    Exit.class, Sound.Escape,
		    Treasure.class, Sound.Treasure,
		    Teleporter.class, Sound.Portal,
		    InfoField.class, Sound.Sign,
		    Player.class, Sound.Door
		);

  /**
   * Constructor.
   *
   * @param l level to get objects to observe
   * @param s SoundPlayer object to generate sound effects
   */
  public EventHandler(Level l, SoundPlayer s) {
    this.sounds = s;
    // add beep sound to all entity interactions
    l.getEntites().forEach(a -> {
      a.attach(() -> s.play((Sound) soundMaps.getOrDefault(a.getClass(), Sound.Interact)));
      activeListeners.add(a);
    });
    // walking sound effect for player
    l.getPlayer().attach(() -> s.play((Sound) soundMaps.getOrDefault(Player.class,
    		Sound.Interact)));
    activeListeners.add(l.getPlayer());

  }
  
  /**
   * Attach an listener observer to a subject.
   *
   * @param s subject
   * @param o observer with onChange lambda method
   */
  public void attach(Subject s, Observer o) {
    s.attach(o);
    activeListeners.add(s);
  }

  /**
   * Removes all observers for the level.
   */
  public void detatchAll() {
    activeListeners.forEach(a -> a.detatch());
    activeListeners.clear();
  }

}
