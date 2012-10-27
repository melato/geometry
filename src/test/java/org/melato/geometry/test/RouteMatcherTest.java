package org.melato.geometry.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.melato.geometry.gpx.RouteMatcher;
import org.melato.geometry.gpx.RouteMatcher.Approach;

public class RouteMatcherTest {
  public @Test void filterTwo() {
    List<Approach> approaches = new ArrayList<Approach>();
    approaches.add(new Approach(2,100));
    approaches.add(new Approach(3,101));
    RouteMatcher.filter(approaches);
    Assert.assertEquals(2, approaches.size());
    Assert.assertEquals(2, approaches.get(0).routeIndex);
    Assert.assertEquals(3, approaches.get(1).routeIndex);
  }
  public @Test void filterFirst() {
    List<Approach> approaches = new ArrayList<Approach>();
    approaches.add(new Approach(20,99));
    approaches.add(new Approach(2,100));
    approaches.add(new Approach(3,101));
    RouteMatcher.filter(approaches);
    Assert.assertEquals(2, approaches.size());
    Assert.assertEquals(2, approaches.get(0).routeIndex);
    Assert.assertEquals(3, approaches.get(1).routeIndex);
  }

  public @Test void filterDuplicates() {
    List<Approach> approaches = new ArrayList<Approach>();
    approaches.add(new Approach(1,101));
    approaches.add(new Approach(1,102));
    approaches.add(new Approach(2,103));
    approaches.add(new Approach(2,104));
    RouteMatcher.filter(approaches);
    Assert.assertEquals(2, approaches.size());
    Assert.assertEquals(1, approaches.get(0).routeIndex);
    Assert.assertEquals(102, approaches.get(0).trackIndex);
    Assert.assertEquals(2, approaches.get(1).routeIndex);
    Assert.assertEquals(103, approaches.get(1).trackIndex);
  }

  public @Test void filterHairpin() {
    List<Approach> approaches = new ArrayList<Approach>();
    approaches.add(new Approach(2,100));
    approaches.add(new Approach(19,101));
    approaches.add(new Approach(3,102));
    approaches.add(new Approach(18,103));
    approaches.add(new Approach(4,104));
    approaches.add(new Approach(17,105));
    RouteMatcher.filter(approaches);
    Assert.assertEquals(4, approaches.size());
    Assert.assertEquals(2, approaches.get(0).routeIndex);
    Assert.assertEquals(3, approaches.get(1).routeIndex);
    Assert.assertEquals(4, approaches.get(2).routeIndex);
    Assert.assertEquals(17, approaches.get(3).routeIndex); // not what I expected, but this is the algorithm.
  }
}
