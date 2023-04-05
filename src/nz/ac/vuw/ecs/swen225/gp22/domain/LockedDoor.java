package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents the LockedDoor CellState. Must be extended by Red, Blue, Yellow or
 * Green LockedDoor to get the symbol.
 *
 * @author Linda Zhang 300570498
 */
public class LockedDoor implements CellState {

  @Override
  public boolean isSolid(Cell self) {
    return true;
  }

  @Override
  public char symbol(Cell self) {
    throw new IllegalStateException("LockedDoor must be extended.");
  }
}