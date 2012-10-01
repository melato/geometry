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
import org.melato.geometry.gpx.TrackMatcher;
import org.melato.geometry.gpx.TrackMatcher.Score;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.Waypoint;

/** Ant task that compares a track against a list of routes and creates
 *  a match score for each route.
 *  In the end, it prints the routes and the scores, sorted by best score.
 *  The track file is specified by a property.
 *  The routes are specified as files from filesets. 
 * @author Alex Athanasopoulos
 *
 */
public class RouteMatchingTask extends FileTask {
  private File trackFile;
  private TrackMatcher matcher;
  private List<Score> scores = new ArrayList<Score>();
  protected TableWriter tableWriter;
  private int minScore = 1;
  private int maxCount;
  private float targetDistance = 100;

  /** Specify the track file. */
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
  
  TrackMatcher getMatcher() {
    if ( matcher == null ) {
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
      matcher = new TrackMatcher(list, targetDistance);
    }
    return matcher;
    
  }
  
  @Override
  protected void processFile(File file) throws IOException {
    TrackMatcher matcher = getMatcher();
    String basename = Filenames.getBasename(file); 
    GPX gpx = readGPX(file);
    for( int i = 0; i < gpx.getRoutes().size(); i++ ) {
      String routeName = basename;
      if ( i > 0 )
        routeName += "." + i;
      Score score = new Score(routeName);
      matcher.computeScore(gpx.getRoutes().get(i).getWaypoints(), score);
      scores.add(score);
    }
  }

  @Override
  protected void processFiles() throws IOException {
    super.processFiles();
    Score[] scores = this.scores.toArray(new Score[0]);
    Arrays.sort(scores);
    try {
      tableWriter.tableOpen(Filenames.getBasename(trackFile));
      tableWriter.tableHeaders(new String[] {
          "route", "nearCount", "meanSeparation", "directionChanges", "dominantDirection"});
      for( int i = 0; i < scores.length; i++ ) {
        Score score = scores[i];
        if ( maxCount > 0 && i >= maxCount ) {
          break;
        }
        if ( scores[i].getNearCount() < minScore ) {
          break;
        }
        tableWriter.tableRow(new Object[] {
            score.getRouteName(),
            score.getNearCount(),
            score.getMeanSeparation(),
            score.getDirectionChanges(),
            score.getDominantDirection(),
        });
      }
    } finally {
      tableWriter.tableClose();
    }
  }
  
}
