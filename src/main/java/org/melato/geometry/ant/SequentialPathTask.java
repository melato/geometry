package org.melato.geometry.ant;

import org.melato.geometry.gpx.SequentialPathTracker;

/**
 * Given a GPX track and GPX route, compute the path distance of each point on the track.
 * Output the results to a table with columns: lat, lon, path distance.
 * @author Alex Athanasopoulos
 *
 */
public class SequentialPathTask extends PathTask {
  
  
  @Override
  protected TrackTable createTable() {
    return new SequentialTrackTable();
  }

  public SequentialPathTask() {
    super();
    setTracker( new SequentialPathTracker() );
  }
}
