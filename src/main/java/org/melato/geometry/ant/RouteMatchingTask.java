package org.melato.geometry.ant;

import java.io.File;
import java.util.List;

import org.melato.app.ant.AppTask;
import org.melato.geometry.gpx.FileGpxSpecifier;
import org.melato.geometry.gpx.RouteMatcher;
import org.melato.geometry.gpx.RouteMatcher.Approach;
import org.melato.geometry.gpx.WaypointsSpecifier;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;

/** Ant task that matches a track against a route and outputs approaches.
 * @author Alex Athanasopoulos
 *
 */
public class RouteMatchingTask extends AppTask {
  private WaypointsSpecifier  track;
  private WaypointsSpecifier  route;
  private float targetDistance = 100;

  /** Specify the track as a gpx specifier. */
  public void setTrack(WaypointsSpecifier track) {
    this.track = track;
  }
  
  public void setRoute(WaypointsSpecifier route) {
    this.route = route;
  }

  public void setTrackFile(File file) {
    this.track = new FileGpxSpecifier(file);
  }
  
  public void setRouteFile(File file) {
    this.route = new FileGpxSpecifier(file);
  }
  
  
  public void setTargetDistance(float targetDistance) {
    this.targetDistance = targetDistance;
  }

  private void printApproaches(List<Approach> approaches,
      List<Waypoint> trackWaypoints,
      List<Waypoint> routeWaypoints) {
    System.out.println( "approaches: " + approaches.size());
    int size = approaches.size();
    if ( size == 0 )
      return;
    Path path = new Path(trackWaypoints);
    long time0 = trackWaypoints.get(approaches.get(0).trackIndex).getTime(); 
    float distance0 = path.getLength(approaches.get(0).trackIndex);
    for( int i = 0; i < size; i++ ) {
      Approach a = approaches.get(i);
      int trackIndex = approaches.get(i).trackIndex;
      Waypoint p = trackWaypoints.get(trackIndex);
      int time = (int) ((p.getTime() - time0)/1000L);
      System.out.println( a.routeIndex + "," + a.trackIndex + " " + routeWaypoints.get(a.routeIndex).getName() +
          " " + Math.round((path.getLength(trackIndex) - distance0)) +
          "/" + time
          );
    }
  }
  @Override
  public void execute() throws Exception {
    List<Waypoint> trackWaypoints = this.track.loadWaypoints();
    System.out.println( "route=" + this.route.getName() + " track=" + this.track.getName());
    List<Waypoint> routeWaypoints = this.route.loadWaypoints();
    RouteMatcher matcher = new RouteMatcher(trackWaypoints, targetDistance);
    List<Approach> approaches = matcher.match( routeWaypoints );
    printApproaches(approaches, trackWaypoints, routeWaypoints);
  }
}
