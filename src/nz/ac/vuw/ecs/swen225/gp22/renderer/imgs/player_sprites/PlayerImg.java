package nz.ac.vuw.ecs.swen225.gp22.renderer.imgs.player_sprites;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * @author Declan Cross 300567901.
 *
 *         enum storing all player sprites for animation
 */
public enum PlayerImg {
  /** Idle down animation frame image. */
  idle_Down_1,
  /** Idle down animation frame image. */
  idle_Down_2,
  /** Idle left animation frame image. */
  idle_Left_1,
  /** Idle left animation frame image. */
  idle_Left_2,
  /** Idle right animation frame image. */
  idle_Right_1,
  /** Idle right animation frame image. */
  idle_Right_2,
  /** Idle up animation frame image. */
  idle_Up_1,
  /** Idle up animation frame image. */
  idle_Up_2,
  /** Walk down animation frame image. */
  walk_Down_1,
  /** Walk down animation frame image. */
  walk_Down_2,
  /** Walk left animation frame image. */
  walk_Left_1,
  /** Walk left animation frame image. */
  walk_Left_2,
  /** Walk right animation frame image. */
  walk_Right_1,
  /** Walk right animation frame image. */
  walk_Right_2,
  /** Walk up animation frame image. */
  walk_Up_1,
  /** Walk up animation frame image. */
  walk_Up_2;

  /** Loaded image of current frame. */
  public final BufferedImage image;

  /**
   * Constructor for player image.
   */
  PlayerImg() {
    image = loadImage(this.name());
  }

  /**
   * Loads image.
   *
   * @param name of image
   * @return loaded image
   */
  private static BufferedImage loadImage(String name) {
    URL imagePath = PlayerImg.class.getResource(name + ".png");
    // try read file and throw exception if it doesn't exist
    try {
      return ImageIO.read(imagePath);
    } catch (IOException e) {
      System.out.println("Player image does not exist: " + name + ".png");
      throw new Error(e);
    }
  }
}
