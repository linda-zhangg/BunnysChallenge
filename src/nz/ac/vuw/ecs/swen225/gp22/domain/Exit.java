package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents an Exit entity. This interacts with the player when it completes
 * the game.
 *
 * @author Linda Zhang 300570498
 *
 */
public class Exit extends Entity {
  private final Point pos;

  /**
   * The constructor for Exit. Initialises with position.
   *
   * @param pos the location of the Exit
   */
  public Exit(Point pos) {
    this.pos = pos;
  }

  @Override
  public void onInteraction(Player p, Cells cells) {
    if (!p.getPos().equals(pos)) {
      throw new IllegalStateException("Player is not on Exit!");
    }
    p.entitiesToRemove().add(this);
    onChange();
  }

  @Override
  public Point getPos() {
    return pos;
  }
}