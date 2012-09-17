package org.melato.geometry.gpx;

import org.melato.gpx.GlobalDistance;
import org.melato.gpx.Metric;
import org.melato.gpx.Point;

/**
 * Computes the length of a sequence of waypoints, as each point is added.
 * @author Alex Athanasopoulos
 *
 */
public class PathLength {
  private Metric metric;
  private double length;
  private Point last;  
  
  public PathLength() {
    this(new GlobalDistance());
  }
  
  public PathLength(Metric metric) {
    this.metric = metric;
  }

  public void add(Point p) {
    if (last != null) {
      float d = metric.distance(last, p);
      length += d;
    }
    last = p;
  }
  public float getLength() {
    return (float) length;
  }
}
