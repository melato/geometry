package org.melato.geometry.gpx;

import java.io.IOException;
import java.util.List;

import org.melato.gpx.Waypoint;

public interface WaypointsSpecifier {
  List<Waypoint> loadWaypoints() throws IOException;
  String getName();
}
