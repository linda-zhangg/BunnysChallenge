package nz.ac.vuw.ecs.swen225.gp22.global;

import nz.ac.vuw.ecs.swen225.gp22.domain.Point;

/**
 * Direction enum for use on moving objects.
 *
 * @author Shared
 */
public enum Direction{
  /** No direction. */
  None(0,0){},
  /** Upwards direction. */
  Up(0,-1){ 
	  /** Removes up direction if applied. */
	  public Direction unUp(){return None;}
  },
  /** Right direction. */
  Right(+1,0){
	  /** Removes right direction if applied. */
	  public Direction unRight(){return None;}
  },
  /** Down direction. */
  Down(0,+1){
	  /** Removes down direction if applied. */
	  public Direction unDown(){return None;}
  },
  /** Left direction. */
  Left(-1,0){
	  /** Removes left direction if applied. */
	  public Direction unLeft(){return None;}
  };

  /** Calculated next direction. */
  public final Point next;
  
  /**
   * Get up direction.
   *
   * @return Up enum
   */
  public Direction up(){return Up;}
  
  /**
   * Get right direction.
   *
   * @return Right enum
   */
  public Direction right(){return Right;}
  
  /**
   * Get down direction.
   *
   * @return Down enum
   */
  public Direction down(){return Down;}
  
  /**
   * Get Left direction.
   *
   * @return left enum
   */
  public Direction left(){return Left;}
  
  /**
   * Get unUp direction.
   *
   * @return this enum
   */
  public Direction unUp(){return this;}
  
  /**
   * Get unRight direction.
   *
   * @return this enum
   */
  public Direction unRight(){return this;}
  
  /**
   * Get unDown direction.
   *
   * @return this enum
   */
  public Direction unDown(){return this;}
  
  /**
   * Get unLeft direction.
   *
   * @return this enum
   */
  public Direction unLeft(){return this;}
  
  /**
   * Get the point of the next direction.
   *
   * @return next position as a point
   */
  public Point point() {return next;}
  
  /**
   * Constructor
   * @param x position
   * @param y position
   */
  Direction(int x,int y){next = new Point(x, y); }
}