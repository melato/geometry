package org.melato.geometry.gpx;

import org.melato.gpx.util.PathTracker;
import org.melato.gpx.util.SimplePathTracker;

public class Algorithm {
  public static PathTracker newPathTracker() {
    return new SimplePathTracker();
  }
}
