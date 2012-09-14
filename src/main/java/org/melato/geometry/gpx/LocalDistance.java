package org.melato.geometry.gpx;

import org.melato.gpx.Earth;
import org.melato.gpx.Point;

public class LocalDistance implements Metric {
  private float latScale;
  private float lonScale;
  
  public LocalDistance(Point reference) {
    latScale = Earth.metersPerDegreeLatitude();
    lonScale = Earth.metersPerDegreeLongitude(reference.getLat());
  }

  @Override
  public float distance(Point p1, Point p2) {
    float x = (p2.getLon() - p1.getLon()) * lonScale;
    float y = (p2.getLat() - p2.getLat()) * latScale;    
    return (float) Math.sqrt(x*x + y*y);
  }

}
