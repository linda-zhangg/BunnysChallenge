package nz.ac.vuw.ecs.swen225.gp22.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import nz.ac.vuw.ecs.swen225.gp22.global.Direction;

/**
 * The player of the game. Represents 'Chap'
 *
 * @author Linda Zhang 300570498
 */
public class Player extends Subject {
  private Point pos;
  private Point oldPos; // for smooth rendering
  private Direction direction = Direction.None;

  private int timestamp = 5; // update position once every 5 ticks
  private int timeSinceLastMove = 5; // a counter from the last move

  private Set<Entity> entitiesOnBoard;
  private int treasuresToCollect; // a counter for the number of treasures that need collecting
  private Set<Entity> inventory = new HashSet<>();
  
  //set to add to if the entity is to be removed
  private Set<Entity> entitiesToRemove = new HashSet<>(); 

  //for when the player is on an InfoField
  private Optional<InfoField> activeInfoField = Optional.empty(); 

  /**
   * The constructor of Player. Initialised with its location and a list of
   * entities on the board.
   *
   * @param p        the location of the player
   * @param entities the entities on the board
   */
  public Player(Point p, Set<Entity> entities) {
    pos = p;
    entitiesOnBoard = entities;
    oldPos = getPos();
    // start with the total number of treasures on the board
    treasuresToCollect = totalTreasureCount(); 
  }

  /**
   * Get the position of the Player.
   *
   * @return the position of the player in a new point
   */
  public Point getPos() {
    return new Point(pos.x(), pos.y());
  }

  /**
   * Get the previous position of the Player.
   *
   * @return the previous old point of the player (for smooth animation in render)
   */
  public Point getOldPos() {
    return new Point(oldPos.x(), oldPos.y());
  }

  /**
   * Set the position of the Player.
   *
   * @param newPos set the position of the player to the newPos, assigning oldPos
   *               to pos
   */
  public void setPos(Point newPos) {
    oldPos = pos;
    pos = newPos;
  }

  /**
   * Get the moving time of the Player. (for smooth animation in render)
   *
   * @return the moving time
   */
  public float getMoveTime() {
    return (float) timeSinceLastMove / (float) timestamp;
  }

  /**
   * Get the Player's current direction.
   *
   * @return the player's current direction
   */
  public Direction direction() {
    return direction;
  }

  /**
   * Set the Player's current direction.
   *
   * @param d direction to set the player to
   */
  public void direction(Direction d) {
    direction = d;
  }

  /**
   * Set the Player's current direction.
   *
   * @param f the function to apply
   * @return the runnable applied to the direction
   */
  public Runnable set(Function<Direction, Direction> f) {
    return () -> {
      direction = f.apply(direction);
    };
  }

  /**
   * Get all the current entities on the board.
   *
   * @return all the entities on the current board
   */
  public Set<Entity> entitiesOnBoard() {
    return entitiesOnBoard;
  }

  /**
   * Get all the current entities in the inventory.
   *
   * @return all the entities in the players inventory (picked up)
   */
  public Set<Entity> inventory() {
    return inventory;
  }

  /**
   * Set the current invetory.
   *
   * @param i the inventory to set to the current inventory
   */
  public void setInventory(Set<Entity> i) {
    inventory = i;
  }

  /**
   * Get the number of treasure to collect.
   *
   * @return the number of treasures on the board currently
   */
  public int treasuresToCollect() {
    return treasuresToCollect;
  }

  /**
   * Decrease the count for the number of treasures on the board.
   */
  public void decreaseTreasureCount() {
    treasuresToCollect--;
  }

  /**
   * Get the list of entities to me removed.
   *
   * @return the set to queue entities to be removed
   */
  public Set<Entity> entitiesToRemove() {
    return entitiesToRemove;
  }

  /**
   * Get the optional active InfoField on the board.
   *
   * @return the InfoField the player is standing on. empty optional if not
   */
  public Optional<InfoField> getActiveInfoField() {
    return activeInfoField;
  }

  /**
   * Set the active InfoField.
   *
   * @param i set i to the active info field. Could be empty optional.
   */
  public void setActiveInfoField(InfoField i) {
    activeInfoField = Optional.of(i);
  }

  /**
   * Sets the infoField Optional back to empty.
   */
  public void removeActiveInfoField() {
    activeInfoField = Optional.empty();
  }

  /**
   * Returns true if there exits a matching Teleporter for the given Teleporter.
   *
   * @param t the teleporter to find a match for
   * @return if there exists a teleporter that matches the given one
   */
  public boolean foundMatchingTeleporter(Teleporter t) {
    if (t.getOther() == null) {
      return false; // other is null
    }
    long count = entitiesOnBoard.stream().filter(e -> e.equals(t.getOther())).count();
    if (count != 1) {
      return false; // must have one other also in entities
    }
    if (!t.getOther().getOther().equals(t)) {
      return false; // other's other must be t
    }
    return true;
  }

  /**
   * Returns true if there are boots in the inventory.
   *
   * @return true if there are boots in the inventory
   */
  public boolean bootsInInventory() {
    return inventory.stream().anyMatch(e -> e instanceof Boots);
  }

  /**
   * The player at each tick.
   *
   * @param cells the cells on current level
   */
  public void tick(Cells cells) {
    if (timeSinceLastMove < timestamp) {
      timeSinceLastMove++;
    }

    // allow movement every 5 ticks
    if (timeSinceLastMove >= timestamp) {
      Cell nextCell = cells.get(pos.add(direction.point()));
      if (!nextCell.isSolid() || nextCell.state() instanceof LockedDoor) {
        move(direction, cells); // only call move if move is legal
      }
    }
  }

  /**
   * Makes player move in a direction.
   *
   * @param d direction
   * @param cells the cells on current level
   */
  public void move(Direction d, Cells cells) {
    if (d == Direction.None) {
      return; // no movement
    }

    Point newPos = pos.add(d.point());
    Cell nextCell = cells.get(newPos);

    // remove LockedDoor if key exits to unlock it (has matching color)
    if (nextCell.state() instanceof LockedDoor) {
      Key key = findMatchingKey(nextCell.symbol());
      if (key != null) {
        if (key.getColor() != 'G' || cells.getAllLockedDoorsOfType(key.getColor()).size() == 1) {
          inventory.remove(key); // remove from inventory if red or blue, and green if last door
        }
        nextCell.setState(new Floor());
        onChange(); // Observer pattern for Renderer
      } else {
        return; // otherwise don't move
      }
    }

    timeSinceLastMove = 0;
    if (nextCell.isSolid()) {
      throw new IllegalArgumentException("Chap cannot be on a solid tile");
    }
    oldPos = getPos();
    pos = newPos;
  }

  // total treasure count on the board
  private int totalTreasureCount() {
    return (int) entitiesOnBoard.stream().filter(e -> e instanceof Treasure).count();
  }

  // finds a key that matches this color or else return null
  private Key findMatchingKey(char col) {
    return inventory.stream().filter(e -> e instanceof Key && ((Key) e).getColor() == col)
        .map(e -> (Key) e).findFirst()
        .orElse(null);
  }

}
