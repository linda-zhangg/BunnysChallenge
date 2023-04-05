package test.nz.ac.vuw.ecs.swen225.gp22.domain;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nz.ac.vuw.ecs.swen225.gp22.global.Direction;
import nz.ac.vuw.ecs.swen225.gp22.domain.Boots;
import nz.ac.vuw.ecs.swen225.gp22.domain.Entity;
import nz.ac.vuw.ecs.swen225.gp22.domain.Exit;
import nz.ac.vuw.ecs.swen225.gp22.domain.InfoField;
import nz.ac.vuw.ecs.swen225.gp22.domain.Key;
import nz.ac.vuw.ecs.swen225.gp22.domain.Level;
import nz.ac.vuw.ecs.swen225.gp22.domain.Player;
import nz.ac.vuw.ecs.swen225.gp22.domain.Point;
import nz.ac.vuw.ecs.swen225.gp22.domain.Teleporter;
import nz.ac.vuw.ecs.swen225.gp22.domain.Treasure;
import nz.ac.vuw.ecs.swen225.gp22.persistency.Levels;

/**
 * All tests that directly test the Domain module (currently 90% coverage)
 * 
 * @author Linda Zhang 300570498
 */
class DomainTests {

	@Test void initialiseLevel() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#', '#', '#' ,'#' ,'#', '#', '#'},
				{'#', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
				{'#', '.', '#', '.', '.', '.', '#', '.', '.', '#'},
				{'#', '.', '#', '.', 's', '.', '.', '.', '.', '#'},
				{'#', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
				{'#', '.', '.', '.', 'R', '.', '#', '#', '.', '#'},
				{'#', '.', '.', '.', '.', '.', '#', '.', 'X', '#'},
				{'#', '#', '#', '#', '#', '#' ,'#' ,'#', '#', '#'}
		};
		Set<Entity> entities = Set.of(new Key(new Point(1,1),'R'), 
										new Treasure(new Point(1,2)), 
										new InfoField(new Point(1,3), ""),
										new Exit(new Point(1,4)));
		Player p = new Player(new Point(4,3), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		l.getPlayer().setInventory(Set.of());
		
		assert l.getPlayer().getPos().equals(l.getCells().getSpawn());
		
		assert map.length == l.getCells().getMaxY(); //check Y boundary
		for(int y = 0; y < map.length; y++) assert map[y].length == l.getCells().getMaxX(); //check X boundaries
		assert mapsMatch(map, l.getCells().toMap()); //check map
		assert entities.equals(l.getPlayer().entitiesOnBoard()); //check entities
		assert entities.stream().toList().equals(l.getEntites()); //check entities toList
		
		
		l.getCells().get(-1,0).state(); //point not on map is allowed
		l.getCells().get(0,-1).state();
		l.getCells().get(l.getCells().getMaxX()+1,0).state();
		l.getCells().get(0,l.getCells().getMaxY()+1).state();
		
		assert l.getPlayer().direction() == Direction.None; //player does not move
		l.tick(); //tick level without moving
		l.gameOver(); //stop music looping
	}
	
	@Test void moveOnceLegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = Set.of();
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos
		
		assert l.getPlayer().getPos().equals(new Point(2,1));
		assert l.getPlayer().getOldPos().equals(new Point(1,1));
		
		l.tick();
		l.gameOver();

	}
	
	@Test void moveOnceIllegal1() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = Set.of();
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Left);
		try { l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to illegal pos
		}catch(IllegalArgumentException e) {}
		
		l.tick();
		l.gameOver();

	}
	@Test void moveOnceIllegal2() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'L', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = Set.of();
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Left);
		try { l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to illegal pos
		}catch(IllegalArgumentException e) {}
		
		l.tick();
		l.gameOver();

	}
	
	@Test void moveOnceIllegal3() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'X', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = Set.of();
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Left);
		try { l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to illegal pos
		}catch(IllegalArgumentException e) {}
		
		l.tick();
		l.gameOver();

	}
	
	@Test void moveToExitLegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Exit exit = new Exit(new Point(2,1));
		entities.add(exit);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on exit
		
		assert l.getPlayer().getPos().equals(new Point(2,1));
		assert l.getPlayer().getOldPos().equals(new Point(1,1));
		
		entities.stream().forEach(e -> e.onInteraction(l.getPlayer(), l.getCells())); //should not throw exception
		l.tick(); //should call gameOver

	}
	
	@Test void moveToExitIllegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		entities.add(new Exit(new Point(2,1)));
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		try {
			entities.stream().forEach(e -> e.onInteraction(l.getPlayer(), l.getCells())); //player not on exit
		}catch(IllegalStateException e) {}
		
		l.tick(); //should not call gameOver
		l.gameOver();
	}
	
	@Test void moveToInfoFieldLegal1() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		InfoField i = new InfoField(new Point(2,1), "");
		entities.add(i);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on InfoField
		
		assert l.getPlayer().getPos().equals(new Point(2,1));
		assert l.getPlayer().getOldPos().equals(new Point(1,1));
		
		l.tick();
		
		assert l.getPlayer().getActiveInfoField().get() == i; //should make activeInfoField i
		
		l.getPlayer().direction(Direction.Left);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to back from InfoField
		
		assert l.getPlayer().getPos().equals(new Point(1,1));
		assert l.getPlayer().getOldPos().equals(new Point(2,1));
		
		l.tick(); //should make activeInfoPos null
		assert l.getPlayer().getActiveInfoField().isEmpty();
		
		l.gameOver();
	}
	
	@Test void moveToInfoFieldLegal2() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		InfoField i = new InfoField(new Point(2,1), "");
		entities.add(i);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on InfoField
		
		assert l.getPlayer().getPos().equals(new Point(2,1));
		assert l.getPlayer().getOldPos().equals(new Point(1,1));
		
		l.tick();
		assert l.getPlayer().getActiveInfoField().get() == i; //should make activeInfoField i
		
		l.tick(); //tick again to check that infoField is only displayed once
		assert l.getPlayer().getActiveInfoField().get() == i;
		
		l.gameOver();
	}
	
	@Test void moveToInfoFieldIllegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		InfoField i = new InfoField(new Point(2,1), "");
		entities.add(i);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		try {
			entities.stream().forEach(e -> e.onInteraction(l.getPlayer(), l.getCells())); //player not on InfoField
		}catch(IllegalStateException e) {}
		
		assert l.getPlayer().getActiveInfoField().isEmpty();
		
		l.tick();
		l.gameOver();
	}
	
	@Test void moveToRedKeyLegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'B', 'G', '#', '#'},
				{'s', '.', '.', 'R'},
				{'.', '.', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Key key = new Key(new Point(1,1), 'R');
		entities.add(key);
		entities.add(new Key(new Point(0,2), 'B'));
		entities.add(new Key(new Point(1,2), 'G'));
		Player p = new Player(new Point(0,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);

		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on Key
		
		l.tick(); //call onInteraction for the key
		
		assert l.getPlayer().inventory().contains(key); //key added to inventory
		assert entities.size() == 2; //key removed from entities
		assert mapsMatch(map,l.getCells().toMap()); //lockedDoor not removed yet
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move legal pos
		assert mapsMatch(map,l.getCells().toMap()); //lockedDoor not removed yet
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on lockedDoor
		char[][] newMap = {
				{'B', 'G', '#', '#'},
				{'s', '.', '.', '.'},
				{'.', '.', '#', '#'}
		};
		assert mapsMatch(newMap, l.getCells().toMap()); //locked door is now a floor tile
		assert l.getPlayer().inventory().isEmpty(); //key removed frm inventory
	}
	
	@Test void moveToBlueKeyLegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'B', 'G', '#', '#'},
				{'s', '.', '.', 'B'},
				{'.', '.', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Key key = new Key(new Point(1,1), 'B');
		entities.add(key);
		entities.add(new Key(new Point(0,2), 'B'));
		entities.add(new Key(new Point(1,2), 'G'));
		Player p = new Player(new Point(0,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);

		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on Key
		
		l.tick(); //call onInteraction for the key
		
		assert l.getPlayer().inventory().contains(key); //key added to inventory
		assert entities.size() == 2; //key removed from entities
		assert mapsMatch(map,l.getCells().toMap()); //lockedDoor not removed yet
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move legal pos
		assert mapsMatch(map,l.getCells().toMap()); //lockedDoor not removed yet
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on lockedDoor
		char[][] newMap = {
				{'B', 'G', '#', '#'},
				{'s', '.', '.', '.'},
				{'.', '.', '#', '#'}
		};
		assert mapsMatch(newMap, l.getCells().toMap()); //locked door is now a floor tile
		assert l.getPlayer().inventory().isEmpty(); //key removed frm inventory
	}
	
	@Test void moveToYellowKeyLegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'Y', 'G', '#', '#'},
				{'s', '.', '.', 'Y'},
				{'.', '.', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Key key = new Key(new Point(1,1), 'Y');
		entities.add(key);
		entities.add(new Key(new Point(0,2), 'Y'));
		entities.add(new Key(new Point(1,2), 'G'));
		Player p = new Player(new Point(0,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);

		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on Key
		
		l.tick(); //call onInteraction for the key
		
		assert l.getPlayer().inventory().contains(key); //key added to inventory
		assert entities.size() == 2; //key removed from entities
		assert mapsMatch(map,l.getCells().toMap()); //lockedDoor not removed yet
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move legal pos
		assert mapsMatch(map,l.getCells().toMap()); //lockedDoor not removed yet
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on lockedDoor
		char[][] newMap = {
				{'Y', 'G', '#', '#'},
				{'s', '.', '.', '.'},
				{'.', '.', '#', '#'}
		};
		assert mapsMatch(newMap, l.getCells().toMap()); //locked door is now a floor tile
		assert l.getPlayer().inventory().isEmpty(); //key removed frm inventory
	}
	
	@Test void moveToGreenKeyLegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'B', 'R', '#', '#'},
				{'s', '.', 'G', 'G'},
				{'.', '.', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Key key = new Key(new Point(1,1), 'G');
		entities.add(key);
		entities.add(new Key(new Point(0,2), 'B'));
		entities.add(new Key(new Point(1,2), 'R'));
		Player p = new Player(new Point(0,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);

		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on Key
		
		l.tick(); //call onInteraction for the key
		
		assert l.getPlayer().inventory().contains(key); //key added to inventory
		assert entities.size() == 2; //key removed from entities
		assert mapsMatch(map,l.getCells().toMap()); //lockedDoor not removed yet
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on lockedDoor
		char[][] newMap = {
				{'B', 'R', '#', '#'},
				{'s', '.', '.', 'G'},
				{'.', '.', '#', '#'}
		};
		
		assert mapsMatch(newMap,l.getCells().toMap()); //first door removed
		assert !l.getPlayer().inventory().isEmpty(); //key should not be removed from inventory (green key!)
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on another lockedDoor
		char[][] newMap2 = {
				{'B', 'R', '#', '#'},
				{'s', '.', '.', '.'},
				{'.', '.', '#', '#'}
		};
		assert mapsMatch(newMap2, l.getCells().toMap()); //secodn door removed
		assert l.getPlayer().inventory().isEmpty(); //key removed from inventory after all green doors are gone
	}
	
	@Test void moveToRedKeyIllegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'B', 'G', '#', '#'},
				{'s', '.', 'R', 'R'},
				{'.', '.', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Key key = new Key(new Point(1,1), 'R');
		entities.add(key);
		entities.add(new Key(new Point(0,2), 'B'));
		entities.add(new Key(new Point(1,2), 'G'));
		Player p = new Player(new Point(0,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);

		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on Key
		
		l.tick(); //call onInteraction for the key
		
		assert l.getPlayer().inventory().contains(key); //key added to inventory
		assert entities.size() == 2; //key removed from entities
		assert mapsMatch(map,l.getCells().toMap()); //lockedDoor not removed yet
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move legal pos
		char[][] newMap = {
				{'B', 'G', '#', '#'},
				{'s', '.', '.', 'R'},
				{'.', '.', '#', '#'}
		};
		assert mapsMatch(newMap,l.getCells().toMap()); //lockedDoor removed
		assert l.getPlayer().inventory().isEmpty(); //key removed from inventory
		
		l.getPlayer().direction(Direction.Right);
		try {
			l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //key no longer in inventory so illegal
		}catch(IllegalArgumentException e) {}
	}
	
	@Test void moveToKeyIllegalNotOnKey() {
	Runnable next = () -> {};
	Runnable die = () -> {};
	
	char[][] map = {
			{'#', '#', '#', '#'},
			{'#', 's', '.', '#'},
			{'#', '#', '#', '#'}
	};
	Set<Entity> entities = new HashSet<>();
	Key key = new Key(new Point(2,1),'R');
	entities.add(key);
	Player p = new Player(new Point(1,1), entities);
	Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
	
	try {
		entities.stream().forEach(e -> e.onInteraction(l.getPlayer(), l.getCells())); //player not on Key
	}catch(IllegalStateException e) {}
	
	assert l.getPlayer().inventory().isEmpty();
}
	
	@Test void moveToTreasureLegal1() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', 'X'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Treasure t = new Treasure(new Point(2,1));
		entities.add(t);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on Treasure
		
		assert l.getPlayer().getPos().equals(new Point(2,1));
		assert l.getPlayer().getOldPos().equals(new Point(1,1));
		
		l.tick(); 
		assert l.getPlayer().treasuresToCollect() == 0; //no more treasures to collect
		assert entities.isEmpty(); //treasure removed from entities
		assert !mapsMatch(map,l.getCells().toMap()); //cells should now be different
		char[][] newMap = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '.'},
				{'#', '#', '#', '#'}
		};
		assert mapsMatch(newMap, l.getCells().toMap()); //exit lock is unlocked when treasure is picked up
		l.gameOver();
	}
	
	@Test void moveToTreasureLegal2() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'s', '.', '.', 'X'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		entities.add(new Treasure(new Point(1,1)));
		entities.add(new Treasure(new Point(2,1)));
		Player p = new Player(new Point(0,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on Treasure
		
		assert l.getPlayer().getPos().equals(new Point(1,1));
		assert l.getPlayer().getOldPos().equals(new Point(0,1));
		
		l.tick(); 
		assert l.getPlayer().treasuresToCollect() == 1; //1 more treasures to collect
		assert entities.size() == 1; //treasure removed from entities
		assert mapsMatch(map,l.getCells().toMap()); //cells should now be different
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on Treasure
		
		assert l.getPlayer().getPos().equals(new Point(2,1));
		assert l.getPlayer().getOldPos().equals(new Point(1,1));
		
		l.tick(); 
		assert l.getPlayer().treasuresToCollect() == 0; //no more treasures to collect
		assert entities.isEmpty(); //treasure removed from entities
		assert !mapsMatch(map,l.getCells().toMap()); //cells should now be different
		char[][] newMap = {
				{'#', '#', '#', '#'},
				{'s', '.', '.', '.'},
				{'#', '#', '#', '#'}
		};
		assert mapsMatch(newMap, l.getCells().toMap()); //exit lock is unlocked when treasure is picked up
		l.gameOver();
	}
	
	@Test void moveToTreasureIllegalNotOnTreasure() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Treasure t = new Treasure(new Point(2,1));
		entities.add(t);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		try {
			entities.stream().forEach(e -> e.onInteraction(l.getPlayer(), l.getCells())); //player not on Treasure
		}catch(IllegalStateException e) {}
		
		assert l.getPlayer().treasuresToCollect() == 1;
		
		l.tick();
		l.gameOver();
	}
	
	@Test void moveToTreasureIllegalNoExitLock() {
		Runnable next = () -> {};
		Runnable die = () -> {};

		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Treasure t = new Treasure(new Point(2,1));
		entities.add(t);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move to legal pos on Treasure
		
		assert l.getPlayer().getPos().equals(new Point(2,1));
		assert l.getPlayer().getOldPos().equals(new Point(1,1));
		
		try {
			l.tick(); //no exit lock on board
		}catch(IllegalStateException e) {}
		l.gameOver();
	}
	
	
	
	@Test void initialiseWithMonster() {
		Runnable next = () -> {};
		Runnable die = () -> {};

		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = Set.of();
		Player p = new Player(new Point(1,1), entities);
		Level l;
		try {
			l = new Level(next, die, map,entities, 1, List.of(Levels.createTestMonster("level2", new Point(2,1), List.of())), 60, p);
			assert l.getLevelNum() == 1;
			assert l.getMonsters().get(0).getRoute().equals(List.of());
			
			assert l.getPlayer().getPos().equals(new Point(1,1));
			assert l.getMonsters().get(0).getPos().equals(new Point(2,1));
			
			try {
				l.getMonsters().get(0).move(Direction.Down, l.getCells()); //moving monster to solid block should throw error
			}catch(IllegalArgumentException e) {}
			
			l.getMonsters().get(0).move(Direction.Left, l.getCells()); //moves Monster onto player
			
			assert l.getPlayer().getPos().equals(new Point(1,1));
			assert l.getMonsters().get(0).getPos().equals(new Point(1,1));
			assert l.getMonsters().get(0).getOldPos().equals(new Point(2,1));
			
			l.tick(); //calls playerDiesGameOver
			l.playerDiesGameOver();
		} catch (MalformedURLException e1) {}
	}
	
	@Test void moveMonsterWithRoute() {
		Runnable next = () -> {};
		Runnable die = () -> {};

		char[][] map = {
				{'#', '#', '#', '#'},
				{'s', '.', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = Set.of();
		Player p = new Player(new Point(1,1), entities);
		Level l;
		try {
			l = new Level(next, die, map,entities, 1, 
					List.of(Levels.createTestMonster("level2", new Point(2,1), List.of(Direction.Left,Direction.Right))), 60, p);
			assert l.getPlayer().getPos().equals(new Point(1,1));
			assert l.getMonsters().get(0).getPos().equals(new Point(2,1));
			
			l.tick(); //route moves monster left
			
			assert l.getMonsters().get(0).getPos().equals(new Point(1,1));
			assert l.getMonsters().get(0).getOldPos().equals(new Point(2,1));
			
			l.getMonsters().get(0).move(l.getMonsters().get(0).getRoute().get(1), l.getCells()); //move monster right
			
			assert l.getMonsters().get(0).getPos().equals(new Point(2,1));	
			assert l.getMonsters().get(0).getOldPos().equals(new Point(1,1));	
		} catch (MalformedURLException e) {}
	}
	
	@Test void moveMonsterWithRouteIllegal1() {
		Runnable next = () -> {};
		Runnable die = () -> {};

		char[][] map = {
				{'#', '#', '#', '#'},
				{'s', '.', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = Set.of();
		Player p = new Player(new Point(1,1), entities);
		try {
			new Level(next, die, map,entities, 1, 
					List.of(Levels.createTestMonster("level2", new Point(2,1), List.of(Direction.Left))), 60, p);
		}catch(IllegalArgumentException | MalformedURLException e) {}
	}
	
	@Test void moveMonsterWithRouteIllegal2() {
		Runnable next = () -> {};
		Runnable die = () -> {};

		char[][] map = {
				{'#', '#', '#', '#'},
				{'s', '.', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = Set.of();
		Player p = new Player(new Point(1,1), entities);
		try {
			new Level(next, die, map,entities, 1, 
					List.of(Levels.createTestMonster("level2", new Point(2,1), List.of(Direction.Left))), 60, p);
		}catch(IllegalArgumentException | MalformedURLException e) {}
	}
	
	@Test void playerDiesOnWater() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', 'w', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = Set.of();
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		l.getCountdown();
		assert l.getPlayer().getPos().equals(new Point(1,1));
		
		l.getPlayer().move(Direction.Right, l.getCells()); //move player on water
		
		assert l.getPlayer().getPos().equals(new Point(2,1));
		
		l.tick(); //calls playerDiesGameOver
		l.playerDiesGameOver();
	}
	
	@Test void moveToTeleporterLegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		char[][] map = {
				{'#', '#', '#', '#', '#', '#' ,'#' ,'#', '#', '#'},
				{'#', 's', '.', '.', '.', '.', '.', '.', '.', '#'},
				{'#', '.', '#', '.', '.', '.', '#', '.', '.', '#'},
				{'#', '.', '#', '.', '.', '.', '.', '.', '.', '#'},
				{'#', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
				{'#', '.', '.', '.', 'L', '.', '#', '#', '.', '#'},
				{'#', '.', '.', '.', '.', '.', '#', '.', 'X', '#'},
				{'#', '#', '#', '#', '#', '#' ,'#' ,'#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Teleporter t1 = new Teleporter(new Point(2,1), null);
		Teleporter t2 = new Teleporter(new Point(4,1), null);
		t1.setOther(t2);
		t2.setOther(t1);
		entities.add(t1);
		entities.add(t2);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move player on t1
		
		l.tick(); //calls onInteraction to teleport player
		
		assert l.getPlayer().getPos().equals(t2.getPos()); //player should land on other
		
		l.tick();
		l.gameOver();
	}
	
	@Test void moveToTeleporterIllegal1() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', 'w', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Teleporter t1 = new Teleporter(new Point(2,1), null);
		entities.add(t1);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move player on t1
		
		try {
			l.tick(); //calls onInteraction to teleport player. should fail bc other is null
		}catch(IllegalArgumentException e) {}
	}
	
	@Test void moveToTeleporterIllegal2() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', 'w', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Teleporter t1 = new Teleporter(new Point(2,1), null);
		t1.setOther(new Teleporter(new Point(0,0),null)); //teleporter that is not in entities
		entities.add(t1);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move player on t1
		
		try {
			l.tick(); //calls onInteraction to teleport player. should fail bc other is not in entities
		}catch(IllegalArgumentException e) {}
	}
	
	@Test void moveToTeleporterIllegal3() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', 'w', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Teleporter t1 = new Teleporter(new Point(2,1), null);
		Teleporter t2 = new Teleporter(new Point(4,1), null);
		t1.setOther(t2);
		t2.setOther(new Teleporter(new Point(3,1), null)); //other of t1 does not have t1 has its other
		entities.add(t1);
		entities.add(t2);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move player on t1
		
		try {
			l.tick(); //calls onInteraction to teleport player. should fail bc others don't match
		}catch(IllegalArgumentException e) {}
	}
	
	@Test void moveToTeleporterIllegal4() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		
		char[][] map = {
				{'#', '#', '#', '#'},
				{'#', 's', '.', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();;
		Teleporter t1 = new Teleporter(new Point(2,1), null);
		entities.add(t1);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		try {
			entities.stream().forEach(e -> e.onInteraction(l.getPlayer(), l.getCells())); //player not on Teleporter
		}catch(IllegalStateException e) {}
	}
	
	@Test void moveToTeleporterIllegal5() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		char[][] map = {
				{'#', '#', '#', '#', '#', '#' ,'#' ,'#', '#', '#'},
				{'#', 's', '.', '.', '.', '#', '.', '.', '.', '#'},
				{'#', '.', '#', '.', '.', '.', '#', '.', '.', '#'},
				{'#', '.', '#', '.', '.', '.', '.', '.', '.', '#'},
				{'#', '.', '.', '.', '.', '.', '.', '.', '.', '#'},
				{'#', '.', '.', '.', 'L', '.', '#', '#', '.', '#'},
				{'#', '.', '.', '.', '.', '.', '#', '.', 'X', '#'},
				{'#', '#', '#', '#', '#', '#' ,'#' ,'#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Teleporter t1 = new Teleporter(new Point(2,1), null);
		Teleporter t2 = new Teleporter(new Point(4,1), null);
		t1.setOther(t2);
		t2.setOther(t1);
		entities.add(t1);
		entities.add(t2);
		Player p = new Player(new Point(1,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move player on t1
		
		try {
			l.tick(); //calls onInteraction to teleport player. should fail bc the landing pos is solid
		}catch(IllegalStateException e) {}
	}
	
	@Test void moveToBootsLegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		char[][] map = {
				{'#', '#', '#', '#'},
				{'s', '.', 'w', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Boots b = new Boots(new Point(1,1));
		entities.add(b);
		Player p = new Player(new Point(0,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move player on boots
		
		l.tick(); //calls onInteraction for boots
		
		assert l.getPlayer().inventory().contains(b);
		
		l.getPlayer().direction(Direction.Right);
		l.getPlayer().move(l.getPlayer().direction(), l.getCells()); //move player on water. Should be legal
		
		assert l.getPlayer().getPos().equals(new Point(2,1));
		l.tick();
	}
	
	@Test void moveToBootsIllegal() {
		Runnable next = () -> {};
		Runnable die = () -> {};
		char[][] map = {
				{'#', '#', '#', '#'},
				{'s', '.', 'w', '#'},
				{'#', '#', '#', '#'}
		};
		Set<Entity> entities = new HashSet<>();
		Boots b = new Boots(new Point(1,1));
		entities.add(b);
		Player p = new Player(new Point(0,1), entities);
		Level l = new Level(next,die,map,entities, 1, List.of(),60,p);
		
		try {
			entities.stream().forEach(e -> e.onInteraction(l.getPlayer(), l.getCells())); //player not on Teleporter
		}catch(IllegalStateException e) {}
	}
	
	//helper method to check if the map of cells match
	private boolean mapsMatch(char[][] map, char[][] cellMap) {
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				if(map[y][x] != cellMap[y][x]) return false;
			}
		}
		return true;
	}

}
