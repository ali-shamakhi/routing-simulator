/** A subclass of Event representing packet deliveries.
*   @see Event
*/
public class PacketEvent extends Event {
  /** Create a new packet delivery event.
  *   @param time The time at which the event will occur.
  *   @param source The address from which the packet originated.
  *   @param dest The address to which the packet is going.
  *   @param pkt The packet being delivered.
  */
  public PacketEvent(double time, int source, int dest, String pkt) {
    super(time);
    this.source = source;
    this.dest = dest;
    this.pkt = pkt;
  }
  /** Run the event (deliver the packet). */
  public void run() {
    Network.deliver(source, dest, pkt);
  }
  /** Returns a string representation.  For example, <code>1-&gt;2:
  * foo</code>
  */
  public String toString() {
    return getTime() + ": " + source + "->" + dest + ": " + pkt;
  }

  /** The address of the node from which the packet originated. */
  private int source;
  /** The address of the node that will receive the packet. */
  private int dest;
  /** The packet being delivered. */
  private String pkt;
}
