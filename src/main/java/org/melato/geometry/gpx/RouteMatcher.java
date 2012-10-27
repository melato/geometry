package org.melato.geometry.gpx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.melato.gps.Earth;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;

/** Matches a track to a route and returns a list of approaches.
 *  An approach is a pair of (route-index, track-index),
 *  where the distance between a route waypoint and the track is shorter than a threshold
 *  and is at a local minimum.
 *  The class puts the approaches in order and may remove some if they don't fit in the sequence
 *  specified by the track.
 */
public class RouteMatcher {
  private ProximityFinder proximity;
  private float startSpeed;
  
  /**
   * An approach.
   * It sorts by track index, so that the approaches are in the order that they are encountered.
   * @author Alex Athanasopoulos
   *
   */
  public static class Approach implements Comparable<Approach> {
    public int routeIndex;
    public int trackIndex;
    public Approach(int routeIndex, int trackIndex) {
      super();
      this.routeIndex = routeIndex;
      this.trackIndex = trackIndex;
    }
    @Override
    public int compareTo(Approach a) {
      int d = trackIndex - a.trackIndex;
      if ( d != 0 )
        return d;
      return routeIndex - a.routeIndex;
    }
  }
  
  public void setStartSpeed(float startSpeed) {
    this.startSpeed = startSpeed * 1000f / 3600f;
  }

  public RouteMatcher(List<Waypoint> track, float proximityDistance ) {
    this(new Path(track), proximityDistance);
  }
  
  public RouteMatcher(Path path, float proximityDistance ) {
    proximity = new ProximityFinder();
    proximity.setPath(path);
    proximity.setTargetDistance(proximityDistance);
  }
  
  private int trim(Waypoint[] waypoints, int index, int nextIndex) {
    for( ; index < nextIndex; index++ ) {
      Waypoint p1 = waypoints[index];
      Waypoint p2 = waypoints[index+1];
      float speed = Earth.distance(p1,  p2) - Waypoint.timeDifference(p1,  p2);
      if ( speed > startSpeed ) {
        return index;
      }
    }
    return index;    
  }
  
  /** remove approaches so that the remaining approaches are in non-decreasing order of route indexes.
   * approaches are removed by setting their place to null in the array.
   * */ 
  public void removeOutOfOrder(Approach[] approaches, int start, int end) {
    // find the longest sequence of non-decrementing route indexes
    int bestStart = 0;
    int bestEnd = 0;
    int bestLength = 0;
    for( int i = start; i < end; ) {
      Approach a = approaches[i];
      if ( a != null ) {
        int length = 1;
        int j = i + 1;
        for( ; j < end ;j++ ) {
          Approach b = approaches[j];
          if ( b != null ) {
            if ( a.routeIndex <= b.routeIndex ) {
              length++;
            } else {
              break;
            }
          }
        }
        if ( length > bestLength ) {
          bestStart = i;
          bestLength = length;
          bestEnd = j;
        }
        i = j;
      } else {
        i++;
      }
    }
    if ( bestLength == 0 ) {
      // there is nothing
      return;
    }
    int minRouteIndex = approaches[bestStart].routeIndex;
    int maxRouteIndex = minRouteIndex;
    for( int i = bestEnd - 1; i >= bestStart; i-- ) {
      Approach a = approaches[i];
      if ( a != null ) {
        maxRouteIndex = a.routeIndex;
        break;
      }
    }
    // remove approaches from the left that don't fit the best sequence
    for( int i = start; i < bestStart; i++ ) {
      Approach a = approaches[i];
      if ( a != null && a.routeIndex > minRouteIndex ) {
        approaches[i] = null;
      }
    }
    // remove approaches from the right that don't fit the best sequence
    for( int i = bestEnd; i < end; i++ ) {
      Approach a = approaches[i];
      if ( a != null && a.routeIndex < maxRouteIndex ) {
        approaches[i] = null;
      }
    }
    // do the same on each side
    removeOutOfOrder( approaches, start, bestStart );
    removeOutOfOrder( approaches, bestEnd, end );
  }

  public void removeDuplicates(Approach[] approaches) {
    Approach last = null;
    // keep the last approach that has the first route index.
    for( int i = 0; i < approaches.length; i++ ) {
      Approach a = approaches[i];
      if ( a != null ) {
        if ( last != null && a.routeIndex != last.routeIndex ) {
          break;
        }
        last = a;
      }
    }
    
    // for subsequent route indexes, keep the first approach from approaches with equal route index.
    for( int i = 0; i < approaches.length; i++ ) {
      Approach a = approaches[i];
      if ( a != null ) {
        if ( last.routeIndex == a.routeIndex ) {
          approaches[i] = null;
        } else {
          last = a;
        }
      }      
    }
  }
  
  public List<Approach> match(List<Waypoint> route) {
    List<Approach> list = new ArrayList<Approach>();
    List<Integer> nearby = new ArrayList<Integer>();
    int routeSize = route.size();
    for( int i = 0; i < routeSize; i++ ) {
      nearby.clear();
      proximity.findNearby(route.get(i), nearby);
      //System.out.println( route.get(i).getName() + " nearby.size=" + nearby.size());
      int nearbySize = nearby.size();
      for( int j = 0; j < nearbySize; j++ ) {
        list.add( new Approach(i, nearby.get(j)));
      }
    }
    Approach[] approaches = list.toArray(new Approach[0]);
    Arrays.sort(approaches);
    removeOutOfOrder( approaches, 0, approaches.length );
    removeDuplicates(approaches);
    list.clear();
    for( int i = 0; i < approaches.length; i++ ) {
      Approach a = approaches[i];
      if ( a != null ) {
        list.add(a);
      }
    }
    return list;
  }
}
