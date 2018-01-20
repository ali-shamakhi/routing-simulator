import java.util.Enumeration;
import java.lang.String;
import java.util.Vector;

/** Base functionality for a node.  This class does not implement any
*   routing functionality, and cannot be instantiated directly.
*/
public abstract class NodeBase {
  /** Create a new node with the given address. */
  public NodeBase(int address) {
    this.address = address;
    interfaces = new Vector();
    routingTable = new RoutingTable();
  }
  /** Get the node's address.  */
  public final int getAddress() {
    return address;
  }
  /** Send a packet.
  *   @param dest The address of the node that will receive the packet.
  *   @param pkt The packet to send.
  */
  public void sendPacket(int dest, String pkt) {
    Network.sendPacket(getAddress(), dest, pkt);
  }

  /** Add a new interface. */
  public void addInterface(Link l) {
    interfaces.addElement(l);
  }
  /** Return an enumeration of the interfaces. */
  public Enumeration getInterfaces() {
    return interfaces.elements();
  }

  /** Return an enumeration of the routes in the routing tables. */
  public Enumeration getRoutingTable() {
    return routingTable.enumerate();
  }

  /** @see Node */
  public abstract void init();
  /** @see Node */
  public abstract void interfaceUp(Link lnk);
  /** @see Node */
  public abstract void interfaceDown(Link lnk);
  /** @see Node */
  public abstract void receivePacket(int source, String pkt);

  /** The numeric address of the node. */
  private int address;
  /** A table of routes, maintained by init, interfaceUp, interfaceDown,
  *   and receivePacket. */
  protected RoutingTable routingTable;
  /** A table of interfaces.  The node never needs to change these
  * directly. */
  protected Vector interfaces;
}
