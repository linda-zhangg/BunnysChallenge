package test.nz.ac.vuw.ecs.swen225.gp22.persistency;

import java.io.File;
import java.io.IOException;
import java.util.List;
import nz.ac.vuw.ecs.swen225.gp22.domain.*;
import nz.ac.vuw.ecs.swen225.gp22.persistency.*;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.Test;

/**
 * Tests for the persistency module.
 *
 * @author Jarvis Warnock 300 578342
 *
 */
public class PersistencyTests {

  /**
   * Load a valid level.
   */
  @Test
  void createValidLevel1() {
    Runnable next = () -> {
    };
    Runnable end = () -> {
    };
    try {
      assert (Levels.loadLevel(next, end, "level1") instanceof Level);
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load a valid level.
   */
  @Test
  void createValidLevel2() {
    Runnable next = () -> {
    };
    Runnable end = () -> {
    };
    try {
      assert (Levels.loadLevel(next, end, "level2") instanceof Level);
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }
  }

  /**
   * Try to load a level with invalid filename.
   */
  @Test
  void createInvalidLevel1() {
    Runnable next = () -> {
    };
    Runnable end = () -> {
    };
    try {
      assert Levels.loadLevel(next, end, "test") == null;
    } catch (IOException e) {
      return;
    } catch (JDOMException jodme) {
      return;
    }
  }

  /**
   * Save valid level and check that the countdown timer is correct.
   */
  @Test
  void countdownSave() {
    Runnable next = () -> {
    };
    Runnable end = () -> {
    };
    Level testLevel1 = null;
    
    // Loads a level from a file and then saves that file to load it again
    try {
      testLevel1 = Levels.loadLevel(next, end, "level1");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }
    try {
      assert testLevel1 != null : "Error loading level";
      Levels.saveLevel(testLevel1, "testLevel");
    } catch (IOException e) {
      e.printStackTrace();
    }
    Level testLevel2 = null;
    try {
      testLevel2 = Levels.loadLevel(next, end, "testLevel");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }

    // Checks that the levels were created correctly
    assert testLevel1 != null : "Error loading original level";
    assert testLevel2 != null : "Error loading saved level";

    // Checks the count downs are the same
    assert testLevel1.getCountdown() == testLevel2.getCountdown();

    // Deletes the test file
    File toDelete = new File("levels/testLevel.xml");
    toDelete.delete();

  }

  /**
   * Save valid level and check that the player position is correct.
   */
  @Test
  void playerSave() {
    Runnable next = () -> {
    };
    Runnable end = () -> {
    };
    Level testLevel1 = null;
    try {
      testLevel1 = Levels.loadLevel(next, end, "level1");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }
    try {
      assert testLevel1 != null : "Error loading level";
      Levels.saveLevel(testLevel1, "testLevel");
    } catch (IOException e) {
      e.printStackTrace();
    }
    Level testLevel2 = null;
    try {
      testLevel2 = Levels.loadLevel(next, end, "testLevel");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }

    assert testLevel1 != null : "Error loading original level";
    assert testLevel2 != null : "Error loading saved level";

    // Checks the player position is the same in both levels
    assert testLevel1.getPlayer().getPos().x() == testLevel2.getPlayer().getPos().x()
        && testLevel1.getPlayer().getPos().y() == testLevel2.getPlayer().getPos().y();

    File toDelete = new File("levels/testLevel.xml");
    toDelete.delete();

  }

  /**
   * Save valid level and check that the monster positions is correct.
   */
  @Test
  void monsterSave() {
    Runnable next = () -> {
    };
    Runnable end = () -> {
    };
    Level testLevel1 = null;
    try {
      testLevel1 = Levels.loadLevel(next, end, "level1");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }
    try {
      assert testLevel1 != null : "Error loading level";
      Levels.saveLevel(testLevel1, "testLevel");
    } catch (IOException e) {
      e.printStackTrace();
    }
    Level testLevel2 = null;
    try {
      testLevel2 = Levels.loadLevel(next, end, "testLevel");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }

    assert testLevel1 != null : "Error loading original level";
    assert testLevel2 != null : "Error loading saved level";

    // Checks that no monsters are missing and that all of them are in the correct
    // position
    // along with the same point in their route
    assert testLevel1.getMonsters().size() == testLevel2.getMonsters().size();
    for (int i = 0; i < testLevel1.getMonsters().size(); i++) {
      assert testLevel1.getMonsters().get(i).getPos().x() 
      == testLevel2.getMonsters().get(i).getPos().x()
          : "Monster at incorrect position";
      assert testLevel1.getMonsters().get(i).getPos().y() 
      == testLevel2.getMonsters().get(i).getPos().y()
          : "Monster at incorrect position";
      assert testLevel1.getMonsters().get(i).getProgress() 
      == testLevel2.getMonsters().get(i).getProgress()
          : "Monster has incorrect progress in route";
      for (int j = 0; j < testLevel1.getMonsters().get(i).getRoute().size(); j++) {
        assert testLevel1.getMonsters().get(i).getRoute().get(j) 
        == testLevel2.getMonsters().get(i).getRoute().get(j)
            : "Monster route is incorrect";
      }
    }
    File toDelete = new File("levels/testLevel.xml");
    toDelete.delete();

  }

  /**
   * Save valid level and check that the entities are correct.
   */
  @Test
  void entitySave() {
    Runnable next = () -> {
    };
    Runnable end = () -> {
    };
    Level testLevel1 = null;
    try {
      testLevel1 = Levels.loadLevel(next, end, "level2");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }
    try {
      assert testLevel1 != null : "Error loading level";
      Levels.saveLevel(testLevel1, "testLevel");
    } catch (IOException e) {
      e.printStackTrace();
    }
    Level testLevel2 = null;
    try {
      testLevel2 = Levels.loadLevel(next, end, "testLevel");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }

    assert testLevel1 != null : "Error loading original level";
    assert testLevel2 != null : "Error loading saved level";

    assert testLevel1.getEntites().size() == testLevel2.getEntites().size();
    for (int i = 0; i < testLevel1.getEntites().size(); i++) {
      Entity entity = testLevel1.getEntites().get(i);
      // Checks that an entity of the same type is at the correct position
      List<Entity> correctEntity = testLevel1.getEntites().stream()
          .filter(e -> e.getClass() == entity.getClass()
          && e.getPos().x() == entity.getPos().x() && e.getPos().y() 
          == entity.getPos().y()).toList();
      assert correctEntity.size() == 1;
    }

    File toDelete = new File("levels/testLevel.xml");
    toDelete.delete();

  }

  /**
   * Save valid level and check that the players inventory is correct.
   */
  @Test
  void inventorySave() {
    Runnable next = () -> {
    };
    Runnable end = () -> {
    };
    Level testLevel1 = null;
    try {
      testLevel1 = Levels.loadLevel(next, end, "level1");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }
    try {
      assert testLevel1 != null : "Error loading level";
      Levels.saveLevel(testLevel1, "testLevel");
    } catch (IOException e) {
      e.printStackTrace();
    }
    Level testLevel2 = null;
    try {
      testLevel2 = Levels.loadLevel(next, end, "testLevel");
    } catch (IOException | JDOMException e) {
      e.printStackTrace();
    }

    assert testLevel1 != null : "Error loading original level";
    assert testLevel2 != null : "Error loading saved level";

    // Checks that the players inventory is the same in both levels
    assert testLevel1.getPlayer().inventory().size() == testLevel2.getPlayer().inventory().size();
    for (int i = 0; i < testLevel1.getPlayer().inventory().size(); i++) {
      Entity entity = testLevel1.getPlayer().inventory().stream().toList().get(i);
      List<Entity> correctEntity = testLevel1.getPlayer().inventory().stream()
          .filter(e -> e.getClass() == entity.getClass() && e.getPos().x() == entity.getPos().x()
              && e.getPos().y() == entity.getPos().y())
          .toList();
      assert correctEntity.size() == 1;
    }

    File toDelete = new File("levels/testLevel.xml");
    toDelete.delete();

  }

}
