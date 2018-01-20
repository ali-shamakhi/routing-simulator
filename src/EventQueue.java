import java.util.Vector;

/** A simple heap to represent the event queue
 *  @see Event
 */
public class EventQueue {
  /** Construct a new queue */
  public EventQueue() {
    data = new Vector();
    lastEventTime = 0.0;  // no events allowed before 0.0
  }
  /** Returns <b>true</b> if the queue is empty (there are no pending
  *   events).
  */
  public boolean empty() {
    return data.size() == 0;
  }
  /** Inserts an event into the queue.  The queue implementation is
  *   a heap stored as an array.
  *   Insertion is done by appending the new element at the end of the
  *   array (i.e., as a new leaf in the heap) and "bubbling" it up as long
  *   as it is smaller than its parent.
  *   When storing a heap (remember, a heap is a tree) as a zero-based array,
  *   the parent of node I is node (I-1)/2.
  *   This function runs in O(lg n) time, where n is the number of events
  *   in the heap.
  *   @param e The event to insert
  *   @exception TimeInconsistencyException Thrown when the new event
  *   being inserted occurs in the past (i.e., before any event already
  *   retrieved with getNext).
  */
  public void insert(Event e) throws TimeInconsistencyException {
    if (e.getTime() < lastEventTime)
      throw new TimeInconsistencyException();
    int i = data.size();
    data.addElement(e);
    // while the node pointed to by I has a smaller value than its parent...
    while (i > 0 && ((Event)data.elementAt(i)).getTime() <
        ((Event)data.elementAt((i-1)/2)).getTime()) {
      // swap the element at I with its parent
      Object t = data.elementAt(i);
      data.setElementAt(data.elementAt((i-1)/2), i);
      data.setElementAt(t, (i-1)/2);
      // continue up the tree -- now consider the parent
      i = (i-1)/2;
    }
  }
  /** Returns the next event (the one with the earliest time) without
  *   removing it from the queue.  The minimum element in a heap is the
  *   root of the heap; when using an array for storage, the root is the
  *   first element of the array.
  *   This function runs in constant time.
  */
  public Event peekNext() {
    return (Event)data.elementAt(0);
  }
  /** Returns the next event (the one with the earliest time) and removes
  *   it from the queue.  The queue implementation is a heap stored as an
  *   array.
  *   Finding the minimum element is easy -- it's the first element of the
  *   array (see peekNext, above).
  *   Removing it is done by copying the last leaf of the heap over the
  *   root, removing the last leaf, and "bubbling" the new root (which was
  *   the last leaf) down as long as it is larger than either of its
  *   children.
  *   When storing a heap (remember, a heap is a tree) as a zero-based array,
  *   the children of node I are nodes (I*2)+1 and (I*2)+2.
  *   This function runs in O(lg n) time, where n is the number of events
  *   in the heap.
  */
  public Event getNext() {
    Event n = (Event)data.elementAt(0);
    lastEventTime = n.getTime();
    int i = 0;
    data.setElementAt(data.elementAt(data.size()-1), 0);
    data.removeElementAt(data.size()-1);
    while (true) {
      // I stores the node being bubbled down.
      // NEXT stores the smaller of its two children.
      int next;
      // If I is now a leaf node (it has no children), we're done.
      if (i*2+1 >= data.size()) break;
      // If I has only one child, I*2+1 is the smallest child by default.
      if (i*2+2 >= data.size()) {
        next = i*2+1;
      }
      // I has two children, so find the smaller and put it in NEXT.
      else {
        if (((Event)data.elementAt(i*2+1)).getTime() <
            ((Event)data.elementAt(i*2+2)).getTime())
          next = i*2+1;
        else
          next = i*2+2;
      }
      // If the value of node I is bigger than the value of node NEXT...
      if (((Event)data.elementAt(i)).getTime() >
          ((Event)data.elementAt(next)).getTime()) {
        // swap them.
        Object t = data.elementAt(i);
        data.setElementAt(data.elementAt(next), i);
        data.setElementAt(t, next);
        // continue down the tree -- now consider what was the smaller child.
        i = next;
      }
      else
        break;
    }
    return n;
  }

  /** The heap, represented as a dynamic array. */
  private Vector data;
  /** The time of the last event fetched by getNext.  This is stored to
  *   prevent users from inserting new events that occur before ones that
  *   have already been retreived.
  */
  private double lastEventTime;
}
