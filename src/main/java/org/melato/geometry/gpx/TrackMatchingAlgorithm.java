package org.melato.geometry.gpx;

import java.util.List;

import org.melato.gpx.Waypoint;

/**
 * Matches a track against a route and creates a matching score that can be used to select
 * the best route that matches the track.
 * @author Alex Athanasopoulos
 *
 */
public interface TrackMatchingAlgorithm {
  void setProximityDistance(float targetDistance);
  void setTrack(List<Waypoint> track);
  Score computeScore(List<Waypoint> route);
  String[] getScoreFieldNames();
  Object[] getFields(Score score);
}
