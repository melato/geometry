package org.melato.geometry.ant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.melato.export.TableWriter;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.gpx.util.PathTracker;

/**
 * Used to generate a table of path computations for each point on a track.
 * Allows adding columns.
 * @author Alex Athanasopoulos
 *
 */
public class TrackTable {
  // input properties
  protected Path path;
  protected PathTracker tracker;
  protected List<TrackColumn> columns = new ArrayList<TrackColumn>();
  protected List<Waypoint> trackWaypoints;  
  
  // computation state
  protected PathDistance distance = new PathDistance();
  protected Waypoint waypoint;
  protected int     waypointIndex;
  private   long    startTime;

  public abstract class TrackColumn {
    private String name;

    public TrackColumn(String name) {
      super();
      this.name = name;
    }

    public String getName() {
      return name;
    }
    
    public abstract Object getValue();
    
  }
  
  public Path getPath() {
    return path;
  }

  public PathTracker getTracker() {
    return tracker;
  }

  public List<Waypoint> getTrackWaypoints() {
    return trackWaypoints;
  }

  public PathDistance getDistance() {
    return distance;
  }

  public Waypoint getWaypoint() {
    return waypoint;
  }

  public int getWaypointIndex() {
    return waypointIndex;
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public void setTracker(PathTracker tracker) {
    this.tracker = tracker;
  }

  public void setTrackWaypoints(List<Waypoint> trackWaypoints) {
    this.trackWaypoints = trackWaypoints;
  }  

  public TrackTable() {
    super();
  }

  public void compute(Waypoint p) {
    waypoint = p;
    trackWaypoints.add(p);
    distance.add(p);
    tracker.setLocation(p);
  }
  
  protected void addColumn(TrackColumn column) {
    columns.add(column);
  }
  public long getElapsedTime() {
    return waypoint.getTime().getTime() - startTime;
  }
  public void writeTable(TableWriter tableWriter) throws IOException {
    tracker.setPath(path);
    String[] headers = new String[columns.size()];
    for( int i = 0; i < headers.length; i++ ) {
      headers[i] = columns.get(i).getName();
    }
    Object[] row = new Object[headers.length];
    tableWriter.tableHeaders( headers );
    int size = trackWaypoints.size();
    if ( size > 0 )
      startTime = trackWaypoints.get(0).getTime().getTime();
    for( int i = 0; i < size; i++ ) {
      waypointIndex = i;
      Waypoint p = trackWaypoints.get(i);
      distance.add(p);
      tracker.setLocation(p);
      compute(p);
      for( int j = 0; j < row.length; j++ ) {
        row[j] = columns.get(j).getValue();
      }
      tableWriter.tableRow( row );
    }
  }
  
}