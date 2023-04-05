package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents a Teleporter entity. The player is able to teleport to the other
 * Teleporter.
 *
 * @author Linda Zhang 300570498
 */
public class Teleporter extends Entity {
  private final Point pos;
  private Teleporter other;

  /**
   * The constructor of the Teleporter. Initialises with a location and another
   * Teleporter.
   *
   * @param pos   the position of this teleporter on the map
   * @param other the other teleporter that connects with this one
   */
  public Teleporter(Point pos, Teleporter other) {
    this.pos = pos;
    this.other = other;
  }

  @Override
  public void onInteraction(Player p, Cells cells) {
    if (!p.getPos().equals(pos)) {
      throw new IllegalStateException("Player is not on Teleporter!");
    }
    if (!p.foundMatchingTeleporter(this)) {
      throw new IllegalArgumentException("Other teleporter not valid!");
    }

    if (p.getPos().euclidean(p.getOldPos()) != 1) {
      return; // prevent recursive teleporting
    }
    // teleport player to landing position (one more in direction of other pos)
    p.setPos(other.getPos()); 
    onChange();

    assert p.getOldPos().equals(pos);
    assert p.getPos().equals(other.getPos());
  }

  @Override
  public Point getPos() {
    return pos;
  }

  /**
   * Set the other Teleporter.
   *
   * @param other set the other teleporter so it can be initialised
   */
  public void setOther(Teleporter other) {
    this.other = other;
  }

  /**
   * Get the other Teleporter.
   *
   * @return the other teleporter that matches this one
   */
  public Teleporter getOther() {
    return other;
  }
}
