package nz.ac.vuw.ecs.swen225.gp22.persistency;

import actor.spi.Actor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.IntStream;
import nz.ac.vuw.ecs.swen225.gp22.domain.*;
import nz.ac.vuw.ecs.swen225.gp22.global.Direction;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Creates and saves levels to and from files.
 *
 * @author Jarvis Warnock 300578342
 *
 */
public class Levels {

  /**
   * Loads a level based off what name is passed in.
   *
   * @param filename - The name of the file to be loaded
   * @param next     - The next phase to be run after the level being loaded
   * @param end      - The phase to be run if the player loses in the level
   * @return Level - A level loaded from a file
   * @throws IOException   - Throws if the file being loaded is invalid
   * @throws JDOMException - Throws if the XML file is incorrectly formatted
   */
  public static Level loadLevel(Runnable next, Runnable end, String filename) 
      throws IOException, JDOMException {
    String prefix = "./levels/"; // Filepath prefix
    
    // Adds the prefic and suffix to the filename
    filename = prefix + filename + ".xml";
    Set<Entity> entities = new HashSet<Entity>();
    try {
      // Sets up the XML parsing
      File file = new File(filename);
      SAXBuilder builder = new SAXBuilder();
      Document document = builder.build(file);

      // Creates the level map
      int rows = Integer.parseInt(document.getRootElement()
          .getChild("map").getAttributeValue("rows"));
      int cols = Integer.parseInt(document.getRootElement()
          .getChild("map").getAttributeValue("cols"));

      char[][] map = new char[rows][cols]; // A map of all the tiles in the level

      List<Element> lines = document.getRootElement().getChild("map").getChildren();
      IntStream.range(0, rows).forEach(row -> {
        IntStream.range(0, cols).forEach(col -> map[row][col] = 
            lines.get(row).getText().charAt(col));
      });

      // Creates the elements from the file
      List<Element> entityList = document.getRootElement().getChild("entities").getChildren();
      entityList.stream().filter(e -> e.getName().equals("key"))
      .forEach(k -> createKey(k, entities));
      entityList.stream().filter(e -> e.getName().equals("treasure"))
      .forEach(k -> createTreasure(k, entities));
      entityList.stream().filter(e -> e.getName().equals("info"))
      .forEach(k -> createInfo(k, entities));
      entityList.stream().filter(e -> e.getName().equals("exit"))
      .forEach(k -> createExit(k, entities));
      entityList.stream().filter(e -> e.getName().equals("teleporter"))
      .forEach(k -> createTeleporter(k, entities));
      entityList.stream().filter(e -> e.getName().equals("boots"))
      .forEach(k -> createBoots(k, entities));

      // Creates the player
      Set<Entity> inventory = new HashSet<Entity>();
      int playerx = Integer.parseInt(document.getRootElement().getChild("player")
          .getAttributeValue("x"));
      int playery = Integer.parseInt(document.getRootElement().getChild("player")
          .getAttributeValue("y"));
      document.getRootElement().getChild("player").getChild("inventory").getChildren().stream()
          .filter(e -> e.getName().equals("key")).forEach(k -> createKey(k, inventory));
          document.getRootElement().getChild("player").getChild("inventory").getChildren().stream()
          .filter(e -> e.getName().equals("boots")).forEach(k -> createBoots(k, inventory));
      Player player = new Player(new Point(playerx, playery), entities);
      player.setInventory(inventory);
      List<Actor> actors = new ArrayList<Actor>();
      document.getRootElement().getChild("actors").getChildren().stream()
          .filter(e -> e.getName().equals("monster"))
          .forEach(k -> {
            try {
              actors.add(createMonster("level2.jar", k));
            } catch (MalformedURLException e1) {
              e1.printStackTrace();
            }
          });
      ;
      Integer levelNum = Integer.parseInt(document.getRootElement().getAttributeValue("num"));

      double time = Double.parseDouble(document.getRootElement().getAttributeValue("time"));
      return new Level(next, end, map, entities, levelNum, actors, time, player);
    } catch (JDOMException e) {
      throw e;
    } catch (IOException ioe) {
      throw ioe;
    }
    // return null;
  }

  /**
   * Creates a key from a string from the file.
   *
   * @param e        - The element from the file
   * @param entities - The current list of entities
   */
  private static void createKey(Element e, Set<Entity> entities) {
    int x = Integer.parseInt(e.getAttributeValue("x"));
    int y = Integer.parseInt(e.getAttributeValue("y"));
    String code = e.getAttributeValue("code");
    entities.add(new Key(new Point(x, y), code.charAt(0)));
  }

  /**
   * Creates treasure from a string from the file.
   *
   * @param e        - The element from the file
   * @param entities - The current list of entities
   */
  private static void createTreasure(Element e, Set<Entity> entities) {
    int x = Integer.parseInt(e.getAttributeValue("x"));
    int y = Integer.parseInt(e.getAttributeValue("y"));
    entities.add(new Treasure(new Point(x, y)));
  }

  /**
   * Creates info from a string from the file.
   *
   * @param e        - The element from the file
   * @param entities - The current list of entities
   */
  private static void createInfo(Element e, Set<Entity> entities) {
    int x = Integer.parseInt(e.getAttributeValue("x"));
    int y = Integer.parseInt(e.getAttributeValue("y"));
    String message = e.getChildText("message");
    entities.add(new InfoField(new Point(x, y), message));
  }

  /**
   * Creates an exit from a string from the file.
   *
   * @param e        - The element from the file
   * @param entities - The current list of entities
   */
  private static void createExit(Element e, Set<Entity> entities) {
    int x = Integer.parseInt(e.getAttributeValue("x"));
    int y = Integer.parseInt(e.getAttributeValue("y"));
    entities.add(new Exit(new Point(x, y)));
  }

  /**
   * Creates boots from a string from the file.
   *
   * @param e        - The element from the file
   * @param entities - The current list of entities
   */
  private static void createBoots(Element e, Set<Entity> entities) {
    int x = Integer.parseInt(e.getAttributeValue("x"));
    int y = Integer.parseInt(e.getAttributeValue("y"));
    entities.add(new Boots(new Point(x, y)));
  }

  /**
   * Creates a teleporter from a string from the file.
   *
   * @param e        - The element from the file
   * @param entities - The current list of entities
   */
  private static void createTeleporter(Element e, Set<Entity> entities) {
    int x = Integer.parseInt(e.getAttributeValue("x"));
    int y = Integer.parseInt(e.getAttributeValue("y"));
    int endx = Integer.parseInt(e.getAttributeValue("endx"));
    int endy = Integer.parseInt(e.getAttributeValue("endy"));
    Teleporter tel1 = new Teleporter(new Point(x, y), null);
    Teleporter tel2 = new Teleporter(new Point(endx, endy), tel1);
    tel1.setOther(tel2);
    entities.add(tel1);
    entities.add(tel2);
  }

  /**
   * Creates a monster from a jar file.
   *
   * @param filename - the filename to load the monster from
   * @param e        - The element that contains the details of the monster
   * @return The created monster
   */
  private static Actor createMonster(String filename, Element e) throws MalformedURLException {
    // Gets the file name to load the monster for the level
    File file = new File(filename + ".jar");
    ClassLoader parent = Actor.class.getClassLoader();
    
    // Gets the file URLs
    URL[] urls = null;
    try {
      urls = new URL[] { file.getAbsoluteFile().toURI().toURL() };
    } catch (MalformedURLException mue) {
      throw mue;
    }
    URLClassLoader c = new URLClassLoader(urls, parent);
    
    // Get the implementations of actor
    ServiceLoader<Actor> loader = ServiceLoader.load(Actor.class, c);
    Actor monster = null;
    Iterator<Actor> itr = loader.iterator();
    while (itr.hasNext()) {
      monster = itr.next();
      if (!monster.getClass().getSimpleName().equals("Monster")) {
        monster = null;
      }
    }

    if (monster == null) {
      return null;
    }
    
    // Sets the attributes of the monster
    int x = Integer.parseInt(e.getAttributeValue("x"));
    int y = Integer.parseInt(e.getAttributeValue("y"));
    String directions = e.getAttributeValue("route");
    List<Direction> dir = new ArrayList<Direction>();
    monster.setPoint(new Point(x, y));
    
    // Sets the route for the monster
    for (int i = 0; i < directions.length(); i++) {
      switch (directions.charAt(i)) {
        case 'L':
          dir.add(Direction.Left);
          break;
  
        case 'R':
          dir.add(Direction.Right);
          break;
  
        case 'U':
          dir.add(Direction.Up);
          break;
  
        case 'D':
          dir.add(Direction.Down);
          break;
        
        default:
          System.err.println("Error loading route for monster");
      }
    }
    monster.setRoute(dir);
    monster.setProgress(Integer.parseInt(e.getAttributeValue("progress")));
    return monster;
  }

  /**
   * Creates a monster from a jar file for the domain tests.
   *
   * @param filename - the filename to load the monster from
   * @param p        - Point where the monster should start
   * @param route    - The route for the monster to take
   * @return The created monster
   * @throws MalformedURLException - Throws if the URL to load is invalid
   */
  public static Actor createTestMonster(String filename, Point p, List<Direction> route) 
      throws MalformedURLException {
    File file = new File(filename + ".jar");
    ClassLoader parent = Actor.class.getClassLoader();
    URL[] urls = null;
    try {
      urls = new URL[] { file.getAbsoluteFile().toURI().toURL() };
    } catch (MalformedURLException mue) {
      throw mue;
    }
    URLClassLoader c = new URLClassLoader(urls, parent);
    // System.out.println(c.toString());
    ServiceLoader<Actor> loader = ServiceLoader.load(Actor.class, c);
    Actor monster = null;
    Iterator<Actor> itr = loader.iterator();
    while (itr.hasNext()) {
      monster = itr.next();
      if (!monster.getClass().getSimpleName().equals("Monster")) {
        monster = null;
      }
    }

    if (monster == null) {
      return null;
    }
    monster.setPoint(p);
    monster.setRoute(route);
    return monster;
  }

  /**
   * Saves the passed in level to a file.
   *
   * @param level    - The level to be saved
   * @param filename - The name the file will be saved as
   * @throws IOException - Throws if there was an error writing to the XML file
   *                     output
   */
  public static void saveLevel(Level level, String filename) throws IOException {
    // Creates the document to save to
    Document doc = new Document();
    
    // Add root level element
    Element lev = new Element("level");
    lev.setAttribute(new Attribute("num", level.getLevelNum() + ""));
    lev.setAttribute(new Attribute("time", level.getCountdown() + ""));
    doc.setRootElement(lev);
    
    // Add map element to the save level
    Cells cells = level.getCells(); // A cells object containing all the cells in the level
    Element map = new Element("map");
    map.setAttribute("rows", cells.getMaxY() + "");
    map.setAttribute("cols", cells.getMaxX() + "");
    // Add rows for the map
    for (int row = 0; row < cells.getMaxY(); row++) {
      String currRow = "";
      for (int col = 0; col < cells.getMaxX(); col++) {
        currRow += cells.get(col, row).symbol();
      }
      Element rowElement = new Element("row");
      rowElement.addContent(currRow);
      map.addContent(rowElement);
    }
    lev.addContent(map);

    // Adds entities to the saved level
    Element entities = new Element("entities");
    level.getEntites().stream().filter(e -> e instanceof Key).forEach(k -> {
      entities.addContent(saveKey((Key) k));
    });
    level.getEntites().stream().filter(e -> e instanceof Treasure).forEach(k -> {
      entities.addContent(saveTreasure((Treasure) k));
    });
    level.getEntites().stream().filter(e -> e instanceof InfoField).forEach(k -> {
      entities.addContent(saveInfo((InfoField) k));
    });
    level.getEntites().stream().filter(e -> e instanceof Exit).forEach(k -> {
      entities.addContent(saveExit((Exit) k));
    });
    level.getEntites().stream().filter(e -> e instanceof Boots).forEach(k -> {
      entities.addContent(saveBoots((Boots) k));
    });
    List<Entity> teleporters = new ArrayList<Entity>(level.getEntites());
    level.getEntites().stream().filter(e -> e instanceof Teleporter).forEach(k -> {
      if (teleporters.contains(k)) {
        entities.addContent(saveTeleporter((Teleporter) k, teleporters));
      }
      teleporters.remove(k);
    });
    lev.addContent(entities);

    // Adds a player to the save file
    Element player = new Element("player");
    player.setAttribute(new Attribute("x", level.getPlayer().getPos().x() + ""));
    player.setAttribute(new Attribute("y", level.getPlayer().getPos().y() + ""));
    Element inventory = new Element("inventory");
    level.getPlayer().inventory().stream().filter(e -> e instanceof Key).forEach(k -> {
      inventory.addContent(saveKey((Key) k));
    });
    level.getPlayer().inventory().stream().filter(e -> e instanceof Boots).forEach(k -> {
      inventory.addContent(saveBoots((Boots) k));
    });
    player.addContent(inventory);
    lev.addContent(player);

    // Adds the actors to the saved level
    Element actors = new Element("actors");
    level.getMonsters().stream().filter(e -> e.getClass().getSimpleName()
        .equals("Monster")).forEach(k -> {
          actors.addContent(saveMonster((Actor) k));
        });
    lev.addContent(actors);
    
    // Creates the XML file
    try {
      new XMLOutputter(Format.getPrettyFormat()).output(doc, 
          new FileWriter("./levels/" + filename + ".xml"));
    } catch (IOException ioe) {
      throw ioe;
    }
  }

  /**
   * Saves a key entitiy.
   *
   * @param k - the key to be saved
   * @return the key as an xml element
   */
  private static Element saveKey(Key k) {
    Element key = new Element("key");
    key.setAttribute(new Attribute("x", k.getPos().x() + ""));
    key.setAttribute(new Attribute("y", k.getPos().y() + ""));
    key.setAttribute(new Attribute("code", k.getColor() + ""));
    return key;
  }

  /**
   * Saves a treasure entitiy.
   *
   * @param k - the treasure to be saved
   * @return the treasure as an xml element
   */
  private static Element saveTreasure(Treasure k) {
    Element treasure = new Element("treasure");
    treasure.setAttribute(new Attribute("x", k.getPos().x() + ""));
    treasure.setAttribute(new Attribute("y", k.getPos().y() + ""));
    return treasure;
  }

  /**
   * Saves a info entitiy.
   *
   * @param k - the info to be saved
   * @return the info as an xml element
   */
  private static Element saveInfo(InfoField k) {
    Element info = new Element("info");
    info.setAttribute(new Attribute("x", k.getPos().x() + ""));
    info.setAttribute(new Attribute("y", k.getPos().y() + ""));
    Element message = new Element("message");
    message.addContent(k.getMessage());
    info.addContent(message);
    return info;
  }

  /**
   * Saves an exit entitiy.
   *
   * @param k - the exit to be saved
   * @return the exit as an xml element
   */
  private static Element saveExit(Exit k) {
    Element exit = new Element("exit");
    exit.setAttribute(new Attribute("x", k.getPos().x() + ""));
    exit.setAttribute(new Attribute("y", k.getPos().y() + ""));
    return exit;
  }

  /**
   * Saves a teleporter entitiy.
   *
   * @param k - the exit to be saved
   * @return the teleporter as an xml element
   */
  private static Element saveTeleporter(Teleporter k, List<Entity> teleList) {
    Element tel1 = new Element("teleporter");
    tel1.setAttribute(new Attribute("x", k.getPos().x() + ""));
    tel1.setAttribute(new Attribute("y", k.getPos().y() + ""));
    tel1.setAttribute(new Attribute("endx", k.getOther().getPos().x() + ""));
    tel1.setAttribute(new Attribute("endy", k.getOther().getPos().y() + ""));
    teleList.remove(k.getOther());
    return tel1;
  }

  /**
   * Saves a boots entitiy.
   *
   * @param k - the boots to be saved
   * @return the boots as an xml element
   */
  private static Element saveBoots(Boots k) {
    Element treasure = new Element("boots");
    treasure.setAttribute(new Attribute("x", k.getPos().x() + ""));
    treasure.setAttribute(new Attribute("y", k.getPos().y() + ""));
    return treasure;
  }

  /**
   * Creates a monster element to save in the file.
   *
   * @param k - The actor to save
   * @return The created monstr element
   */
  private static Element saveMonster(Actor k) {
    Element monster = new Element("monster");
    monster.setAttribute(new Attribute("x", k.getPos().x() + ""));
    monster.setAttribute(new Attribute("y", k.getPos().y() + ""));
    String directions = "";
    for (Direction d : k.getRoute()) {
      if (d == Direction.Down) {
        directions += 'D';
      } else if (d == Direction.Up) {
        directions += 'U';
      } else if (d == Direction.Left) {
        directions += 'L';
      } else if (d == Direction.Right) {
        directions += 'R';
      } else {
        System.err.println("Error saving monsters");
      }

    }
    monster.setAttribute(new Attribute("route", directions));
    monster.setAttribute(new Attribute("progress", k.getProgress() + ""));
    return monster;
  }

}