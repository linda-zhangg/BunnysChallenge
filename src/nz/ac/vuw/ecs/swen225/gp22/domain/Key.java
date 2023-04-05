package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents a Key entity. It is picked up by the player and added to its
 * inventory.
 *
 * @author Linda Zhang 300570498
 */
public class Key extends Entity {
  private final Point pos;
  private final char color;

  /**
   * The constructor for Key. Initialises with position and a its colour (R L B
   * G).
   *
   * @param pos   the location of the Key
   * @param color the char that represents the color of the key
   */
  public Key(Point pos, char color) {
    this.pos = pos;
    this.color = color;
  }

  @Override
  public void onInteraction(Player p, Cells cells) {
    if (!p.getPos().equals(pos)) {
      throw new IllegalStateException("Player is not on Key!");
    }

    // intial values before change is made
    final int inventorySize = p.inventory().size();

    // remove key
    p.inventory().add(this);
    p.entitiesToRemove().add(this);
    onChange();

    assert p.inventory().size() == inventorySize + 1 : "key was not correctly removed";
  }

  @Override
  public Point getPos() {
    return pos;
  }

  /**
   * Returns the char that represents the the colour of the LockedDoor.
   *
   * @return the code that matches a LockedDoor
   */
  public char getColor() {
    return color;
  }
}