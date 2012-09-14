package org.melato.geometry.gpx;

import org.melato.gpx.Point;

public interface Metric {
  float distance(Point p1, Point p2);
}
