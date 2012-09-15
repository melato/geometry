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
        return distance.getDistance() - tracker.getPosition();
      }}
    );
    addColumn(new TrackColumn("path_waypoint") {
      @Override
      public Object getValue() {
        return getPath().getWaypoint(getTracker().getNearestIndex()).getName();
      }}
    );
    addColumn(new TrackColumn("path_index") {
      @Override
      public Object getValue() {
        return getTracker().getNearestIndex();
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
        return speed.getSpeed();
      }});
    addColumn(new TrackColumn("time to end") {
      @Override
      public Object getValue() {
        float time = speed.getRemainingTime(path.size()-1);
        if ( Float.isNaN(time)) {
          return "";
        }
        return (int) time;
      }});
    addColumn(new TrackColumn("ETA") {
      @Override
      public Object getValue() {
        return speed.getETA(path.size()-1);
      }});
    /*
    addColumn(new TrackColumn("algorithm") {
      @Override
      public Object getValue() {
        return getTrackingAlgorithm();
      }});
    */
  }

 @Override
  public void compute() {
    super.compute();
    closestWaypoint = path.getWaypoint(tracker.getNearestIndex());
    speed.compute();
  }
  
  
}