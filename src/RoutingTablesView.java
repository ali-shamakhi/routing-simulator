import java.awt.*;

/** A GUI widget that displays the routing table for each node.  The
*   display consists of one scrollable text widget for each node,
*   containing an arbitrary string (typically a table of the form
*   &lt;destination,nextHop,cost&gt;) for each node.
*/
public class RoutingTablesView extends Frame {
  /** Create a new routing tables container with the given number of
  *   nodes.
  *   @param numNodes The number of nodes in the network.
  */
  public RoutingTablesView(int numNodes) {
    this.numNodes = numNodes;
    tables = new TextArea[numNodes];
    nameLabels = new Label[numNodes];
    
    int numNodesAcross = 2*(int)Math.ceil(Math.sqrt(numNodes/2.0));
    while (Math.ceil(numNodes*1.0/numNodesAcross) ==
        Math.ceil(numNodes*1.0/(numNodesAcross-1)))
      numNodesAcross--;

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    Font monoFont = new Font("monospaced", Font.PLAIN, 12);

    for (int i=0; i<numNodes; i++) {
      tables[i] = new TextArea(numNodes+1, 20);
      tables[i].setEditable(false);
      tables[i].setFont(monoFont);
      nameLabels[i] = new Label("Routing table: " + i);
      gbc.gridx = i % numNodesAcross;
      gbc.gridy = i / numNodesAcross*2;
      add(nameLabels[i], gbc);
      gbc.gridy = i / numNodesAcross*2 + 1;
      add(tables[i], gbc);
    }

    setTitle("Routing Simulator: Routing Tables");
    pack();
  }
  /** Set the text for a given node, typically to a table of the form
  *   &lt;destination,nextHop,cost&gt;).
  *   @param table Which routing table to set (node number).
  *   @param contents What to set the table to.
  */
  public void setTable(int table, String contents) {
    tables[table].setText(contents);
  }
  /** Set the name for the given node.
  *   @param node The number of the node to set.
  *   @param name The string to set the node's name to.
  */
  public void setName(int node, String name) {
    nameLabels[node].setText("Routing table: " + name);
  }
                  
  /** An array of scrollable text widgets, one per node. */
  private TextArea[] tables;
  /** An array of labels, one per node. */
  private Label[] nameLabels;
  /** The number of nodes in the network. */
  private int numNodes;
}
