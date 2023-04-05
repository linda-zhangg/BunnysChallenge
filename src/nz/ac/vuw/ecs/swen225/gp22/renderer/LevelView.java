package nz.ac.vuw.ecs.swen225.gp22.renderer;

import actor.spi.Actor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javax.swing.JPanel;
import nz.ac.vuw.ecs.swen225.gp22.domain.*;
import nz.ac.vuw.ecs.swen225.gp22.global.Direction;
import nz.ac.vuw.ecs.swen225.gp22.renderer.fonts.LoadedFont;
import nz.ac.vuw.ecs.swen225.gp22.renderer.imgs.Img;
import nz.ac.vuw.ecs.swen225.gp22.renderer.imgs.player_sprites.PlayerImg;
import nz.ac.vuw.ecs.swen225.gp22.renderer.sounds.Sound;

/**
 * Renders all content of map and GUI on screen when playing levels.
 *
 * @author Declan Cross 300567902
 */
public class LevelView extends JPanel {
  // serial number
  private static final long serialVersionUID = 1L;

  // level variables
  private final Level curLevel; // stored level for rendering

  // animation variables
  private int fadeIn; // current fade amount
  private final int animLength = 25; // max time in ticks for fade in
  private final double animSteps = 2; // animation steps for fading in objects
  private int tickCount; // current tick count for animations
  private final int maxTicks = 16; // animation cycle max time

  // rendering variables
  private final int renderSize = 64; // default rendering size of cells
  private final int iconSize = renderSize / 2;
  private boolean animationForwards = true; // toggle between enter and exit animation

  // player variables
  Direction oldDir; // old position to remember for player animation

  // active classes
  EventHandler events;
  SoundPlayer sounds;

  /**
   * Constructor.
   *
   * @param newLevel level object to be observed
   */
  public LevelView(Level newLevel) {
    curLevel = newLevel;
    curLevel.setLevelEnd(() -> endLevel());
    // set initial animation direction
    oldDir = Direction.Down;
    // start music
    sounds = new SoundPlayer();
    events = new EventHandler(curLevel, sounds);
    beginLevel();
  }

  /**
   * End level animation that gets called before exiting from level.
   */
  public void endLevel() {
    // fade out music and set animation to reverse
    new Thread(() -> sounds.fadeOut(Sound.Eightbitsong, 50)).start();
    animationForwards = false;
  }

  /**
   * Starts events upon level start.
   */
  public void beginLevel() {
    // fades in music
    new Thread(() -> sounds.loop(Sound.Eightbitsong, 50)).start();
  }

  /**
   * Override method to paint screen every game tick.
   *
   * @param g graphics panel to draw on
   */
  public void paintComponent(Graphics g) {
    /// get size of graphics
    super.paintComponent(g);
    final Dimension s = getSize();

    // fade in or out animation
    if (fadeIn < animLength && animationForwards) {
      fadeIn++;
    } else if (fadeIn > 0 && !animationForwards) {
      fadeIn--;
    }
      

    // tick counter for animated textures
    tickCount++;
    tickCount = tickCount % maxTicks;

    // get player info
    Player curPlayer = curLevel.getPlayer();
    Point pos = curPlayer.getPos();

    // work out difference between positions to make movement animation
    Point diff = curPlayer.getOldPos().distance(pos);
    float shiftX = diff.x() * (1 - curPlayer.getMoveTime());
    float shiftY = diff.y() * (1 - curPlayer.getMoveTime());
    PlayerFields pf = new PlayerFields(pos, shiftX, shiftY);

    // find centre of map relative to player
    var centerP = new Point(-(int) (s.width * 0.65) / (int) (2 * renderSize),
        -s.height / (int) (2 * renderSize));
    var c = pos.add(centerP);
    ScreenFields sf = new ScreenFields(g, c, s);

    // draw map, player, and GUI
    drawMap(sf, pf, curPlayer);
    drawPlayer(sf, pos);
    drawGUI(g, s, curLevel, curPlayer);

  }

  /**
   * Method to draw the map and its child components.
   *
   * @param sf record containing screen fields
   * @param pf record containing player fields
   */
  void drawMap(ScreenFields sf, PlayerFields pf, Player player) {
    // paint background black
    sf.g().setColor(new Color(0, 0, 0, fadeIn * 10));
    sf.g().fillRect(0, 0, sf.size().width, sf.size().height);

    // get cells to draw
    Cells c = curLevel.getCells();
    List<Cell> wallTiles = new ArrayList<>();
    int range = (int) ((float) fadeIn / animSteps);

    // use nestled intstreams to go through for all squares on screen to draw cells
    IntStream.range(pf.player().x() - range + 1, pf.player().x() + range)
        .forEach(row -> IntStream.range(pf.player().y() - range + 1, pf.player().y() + range)
            .forEach(col -> {
              if (c.get(row, col).isSolid()) {
                // draw floor then save solid tile for drawing on top later
                drawCell(sf, pf, player, new Cell(new Floor(), row, col));
                wallTiles.add(c.get(row, col));
              } else {
                // draw cell
                drawCell(sf, pf, player, c.get(row, col));
              }
            }));

    // get entities to draw
    List<Entity> entities = curLevel.getEntites();
    entities.stream().forEach(ent -> drawEntity(sf, pf, ent));

    // walls must be drawn last for 3D effect
    wallTiles.forEach(a -> drawCell(sf, pf, player, a));

    // draw monsters
    curLevel.getMonsters().forEach(mon -> drawEntity(sf, pf, mon));

    // draw shadows over map
    IntStream.range(pf.player().x() - range + 1, pf.player().x() + range)
        .forEach(row -> IntStream.range(pf.player().y() - range + 1, pf.player().y() + range)
            .forEach(col -> drawShadow(sf, pf, new Point(row, col))));
  }

  /**
   * Method to draw shadows.
   *
   * @param sf      record containing screen fields
   * @param pf      record containing player fields
   * @param cellPos current cell object's position
   */
  void drawShadow(ScreenFields sf, PlayerFields pf, Point cellPos) {
    // calculate shadow dimensions
    final int w1 = cellPos.x() * renderSize - (int) ((sf.centre().x() + pf.shiftX()) * renderSize);
    final int h1 = cellPos.y() * renderSize - (int) ((sf.centre().y() + pf.shiftY()) * renderSize);

    // calculate distance of shadow from player and check bounds
    double dist = Math.hypot(cellPos.x() - pf.player().x() - pf.shiftX(),
        cellPos.y() - pf.player().y() - pf.shiftY())
        - 2;
    dist *= 50;
    if (dist < 0) {
      dist = 0;
    } else if (dist > 255) {
      dist = 255;
    }

    // draw shadow
    sf.g().setColor(new Color(0, 0, 0, (int) dist));
    sf.g().fillRect(w1, h1, renderSize, renderSize);
  }

  /**
   * Method to draw a single cell.
   *
   * @param sf record containing screen fields
   * @param pf record containing player fields
   * @param c  current cell object
   */
  void drawCell(ScreenFields sf, PlayerFields pf, Player p, Cell c) {
    // calculate image dimensions
    int w1 = c.x() * renderSize - (int) ((sf.centre().x() + pf.shiftX()) * renderSize);
    int h1 = c.y() * renderSize - (int) ((sf.centre().y() + pf.shiftY()) * renderSize);
    int w2 = w1 + renderSize;
    int h2 = h1 + renderSize;
    int dimension = renderSize;
    if (c.isSolid()) {
      w2 += 8;
      h2 += 8;
      dimension += 8;
    }

    if (c.state() instanceof ExitLock) {
      // special effects for exit lock that changes state depending on treasures left
      sf.g().drawImage(Img.getValue(c.getName(), 
          curLevel.getPlayer().treasuresToCollect(), 4).image,
          w1, h1, w2, h2, 0, 0, dimension, dimension, null);
    } else if (c.state() instanceof Water && p.bootsInInventory()
        && Math.hypot(c.x() - p.getPos().x(), c.y() - p.getPos().y()) < 2.15) {
      // special effects for water that freezes with boots on
      sf.g().drawImage(Img.getValue("Ice").image, w1, h1, w2, h2, 0, 0, dimension, dimension, null);
    } else {
      // default case
      sf.g().drawImage(Img.getValue(c.getName()).image, 
          w1, h1, w2, h2, 0, 0, dimension, dimension, null);
    }

  }

  /**
   * Method to draw a single entity.
   *
   * @param sf  record containing screen fields
   * @param pf  record containing player fields
   * @param ent current entity object
   */
  void drawEntity(ScreenFields sf, PlayerFields pf, Entity ent) {
    // return if out of render distance
    Point pos = ent.getPos();
    if (Math.hypot(pos.x() - pf.player().x() + 0.5, pos.y() - pf.player().y() + 0.5)
        > (int) ((float) fadeIn / animSteps)) {
      return;
    }

    // calculate entity image dimensions
    int w1 = pos.x() * renderSize - (int) ((sf.centre().x() + pf.shiftX()) * renderSize);
    int h1 = pos.y() * renderSize - (int) ((sf.centre().y() + pf.shiftY()) * renderSize);
    int w2 = w1 + renderSize;
    int h2 = h1 + renderSize;

    // draw image
    if (ent instanceof Key) {
      sf.g().drawImage(Img.getValue(ent.getName() + '_' + ((Key) ent).getColor()).image,
          w1, h1, w2, h2, 0, 0, renderSize, renderSize, null);
    } else {
      sf.g().drawImage(Img.getValue(ent.getName()).image,
          w1, h1, w2, h2, 0, 0, renderSize, renderSize, null);
    }
  }

  /**
   * Method to draw a single entity.
   *
   * @param sf  record containing screen fields
   * @param pf  record containing player fields
   * @param ent current entity object
   */
  void drawEntity(ScreenFields sf, PlayerFields pf, Actor mon) {
    // return if out of render distance
    Point pos = mon.getPos();
    if (Math.hypot(pos.x() - pf.player().x() + 0.5, pos.y() - pf.player().y() + 0.5)
        > (int) ((float) fadeIn / animSteps)) {
      return;
    }
    
    // work out difference between positions to make movement animation
    Point diff = mon.getOldPos().distance(pos);
    float shiftX = diff.x() * (1 - mon.getMoveTime());
    float shiftY = diff.y() * (1 - mon.getMoveTime());
    int val = diff.x() < 0  || diff.y() < 0 ? 0 : 1;

    // calculate entity image dimensions
    int w1 = pos.x() * renderSize - (int) ((sf.centre().x() + pf.shiftX() - shiftX) * renderSize);
    int h1 = pos.y() * renderSize - (int) ((sf.centre().y() + pf.shiftY() - shiftY) * renderSize);
    int w2 = w1 + renderSize;
    int h2 = h1 + renderSize;
    // draw image
    sf.g().drawImage(Img.getValue(mon.getName(), val, 1).image,
        w1, h1, w2, h2, 0, 0, renderSize, renderSize, null);
  }

  /**
   * Method to draw a player.
   *
   * @param sf record containing screen fields
   * @param pf record containing player fields
   */
  void drawPlayer(ScreenFields sf, Point pos) {
    // get dimensions of image
    double scale = 0.5;
    double w1 = pos.x() * renderSize - (sf.centre().x() * renderSize) + renderSize * (scale / 2);
    double h1 = pos.y() * renderSize - (sf.centre().y() * renderSize) + renderSize * (scale / 2);
    double w2 = w1 + renderSize * scale;
    double h2 = h1 + renderSize * scale;

    // work out player image to use
    String type = (curLevel.getPlayer().direction() != Direction.None) ? "walk" : "idle";
    oldDir = (curLevel.getPlayer().direction() != Direction.None) 
        ? curLevel.getPlayer().direction() : oldDir;
    int val = tickCount > 8 ? 1 : 2;

    // draw player image
    sf.g().drawImage(PlayerImg.valueOf(type + "_" + oldDir + "_" + val).image,
        (int) w1, (int) h1, (int) w2, (int) h2,
        0, 0, renderSize, renderSize, null);
  }

  /**
   * Method to draw on screen informative elements.
   *
   * @param g graphics to render in
   * @param s size of screen
   * @param p level object
   */
  void drawGUI(Graphics g, Dimension s, Level l, Player p) {
    // draw background card
    int inventoryHeight = s.height - 3 * renderSize;
    int inventoryWidth = (int) (s.width * 3 / 12f);
    g.setColor(new Color(120, 131, 84, fadeIn * 9));
    g.fillRoundRect(s.width - renderSize - inventoryWidth, renderSize / 2,
        inventoryWidth, inventoryHeight, 30, 30);

    // draw text
    g.setColor(Color.white);
    g.setFont(LoadedFont.PixeloidSans.getSize(40f));

    // titles
    g.drawString("Level", s.width - inventoryWidth,  (int) (inventoryHeight*0.3));
    g.drawString("Time", s.width - inventoryWidth, (int) (inventoryHeight*0.6));
    g.drawString("Chips", s.width - inventoryWidth,  (int) (inventoryHeight*0.9));

    g.setFont(LoadedFont.PixeloidSans.getSize(30f));
    g.setColor(new Color(190, 196, 161));

    // values
    g.drawString(String.format("%03d", l.getLevelNum()), 
    	s.width - inventoryWidth, (int) (inventoryHeight*0.3) + 40);
    g.drawString(String.format("%03d", (int) (l.getCountdown())),
        s.width - inventoryWidth, (int) (inventoryHeight*0.6) + 40);
    g.drawString(String.format("%03d", l.getPlayer().treasuresToCollect()),
        s.width - inventoryWidth, (int) (inventoryHeight*0.9) + 40);

    // inventory
    g.setColor(new Color(120, 131, 84, fadeIn * 9));
    g.fillRoundRect(s.width - renderSize - inventoryWidth,
        renderSize + inventoryHeight, inventoryWidth,
        (int) (renderSize * 1.5), 30, 30);
    // inventory items
    int invX = s.width - inventoryWidth - renderSize / 2;
    AtomicInteger count = new AtomicInteger();
    // loop through all items in inventory
    p.inventory().forEach(ent -> {
      BufferedImage img = ent instanceof Key
          ? Img.getValue(ent.getName() + '_' + ((Key) ent).getColor()).image
          : Img.getValue(ent.getName()).image;
     
      int col = (int) (count.get() / 4f);
      int row = (int) col * 4;
      g.drawImage(img, invX + ((int) (renderSize / 1.25) * (count.get() - row)),
          (int) (renderSize * 1.125) + inventoryHeight + col * (int) (renderSize / 1.5),
          invX + ((int) (renderSize / 1.25) * (count.get() - row)) + iconSize,
          (int) (renderSize * 1.125) + inventoryHeight + iconSize + col * (int) (renderSize / 1.5),
          0, 0, renderSize, renderSize, null);
      count.getAndIncrement();
    });

    // info field
    int infoFieldHeight = 2 * renderSize;
    int infoFieldWidth = s.width - inventoryWidth - 3 * renderSize;
    // draw sign field if present.
    p.getActiveInfoField().ifPresent(a -> {
      g.setColor(new Color(122, 101, 91, 225));
      g.fillRoundRect(renderSize, s.height - infoFieldHeight - renderSize,
          infoFieldWidth, infoFieldHeight, 30, 30);
      g.setColor(Color.white);
      g.setFont(LoadedFont.PixeloidSans.getSize(24f));
      g.drawString(a.getMessage(), (int) (renderSize * 1.5), s.height - infoFieldHeight);
    });
  }

  /**
   * Private record containing screen fields to be used for rendering.
   *
   * @author Declan Cross
   * @param g      graphics pane to draw on
   * @param centre defined centre of content
   * @param size   size of screen
   */
  private record ScreenFields(Graphics g, Point centre, Dimension size) {
  }

  /**
   * Private record containing player fields used for positioning.
   *
   * @author Declan Cross
   * @param player position of player object
   * @param shiftX calculated dist between old and new player pos
   * @param shiftY calculated dist between old and new player pos
   */
  private record PlayerFields(Point player, float shiftX, float shiftY) {
  }

}
