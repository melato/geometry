package org.melato.geometry.test;

import junit.framework.Assert;

import org.junit.Test;
import org.melato.geometry.gpx.RollingSpeedManager;
import org.melato.geometry.gpx.RollingSpeedManager.RollingSpeed;
import org.melato.gps.Point;

public class RollingSpeedTest {
  public @Test void constant() {
    RollingSpeedManager speedManager = new RollingSpeedManager(new TestMetric());
    RollingSpeed speed5 = speedManager.getRollingSpeed(5);
    for( int i = 0; i < 20; i++ ) {
      Point p = new Point( i, 0);
      p.setTime(i*1000L);
      speedManager.addPoint(p);
    }
    Assert.assertEquals(1.0f, speed5.getSpeed(), 1e-5);
  }
  public @Test void half() {
    RollingSpeedManager speedManager = new RollingSpeedManager(new TestMetric());
    RollingSpeed speed = speedManager.getRollingSpeed(5);
    for( int i = 0; i < 4; i++ ) {
      Point p = new Point( i, 0);
      p.setTime(i*1000L);
      speedManager.addPoint(p);
    }
    for( int i = 4; i < 6; i++ ) {
      Point p = new Point( 4, 0);
      p.setTime(i*1000L);
      speedManager.addPoint(p);
    }
    Assert.assertEquals(5, speed.getCount());
    Assert.assertEquals(0.8f, speed.getSpeed(), 1e-5);
  }

}
