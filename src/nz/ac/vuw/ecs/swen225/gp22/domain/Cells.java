package nz.ac.vuw.ecs.swen225.gp22.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The board of the current level in the game. Cells consist of many single cell
 * objects.
 *
 * @author Linda Zhang 300570498
 */
public class Cells {
  private int maxX;
  private int maxY;
  private Point spawn;
  private final List<List<Cell>> inner = new ArrayList<>();

  /**
   * The constructor for Cells. Populates the inner ArrayList with the map array.
   *
   * @param map cells loaded from Persistency
   */
  public Cells(char[][] map) {
    maxX = map[0].length;
    maxY = map.length;

    for (int x = 0; x < maxX; x++) {
      var tmp = new ArrayList<Cell>();
      inner.add(tmp);
      for (int y = 0; y < maxY; y++) {
        switch (map[y][x]) {
          case '#':
            tmp.add(new Cell(new Wall(), x, y));
            break;
          case 's':
            tmp.add(new Cell(new Spawn(), x, y));
            spawn = new Point(x, y);
            break;
          case 'R':
            tmp.add(new Cell(new RedLockedDoor(), x, y));
            break;
          case 'B':
            tmp.add(new Cell(new BlueLockedDoor(), x, y));
            break;
          case 'Y':
            tmp.add(new Cell(new YellowLockedDoor(), x, y));
            break;
          case 'G':
            tmp.add(new Cell(new GreenLockedDoor(), x, y));
            break;
          case 'X':
            tmp.add(new Cell(new ExitLock(), x, y));
            break;
          case 'w':
            tmp.add(new Cell(new Water(), x, y));
            break;
          default:
            tmp.add(new Cell(new Floor(), x, y));
            break;
        }
      }
    }
  }

  /**
   * Gets the cell on the x and y positon.
   *
   * @param x x position on the board
   * @param y y position on the board
   * @return the cell on the x and y position. If out of range, return a water
   *         cell.
   */
  public Cell get(int x, int y) {
    if (x < 0 || y < 0 || x >= maxX || y >= maxY) {
      return new Cell(new Water(), x, y); // return water cell if out of the map
    }
    Cell res = inner.get(x).get(y);
    assert res != null;
    return res;
  }

  /**
   * Gets the cell on the point.
   *
   * @param p point of cell
   * @return the cell on the point. If out of range, return a water cell.
   */
  public Cell get(Point p) {
    return get(p.x(), p.y());
  }

  /**
   * Gets all Locked doors on the board of a specific color.
   *
   * @param color the symbol that matches the color of the key
   * @return the list of Locked doors
   */
  public List<Cell> getAllLockedDoorsOfType(char color) {
    return inner.stream().flatMap(cells -> cells.stream())
        .filter(c -> c.state() instanceof LockedDoor)
        .filter(c -> c.symbol() == color).toList();
  }

  /**
   * Gets the cell that represents the ExitLock.
   *
   * @return the cell that represents the ExitLock
   */
  public Cell getExitLock() {
    return inner.stream().flatMap(cells -> cells.stream())
        .filter(c -> c.state() instanceof ExitLock).findFirst()
        .orElseThrow(() -> new IllegalStateException("No ExitLock exists on board!"));
  }

  /**
   * Gets the point at which the player should spawn.
   *
   * @return the point at which the player should spawn
   */
  public Point getSpawn() {
    return spawn;
  }

  /**
   * Get the maxX position of the cell board.
   *
   * @return the maxX position of the cell board
   */
  public int getMaxX() {
    return maxX;
  }

  /**
   * Get the maxY position of the cell board.
   *
   * @return the maxY position of the cell board
   */
  public int getMaxY() {
    return maxY;
  }

  /**
   * Returns the inner ArrayList as a 2D array. Used for unit tests.
   *
   * @return return Cells as a 2D array of symbols.
   */
  public char[][] toMap() {
    char[][] newMap = new char[maxY][maxX];

    for (int y = 0; y < maxY; y++) {
      for (int x = 0; x < maxX; x++) {
        newMap[y][x] = inner.get(x).get(y).symbol();
      }
    }
    return newMap;
  }

}