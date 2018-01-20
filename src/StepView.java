import java.awt.*;
import java.awt.event.*;

/** A GUI widget to control the simulation.  The display consists of the
*   current time (the time of the next event), a Step button, and a Quit
*   button.
*/
public class StepView extends Frame {
  /** Create a new StepView widget.
  *   @param _simulatorMonitor The object which the simulator (the
  *   Network object) is wait()ing on.  We need this here so that we can
  *   notify() it every time the user hits the Step button.
  */
  public StepView(Object _simulatorMonitor) {
    simulatorMonitor = _simulatorMonitor;

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    time = new Label("Time: 0.00000 sec (start)");
    gbc.gridx = gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(time, gbc);

    stepTil = new Button("Step until");
    stepTil.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        try {
          stopAt = Double.valueOf(stopAtEntry.getText()).doubleValue();
          synchronized (simulatorMonitor) { simulatorMonitor.notify(); }
        }
        catch (NumberFormatException e) {}
      }
    });
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    add(stepTil, gbc);

    stopAtEntry = new TextField("0.1", 5);
    gbc.gridx = 1;
    add(stopAtEntry, gbc);

    step = new Button("Next event");
    step.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stopAt = -1;
        synchronized (simulatorMonitor) { simulatorMonitor.notify(); }
      }
    });
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.gridy = 2;
    add(step, gbc);

    Button quit = new Button("Quit");
    quit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    gbc.gridy = 3;
    add(quit, gbc);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    setTitle("Routing Simulator: Control");
    pack();
  }
  /** Set the contents of the time display.
  *   @param contents What to put in the time display, typically a number,
  *   sometimes with the word "(done)".
  */
  public void setTime(String contents) {
    time.setText("Time: " + contents);
  }
  /** Disable the Step button because the simulation is now done. */
  public void disableStepping() {
    step.setEnabled(false);
    stepTil.setEnabled(false);
  }
  /** Get the time to run until */
  public double getStopAt() { return stopAt; }
  /** Set the value of the stopAtEntry */
  public void setStopAt(double when) {
    stopAtEntry.setText(Double.toString(when));
  }

  /** Widget to display the time. */
  private Label time;
  /** Button to step the simulation. */
  private Button step;
  /** Button to step the simulation until a given time. */
  private Button stepTil;
  /** Entry dictating what time the stepTil button proceeds until. */
  private TextField stopAtEntry;
  /** Object to notify() when the Step button is pressed. */
  private Object simulatorMonitor;
  /** The time the stepTil button proceeds until; -1 means "next event" */
  private double stopAt;
}
