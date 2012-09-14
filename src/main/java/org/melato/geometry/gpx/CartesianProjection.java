package org.melato.geometry.gpx;

import org.melato.geometry.MatrixTransform2d;
import org.melato.geometry.Point2d;
import org.melato.gpx.Earth;
import org.melato.gpx.Point;
import org.melato.matrix.Matrix;

/**
 * Projects A GPX coordinate to cartesian coordinates
 * @author Alex Athanasopoulos
 */
public class CartesianProjection {
  /** Copy a geographical coordinate from (lon,lat) to (x,y) as is, without any computations. */
  public static void copy(Point source, Point2d result) {
    result.x = source.lon;
    result.y = source.lat;
  }
  /** Copy a point (x,y) to a gpx point (lon,lat) as is, without any transformations. */
  public static void copy(Point2d source, Point result) {
    result.lat = source.y;
    result.lon = source.x;
  }
  public static Point2d toPoint2d(Point p) {
    return new Point2d(p.getLon(), p.getLat());
  }
  public static Point toPoint(Point2d p) {
    return new Point(p.y, p.x);
  }
  /** Create a Transform2d matrix that transforms a Point2d copied (with copy()) from a Point
   *  into a Point2d that represents x = meters East of center, and y = meters North of center. 
   * @param center
   * @return
   */
  public static Matrix relativeTo(Point center) {
    Point2d p0 = toPoint2d(center);
    float scaleLat = 1f/Earth.latitudeForDistance(1f);
    float scaleLon = 1f/Earth.longitudeForDistance(1f, p0.y);    
    Matrix scale = MatrixTransform2d.scale(scaleLon, scaleLat);
    Matrix translate = MatrixTransform2d.translation(-p0.x, -p0.y);
    return scale.dot(translate);    
  }
  
}
