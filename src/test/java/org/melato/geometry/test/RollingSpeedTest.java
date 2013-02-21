/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
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

import junit.framework.Assert;

import org.junit.Test;
import org.melato.geometry.gpx.RollingSpeedManager;
import org.melato.geometry.gpx.RollingSpeedManager.RollingSpeed;
import org.melato.gps.PointTime;

public class RollingSpeedTest {
  public @Test void constant() {
    RollingSpeedManager speedManager = new RollingSpeedManager(new TestMetric());
    RollingSpeed speed5 = speedManager.getRollingSpeed(5);
    for( int i = 0; i < 20; i++ ) {
      PointTime p = new PointTime( i, 0);
      p.setTime(i*1000L);
      speedManager.addPoint(p);
    }
    Assert.assertEquals(1.0f, speed5.getSpeed(), 1e-5);
  }
  public @Test void half() {
    RollingSpeedManager speedManager = new RollingSpeedManager(new TestMetric());
    RollingSpeed speed = speedManager.getRollingSpeed(5);
    for( int i = 0; i < 4; i++ ) {
      PointTime p = new PointTime( i, 0);
      p.setTime(i*1000L);
      speedManager.addPoint(p);
    }
    for( int i = 4; i < 6; i++ ) {
      PointTime p = new PointTime( 4, 0);
      p.setTime(i*1000L);
      speedManager.addPoint(p);
    }
    Assert.assertEquals(5, speed.getCount());
    Assert.assertEquals(0.8f, speed.getSpeed(), 1e-5);
  }
  public @Test void twoSpeeds() {
    RollingSpeedManager speedManager = new RollingSpeedManager(new TestMetric());
    RollingSpeed speed5 = speedManager.getRollingSpeed(5);
    RollingSpeed speed10 = speedManager.getRollingSpeed(10);
    for( int i = 0; i < 20; i++ ) {
      PointTime p = new PointTime( i, 0);
      p.setTime(i*1000L);
      speedManager.addPoint(p);
    }
    Assert.assertEquals(1.0f, speed5.getSpeed(), 1e-5);
  }

}
