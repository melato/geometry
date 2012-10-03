package org.melato.geometry.test;

import org.melato.gps.Point;
import org.melato.gpx.Metric;

/** A test distance for testing algorithms with easier data. */
public class TestMetric implements Metric {

  @Override
  public float distance(Point p1, Point p2) {
    return Math.abs(p1.getLat() - p2.getLat());
  }

}
