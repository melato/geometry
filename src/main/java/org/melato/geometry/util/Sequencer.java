package org.melato.geometry.util;

public class Sequencer {
  private int[] indexes;
  private boolean[] visited;
  
  public Sequencer(int[] indexes) {
    super();
    this.indexes = indexes;
    visited = new boolean[indexes.length];
  }

  public void findSequence(int start, int end, Sequence sequence) {
    int a = indexes[start];
    sequence.start = start;
    sequence.last = start;
    sequence.length = 1;
    int j = start + 1;
    for( ; j < end ;j++ ) {
      int b = indexes[j];
      if ( b != -1 && ! visited[j]) {
        if ( b == a + 1 ) {
          a = b;
          sequence.length++;
        } else if ( b != a ) {
          continue;
        }
        sequence.last = j;
        visited[j] = true;
      }
    }
    System.out.println( "findSequence start=" + start + " end=" + end + " sequence=" + sequence);
  }
  
  private void filter() {
    System.out.println( "approaches sorted: " + toString(indexes, 0, indexes.length));
    removeOutOfOrder(0, indexes.length );
    //System.out.println( "approaches in-order: " + toString(approaches, 0, approaches.length));
    removeDuplicates();
    System.out.println( "approaches unique: " + toString(indexes, 0, indexes.length));
    
  }
  
  /** remove approaches so that the remaining approaches are in non-decreasing order of route indexes.
   * approaches are removed by setting their place to null in the array.
   * */ 
  private void removeOutOfOrder(int start, int end) {
    if ( end <= start )
      return;
    for( int i = start; i < end; i++ ) {
      visited[i] = false;
    }
    Sequence bestSequence = null;
    Sequence sequence = new Sequence();
    
    // find the longest sub-sequence of sequential or equal route indexes
    for( int i = start; i < end; i++ ) {
      int a = indexes[i];
      if ( a != -1 ) {
        //System.out.println( "i=" + i + " visited=" + a.visited);
        if ( visited[i] )
          continue;
        findSequence(i, end, sequence);
        if ( bestSequence == null || sequence.length > bestSequence.length ) {
          bestSequence = sequence;
          sequence = new Sequence();
        }
      }
    }
    if ( bestSequence == null ) {
      // there is nothing
      return;
    }
    System.out.println( "best sequence: " + bestSequence);
    bestSequence.clearInside(indexes);
    bestSequence.clearLeft(indexes, start);
    bestSequence.clearRight(indexes, end);
    
    //System.out.println( "a: " + toString( approaches, 0, approaches.length ));
    // do the same on each side
    removeOutOfOrder( start, bestSequence.start);
    removeOutOfOrder( bestSequence.last + 1, end );
    //System.out.println( "b: " + toString( approaches, 0, approaches.length ));
  }

  private void removeDuplicates() {
    // keep the last approach that has the first route index.
    int routeIndex = -1;
    int lastIndex = -1;
    int i = 0;
    for( ; i < indexes.length; i++ ) {
      int a = indexes[i];
      if ( a != -1 ) {
        if ( routeIndex == -1 ) {
          routeIndex = a;
          lastIndex = i;
        } else if ( routeIndex == a ) {
            indexes[lastIndex] = -1;
        } else {
          routeIndex = a;
          i++;
          break;
        }
      }
    }
    
    // for subsequent route indexes, keep the first approach from approaches with equal route index.
    for( ; i < indexes.length; i++ ) {
      int a = indexes[i];
      if ( a != -1 ) {
        if ( routeIndex == a ) {
          indexes[i] = -1;
        } else {
          routeIndex = a;
        }
      }      
    }
  }

  public static String toString( int[] indexes, int start, int end ) {
    StringBuilder buf = new StringBuilder();
    buf.append( "[");
    int count = 0;
    for( int i = start; i < end; i++ ) {
      int a = indexes[i];
      if ( a != -1 ) {
        if ( count > 0 ) {
          buf.append( " " );
        }
        count++;
        buf.append( String.valueOf(a) );
      }
    }
    buf.append("]");
    return buf.toString();
  }
  
  /** Find a subsequence of increasing items by setting the remaining items to -1. */
  public static void filter(int[] items) {
    Sequencer sequencer = new Sequencer(items);
    sequencer.filter();
  }
}

