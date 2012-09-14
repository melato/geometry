package org.melato.geometry.ant;

import org.melato.gpx.Earth;
import org.melato.gpx.Waypoint;

/**
 * Computes the path of a sequence of waypoints, as each point is added.
 * @author Alex Athanasopoulos
 *
 */
public class PathDistance {
  private double distance;
  private Waypoint last;
  public float add(Waypoint p) {
    if (last != null) {
      distance += Earth.distance(last, p);
    }
    last = p;
    return getDistance();
  }
  public float getDistance() {
    return (float) distance;
  }
  

}
