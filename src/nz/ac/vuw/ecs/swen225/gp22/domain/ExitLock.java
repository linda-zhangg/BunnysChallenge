package nz.ac.vuw.ecs.swen225.gp22.domain;

/**
 * Represents the ExitLock CellState. This turns into a Floor state when all
 * treasures are collected.
 *
 * @author Linda Zhang 300570498
 */
public class ExitLock implements CellState {

  @Override
  public boolean isSolid(Cell self) {
    return true;
  }

  @Override
  public char symbol(Cell self) {
    return 'X';
  }
}