package org.melato.geometry.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.melato.ant.FileTask;
import org.melato.common.util.Filenames;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.gpx.util.ProximityFinder;

public class RouteMatchingTask extends FileTask {
  private File trackFile;
  private ProximityFinder trackProximity;
  private List<RouteScore> scores = new ArrayList<RouteScore>();

  static class RouteScore implements Comparable<RouteScore> {
    String routeName;
    int   score;
    public RouteScore(String routeName, int score) {
      this.routeName = routeName;
      this.score = score;
    }
    @Override
    public int compareTo(RouteScore t) {
      return t.score - this.score;
    }
    @Override
    public String toString() {
      return routeName + " " + score;
    }
  }
  
  
  public void setTrackFile(File trackFile) {
    this.trackFile = trackFile;
  }

  GPX readGPX(File file) throws IOException {
    GPXParser parser = new GPXParser();
    return parser.parse(file);
  }
  
  ProximityFinder getTrackProximityFinder() {
    if ( trackProximity == null ) {
      GPX gpx;
      try {
        gpx = readGPX(trackFile);
      } catch (IOException e) {
        throw new RuntimeException( e );
      }
      List<Waypoint> list = new ArrayList<Waypoint>();
      for( Waypoint p: GPXIterators.trackWaypoints(gpx.getTracks())) {
        list.add(p);
      }
      trackProximity = new ProximityFinder();
      trackProximity.setWaypoints(list);
    }
    return trackProximity;
  }
  
  private int matchRoute(List<Waypoint> waypoints, String name ) {
    Path path = new Path(waypoints);
    ProximityFinder track = getTrackProximityFinder();
    int size = path.size();
    if ( size < 2 )
      return 0;
    int match = 0;
    for( int i = 0; i < size; i++ ) {
      float targetDistance = 0;
      if ( i > 0 ) {
        targetDistance = path.getLength(i-1,i);
      }
      if ( i + 1 < size ) {
        targetDistance = Math.max(targetDistance, path.getLength(i,i+1));
      }
      track.setTargetDistance(targetDistance);
      if ( track.isNear(path.getWaypoint(i))) {
        match++;        
      }
    }
    return match;
  }
  
  @Override
  protected void processFile(File file) throws IOException {
    //System.out.println(file);
    String basename = Filenames.getBasename(file); 
    GPX gpx = readGPX(file);
    for( int i = 0; i < gpx.getRoutes().size(); i++ ) {
      String routeName = basename;
      if ( i > 0 )
        routeName += "." + i; 
      int score = matchRoute( gpx.getRoutes().get(i).getWaypoints(), routeName );
      scores.add( new RouteScore(routeName, score));
    }
  }

  @Override
  protected void processFiles() throws IOException {
    super.processFiles();
    RouteScore[] scores = this.scores.toArray(new RouteScore[0]);
    Arrays.sort(scores);
    System.out.println( "scores: " + scores.length);
    for( int i = 0; i < 10 && i < scores.length; i++ ) {
      System.out.println( scores[i]);
    }
  }
  
}
