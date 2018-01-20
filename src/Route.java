/** Class representing one entry in a routing table. */
public class Route {
  /** Create a new route.
  *   @param dest The destination.
  *   @param nextHop The directly-connected neighbor that is next on the
  *   path to the destination.
  *   @param cost The cost of the route.
  */
  public Route(int dest, int nextHop, int cost) {
    this.dest= dest;
    this.nextHop = nextHop;
    this.cost = cost;
  }
  /** Get the address of the destination for this route. */
  public final int getDest() { return dest; }
  /** Get the next hop on this route. */
  public final int getNextHop() { return nextHop; }
  /** Get the cost (typically the number of hops) for this route. */
  public final int getCost() { return cost; }
  
  /** The destination. */
  private int dest;
  /** The next hop on the path to the destination. */
  private int nextHop;
  /** The cost (typically the number of hops) to get to the destination. */
  private int cost;
}
