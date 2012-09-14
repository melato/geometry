package org.melato.geometry.gpx;

import java.util.List;

import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.gpx.util.PathTracker;

/**
 * PathTracker algorithm that assumes the incoming positions follow the set path.
 * It uses the previous position(s) to determine if the location is moving as expected along the path
 */
public class SequentialPathTracker implements PathTracker {
  private Path path;

  /** The last location */
  private Point location;
  
  /** The index of the waypoint closest to the last location in path terms */
  private int   nearestIndex = -1;
  /** The last computed path position */ 
  private float pathPosition;
  
  /** The waypoint at the nearest index */
  private Point nearestWaypoint;
  
  /** The distance from location to the nearestWaypoint */
  private float nearestDistance;
  
  private boolean inPath;
  
  private Metric metric = new GlobalDistance();
  
  public SequentialPathTracker() {
    super();
    setPath(new Path());
  }
  
  public SequentialPathTracker(List<Waypoint> waypoints) {
    setPath(new Path(waypoints));
  }

  
  @Override
  public void setPath(Path path) {
    this.path = path;
    /*
    if ( path.size() > 0 && path.getLength() < 200000) {
      // for path lengths < 200 Km, use local distance
      metric = new LocalDistance(path.getWaypoints()[0]);
    } else {
      metric = new GlobalDistance();
    } 
    */
  }

  @Override
  public int getNearestIndex() {
    return nearestIndex;
  }

  @Override
  public float getPosition() {
    return pathPosition;
  }
  
  /**
   * Whether or not we've determined we are following the path.
   */
  public boolean isInPath() {
    return inPath;
  }

  private boolean isSameLocation(Point p1, Point p2) {
    return p1.getLat() == p2.getLat() && p1.getLon() == p2.getLon();
  }
  
  private void setCurrentPosition(Point point, int index) {
    location = point;
    nearestIndex = index;
    nearestWaypoint = path.getWaypoints()[nearestIndex];      
    nearestDistance = metric.distance(location, nearestWaypoint);
  }
  
  /**
   * Interpolate the position between two points.
   * Either index could be out of bounds, but not both.
   * @param path
   * @param point
   * @param index1
   * @param index2
   * @return
   */
  float interpolatePosition(Path path, Point point, int index1, int index2) {
    if ( index1 > index2 || index2 < 0 || index1 >= path.size() ) {
      throw new IllegalArgumentException();
    }
    if ( index1 < 0 ) {
      // we are before the start.  our position is negative
      return - metric.distance(point, path.getWaypoints()[index2]);
    }
    if ( index2 >= path.size() ) {
      // we are past the end.
      return path.getLength(index2) + metric.distance(point, path.getWaypoints()[index2]);
    }
    // we are inside the path
    float d1 = metric.distance(point, path.getWaypoints()[index1]);
    float d2 = metric.distance(point, path.getWaypoints()[index2]);
    float p1 = path.getLength(index1);
    float p2 = path.getLength(index2);
    return p1 + (p2-p1)*d1/(d1+d2);    
  }
  
  void getDistances(Point point, int[] indexes, float[] distances) {
    for( int i = 0; i < indexes.length; i++ ) {
      distances[i] = 0;
      int index = indexes[i];
      if ( 0 <= index && index < path.size() -1 ) {
        distances[i] = metric.distance(point, path.getWaypoint(indexes[i]));
      }
    }
  }
  void setInitialLocation(Point point) {
    inPath = false;
    setCurrentPosition(point, path.findNearestIndex(point));
    if ( nearestIndex == 0 ) {
      // assume we're before the start
      pathPosition = -nearestDistance;
    } else if ( nearestIndex + 1 >= path.size() ) {
      // assume we're past the end
      pathPosition = path.getLength() + nearestDistance;
    } else {
      float d1 = metric.distance(point, path.getWaypoints()[nearestIndex-1]);
      float d2 = metric.distance(point, path.getWaypoints()[nearestIndex+1]);
      if ( d1 < d2 ) {
        pathPosition = interpolatePosition(path, point, nearestIndex - 1, nearestIndex);
      } else {
        pathPosition = interpolatePosition(path, point, nearestIndex, nearestIndex + 1);
      }
    }
  }
  
  @Override
  public void setLocation(Point point) {
    if ( location != null && isSameLocation(point, location)) {
      return;
    }
    if ( location == null ) {
      setInitialLocation(point);
    } else {
      if ( inPath ) {
        float d = metric.distance(point, nearestWaypoint);
        if ( d <= nearestDistance ) {
          // we seem to be moving towards the nearest waypoint
          nearestDistance = d;
          location = point;
          pathPosition = interpolatePosition(path, point, nearestIndex - 1, nearestIndex);
        } else {
          // we seem to be moving away from nearest waypoint
          // check if we're approaching the next one
          if ( nearestIndex < path.size() - 1 ) {
            Point nextWaypoint = path.getWaypoints()[nearestIndex+1];
            float dNext = metric.distance(point, nextWaypoint);
            float dLocationNext = metric.distance(location, nextWaypoint);
            if ( dNext < dLocationNext ) {
              // ok, we're moving closer to the next waypoint
              pathPosition = interpolatePosition(path, point, nearestIndex, nearestIndex + 1);
              setCurrentPosition(point, nearestIndex+1);
            }
          } else {
            // we've gone past the end of the path and we're no longer following it.
            setInitialLocation(point);
          }
        }
      } else {
        // decide whether we're following the path if both:
        // A) nearestDistance is smaller than the distance
        // between the nearest waypoint and either of its neighbors
        // B) at least one of the three distances is decreasing.
        // This is not very accurate, but we also check along the way
        Point previousLocation = location;
        setInitialLocation(point);
        if ( isNear() || isApproaching(previousLocation, point, nearestIndex-1, nearestIndex+1)) {
          inPath = true;
        }
      }
    }
    location = point;
  }
  
  /**
   * return true if nearestDistance is smaller than the distance
   * between the nearest waypoint and either of its neighbors
   * @param point
   * @return
   */
  private boolean isNear() {
    if ( path.size() < 2 )
      return false;    
    if ( nearestIndex > 0 && nearestDistance < path.getLength(nearestIndex-1,nearestIndex))
      return true;
    if ( nearestIndex < path.size() - 1 && nearestDistance < path.getLength(nearestIndex,nearestIndex+1)) {
      return true;
    }
    return false;
  }
  
  /**
   * Return true if distance between the current point and a waypoint
   * is smaller than the corresponding distance of the previous point
   * for any path waypoint between index1 and index2 (inclusive)
   * @param last
   * @param current
   * @param index1
   * @param index2
   * @return
   */
  private boolean isApproaching(Point previous, Point current, int index1, int index2) {
    for( int index = index1; index <= index2; index++ ) {
      if ( 0 <= index && index < path.size() ) {
        Waypoint p = path.getWaypoint(index);
        if ( metric.distance(current, p) < metric.distance(previous, p) )
          return true;
      }
    }
    return false;
  }
  
}
