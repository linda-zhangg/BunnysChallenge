package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents the Floor CellState.
 *
 * @author Linda Zhang 300570498
 */
public class Floor implements CellState {

  @Override
  public boolean isSolid(Cell self) {
    return false;
  }

  @Override
  public char symbol(Cell self) {
    return '.';
  }
}