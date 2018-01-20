import java.util.*;

/** A class representing a routing-capable node in the network. */
public class Node extends NodeBase {
  /** Create a new node with the given address. */
  public Node(int address) {
    super(address);
  }

  /** Called when the node "boots".  This function should initialize
  *   the node's routing table and send out appropriate boot-time packets.
  */
  public void init() {
    System.out.println("node " + getAddress() + " - initialized");
  }
  /** Called when one of the node's interfaces is brought up.
  */
  public void interfaceUp(Link lnk) {
    System.out.println("node " + getAddress() + " - interface to " +
      lnk.getDest(getAddress()) + " up");
  }
  /** Called when one of the node's interfaces is brought down.
  */
  public void interfaceDown(Link lnk) {
    System.out.println("node " + getAddress() + " - interface to " +
      lnk.getDest(getAddress()) + " down");
  }
  /** Called when the node receives a packet.
  *   @param source The node that sent the packet.
  *   @param pkt The packet itself.
  */
  public void receivePacket(int source, String pkt) {
    System.out.println("node " + getAddress() + " - received from " + source +
      ": \"" + pkt + "\"");
  }
}
