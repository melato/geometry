/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.geometry.gpx;

import java.util.Arrays;

import org.melato.gps.PointTime;
import org.melato.gpx.Metric;
import org.melato.gpx.util.Path;

/**
 * base class for path tracker algorithms.
 * It maintains some common variables and methods.
 */
public abstract class BasePathTracker2 implements TrackingAlgorithm {
  protected Path path;
  protected Metric metric;
    
  /** The index of the waypoint closest to the last location in path terms */
  protected int   nearestIndex = -1;
  
/** The last computed path position */ 
  protected float pathPosition;
  
  /**
   * The index of the waypoint that we're currently tracking.  The exact definition depends on the algorithm.
   * May differ from nearestIndex
   */
  protected int   currentIndex = -1;

  /** The waypoint at the current index */
  protected PointTime currentWaypoint;
  
  protected boolean inPath;

  /** The previous position */
  protected PositionState previous = new PositionState();
  
  /** The last position */
  protected PositionState current = new PositionState();
  
  /** Keeps track of 3-neighbor distances around currentIndex */
  class PositionState {
    PointTime position;
    int currentIndex;
    float[] distance = new float[4];
    boolean[] computed = new boolean[distance.length];
    
    public void setCurrentIndex( int currentIndex ) {
      if ( currentIndex != this.currentIndex ) {
        this.currentIndex = currentIndex;
        reset();
      }
    }
    public void reset() {
      Arrays.fill(computed,  0, computed.length, false);
    }
    void setLocation( PointTime position) {
      this.position = position;
      reset();
    }
    PointTime getLocation() {
      return position;
    }
    /**
     * Return the distance of this location from a path waypoint 
     * @param i The index of the waypoint, relative to the current waypoint, one of -1, 0, 1, 2 
     * @return
     */
    float distance(int i) {
      if ( ! hasLocation() )
        return Float.NaN;
      int waypointIndex = currentIndex+i;
      if ( waypointIndex < 0 || waypointIndex >= path.size() ) {
        return Float.NaN;
      }
      int index = i + 1;
      computed[index] = false;
      if ( ! computed[index] ) {
        computed[index] = true;
        distance[index] = metric.distance(position, path.getWaypoint(waypointIndex));
      }
      return distance[index];
    }
    boolean hasLocation() {
      return position != null;
    }
  }
  
  protected boolean inRange(int i) {
    return i + currentIndex >= 0 && i + currentIndex < path.size();
  }
  
  protected boolean isMoving(int from, int to) {
    if ( ! inRange(from) || ! inRange(to) )
      return false;
    return isLeaving(from) && isApproaching(to);
  }
  
  
  protected boolean isApproaching(int offset) {
    if ( ! previous.hasLocation() )
      return false;
    return current.distance(offset) <= previous.distance(offset);    
  }
  
  protected boolean isLeaving(int offset) {
    if ( ! previous.hasLocation() )
      return false;
    return previous.distance(offset) <= current.distance(offset);    
  }
  
  @Override
  public void clearLocation() {
    previous.setLocation(null);
    current.setLocation(null);
    currentIndex = -1;
    currentWaypoint = null;
    nearestIndex = -1;
    pathPosition = 0;
    inPath = false;
  }

  public BasePathTracker2() {
    super();
    setPath(new Path());
  }
  
  @Override
  public void setPath(Path path) {
    clearLocation();
    this.path = path;
    this.metric = path.getMetric();
  }

  @Override
  public int getNearestIndex() {
    if ( nearestIndex == -1 && path.size() > 0 ) {
      nearestIndex = path.findNearestIndex(pathPosition);
    }
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

  public int getCurrentIndex() {
    return currentIndex;
  }
  
  protected boolean isSameLocation(PointTime p1, PointTime p2) {
    return p1.getLat() == p2.getLat() && p1.getLon() == p2.getLon();
  }
  
  protected void setInitialLocation(PointTime point) {
    inPath = false;
    setCurrentIndex(path.findNearestIndex(point));
    setCurrentLocation(point);
    nearestIndex = currentIndex;
    if ( currentIndex == 0 ) {
      // assume we're before the start
      //pathPosition = -currentDistance;
      pathPosition = 0;
    } else if ( currentIndex + 1 >= path.size() ) {
      pathPosition = path.getLength();
      // assume we're past the end
      //pathPosition += currentDistance;
    } else {
      if ( current.distance(-1) < current.distance(1) ) {
        pathPosition = interpolatePosition(-1);
      } else {
        pathPosition = interpolatePosition(0);
      }
    }
  }    
  
  /**
   * Set the current and previous locations.
   * @param point
   * @return true if there are current and previous locations
   */
  protected boolean setCurrentLocation(PointTime point) {
    PointTime location = current.getLocation();
    if ( location != null && isSameLocation(point, location)) {
      return false;
    }
    PositionState temp = previous;
    previous = current;
    current = temp;
    current.setLocation(point);
    return true;
  }
  
  protected void setCurrentIndex(int index) {
    currentIndex = index;
    current.setCurrentIndex(index);
    previous.setCurrentIndex(index);
    currentWaypoint = path.getWaypoints()[currentIndex];          
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
  protected float interpolatePosition(Path path, PointTime point, int index1, int index2) {
    if ( index1 > index2 || index2 < 0 || index1 >= path.size() ) {
      throw new IllegalArgumentException();
    }
    if ( index1 < 0 ) {
      // we are before the start.  our position is negative
      return - metric.distance(point, path.getWaypoints()[index2]);
    } else if ( index2 >= path.size() ) {
      // we are past the end.
      return path.getLength(index2) + metric.distance(point, path.getWaypoints()[index2]);
    } else {
      // we are inside the path
      float d1 = metric.distance(point, path.getWaypoints()[index1]);
      float d2 = metric.distance(point, path.getWaypoints()[index2]);
      float p1 = path.getLength(index1);
      float p2 = path.getLength(index2);
      if ( index1 + 1 == index2 ) {
        // adjust the nearest index, since we just calculated the distances
        nearestIndex = (d1 < d2) ? index1 : index2;
      }
      return p1 + (p2-p1)*d1/(d1+d2);
    }
  }
  
  protected float interpolatePosition(int offset) {
    if ( currentIndex + offset < 0 ) {
      // we are before the start.  our position is negative
      return - current.distance(offset+1);
    } else if ( currentIndex + offset + 1 >= path.size() ) {
      // we are past the end.
      return path.getLength() + current.distance(offset + 1);
    } else {
      // we are inside the path
      float d1 = current.distance(offset);
      float d2 = current.distance(offset+1);
      float p1 = path.getLength(currentIndex + offset);
      float p2 = path.getLength(currentIndex + offset + 1);
      // adjust the nearest index, since we just calculated the distances
      nearestIndex = currentIndex + offset;
      if ( d1 >= d2 ) {
        nearestIndex++;
      }
      return p1 + (p2-p1)*d1/(d1+d2);
    }
  }
  
  void getDistances(PointTime point, int[] indexes, float[] distances) {
    for( int i = 0; i < indexes.length; i++ ) {
      distances[i] = 0;
      int index = indexes[i];
      if ( 0 <= index && index < path.size() -1 ) {
        distances[i] = metric.distance(point, path.getWaypoint(indexes[i]));
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append( "inPath=" + inPath );
    buf.append( " current=" + currentIndex );
    buf.append( " nearest=" + nearestIndex);
    buf.append( " position=" + getPosition());
    if ( currentIndex >= 0 ) {
      buf.append( " waypoint=" + path.getWaypoint(currentIndex));      
    }
    return buf.toString();
  }  
}
