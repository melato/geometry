package org.melato.geometry.gpx;

import org.melato.gpx.GlobalDistance;
import org.melato.gpx.Metric;
import org.melato.gpx.Point;

/**
 * Computes the path of a sequence of waypoints, as each point is added.
 * @author Alex Athanasopoulos
 *
 */
public class PathDistance {
  private Metric metric;
  private double distance;
  private Point last;
  
  
  public PathDistance() {
    this(new GlobalDistance());
  }
  
  public PathDistance(Metric metric) {
    this.metric = metric;
  }

  // skip the next waypoint in distance computations.
  public void skipNext() {
    last = null;
  }
  
  public void add(Point p) {
    if (last != null) {
      distance += metric.distance(last, p);
    }
    last = p;
  }
  public float getDistance() {
    return (float) distance;
  }
  

}
