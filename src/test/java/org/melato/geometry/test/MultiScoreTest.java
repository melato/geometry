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

import junit.framework.Assert;

import org.junit.Test;
import org.melato.geometry.gpx.MultiScoreTrackMatcher.MultiScore;

public class MultiScoreTest {
  public @Test void scoreTest() {
    MultiScore score1 = new MultiScore();
    score1.setId("a");
    MultiScore score2 = new MultiScore();
    score2.setId("b");
    score1.setDominantDirection(1);
    score2.setDominantDirection(-1);
    MultiScore[] array = new MultiScore[] { score1, score2 };
    Arrays.sort(array);
    Assert.assertEquals(1, array[0].getDominantDirection());    
  }

}
