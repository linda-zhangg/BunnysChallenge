package nz.ac.vuw.ecs.swen225.gp22.src.app;

import javax.swing.SwingUtilities;

/**
 * Main.
 *
 * @author lukepiper
 */
public class Main {
  /**
   * Invoke new Chip.
   *
   * @param a - String[]
   */
  public static void main(String[] a) {
    SwingUtilities.invokeLater(Chips::new);
  }
}
