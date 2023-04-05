package nz.ac.vuw.ecs.swen225.gp22.src.app;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import nz.ac.vuw.ecs.swen225.gp22.domain.Player;
import nz.ac.vuw.ecs.swen225.gp22.global.Direction;

/**
 * Controller for all movements/functionality of the user in game.
 *
 */
public class Controller extends Keys {
  private static HashMap<String, Integer> keyConfig = new HashMap<>();

  /**
   * Add initial keyBinds to keyConfig.
   *
   * @param c - current Chip
   */
  public Controller(Chips c) {
    chip = c;
  }

  /**
   * newInstance of Controller.
   *
   * @param p - Player
   * @return Controller
   */
  public Controller newInstance(Player p) {
    // if keyConfig is empty, set initial values
    // Basic movement keyBinds
    setAction(keyConfig.getOrDefault("up", KeyEvent.VK_W), p.set(Direction::up),
        p.set(Direction::unUp));
    setAction(keyConfig.getOrDefault("down", KeyEvent.VK_S), p.set(Direction::down),
        p.set(Direction::unDown));
    setAction(keyConfig.getOrDefault("left", KeyEvent.VK_A), p.set(Direction::left),
        p.set(Direction::unLeft));
    setAction(keyConfig.getOrDefault("right", KeyEvent.VK_D), p.set(Direction::right),
        p.set(Direction::unRight));
    return this;
  }

  /**
   * Copy all new key binds for all moves to map.
   *
   * @param movements - map of movements
   */
  public static void updateMoves(Map<String, Integer> movements) {
    keyConfig.putAll(movements);
  }

  /**
   * Set key inside map.
   *
   * @param name - String name keySet
   * @param code - keyCode 
   */
  public void setKey(String name, int code) {
    keyConfig.put(name, code);
  }

  /**
   * Return keySet of map.
   *
   * @return keyConfig hashMap
   */
  public HashMap<String, Integer> getKeyset() {
    return keyConfig;
  }
}
