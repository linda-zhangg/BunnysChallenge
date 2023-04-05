package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
* Represents a single square cell on the game board. It can have different
 * states e.g. exitLock becomes floor once all treasures are collected.
 *
 * @author Linda Zhang 300570498
 */
public class Cell {
  private CellState state;
  private int x;
  private int y;

  /**
   * The constructor for Cell. Initialised with a state, x and y positions.
   *
   * @param state current state of the cell
   * @param x     x position
   * @param y     y position
   */
  public Cell(CellState state, int x, int y) {
    this.state = state;
    this.x = x;
    this.y = y;
  }

  /**
   * Returns the state of the Cell.
   *
   * @return the state of the cell
   */
  public CellState state() {
    return state;
  }

  /**
   * Sets the state of the Cell.
   *
   * @param s set the state of the cell
   */
  public void setState(CellState s) {
    state = s;
  }

  /**
   * Gets the x location.
   *
   * @return x location
   */
  public int x() {
    return x;
  }

  /**
   * Gets the y location.
   *
   * @return y location
   */
  public int y() {
    return y;
  }

  /**
   * Returns true if the this Cell is solid.
   *
   * @return if cell is solid
   */
  public boolean isSolid() {
    return state.isSolid(this);
  }

  /**
   * Returns the character representing the state of the cell.
   *
   * @return the symbol character
   */
  public char symbol() {
    return state.symbol(this);
  }

  /**
   * Returns the class name that represents the state of the Cell.
   *
   * @return the name displaying the state of the cell
   */
  public String getName() {
    return state.getClass().getSimpleName();
  }

}