package org.melato.geometry.gpx;

import java.util.List;

import org.melato.gpx.Metric;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;

/**
 * Stateless path tracking algorithm that does not take into account location history
 * Simply finds the closest stop and then interpolates between the closest and the next closest stops.
 * It is accurate on a straight path but may jump to the wrong waypoint on circular paths.
 * @param p
 * @return
 */
public class StaticPathTracker implements TrackingAlgorithm {
  private Path path;
  private Metric metric;

  private int   nearestIndex;
  private float pathPosition;
    
  @Override
  public void clearLocation() {
    nearestIndex = -1;
    pathPosition = 0;
  }

  public StaticPathTracker() {
    super();
    setPath(new Path());
  }
  
  public StaticPathTracker(List<Waypoint> waypoints) {
    setPath(new Path(waypoints));
  }

  
  @Override
  public void setPath(Path path) {
    this.path = path;
    this.metric = path.getMetric();
  }

  @Override
  public int getNearestIndex() {
    return nearestIndex;
  }

  @Override
  public float getPosition() {
    return pathPosition;
  }


  @Override
  public void setLocation(Point p) {
    nearestIndex = path.findNearestIndex(p);
    pathPosition = findPathLength(p);
  }

  float interpolatePosition(Point point, int index1, int index2) {
    float d1 = metric.distance(point, path.getWaypoints()[index1]);
    float d2 = metric.distance(point, path.getWaypoints()[index2]);
    float p1 = path.getLength(index1);
    float p2 = path.getLength(index2);
    return p1 + (p2-p1)*d1/(d1+d2);
  }
  
  private float findPathLength(Point p) {
    if ( path.size() < 2 )
      return Float.NaN;
    int nearest = path.findNearestIndex(p);
    if ( nearest == 0 ) {
      return interpolatePosition(p, 0, 1);
    }
    if ( nearest == path.size() - 1 ) {
      return interpolatePosition(p, nearest-1, nearest );
    }
    float d1 = metric.distance(p, path.getWaypoint(nearest-1));
    float d2 = metric.distance(p, path.getWaypoint(nearest+1));
    if ( d1 < d2 ) {
      return interpolatePosition(p, nearest-1, nearest );
    } else {
      return interpolatePosition(p, nearest, nearest+1 );
    }
  }
}
