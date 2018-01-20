/** A subclass of Event representing link state changes.
*   @see Event
*/
public class LinkEvent extends Event {
  /** Create a new link state event.
  *   @param time The time at which the event will occur.
  *   @param link The link that will be brought up or down.
  *   @param up <b>true</b> if the link is being brought up.
  */
  public LinkEvent(double time, Link link, boolean up) {
    super(time);
    this.link = link;
    this.up = up;
  }
  /** Run the event (bring the link up or down). */
  public void run() {
    Network.linkSet(link, up);
  }
  /** Returns a string representation.  For example, <code>1-&gt;2
  * down</code>
  */
  public String toString() {
    return getTime() + ": " + link.getEndpointA() + "->" +
      link.getEndpointB() + " " + (up ? "up" : "down");
  }

  /** The link being brought up or down. */
  private Link link;
  /** <b>true</b> if the link is being brought up. */
  private boolean up;
}
