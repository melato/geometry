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
        return tracker.getPosition();
      }}
    );
    addColumn(new TrackColumn("closest_waypoint") {
      @Override
      public Object getValue() {
        return closestWaypoint.getName();
      }}
    );
    addColumn(new TrackColumn("closest_index") {
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
  }

  @Override
  public void compute(Waypoint p) {
    super.compute(p);
    closestIndex = path.findNearestIndex(p);
    closestWaypoint = path.getWaypoints()[closestIndex];
  }
  
  
}