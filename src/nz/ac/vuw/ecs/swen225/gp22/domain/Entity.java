package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * This represents the objects that interact with the player. These are
 * different from cells. They go on top of cells. Entities could be collected by
 * the player.
 *
 * @author Linda Zhang 300570498
 */
public abstract class Entity extends Subject {
  /**
   * Do the interaction with the player.
   *
   * @param p the player
   * @param cells the current cells board
   */
  public abstract void onInteraction(Player p, Cells cells);

  /**
   * Get the position of the entity.
   *
   * @return the Point for the location of the entity
   */
  public abstract Point getPos();

  /**
   * Get the class name of the entity.
   *
   * @return name representing the entity
   */
  public String getName() {
    return this.getClass().getSimpleName();
  }
}
