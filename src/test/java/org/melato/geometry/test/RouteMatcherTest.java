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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.melato.geometry.gpx.RouteMatcher;
import org.melato.geometry.gpx.RouteMatcher.Approach;

public class RouteMatcherTest {
  private List<Approach> filter(List<Approach> list) {
    Approach[] approaches = list.toArray(new Approach[0]);
    RouteMatcher.filter(approaches);
    int size = RouteMatcher.pack(approaches);
    return Arrays.asList(approaches).subList(0,  size);
  }
  public @Test void filterTwo() {
    List<Approach> approaches = new ArrayList<Approach>();
    approaches.add(new Approach(2,100));
    approaches.add(new Approach(3,101));
    approaches = filter(approaches);
    Assert.assertEquals(2, approaches.size());
    Assert.assertEquals(2, approaches.get(0).routeIndex);
    Assert.assertEquals(3, approaches.get(1).routeIndex);
  }
  public @Test void filterFirst() {
    List<Approach> approaches = new ArrayList<Approach>();
    approaches.add(new Approach(20,99));
    approaches.add(new Approach(2,100));
    approaches.add(new Approach(3,101));
    approaches = filter(approaches);
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
    approaches = filter(approaches);
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
    approaches = filter(approaches);
    Assert.assertEquals(4, approaches.size());
    Assert.assertEquals(2, approaches.get(0).routeIndex);
    Assert.assertEquals(3, approaches.get(1).routeIndex);
    Assert.assertEquals(4, approaches.get(2).routeIndex);
    Assert.assertEquals(17, approaches.get(3).routeIndex); // not what I expected, but this is the algorithm.
  }
}
