package org.melato.geometry.ant;

import org.melato.gpx.util.SimplePathTracker;

/**
 * Given a GPX track and GPX route, compute the path distance of each point on the track.
 * Output the results to a table with columns: lat, lon, path distance.
 * @author Alex Athanasopoulos
 *
 */
public class SimplePathTask extends PathTask {
  
  
  @Override
  protected TrackTable createTable() {
    return new SimpleTrackTable();
  }

  public SimplePathTask() {
    super();
    setTracker( new SimplePathTracker() );
  }
}
