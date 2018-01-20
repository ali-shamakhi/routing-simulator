import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;

/** A GUI widget that displays a network map.  The display consists of one
*   square for each node, with the node's name, and a line for each link
*   that is up.  See the documentation for the base class, Component, for
*   descriptions of most of the functions.
*   @see Component
*/
public class NetworkMap extends Component {
  /** The margin between node label text and the node's rectangle. */
  private static final int textMargin = 3;
  /** The margin, in pixels, between the edge of the map and the edge of
  * the widget.
  */
  private static final int margin = 40;

  /** Create a new network map widget with the given number of nodes.
  */
  public NetworkMap(Node[] _nodes, Link[] _links) {
  // note, I use _ to indicate constructor parameters to avoid naming
  // confusion with inner classes
    nodes = _nodes;
    links = _links;
    fcoords = new FPoint[nodes.length];
    coords = new Point[nodes.length];
    nodeName = new String[nodes.length];
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        int width = getSize().width;
        int height = getSize().height;
        for (int i=0; i<nodes.length; i++) {
          coords[i] = new Point(
            (int)(fcoords[i].x * (width - margin) + margin/2),
            (int)(fcoords[i].y * (height - margin) + margin/2));
        }
      }
    });
  }
  public Dimension getPreferredSize() {
    return new Dimension(2*margin+400, 2*margin+300);
  }
  public Dimension getMinimumSize() {
    return new Dimension(2*margin+16, 2*margin+12);
  }
  public void paint(Graphics g) {
    FontMetrics fm = getFontMetrics(g.getFont());
    int ascent = fm.getMaxAscent();
    int descent = fm.getMaxDescent();
    for (int i=0; i<links.length; i++)
      if (links[i].isUp()) {
        int a = links[i].getEndpointA();
        int b = links[i].getEndpointB();
        int midx = (coords[a].x + coords[b].x) / 2;
        int midy = (coords[a].y + coords[b].y) / 2;
        String cost = Integer.toString(links[i].getCost());
        int width = fm.stringWidth(cost);
        g.drawLine(coords[a].x, coords[a].y, coords[b].x, coords[b].y);
        g.clearRect(
          midx-width/2-textMargin, midy-ascent-textMargin,
          width+2*textMargin, ascent+descent+2*textMargin);
        g.drawString(cost, midx-width/2, midy);
      }
    for (int i=0; i<nodes.length; i++) {
      int width = fm.stringWidth(nodeName[i]);
      g.clearRect(
        coords[i].x-width/2-textMargin, coords[i].y-ascent-textMargin,
        width+2*textMargin, ascent+descent+2*textMargin);
      g.drawRect(
        coords[i].x-width/2-textMargin, coords[i].y-ascent-textMargin,
        width+2*textMargin, ascent+descent+2*textMargin);
      g.drawString(nodeName[i], coords[i].x-width/2, coords[i].y);
    }
  }
  /** Set the location to draw the given node on the screen.
  *   @param node The number of the node to set.
  *   @param p Where to draw the node on the screen.  Coordinates are in
  *   the range 0.0-1.0 rather than literal screen coordinates.  (0.0,
  *   0.0) is still the upper-left corner.
  */
  public void setLocation(int node, FPoint p) {
    fcoords[node] = p;
    coords[node] = new Point(
      (int)(fcoords[node].x * (getSize().width - margin) + margin/2),
      (int)(fcoords[node].y * (getSize().height - margin) + margin/2));
  }
  /** Set the name for the given node.
  *   @param node The number of the node to set.
  *   @param name The string to set the node's name to.
  */
  public void setName(int node, String name) {
    nodeName[node] = name;
  }

  Node[] nodes;
  Link[] links;
  /** The coordinates at which to draw each node, in the range 0.0-1.0. */
  private FPoint[] fcoords;
  /** The literal coordinates at which to draw each node, calculated
  *   automatically from fcoords, above.
  */
  private Point[] coords;
  /** The name of each node. */
  private String[] nodeName;
}
