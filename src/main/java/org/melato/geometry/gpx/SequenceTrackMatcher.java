package org.melato.geometry.gpx;

import java.util.List;

import org.melato.geometry.gpx.RouteMatcher.Approach;
import org.melato.gpx.Waypoint;

public class SequenceTrackMatcher implements TrackMatchingAlgorithm {
  private float proximityDistance;
  private RouteMatcher matcher;
  
  @Override
  public void setProximityDistance(float targetDistance) {
    this.proximityDistance = targetDistance;
  }

  @Override
  public void setTrack(List<Waypoint> track) {
    matcher = new RouteMatcher(track, proximityDistance);
  }

  
  /** Compute the score for a route.
   * @param route The route, specified as a sequence of waypoints.
   * @param score
   */
  public Score computeScore(List<Waypoint> route) {
    Score score = new Score();
    List<Approach> approaches = matcher.match(route);
    score.setCount(approaches.size());
    return score;
  }
  
  /** Get the names of the score components.  Use for debugging. */
  @Override
  public String[] getScoreFieldNames() {
    return new String[] {
        "id", "count"};      
  }
  /** Get the score components as an array (corresponding to the field names).  Use for debugging. */
  @Override
  public Object[] getFields(Score score) {
    return new Object[] {
        score.getId(),
        score.getCount()};
  }
}
