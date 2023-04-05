package nz.ac.vuw.ecs.swen225.gp22.domain;

import java.util.Optional;

/**
 * Classes that extend this class can be used to call onChange. Helps to remove
 * unwanted dependencies (Observer Pattern).
 *
 * @author Linda Zhang 300570498
 */
public abstract class Subject {
  /**
   * The Optional Observer in the subject for Observer Pattern.
   */
  public Optional<Observer> ob = Optional.empty();

  /**
   * Update the observer.
   */
  public void onChange() {
    ob.ifPresent(o -> o.update());
  }

  /**
   * Attatch the Observer.
   *
   * @param o set observer
   */
  public void attach(Observer o) {
    ob = Optional.of(o);
  }

  /**
   * Deattach the Observer.
   */
  public void detatch() {
    ob = Optional.empty();
  }
}