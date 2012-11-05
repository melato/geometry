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
package org.melato.geometry.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.melato.geometry.gpx.ProximityFinder;
import org.melato.gps.Earth;
import org.melato.gpx.Waypoint;

public class ProximityFinderTest {
  /**
   * create a path going South to North
   * and test two nearby points for proximity
   */
  public @Test void isNearVertical() {
    float lat = 38f;
    float lon = 24f;
    float step = 0.001f;
    int size = 30;
    int offset = 10;
    
    Waypoint[] seq = new Waypoint[size];
    for( int i = 0; i < size; i++ ) {
      seq[i] = new Waypoint(38 + (i-offset) * step + step / 3, lon);
    }
    Waypoint query = new Waypoint(lat, 24f + step);
    Waypoint s0 = seq[offset];  // this is the nearest point on the sequence.
    ProximityFinder f = new ProximityFinder();
    f.setWaypoints(Arrays.asList(seq));
    float d = Earth.distance(s0, query);
    f.setTargetDistance(d + 50);
    Assert.assertTrue(f.isNear(query));
    query = new Waypoint(lat, 24f + 2 * step );
    f.setTargetDistance(d);
    Assert.assertFalse(f.isNear(query));
  }
  
  /**
   * create a path going South to North
   * and test a nearby point for proximity
   */
  public @Test void findPointsVertical() {
    float lat = 38f;
    float lon = 24f;
    float step = 0.001f;
    int size = 30;
    int offset = 10;
    
    Waypoint[] seq = new Waypoint[size];
    for( int i = 0; i < size; i++ ) {
      seq[i] = new Waypoint(38 + (i-offset) * step, lon);
      // seq[offset] should be the closest point
    }
    Waypoint query = new Waypoint(lat, 24f + step);
    Waypoint s0 = seq[offset];  // this is the nearest point on the sequence.
    float d = Earth.distance(s0, query);
    ProximityFinder f = new ProximityFinder();
    f.setWaypoints(Arrays.asList(seq));
    f.setTargetDistance(d + 50); // add a small amount to the known distance to account for rounding errors.
    List<Integer> nearby = f.findNearbyIndexes(query);
    Assert.assertEquals(1, nearby.size());
    int index = nearby.get(0);
    Assert.assertEquals( offset, index );
    int closest = f.findClosestNearby(query);
    Assert.assertEquals(offset, closest);
  }
  
}
