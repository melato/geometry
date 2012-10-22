package org.melato.geometry.gpx;

import java.util.List;

import org.melato.gps.Point;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.gpx.util.ProximityFinder;


/**
 * Matches a track against a route and creates a matching score that can be used to select
 * the best route that matches the track.
 * @author Alex Athanasopoulos
 *
 */
public class TrackMatcher {
  private ProximityFinder proximity = new ProximityFinder();

  /** A multi-valued score for the match.
   * The score is computed as follows.
   * Every point of the route is matched against a point on the track, if it is near the track.
   * Nearness is defined by having distance is less than the targetDistance.
   * Every matching route point after the first one, defines a direction.
   * The direction is +1 (-1) if the index of the closest track point is greater (less) than the index of the previously
   * matched track point.
   * The direction is 0 if the route point matched the same track point as the previous route point.
   * This is not so unusual as it first seems.  It is common if the track is unrelated to the route.
   * The dominant direction is the direction that has the highest number of occurences.
   * The number of direction changes is the number of times that the computed direction
   * changes from the previously computed direction.
   * Mean-separation is the mean distance between each matching pair between route points and track points.
   * The built-in score comparator sorts by:
   *   nearCount desc,
   *   dominantDirection,
   *   directionChanges asc,
   *   meanSeparation asc. 
   * */
  public static class Score implements Comparable<Score> {
    Object  id;
    int     nearCount = 0;
    int     directionChanges;
    int     dominantDirection;
    float   meanSeparation;
    
    public Score(Object id) {
      this.id = id;
    }    
    @Override
    public int compareTo(Score t) {
      // return -1 if this object has better score than t (it is "smaller" in the sorting order).
      // larger near count sorts first
      int d = t.nearCount - this.nearCount;
      if ( d != 0 )
        return d;
      if ( dominantDirection != t.dominantDirection ) {
        // non-zero direction sorts first
        if ( dominantDirection == 0 )
          return 1;
        if ( t.dominantDirection == 0 )
          return -1;
        // positive direction sorts first
        return t.dominantDirection - dominantDirection;
      }
      // smaller direction changes sorts first
      d = directionChanges - t.directionChanges;
      if ( d != 0 )
        return d;
      // smaller mean separation sorts first
      return sign( meanSeparation - t.meanSeparation );
    }
    @Override
    public String toString() {
      return id + " " + nearCount;
    }
    /**
     * The id of the score, as passed in the constructor.
     * Used for identification.
     * @return
     */
    public Object getId() {
      return id;
    }
    
    public void setId(Object id) {
      this.id = id;
    }
    /**
     * The number of route points that are near the track.
     * @return
     */
    public int getNearCount() {
      return nearCount;
    }
    /**
     * The number of times the matched direction changes
     * @return
     */
    public int getDirectionChanges() {
      return directionChanges;
    }
    /**
     * Get the dominant direction for the match.
     * The dominant direction is the most common direction.
     * @return
     */
    public int getDominantDirection() {
      return dominantDirection;
    }
    /** Get the average distance between a route point and its corresponding matched track point. */
    public float getMeanSeparation() {
      return meanSeparation;
    }
    
    public void setNearCount(int nearCount) {
      this.nearCount = nearCount;
    }
    public void setDirectionChanges(int directionChanges) {
      this.directionChanges = directionChanges;
    }
    public void setDominantDirection(int dominantDirection) {
      this.dominantDirection = dominantDirection;
    }
    public void setMeanSeparation(float meanSeparation) {
      this.meanSeparation = meanSeparation;
    }    
    
  }

  public TrackMatcher(List<Waypoint> trackWaypoints, float targetDistance) {
    proximity = new ProximityFinder();
    proximity.setWaypoints(trackWaypoints);
    proximity.setTargetDistance(targetDistance);
  }

  /** Compute the score for a route.
   * @param route The route, specified as a sequence of waypoints.
   * @param score
   */
  public void computeScore(List<Waypoint> route, Score score) {
    Path path = new Path(route);
    double separationSum = 0;
    int size = path.size();
    int nearCount = 0;
    int directionChanges = 0;
    int[] directionCounts = new int[3];
    int lastDirection = 0;
    int lastTrackIndex = -1;
    for( int i = 0; i < size; i++ ) {
      Point p = path.getWaypoint(i);
      int trackIndex = proximity.findClosestNearby(p);
      if ( trackIndex >= 0 ) {
        nearCount++;
        separationSum += proximity.getMetric().distance(p,  proximity.getWaypoints()[trackIndex]);
        if ( nearCount > 1 ) {
          int direction = sign( trackIndex - lastTrackIndex );
          directionCounts[1+direction]++;
          if ( nearCount > 2 && lastDirection != direction ) {
            directionChanges++;            
          }
          lastDirection = direction;
        }
        lastTrackIndex = trackIndex;
      }
    }
    score.nearCount = nearCount;
    score.meanSeparation = (float) (separationSum / nearCount);
    score.dominantDirection = maxIndex(directionCounts) - 1;
    score.directionChanges = directionChanges;
  }

  private static int sign(int d) {
    if ( d > 0 ) return 1;
    if ( d < 0 ) return -1;
    return 0;
  }
  
  private static int sign(float d) {
    if ( d > 0 ) return 1;
    if ( d < 0 ) return -1;
    return 0;
  }
  
  private static int maxIndex(int[] values) {
    if ( values.length == 0 )
      return -1;
    int maxValue = values[0];
    int maxIndex = 0;
    for( int i = 1; i < values.length; i++ ) {
      if ( values[i] > maxValue ) {
        maxValue = values[i];
        maxIndex = i;
      }
    }
    return maxIndex;
  }
}
