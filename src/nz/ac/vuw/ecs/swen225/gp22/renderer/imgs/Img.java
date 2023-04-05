package nz.ac.vuw.ecs.swen225.gp22.renderer.imgs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

/**
 * enum storing all usable images for cells and entities.
 *
 * @author Declan Cross 300567901
 */
public enum Img {
  /** Floor image. */
  Floor,
  /** Wall image. */
  Wall,
  /** Spawn image. */
  Spawn,
  /** Player image. */
  Player,
  /** Red Locked Door image. */
  RedLockedDoor,
  /** Blue Locked Door image. */
  BlueLockedDoor,
  /** Green Locked Door image. */
  GreenLockedDoor,
  /** Yellow Locked Door image. */
  YellowLockedDoor,
  /** Info field sign image. */
  InfoField,
  /** Exit entity image. */
  Exit,
  /** Red Key image. */
  Key_R,
  /** Green Key image. */
  Key_G,
  /** Blue Key image. */
  Key_B,
  /** Yellow Key image. */
  Key_Y,
  /** Lock for exit gate image. */
  ExitLock,
  /** Lock for exit gate image. */
  ExitLock_1,
  /** Lock for exit gate image. */
  ExitLock_2,
  /** Lock for exit gate image. */
  ExitLock_3,
  /** Lock for exit gate image. */
  ExitLock_4,
  /** Treasure chip image. */
  Treasure,
  /** Water image. */
  Water,
  /** Ice image. */
  Ice,
  /** Monster image. */
  Monster,
  /** Alternate monster appearance. */
  Monster_1,
  /** Frozen water boots image. */
  Boots,
  /** Flipper image. */
  Flippers,
  /** Background image for main menu. */
  Background,
  /** Teleporter image .*/
  Teleporter;

  /** Stored image. */
  public final BufferedImage image;

  /**
   * Constructor for image which loads the image from enum.
   */
  Img() {
    image = loadImage(this.name());
  }

  /**
   * Value safe enum getter for names, will not cause game to crash and uses
   * replacement textures instead.
   *
   * @param name of image
   * @return enum of correct value
   */
  public static Img getValue(String name) {
    try {
      return valueOf(name);
    } catch (IllegalArgumentException e) {
      // print exception and use default value
      System.out.println(name + " enum value does not exist.");
      return Img.Water;
    }
  }

  /**
   * Overloaded getter for alternate textures - useful for animated textures.
   *
   * @param name   of image
   * @param curVal current frame of texture set
   * @param maxVal highest value of set
   * @return Image enum
   */
  public static Img getValue(String name, int curVal, int maxVal) {
    if (curVal > maxVal) {
      curVal = maxVal;
    }
    String image = name + "_" + curVal; // (int)(curVal/(float)maxVal)
    return Stream.of(Img.values()).anyMatch(v -> v.name().equals(image))
        ? getValue(image) : getValue(name);
  }

  /**
   * Loads an image given a name but checks for image loading errors.
   *
   * @param name of image
   * @return loaded image
   */
  private static BufferedImage loadImage(String name) {
    URL imagePath = Img.class.getResource(name + ".png");
    try {
      // try get image and use default image if item does not exist
      return ImageIO.read(imagePath);
    } catch (IOException e) {
      // throw exception if file does not exist
      System.out.println(name + ".png does not exist.");
      return Img.Water.image;
    }
  }
}