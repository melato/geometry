package org.melato.geometry.test;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;
import org.melato.geometry.gpx.TrackMatcher.Score;

public class TrackMatcherTest {
  public @Test void scoreTest() {
    Score score1 = new Score("a");
    Score score2 = new Score("b");
    score1.setDominantDirection(1);
    score2.setDominantDirection(-1);
    Score[] array = new Score[] { score1, score2 };
    Arrays.sort(array);
    Assert.assertEquals(1, array[0].getDominantDirection());    
  }

}
