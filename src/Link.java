/** A class representing a link between two nodes. */
public class Link {
  /** Create a new link.
  *   @param nodeA The address of one endpoint.
  *   @param nodeB The address of the other endpoint.
  *   @param speed The speed, in bits per second.
  *   @param latency The latency, in seconds.
  */
  public Link(int nodeA, int nodeB, int speed, double latency, int cost) {
    this.nodeA = nodeA;
    this.nodeB = nodeB;
    this.speed = speed;
    this.latency = latency;
    this.cost = cost;
    up = true;
  }

  /** Returns the time required to transmit a packet of a given size over
  *   this link.
  *   @param bytes The size of the packet, in bytes.
  */
  public double timeToTransmit(int bytes) {
    return latency + bytes*8/(double)speed;
  }
  /** Returns the address of the first endpoint. */
  public final int getEndpointA() { return nodeA; }
  /** Returns the address of the second endpoint. */
  public final int getEndpointB() { return nodeB; }
  /** Returns one endpoint given the other. */
  public final int getDest(int addr) {
    if (addr == nodeA)
      return nodeB;
    else if (addr == nodeB)
      return nodeA;
    else
      return -1;
  }
  /** Returns the per-packet cost of using the link. */
  public final int getCost() { return cost; }
  /** Returns <b>true</b> if the link is up. */
  public final boolean isUp() { return up; }
  /** Brings the link up. */
  public final void up() { up = true; }
  /** Brings the link down*/
  public final void down() { up = false; }

  /** The addresses of the endpoints. */
  private int nodeA, nodeB;
  /** The link speed, in bits per second. */
  private int speed;
  /** The link latency, in seconds. */
  private double latency;
  /** The per-packet cost of using the link. */
  private int cost;
  /** Whether or not the link is up. */
  private boolean up;
}
