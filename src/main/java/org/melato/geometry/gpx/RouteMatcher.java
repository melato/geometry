package org.melato.geometry.gpx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.melato.gps.Earth;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;

/** Matches a track to a route and returns a list of approaches.
 *  An approach is a pair of (route-index, track-index).
 *  It sorts first by route-index, then by track-index.
 */
public class RouteMatcher {
  private ProximityFinder proximity;
  private float startSpeed;
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
      int d = routeIndex - a.routeIndex;
      if ( d != 0 )
        return d;
      return trackIndex - a.trackIndex;
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
  
  public List<Approach> match(List<Waypoint> route) {
    List<Approach> list = new ArrayList<Approach>();
    List<Integer> nearby = new ArrayList<Integer>();
    int[] first = null; // the track indexes of the first route point.
    int routeSize = route.size();
    int lastTrackIndex = -1;
    for( int i = 0; i < routeSize; i++ ) {
      nearby.clear();
      proximity.findNearby(route.get(i), nearby);
      //System.out.println( route.get(i).getName() + " nearby.size=" + nearby.size());
      int nearbySize = nearby.size();
      if ( nearbySize == 0 )
        continue;
      if ( first == null ) {
        first = new int[nearbySize];
        for( int j = 0; j < nearbySize; j++ ) {
          first[j] = nearby.get(j);
        }
        Arrays.sort(first);
        lastTrackIndex = first[0];
      }
      int minTrackIndex = -1; 
      for( int j = 0; j < nearbySize; j++) {
        int trackIndex = nearby.get(j);
        if ( trackIndex > lastTrackIndex ) {
          if ( minTrackIndex == -1 || trackIndex < minTrackIndex ) {
            minTrackIndex = trackIndex;
          }
        }
      }
      if ( minTrackIndex != -1 ) {
        list.add( new Approach(i, minTrackIndex ));
        lastTrackIndex = minTrackIndex;
      }
    }
    if ( list.isEmpty() )
      return list;
    if ( first != null && list.size() > 1 ) {
      int secondTrackIndex = list.get(1).trackIndex;
      int maxFirstIndex = first[0];
      for( int j = 1; j < first.length; j++ ) {
        int index = first[j];
        if ( maxFirstIndex < index && index < secondTrackIndex) {
          maxFirstIndex = index;          
        }
      }
      maxFirstIndex = trim(proximity.getWaypoints(), maxFirstIndex, secondTrackIndex);
      list.get(0).trackIndex = maxFirstIndex;
    }    
    return list;
  }
}
