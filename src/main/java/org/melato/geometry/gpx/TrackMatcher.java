package org.melato.geometry.gpx;

import java.util.List;

import org.melato.gpx.Waypoint;


/**
 * Matches a track against a route and creates a matching score that can be used to select
 * the best route that matches the track.
 * @author Alex Athanasopoulos
 *
 */
public class TrackMatcher {
  private TrackMatchingAlgorithm algorithm = new SequenceTrackMatcher();

  public TrackMatcher(List<Waypoint> trackWaypoints, float targetDistance) {
    algorithm.setProximityDistance(targetDistance);
    algorithm.setTrack(trackWaypoints);
  }

  /** Compute the score for a route.
   * @param route The route, specified as a sequence of waypoints.
   * @param score
   */
  public Score computeScore(List<Waypoint> route) {
    return algorithm.computeScore(route);
  }

  public TrackMatchingAlgorithm getAlgorithm() {
    return algorithm;
  }
}
