import java.util.Enumeration;
import java.util.Vector;

/** A class to represent a node's routing table. */
public class RoutingTable {
  /** Create a new, empty routing table. */
  public RoutingTable() {
    routes = new Vector();
  }
  /** Add a route to the table.  This function does not check for
  *   duplicate routes or routes for which the nextHop is not an available
  *   neighbor.
  *   @param r The route to add.
  */
  public void add(Route r) {
    routes.addElement(r);
  }
  /** Remove the route for the given destination from the table.  If the
  *   routing table is internally consistent, only one route will have
  *   the given destination.
  *   @param dest The destination for the route to remove.
  */
  public void remove(int dest) {
    int i = findRouteIndex(dest);
    if (i != -1) {
      routes.removeElementAt(i);
    }
  }
  /** Remove all routes in the table. */
  public void flush() {
    routes.removeAllElements();
  }
  /** Find the route for the given destination.  If the routing table is
  *   internally consistent, only one route will match.
  *   @param dest The destination to search for.
  */
  public Route findRoute(int dest) {
    int i = findRouteIndex(dest);
    return (i == -1) ? null : (Route)routes.elementAt(i);
  }
  /** Find the first route with the given next hop.  There may be many
  *   routes with the given next hop; this function returns only the
  *   first.
  *   @param nextHop The value of nextHop to search for.
  */
  public Route findRouteByNextHop(int nextHop) {
    for (int i=0; i<routes.size(); i++) {
      if (((Route)(routes.elementAt(i))).getNextHop() == nextHop) {
        return (Route)routes.elementAt(i);
      }
    }
    return null;
  }
  /** Return an Enumeration representing all routes in the table. */
  public Enumeration enumerate() {
    return routes.elements();
  }
  /** Find the index in the routing table of the route for the given
  *   destination.  If the routing table is internally consistent, only
  *   one route will match.
  *   @param dest The destination to search for.
  */
  private int findRouteIndex(int dest) {
    for (int i=0; i<routes.size(); i++) {
      if (((Route)(routes.elementAt(i))).getDest() == dest) {
        return i;
      }
    }
    return -1;
  }
  
  /** The array representing the actual routes.  Not in any particular
  *   order.
  */
  private Vector routes;
}
