package org.melato.geometry.gpx;

import org.melato.gpx.util.TrackingAlgorithm;


public class Algorithm {
  public static TrackingAlgorithm newTrackingAlgorithm() {
    return new SequentialPathTracker();
  }
}
