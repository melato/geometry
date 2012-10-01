package org.melato.geometry.ant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.melato.ant.FileTask;
import org.melato.common.util.Filenames;
import org.melato.export.CsvWriter;
import org.melato.export.TableWriter;
import org.melato.gps.Point;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.gpx.util.ProximityFinder;

public class RouteMatchingTask extends FileTask {
  private File trackFile;
  private ProximityFinder trackProximity;
  private List<RouteScore> scores = new ArrayList<RouteScore>();
  protected TableWriter tableWriter;
  private int minScore = 1;
  private int maxCount;
  private float targetDistance = 100;

  static class RouteScore implements Comparable<RouteScore> {
    String  routeName;
    int     nearCount = 0;
    int     directionChanges;
    int     dominantDirection;
    float   meanSeparation;
    public RouteScore(String routeName) {
      this.routeName = routeName;
    }    
    @Override
    public int compareTo(RouteScore t) {
      return t.nearCount - this.nearCount;
    }
    @Override
    public String toString() {
      return routeName + " " + nearCount;
    }
  }
    
  public void setTrackFile(File trackFile) {
    this.trackFile = trackFile;
  }

  public void setMinScore(int minScore) {
    this.minScore = minScore;
  }

  public void setMaxCount(int maxCount) {
    this.maxCount = maxCount;
  }
  
  public void setOutputDir(File outputDir) {
    tableWriter = new CsvWriter(outputDir);
  }  
  
  public void setTargetDistance(float targetDistance) {
    this.targetDistance = targetDistance;
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
      trackProximity.setTargetDistance(targetDistance);
    }
    return trackProximity;
  }
  
  private int nearCount(List<Waypoint> waypoints) {
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

  private static int sign(int d) {
    if ( d > 0 ) return 1;
    if ( d < 0 ) return -1;
    return 0;
  }
  
  private int maxIndex(int[] values) {
    if ( values.length == 0 )
      return -1;
    int maxValue = values[0];
    int maxIndex = 0;
    for( int i = 1; i < values.length; i++ ) {
      if ( values[i] > maxValue ) {
        maxValue = values[i];
        maxIndex = i;
      }
    }
    return maxIndex;
  }
  
  private void computeScore(List<Waypoint> route, RouteScore score) {
    Path path = new Path(route);
    ProximityFinder track = getTrackProximityFinder();
    double separationSum = 0;
    int size = path.size();
    int nearCount = 0;
    int directionChanges = 0;
    int[] directionCounts = new int[3];
    int lastDirection = 0;
    int lastTrackIndex = -1;
    for( int i = 0; i < size; i++ ) {
      Point p = path.getWaypoint(i);
      int trackIndex = track.findClosestNearby(p);
      if ( trackIndex >= 0 ) {
        nearCount++;
        separationSum += track.getMetric().distance(p,  track.getWaypoints()[trackIndex]);
        if ( nearCount > 1 ) {
          int direction = sign( trackIndex - lastTrackIndex );
          directionCounts[1+direction]++;
          if ( nearCount > 2 && lastDirection != direction ) {
            directionChanges++;            
          }
          lastDirection = direction;
        }
        lastTrackIndex = trackIndex;
      }
      /*
      if ( track.isNear(p)) {
        nearCount++;        
      }
      */
    }
    score.nearCount = nearCount;
    score.meanSeparation = (float) (separationSum / nearCount);
    score.dominantDirection = maxIndex(directionCounts) - 1;
    score.directionChanges = directionChanges;
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
      RouteScore score = new RouteScore(routeName);
      computeScore(gpx.getRoutes().get(i).getWaypoints(), score);
      //score.nearCount = nearCount( gpx.getRoutes().get(i).getWaypoints() );
      scores.add(score);
    }
  }

  @Override
  protected void processFiles() throws IOException {
    super.processFiles();
    RouteScore[] scores = this.scores.toArray(new RouteScore[0]);
    Arrays.sort(scores);
    try {
      tableWriter.tableOpen(Filenames.getBasename(trackFile));
      tableWriter.tableHeaders(new String[] {
          "route", "nearCount", "meanSeparation", "directionChanges", "dominantDirection"});
      for( int i = 0; i < scores.length; i++ ) {
        RouteScore score = scores[i];
        if ( maxCount > 0 && i >= maxCount ) {
          break;
        }
        if ( scores[i].nearCount < minScore ) {
          break;
        }
        tableWriter.tableRow(new Object[] {
            score.routeName,
            score.nearCount,
            score.meanSeparation,
            score.directionChanges,
            score.dominantDirection,
        });
      }
    } finally {
      tableWriter.tableClose();
    }
  }
  
}
