package org.melato.geometry.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.melato.app.ant.AppType;
import org.melato.common.util.Filenames;
import org.melato.export.CsvWriter;
import org.melato.export.TableWriter;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.gpx.util.PathTracker;

public class PathTask extends AppType {
  protected File trackFile;
  protected File routeFile;
  protected TableWriter tableWriter;
  protected PathTracker pathTracker;

  public PathTask() {
    super();
  }

  public void setTrackFile(File trackFile) {
    this.trackFile = trackFile;
  }

  public void setRouteFile(File routeFile) {
    this.routeFile = routeFile;
  }

  public void setOutputDir(File outputDir) {
    tableWriter = new CsvWriter(outputDir);
  }  

  public void setPathTracker(PathTracker pathTracker) {
    this.pathTracker = pathTracker;
  }

  protected GPX readGPX(File file) throws IOException {
    GPXParser parser = new GPXParser();
    return parser.parse(file);
  }

  protected TrackTable createTable() {
    return new StandardTrackTable();    
  }
  public void execute() throws IOException {
    GPX route = readGPX(routeFile);
    GPX track = readGPX(trackFile);
    TrackTable table = createTable();
    Path path = new Path(route.getRoutes().get(0).getWaypoints());
    table.setPath(path);
    pathTracker.setPath(path);
    table.setTracker(pathTracker);
    List<Waypoint> waypoints = new ArrayList<Waypoint>();
    for( Waypoint p: GPXIterators.trackWaypoints(track.getTracks())) {
      waypoints.add(p);
    }
    table.setTrackWaypoints(waypoints);
    try {
      tableWriter.tableOpen(Filenames.getBasename(trackFile));
      table.writeTable(tableWriter);
   } finally {
      tableWriter.tableClose();
    }
  }
  
}