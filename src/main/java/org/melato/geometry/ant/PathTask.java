package org.melato.geometry.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.melato.ant.FileTask;
import org.melato.common.util.Filenames;
import org.melato.export.CsvWriter;
import org.melato.export.TableWriter;
import org.melato.geometry.gpx.PathTracker;
import org.melato.geometry.gpx.TrackingAlgorithm;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;

/**
 * Ant path for testing a PathTracker algorithm.
 * For each input track file, it outpus a csv file in the output directory
 * The output file contains one row for each track waypoint with the corresponding information from the path tracker. 
 *  
 * @author Alex Athanasopoulos
 *
 */
public class PathTask extends FileTask {
  protected File routeFile;
  protected TableWriter tableWriter;
  protected PathTracker pathTracker = new PathTracker();
  private   Path path;

  public PathTask() {
    super();
  }

  /**
   * The route to use 
   * @param routeFile A GPX file with a route
   */
  public void setRouteFile(File routeFile) {
    this.routeFile = routeFile;
  }

  public void setOutputDir(File outputDir) {
    tableWriter = new CsvWriter(outputDir);
  }  

  public void setTracker(TrackingAlgorithm pathTracker) {
    this.pathTracker = new PathTracker(pathTracker);
  }

  protected Path getPath() throws IOException {
    if ( path == null ) {
      GPX gpx = readGPX(routeFile);
      path = new Path(gpx.getRoutes().get(0).getWaypoints());
      System.out.println("route waypoints: " + path.size());
    }
    return path;    
  }
  protected GPX readGPX(File file) throws IOException {
    GPXParser parser = new GPXParser();
    return parser.parse(file);
  }

  protected TrackTable createTable() {
    return new StandardTrackTable();    
  }
  @Override
  public void processFile(File file) throws IOException {
    Path path = getPath();
    System.out.println( file );
    GPX track = readGPX(file);
    TrackTable table = createTable();
    table.setPath(path);
    pathTracker.setPath(path);
    pathTracker.clearLocation();
    table.setTracker(pathTracker);
    List<Waypoint> waypoints = new ArrayList<Waypoint>();
    for( Waypoint p: GPXIterators.trackWaypoints(track.getTracks())) {
      waypoints.add(p);
    }
    table.setTrackWaypoints(waypoints);
    try {
      tableWriter.tableOpen(Filenames.getBasename(file));
      table.writeTable(tableWriter);
    } finally {
      tableWriter.tableClose();
    }
  }
  
}