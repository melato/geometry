package org.melato.geometry.gpx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.melato.common.util.Filenames;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.Waypoint;

public class FileGpxSpecifier implements WaypointsSpecifier {
  private File file;
  private boolean route;
  
  public void setFile(File file) {
    this.file = file;
  }

  
  public FileGpxSpecifier() {
  }

  public FileGpxSpecifier(File file) {
    this.file = file;
  }

  @Override
  public List<Waypoint> loadWaypoints() throws IOException {
    GPXParser parser = new GPXParser();
    GPX gpx = parser.parse(file);
    List<Waypoint> waypoints = new ArrayList<Waypoint>();
    if ( route ) {
      
    } else {
      for( Waypoint p: GPXIterators.trackWaypoints(gpx.getTracks())) {
        waypoints.add(p);
      }      
    }
    return waypoints;
  }
  
  @Override
  public String getName() {
    return Filenames.getBasename(file);
  }
}
