package org.melato.geometry.gpx;



public class Algorithm {
  public static TrackingAlgorithm newTrackingAlgorithm() {
    return new SequentialPathTracker();
  }
}
