package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents the Water CellState. Player dies if in contact with it, unless
 * Boots are in the inventory.
 *
 * @author Linda Zhang 300570498
 */
public class Water implements CellState {

  @Override
  public boolean isSolid(Cell self) {
    return false;
  }

  @Override
  public char symbol(Cell self) {
    return 'w';
  }
}