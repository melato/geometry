package org.melato.geometry.gpx;

import org.melato.gpx.Earth;
import org.melato.gpx.Point;

public class GlobalDistance implements Metric {

  @Override
  public float distance(Point p1, Point p2) {
    return Earth.distance(p1, p2);
  }

}
