package nz.ac.vuw.ecs.swen225.gp22.renderer.fonts;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * Loaded font class storing all fonts.
 *
 * @author Declan Cross 300567901
 */
public enum LoadedFont {
  /** Bold font for titles. */
  PixeloidBold,
  /** Default pixel font. */
  PixeloidSans,
  /** Mono font for numbers. */
  PixeloidMono;

  /** Current loaded font style. */
  public final Font style;

  /**
   * Constructor for LoadedFont.
   */
  LoadedFont() {
    style = loadFont(this.name());
  }

  /**
   * Load font on initialisation.
   *
   * @param name of font
   * @return loaded font
   */
  private static Font loadFont(String name) {
    try {
      // try create font and register
      Font tempFont = Font.createFont(Font.TRUETYPE_FONT,
          LoadedFont.class.getResourceAsStream(name + ".ttf"));
      GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(tempFont);
      return tempFont;
    } catch (Exception e) {
      // throw if font file does not exist
      System.out.println("Font does not exist: " + name + ".ttf");
      throw new Error(e);
    }

  }

  /**
   * Return correct requested font of certain size for rendering.
   *
   * @param size of font
   * @return font of size
   */
  public Font getSize(Float size) {
    return style.deriveFont(size);
  }
}
