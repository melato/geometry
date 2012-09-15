package org.melato.geometry.ant;

import org.melato.geometry.gpx.SequentialPathTracker;


public class SequentialTrackTable extends StandardTrackTable {
  private SequentialPathTracker tracker;
  @Override
  public void compute() {
    super.compute();
    tracker = (SequentialPathTracker) getTrackingAlgorithm();
  }


  public SequentialTrackTable() {
    super();
    addColumn(new TrackColumn("inPath") {
      @Override
      public Object getValue() {
        return tracker.isInPath();
      }});
    addColumn(new TrackColumn("currentIndex") {
      @Override
      public Object getValue() {
        return tracker.getCurrentIndex();
      }});
  }

}
