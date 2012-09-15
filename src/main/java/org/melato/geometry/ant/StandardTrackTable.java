package org.melato.geometry.ant;

import org.melato.gpx.Earth;
import org.melato.gpx.Waypoint;


/**
 * Used to generate a table of path computations for each point on a track.
 * Allows adding columns.
 * @author Alex Athanasopoulos
 *
 */
public class StandardTrackTable extends TrackTable {
  protected int       closestIndex;
  protected Waypoint  closestWaypoint;
  protected long  speedStartTime;
  protected float speedStartPosition;
  protected float speed;
  
  public StandardTrackTable() {
    addColumn(new TrackColumn("time") {
      @Override
      public Object getValue() {
        return getElapsedTime();
      }}
    );
    addColumn(new TrackColumn("lat") {
      @Override
      public Object getValue() {
        return waypoint.getLat();
      }}
    );
    addColumn(new TrackColumn("lon") {
      @Override
      public Object getValue() {
        return waypoint.getLon();
      }}
    );
    addColumn(new TrackColumn("track_distance") {
      @Override
      public Object getValue() {
        return distance.getDistance();
      }}
    );
    addColumn(new TrackColumn("route_distance") {
      @Override
      public Object getValue() {
        return tracker.getPosition();
      }}
    );
    addColumn(new TrackColumn("track-route") {
      @Override
      public Object getValue() {
        return distance.getDistance() - tracker.getPosition();
      }}
    );
    addColumn(new TrackColumn("path_waypoint") {
      @Override
      public Object getValue() {
        return closestWaypoint.getName();
      }}
    );
    addColumn(new TrackColumn("path_index") {
      @Override
      public Object getValue() {
        return closestIndex;
      }}
    );
    addColumn(new TrackColumn("closest_d") {
      @Override
      public Object getValue() {
        return Earth.distance(waypoint, closestWaypoint);
      }}
    );
    addColumn(new TrackColumn("route speed") {
      @Override
      public Object getValue() {
        return speed;
      }});
    addColumn(new TrackColumn("time to end") {
      @Override
      public Object getValue() {
        return (path.getLength() - tracker.getPosition())/speed;
      }});
  }

  float computeSpeed() {
    long time = waypoint.getTime().getTime();
    if ( speedStartTime == 0 ) {
      speedStartTime = time;
      speedStartPosition = tracker.getPosition();
      return 0f;
    } else {
      time -= speedStartTime;
      float distance = tracker.getPosition() - speedStartPosition;
      return distance * 1000f / time;
    }      
  }
  
 @Override
  public void compute() {
    super.compute();
    closestIndex = tracker.getNearestIndex();
    closestWaypoint = path.getWaypoints()[closestIndex];
    speed = computeSpeed();
  }
  
  
}