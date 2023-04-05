package nz.ac.vuw.ecs.swen225.gp22.recorder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import nz.ac.vuw.ecs.swen225.gp22.global.Direction;

/**
 * @author Quinten Smit 300584150
 *
 */
public class Replay extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1874411741562700245L;
	Element replayElement;
	int time;
	int speed = 3;
	boolean end;
	Consumer<Direction> movePlayer;
	Runnable advanceByTick;
	Timer timer = null;
	
	
	/**
	 * Loads a Recording from a file
	 * @param fileName The name of the file to load from
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public Replay(String fileName) throws JDOMException, IOException {
		replayElement = ((Document) (new SAXBuilder()).build(new File(fileName))).getRootElement();
		time = Integer.parseInt(replayElement.getChild("time").getText());
		JButton step = new JButton("Advance By Step");
		step.addActionListener(e->advanceByTick());
		
		JButton autoReplay = new JButton("Play");
		autoReplay.addActionListener(e->startRunning());
		
		JSlider replaySpeed = new JSlider(0,10, speed);
		replaySpeed.addChangeListener((e)->timer.setDelay((int) Math.pow(10 - (speed=((JSlider)e.getSource()).getValue()),2)));
		
		JButton pause = new JButton("Pause");
		pause.addActionListener(e->stop());
		
		add(step);
		add(autoReplay);
		add(replaySpeed);
		add(pause);
		
		timer = new Timer((int)Math.pow(10 - speed,2),unused->{
		      assert SwingUtilities.isEventDispatchThread();
		      advanceByTick();
		});
	}
	
	/**
	 * @param movePlayer The consumer for the player movement
	 */
	public void setController(Consumer<Direction> movePlayer) {
		this.movePlayer = movePlayer;
	}
	
	/**
	 * @param advanceByTick the runnable that will advance the game by one tick
	 */
	public void setAdvanceByTick(Runnable advanceByTick) {
		this.advanceByTick = advanceByTick;
	}
	
	/**
	 * Advance the replay by one tick until it reaches the end
	 */
	public void advanceByTick() {
		if (!end) {
			advanceByTick.run();
			movePlayer();
			if (time >= findEndTime()) {
				end = true;
				if (timer != null)timer.stop();
			}
		}
	}
	
	/**
	 * Start running the replay 
	 */
	public void startRunning() {
		timer.start();
	}
	
	/**
	 * pause/stop the game
	 */
	public void stop() {
		timer.stop();
	}
	
	/**
	 * @return The the filename of the map that was loaded
	 */
	public String getLevelPath() {
		return replayElement.getChild("map").getText();
	}
	
	/**
	 * Returns the formated XML String
	 */
	public String toString() {
		return new XMLOutputter(Format.getPrettyFormat()).outputString(replayElement);
	}
	
	/**
	 * @return  if the move was stored in the recorder
	 * 
	 */
	private boolean movePlayer() {
		List<Element> eventList = replayElement.getChild("events").getChildren("event");
		Optional<Element> event = eventList.stream()
										   .filter((e)->(Integer.parseInt(e.getAttribute("time").getValue()) == time))
										   //.filter((e)->e.getChildText("actor").equals("player"))
										   .findFirst();
		if (event.isEmpty())return false;
		Direction direction = Direction.valueOf(event.get().getChildText("action"));
		movePlayer.accept(direction);
		time++;
		return true;
	}
	
	/**
	 * The last time recording
	 * @return the time of the last event
	 */
	private int findEndTime() {
		List<Element> eventList = replayElement.getChild("events").getChildren("event");
		if (eventList.isEmpty())return 0;
		return Integer.parseInt(eventList.get(eventList.size()-1).getAttributeValue("time"));
	}
}
