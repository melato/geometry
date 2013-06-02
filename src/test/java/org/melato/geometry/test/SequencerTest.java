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
import org.melato.geometry.util.Sequencer;

public class SequencerTest {
  private void verify(int[] list, int[] expected) {
    Sequencer.filter(list);
    for( int i = 0; i < list.length; i++ ) {
      Assert.assertEquals(expected[i], list[i]);
    }
  }
  public @Test void filterTwo() {
    verify( new int[] {2, 3},
            new int[] {2, 3} );
  }
  public @Test void filterFirst() {
    verify( new int[] {20, 2, 3},
            new int[] {-1, 2, 3} );
  }

  public @Test void filterDuplicates() {
    verify( new int[] {1, 1, 2, 2},
            new int[] {-1,1, 2, -1} );
  }

  public @Test void filterDuplicates3() {
    verify( new int[] {1, 1, 1, 2, 2},
            new int[] {-1,-1,1, 2, -1} );
  }

  public @Test void filterHairpin() {
    verify( new int[] {2, 19, 3, 18, 4, 17},
            new int[] {2, -1, 3, -1, 4, 17});
  }
  
  public @Test void filterE22() {
    int[] list = new int[] {
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
      19,
      16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41};
    int[] expected1 = new int[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        -1,
        16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41};
    int[] expected2 = new int[] {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        19,
        -1, -1, -1, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41};
    verify(list, expected2);
  }
  
}
