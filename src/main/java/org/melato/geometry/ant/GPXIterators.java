package org.melato.geometry.ant;

import java.util.Iterator;
import java.util.List;

import org.melato.gpx.Sequence;
import org.melato.gpx.Track;
import org.melato.gpx.Waypoint;

public class GPXIterators {
  public static <T> Iterator<T> emptyIterator() {
    return new EmptyIterator<T>();
  }
  
  public static Iterable<Waypoint> trackWaypoints(List<Track> tracks) {
    return new WaypointIterable(tracks);
  }

  private static class EmptyIterator<T> implements Iterator<T> {
    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public T next() {
      return null;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }    
  }
  public static class WaypointIterable implements Iterable<Waypoint> {
    private List<Track> tracks;

    public WaypointIterable(List<Track> tracks) {
      super();
      this.tracks = tracks;
    }

    @Override
    public Iterator<Waypoint> iterator() {
      return new WaypointIterator(tracks);
    }
    
  }
  private static class WaypointIterator implements Iterator<Waypoint> {
    private Iterator<Track> tracks;
    private Iterator<Sequence> segments = emptyIterator();
    private Iterator<Waypoint> waypoints = emptyIterator();

    public WaypointIterator(List<Track> tracks) {    
      this.tracks = tracks.iterator();
    }
    @Override
    public boolean hasNext() {
      for(;;) {
        if ( waypoints.hasNext() ) {
          return true;
        }
        if ( segments.hasNext() ) {
          waypoints = segments.next().getWaypoints().iterator();
          continue;
        }
        if ( tracks.hasNext() ) {
          segments = tracks.next().getSegments().iterator();
          continue;
        }
        return false;
      }
    }
    @Override
    public Waypoint next() {
      return waypoints.next();
    }
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
    
  }
}
