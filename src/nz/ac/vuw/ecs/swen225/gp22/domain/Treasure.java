package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents a Treasure entity. Decreases the tresure count displayed on screen
 * of the game when player picks it up.
 *
 * @author Linda Zhang 300570498
 */
public class Treasure extends Entity {
  private final Point pos;

  /**
   * The constructor for Treasure. Initialises with position.
   *
   * @param pos the location of the Treasure
   */
  public Treasure(Point pos) {
    this.pos = pos;
  }

  @Override
  public void onInteraction(Player p, Cells cells) {
    if (!p.getPos().equals(pos)) {
      throw new IllegalStateException("Player is not on Treasure!");
    }

    // intial values before change is made
    int treasureCount = p.treasuresToCollect();
    if (treasureCount <= 0) {
      throw new IllegalStateException("Cannot have negative treasure to collect!");
    }

    // queue to remove treasure, decrease the number of treasures to collect
    p.decreaseTreasureCount();
    p.entitiesToRemove().add(this);
    onChange(); // all subscribers will hear this

    if (p.treasuresToCollect() <= 0) {
      Cell exitlock = cells.getExitLock();
      exitlock.setState(new Floor()); // unlocked
    }
    assert treasureCount == p.treasuresToCollect() + 1 :
          "Treasure count was not correctly decreased";
  }

  @Override
  public Point getPos() {
    return pos;
  }
}