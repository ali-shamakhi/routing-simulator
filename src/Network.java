import java.awt.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;

/** A class representing an entire network to be simulated.
*   Since everything here is static, these are essentially scoped globals.
*   You may only run one network at a time.
*/
public class Network {
  /** Start the simulation.
  *   @param argv Command-line arguments.  argv[0] must be the name of a
  *   .net file to read.
  */
  public static void main(String[] argv) {
    nodesByName = new Hashtable();
    linksByName = new Hashtable();
    nodes = new Vector();
    nodeCoords = new Vector();
    links = new Vector();
    nodeNames = new Vector();
    now = 0.0;
    eventQueue = new EventQueue();

    readTopology(argv[0]);
    for (int i=0; i<nodes.size(); i++)
      ((Node)nodes.elementAt(i)).init();

    Frame outputFrame = new Frame();
    GridBagConstraints gbc = new GridBagConstraints();
    outputFrame.setLayout(new GridBagLayout());
    outputFrame.setTitle("Routing Simulator: Network Map");

    rtview = new RoutingTablesView(nodes.size());
    for (int i=0; i<nodes.size(); i++) {
      rtview.setName(i, (String)nodeNames.elementAt(i));
    }
    rtview.show();

    Node[] nodesArray = new Node[nodes.size()];
    nodes.copyInto(nodesArray);
    Link[] linksArray = new Link[links.size()];
    links.copyInto(linksArray);
    nmview = new NetworkMap(nodesArray, linksArray);
    for (int i=0; i<nodes.size(); i++) {
      nmview.setName(i, (String)nodeNames.elementAt(i));
      nmview.setLocation(i, (FPoint)nodeCoords.elementAt(i));
    }
    gbc.gridx = gbc.gridy = 0;
    gbc.weightx = gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    outputFrame.add(nmview, gbc);

    monitor = new Object();
    sview = new StepView(monitor);
    gbc.gridy = 1;
    sview.show();

    nodesByName = null;
    linksByName = null;
    nodeCoords = null;
    
    outputFrame.pack();
    outputFrame.show();
    simulate();
  }
  /** Send a packet (really queue a packet receive event).
  *   @param source The node sending.
  *   @param dest The node receiving.
  *   @param pkt The actual packet to be sent.
  */
  public static void sendPacket(int source, int dest, String pkt) {
    packetsSent++;
    Link l = findLinkByNodes(source, dest);
    PacketEvent pe = new PacketEvent(now+l.timeToTransmit(pkt.length()),
      source, dest, pkt);
    try { eventQueue.insert(pe); }
    catch (TimeInconsistencyException e) {
      System.out.println("Internal error: event occurs in the past.");
    }
  }

  /** Deliver a packet.  This is called by PacketEvent.run() only. */
  public static void deliver(int source, int dest, String pkt) {
    Link l = findLinkByNodes(source, dest);
    if (l.isUp())
      ((Node)nodes.elementAt(dest)).receivePacket(source, pkt);
  }

  /** Read in a topology file.
  *   @param filename The name of the file, typically ending in ".net"
  */
  private static void readTopology(String filename) {
    int line = 0;
    BufferedReader in;
    try {
      in = new BufferedReader(new FileReader(filename));
    }
    catch (FileNotFoundException e) {
      System.out.println(filename + ": file not found");
      System.exit(-1);
      return;  // not reached
    }
    try {
    String s;
    while ((s = in.readLine()) != null) {
      line++;
      int comment = s.indexOf('#');
      if (comment != -1)
        s = s.substring(0, comment);
      s = s.trim();
      if (s.equals("")) continue;
      StringTokenizer t = new StringTokenizer(s);
      String command = t.nextToken();
      try {
        if (command.equalsIgnoreCase("node")) {
          cmdNode(t);
          if (t.hasMoreElements())
            System.out.println(filename + ":" + line +
              ": extra argument(s) ignored");
        }
        else if (command.equalsIgnoreCase("link")) {
          cmdLink(t);
          if (t.hasMoreElements())
            System.out.println(filename + ":" + line +
              ": extra argument(s) ignored");
        }
        else if (command.equalsIgnoreCase("event")) {
          cmdEvent(t);
          if (t.hasMoreElements())
            System.out.println(filename + ":" + line +
              ": extra argument(s) ignored");
        }
        else {
          System.out.println(filename + ":" + line +
            ": unknown command: " + command);
        }
      }
      catch (NoSuchElementException e) {
        System.out.println(filename + ":" + line +
          ": missing argument(s) to " + command);
      }
      catch (NumberFormatException e) {
        System.out.println(filename + ":" + line + ": " + e);
      }
      catch (NoSuchNodeException e) {
        System.out.println(filename + ":" + line + ": " + e);
      }
      catch (NoSuchLinkException e) {
        System.out.println(filename + ":" + line + ": " + e);
      }
    }
    }
    catch (IOException e) {
      System.out.println(filename + ":" + line + ": " + e);
    }
    System.out.println(nodes.size() + " nodes read.");
    System.out.println(links.size() + " links read.");
  } 
  /** Process a "node" command.  This function creates a new node based
  *   on the information it parses.
  *   @param t A string tokenizer object representing the line from the
  *   .net file with the first token (the command) already removed.
  *   @exception java.lang.NumberFormatException Thrown when a numeric
  *   field (i.e., bandwidth or latency) contains an invalid number.
  */
  private static void cmdNode(StringTokenizer t) throws
      NumberFormatException {
    String nodeName = t.nextToken();
    double nodeX = Double.valueOf(t.nextToken()).doubleValue();
    double nodeY = Double.valueOf(t.nextToken()).doubleValue();
    Node newNode = new Node(nodes.size());
    nodes.addElement(newNode);
    nodesByName.put(nodeName, newNode);
    nodeCoords.addElement(new FPoint(nodeX, nodeY));
    nodeNames.addElement(nodeName);
  }
  /** Process a "link" command.  This function creates a new link based
  *   on the information it parses.
  *   @param t A string tokenizer object representing the line from the
  *   .net file with the first token (the command) already removed.
  *   @exception NoSuchLinkException Thrown when a link description
  *   references a node that cannot be found in the nodesByName mapping.
  *   @exception java.lang.NumberFormatException Thrown when a numeric
  *   field (i.e., bandwidth or latency) contains an invalid number.
  */
  private static void cmdLink(StringTokenizer t)
      throws NoSuchNodeException, NumberFormatException {
    String linkName = t.nextToken();
    String firstNodeName = t.nextToken();
    String secondNodeName = t.nextToken();
    Node firstNode = (Node)nodesByName.get(firstNodeName);
    if (firstNode == null)
      throw new NoSuchNodeException(firstNodeName);
    Node secondNode = (Node)nodesByName.get(secondNodeName);
    if (secondNode == null)
      throw new NoSuchNodeException(secondNodeName);
    int speed = Integer.parseInt(t.nextToken());
    double latency = Double.valueOf(t.nextToken()).doubleValue();
    int cost = t.hasMoreTokens() ? Integer.parseInt(t.nextToken()) : 1;
    Link newLink = new Link(firstNode.getAddress(), secondNode.getAddress(),
      speed, latency, cost);
    firstNode.addInterface(newLink);
    secondNode.addInterface(newLink);
    links.addElement(newLink);
    linksByName.put(linkName, newLink);
  } 
  /** Process an "event" command.  This function creates and queues a new
  *   event based on the information it parses.
  *   @param t A string tokenizer object representing the line from the
  *   .net file with the first token (the command) already removed.
  *   @exception NoSuchLinkException Thrown when an event description
  *   references a link that cannot be found in the linksByName mapping.
  */
  private static void cmdEvent(StringTokenizer t) throws NoSuchLinkException {
    String eventTime = t.nextToken();
    String eventType = t.nextToken();
    if (eventType.equalsIgnoreCase("state")) {
      String linkName = t.nextToken();
      Link link = (Link)linksByName.get(linkName);
      if (link == null)
        throw new NoSuchLinkException(linkName);
      String linkState = t.nextToken();
      LinkEvent le = new LinkEvent(
        Double.valueOf(eventTime).doubleValue(), link,
        linkState.equalsIgnoreCase("up"));
      try { eventQueue.insert(le); }
      catch (TimeInconsistencyException e) {
        System.out.println("Internal error: event occurs in the past.");
      }
    }
    else
      System.out.println("Unknown event type: " + eventType);
  }

  /** Bring a given link up or down.  This function takes care of calling
  *   Up() or Down() for all appropriate interfaces and calling
  *   interfaceUp() or interfaceDown() for all appropriate nodes.
  *   @param link The link to bring up or down.
  *   @param up <b>true</b> if the link is being brought up.
  */
  public static void linkSet(Link link, boolean up) {
    Node a = (Node)nodes.elementAt(link.getEndpointA());
    Node b = (Node)nodes.elementAt(link.getEndpointB());
    if (up) {
      link.up();
      a.interfaceUp(link);
      b.interfaceUp(link);
    }
    else {
      link.down();
      a.interfaceDown(link);
      b.interfaceDown(link);
    }
    nmview.repaint();
  }

  /** Returns the current time according to the network. */
  public static double getCurrentTime() { return now; }

  /** Actually run the simulation -- pop events off the queue and run them
  *   until no events remain.
  */
  private static void simulate() {
    NumberFormat nf = NumberFormat.getInstance();
    while (!eventQueue.empty()) {
      Event e = eventQueue.getNext();
      nf.setMaximumFractionDigits(5);
      now = e.getTime();
      dump();
      sview.setTime(nf.format(now) + " sec");
      sview.setStopAt(Math.floor(now*10+1)/10);
      synchronized (monitor) {
        try { monitor.wait(); }
        catch (InterruptedException ex) {}
      }
      double stopTime = sview.getStopAt();
      System.out.println(">> Time = " + now
        + "  packetsSent = " + packetsSent);
      //System.out.println("Event at time " + now + ": " + e);
      e.run();
      while (!eventQueue.empty() && (eventQueue.peekNext().getTime() == now
          || eventQueue.peekNext().getTime() < stopTime)) {
        e = eventQueue.getNext();
        if (now != e.getTime()) {
          now = e.getTime();
          System.out.println(">> Time = " + now
            + "  packetsSent = " + packetsSent);
        }
        //System.out.println("Event at time " + now + ": " + e);
        e.run();
      }
    }
    dump();
    sview.setTime(nf.format(now) + " sec (done)");
    sview.disableStepping();
    System.out.println("Total packets sent: " + packetsSent);
  }

  /** Dump the current state of the simulator into the output frame.  This
  *   involves updating the state of the routing table view (rtview) and
  *   the network map view (nmview) with new link state and routing table
  *   information.
  */
  private static void dump() {
    for (int i=0; i<nodes.size(); i++) {
      Node n = (Node)nodes.elementAt(i);
      String table = "dest  next  cost\n";
      Enumeration e = n.getRoutingTable();
      while (e.hasMoreElements()) {
        Route r = (Route)e.nextElement();
        table += "  " + r.getDest() + "     " + r.getNextHop() + "     " +
          r.getCost() + "\n";
      }
      rtview.setTable(i, table);
    }
  }

  /** Finds a link object given two endpoints.  If the data structures are
  *   consistent, the link returned should be unique.
  *   @param a The address of one endpoint.
  *   @param b The address of the other endpoint.
  */
  private static Link findLinkByNodes(int a, int b) {
    for (int i=0; i<links.size(); i++) {
      if ((((Link)links.elementAt(i)).getEndpointA() == a && 
          ((Link)links.elementAt(i)).getEndpointB() == b) ||
          (((Link)links.elementAt(i)).getEndpointA() == b && 
          ((Link)links.elementAt(i)).getEndpointB() == a))
        return (Link)links.elementAt(i);
    }
    return null;
  }
  
  /** All nodes in the network. */
  private static Vector nodes;
  /** All links in the network. */
  private static Vector links;
  /** The coordinates (X and Y in the range 0.0-1.0) of each node. */
  private static Vector nodeCoords;
  /** A mapping from node names (in the .net file) to node objects. */
  private static Hashtable nodesByName;
  /** A mapping from link names (in the .net file) to link objects. */
  private static Hashtable linksByName;
  /** Each node's name (in the .net file) */
  private static Vector nodeNames;
  /** The simulator's current time (the time of the next event in the
  * queue). */
  private static double now;
  /** The event queue. */
  private static EventQueue eventQueue;
  /** The GUI widget representing all routing tables. */
  private static RoutingTablesView rtview;
  /** The GUI widget representing the network map. */
  private static NetworkMap nmview;
  /** The GUI widget with a time display, Step button, and Quit button. */
  private static StepView sview;
  /** A count of all packets sent so far. */
  private static int packetsSent = 0;
  private static Object monitor;
}

class NoSuchNodeException extends Exception {
  NoSuchNodeException(String name) {
    super(name);
  }
}
class NoSuchLinkException extends Exception {
  NoSuchLinkException(String name) {
    super(name);
  }
}
