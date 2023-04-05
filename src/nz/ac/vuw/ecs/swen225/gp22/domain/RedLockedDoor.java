package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents a type of LockedDoor.
 *
 * @author Linda Zhang 300570498
 */
public class RedLockedDoor extends LockedDoor {

  @Override
  public char symbol(Cell self) {
    return 'R';
  }
}
