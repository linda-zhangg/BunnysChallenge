package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Prepresents the Boots enitity. 
 * If these are picked up, the player can walk on water.
 *
 * @author Linda Zhang 300570498
 */
public class Boots extends Entity {
  private final Point pos;
  
  /**
   * The constructor for Boots. Initialises with position.
   *
   * @param pos the location of the Boots
   */
  public Boots(Point pos) {
    this.pos = pos;
  }

  @Override
  public void onInteraction(Player p, Cells cells) {
    if (!p.getPos().equals(pos)) {
      throw new IllegalStateException("Player is not on Boots!");
    }

    // intial values before change is made
    final int inventorySize = p.inventory().size();

    // remove boots
    p.inventory().add(this);
    p.entitiesToRemove().add(this);
    onChange();

    assert p.inventory().size() == inventorySize + 1 : "Boots were not correctly removed";
  }

  @Override
  public Point getPos() {
    return pos;
  }
}
