package org.melato.geometry.gpx;

import org.melato.gpx.Point;
import org.melato.gpx.util.Path;
import org.melato.gpx.util.TrackingAlgorithm;

/**
 * Matches actual track locations to a fixed path.
 * @author Alex Athanasopoulos
 *
 */
public class PathTracker {
  private TrackingAlgorithm tracker;
  private Path path;
  private Point location;

  public PathTracker(TrackingAlgorithm tracker) {
    super();
    this.tracker = tracker;
  }

  public PathTracker() {
    this(Algorithm.newTrackingAlgorithm());
  }
  
  public TrackingAlgorithm getTracker() {
    return tracker;
  }

  public void setPath(Path path) {
    this.path = path;
    tracker.setPath(path);
  }
  
  public Path getPath() {
    return path;
  }

  public Point getLocation() {
    return location;
  }

  public void clearLocation() {
    this.location = null;
    tracker.clearLocation();
  }

  /**
   * Add a new location to the track, which becomes the current location.
   * The sequence of calls to setLocation() matters because it provides a series of past locations
   * that the path may use in its algorithm.
   * @param p
   */
  public void setLocation(Point p) {
    this.location = p;
    tracker.setLocation(p);
  }

  /**
   * Return the index of the waypoint nearest the current location, along the current path.
   * @return
   */
  public int getNearestIndex() {
    return tracker.getNearestIndex();
  }

  /**
   * Return the distance of the current location from the beginning of the path, along the path.
   * If the current location is not in the path, the current position is approximately the position of the nearest waypoint.
   * @param p
   * @return
   */
  public float getPosition() {
    return tracker.getPosition();
  }
}