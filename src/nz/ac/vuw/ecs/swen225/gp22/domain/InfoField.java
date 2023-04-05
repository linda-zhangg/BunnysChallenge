package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents an InfoField entity. Displays message when player walks on it.
 *
 * @author Linda Zhang 300570498
 */
public class InfoField extends Entity {
  private final Point pos;
  private final String message;

  /**
   * The constructor for InfoField. Initialises with position and a message.
   *
   * @param pos the location of the InfoField
   * @param message the message contained in the InfoField
   */
  public InfoField(Point pos, String message) {
    this.pos = pos;
    this.message = message;
  }

  @Override
  public void onInteraction(Player p, Cells cells) {
    if (!p.getPos().equals(pos)) {
      p.removeActiveInfoField();
      throw new IllegalStateException("Player is not on InfoField!");
    }

    // only display the first time player is on the info field
    if (p.getActiveInfoField().isEmpty()) { 
      p.setActiveInfoField(this);
      System.out.println("InfoField: " + message); // change later to display message differently
      onChange();
    }
  }

  @Override
  public Point getPos() {
    return pos;
  }

  /**
   * Returns the message contained in the InfoField.
   *
   * @return the message that the InfoField displays
   */
  public String getMessage() {
    return message;
  }
}