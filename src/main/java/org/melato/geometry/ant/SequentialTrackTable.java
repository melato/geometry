package org.melato.geometry.ant;

import org.melato.geometry.gpx.SequentialPathTracker;


public class SequentialTrackTable extends StandardTrackTable {
  private boolean inPath;  
  
  @Override
  public void compute() {
    super.compute();
    SequentialPathTracker tracker = (SequentialPathTracker) getTracker();
    inPath = tracker.isInPath();
  }


  public SequentialTrackTable() {
    super();
    addColumn(new TrackColumn("inPath") {
      @Override
      public Object getValue() {
        return inPath;
      }}
    );
  }

}
