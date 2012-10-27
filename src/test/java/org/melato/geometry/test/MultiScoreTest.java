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
