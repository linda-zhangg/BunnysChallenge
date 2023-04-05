package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents a CellState for different types of cells (State pattern) The state
 * of a cell can be changed this way.
 *
 * @author Linda Zhang 300570498
 */
public interface CellState {

  /**
   * Checks if this state of cell is solid.
   *
   * @param self the Cell object continaing the CellState
   * @return if the cell is solid (player cannot go through)
   */
  boolean isSolid(Cell self);

  /**
   * Gets the char symbol that represents this state of Cell.
   *
   * @param self the Cell object continaing the CellState
   * @return the char representing this type of cell
   */
  char symbol(Cell self);
}

// Wall CellState. Other modules cannot access this state.
class Wall implements CellState {
  public boolean isSolid(Cell self) {
    return true;
  }

  public char symbol(Cell self) {
    return '#';
  }
}

// Spawn CellState. Other modules cannot acces this state.
class Spawn implements CellState {
  public boolean isSolid(Cell self) {
    return false;
  }

  public char symbol(Cell self) {
    return 's';
  }
}