package org.melato.geometry.ant;

import org.melato.geometry.gpx.GlobalDistance;
import org.melato.geometry.gpx.Metric;
import org.melato.gpx.Waypoint;

/**
 * Computes the path of a sequence of waypoints, as each point is added.
 * @author Alex Athanasopoulos
 *
 */
public class PathDistance {
  private Metric metric;
  private double distance;
  private Waypoint last;
  
  
  public PathDistance() {
    this(new GlobalDistance());
  }
  
  public PathDistance(Metric metric) {
    this.metric = metric;
  }

  public float add(Waypoint p) {
    if (last != null) {
      distance += metric.distance(last, p);
    }
    last = p;
    return getDistance();
  }
  public float getDistance() {
    return (float) distance;
  }
  

}
