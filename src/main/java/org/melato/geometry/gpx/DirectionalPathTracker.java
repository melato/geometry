/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.geometry.gpx;

import org.melato.gps.PointTime;
import org.melato.log.Log;


/**
 * PathTracker that tracks its position by looking for pairs of consecutive waypoints p1, p2
 * so that the current position moves from p1 to p2.
 */
public class DirectionalPathTracker extends BasePathTracker2 {

  private int findBestPair() {
    int bestIndex = -1;
    float bestDistance = 0;
    int n = path.size() - 1;
    for( int i = 0; i < n; i++ ) {
      float pairDistance = Math.min(current.absDistance(i), current.absDistance(i+1));
      if ( (bestIndex < 0 || pairDistance < bestDistance) && isMoving(i, i+1)) {
        bestIndex = i;
        bestDistance = pairDistance;
      }
    }
    return bestIndex;
  }
  
  @Override
  public void setLocation(PointTime point) {
    Log.info( "setLocation: " + point );
    if ( ! setCurrentLocation(point)) {
      Log.info( "setCurrentLocation: false" );
      return;
    }
    if ( inPath ) {        
      if ( isLeaving(currentIndex) && isApproaching(currentIndex+1) ) {
        // we seem to be moving from 0 to 1
        pathPosition = interpolatePosition(0);
        //Log.info( "approaching " + currentWaypoint );
      } else if ( isLeaving(currentIndex+1) && isApproaching(currentIndex+2) ) {
        // move to the next pair
        pathPosition = interpolatePosition(1);
        setCurrentIndex(currentIndex + 1);
      } else {
        //Log.info( "left path");
        // we are not approaching any path waypoint.  Assume we are no longer following it.
        setInitialLocation(point);
      }
    } else {
      setInitialLocation(point);
      int best = findBestPair();
      if ( best >= 0 ) {
        setCurrentIndex(best);
        inPath = true;
        pathPosition = interpolatePosition(0);
      }
    }
    //nearestIndex = path.findNearestIndex(location, currentIndex-1, currentIndex+1);
    Log.info( this );
  }  
}
