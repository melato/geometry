package org.melato.geometry.gpx;

import java.util.Date;

import org.melato.gpx.Point;

/** Computes speed and expected times of arrival on a path */
public class SpeedTracker {
  protected PathTracker tracker;
  protected long  speedStartTime;
  protected float speedStartPosition;
  protected float speed = Float.NaN;

  public SpeedTracker(PathTracker tracker) {
    super();
    this.tracker = tracker;
  }
  
  public void compute() {
    speed = computeSpeed();
  }
  
  public float getSpeed() {
    return speed;    
  }
  
  /** Get the expected time to reach the given waypoint.
   * @param pathIndex
   * @return time in seconds from the last location
   */
  public float getRemainingTime(int pathIndex) {
    speed = computeSpeed();
    float time = (tracker.getPath().getLength(pathIndex) - tracker.getPosition())/getSpeed();
    return time;
  }
  
  public Date getETA(int pathIndex) {
    float time = getRemainingTime(pathIndex);
    if ( Float.isNaN(time)) {
      return null;
    }
    Point location = tracker.getLocation();
    if ( location == null )
      return null;
    return new Date(location.getTime().getTime() + (long) (time * 1000)); 
  }
  
  private float computeSpeed() {
    Point location = tracker.getLocation();
    if (location == null)
      return Float.NaN;
    long time = location.getTime().getTime();
    if ( speedStartTime == 0 ) {
      speedStartTime = time;
      speedStartPosition = tracker.getPosition();
      return 0f;
    } else {
      time -= speedStartTime;
      float distance = tracker.getPosition() - speedStartPosition;
      return distance * 1000f / time;
    }      
  }

  @Override
  public String toString() {
    return String.valueOf(getSpeed());
  }  
}
