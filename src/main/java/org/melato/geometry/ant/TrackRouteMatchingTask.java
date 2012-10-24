package org.melato.geometry.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.melato.app.ant.AppTask;
import org.melato.geometry.gpx.FileGpxSpecifier;
import org.melato.geometry.gpx.GpxSpecifier;
import org.melato.geometry.gpx.TrackMatcher2;
import org.melato.geometry.gpx.TrackMatcher2.Approach;
import org.melato.gpx.GPX;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;

/** Ant task that matches a track against a route and outputs approaches.
 * @author Alex Athanasopoulos
 *
 */
public class TrackRouteMatchingTask extends AppTask {
  private GpxSpecifier  track;
  private GpxSpecifier  route;
  private float targetDistance = 100;

  /** Specify the track as a gpx specifier. */
  public void setTrack(GpxSpecifier track) {
    this.track = track;
  }
  
  public void setRoute(GpxSpecifier route) {
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
    GPX trackGpx = this.track.loadGpx();
    List<Waypoint> trackWaypoints = new ArrayList<Waypoint>();
    for( Waypoint p: GPXIterators.trackWaypoints(trackGpx.getTracks())) {
      trackWaypoints.add(p);
    }
    GPX routeGpx = this.route.loadGpx();
    List<Waypoint> routeWaypoints = routeGpx.getRoutes().get(0).getWaypoints();
    TrackMatcher2 matcher = new TrackMatcher2(trackWaypoints, targetDistance);
    List<Approach> approaches = matcher.match( routeWaypoints );
    printApproaches(approaches, trackWaypoints, routeWaypoints);
  }
}
