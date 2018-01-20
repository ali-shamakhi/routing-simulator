/** A class representing an event for the simulator to handle.
*   This class cannot be instantiated directly.
*   @see PacketEvent
*   @see LinkEvent
*/
public abstract class Event {
  /** Create a new event.
  *   @param time The time that the event will occur
  */
  public Event(double time) {
    this.time = time;
  }
  /** Called when the event's time is arrived to trigger the event. */
  public abstract void run();
  /** Returns the event's trigger time.  */
  public double getTime() {
    return time;
  }

  /** The event's trigger time. */
  private double time;
}
