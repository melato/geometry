package org.melato.geometry.ant;

import org.melato.gpx.Earth;
import org.melato.gpx.util.SimplePathTracker;


public class SimpleTrackTable extends StandardTrackTable {
  private int[] near2;  
  
  @Override
  public void compute() {
    SimplePathTracker simpleTracker = (SimplePathTracker) tracker;
    near2 = simpleTracker.find2Neighbors(waypoint);
  }


  public SimpleTrackTable() {
    super();
    addColumn(new TrackColumn("near1") {
      @Override
      public Object getValue() {
        return near2[0];
      }}
    );
    addColumn(new TrackColumn("near2") {
      @Override
      public Object getValue() {
        return near2[1];
      }}
    );
    addColumn(new TrackColumn("d1") {
      @Override
      public Object getValue() {
        return Earth.distance(waypoint, path.getWaypoints()[near2[0]]);
      }}
    );
    addColumn(new TrackColumn("d2") {
      @Override
      public Object getValue() {
        return Earth.distance(waypoint, path.getWaypoints()[near2[1]]);
      }}
    );
    addColumn(new TrackColumn("p1") {
      @Override
      public Object getValue() {
        return path.getLength(near2[0]);
      }}
    );
    addColumn(new TrackColumn("p2") {
      @Override
      public Object getValue() {
        return path.getLength(near2[0]);
      }}
    );    
  }

}
