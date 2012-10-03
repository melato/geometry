package org.melato.geometry.gpx;

import java.util.ArrayList;
import java.util.List;

import org.melato.gps.Point;
import org.melato.gpx.LocalDistance;
import org.melato.gpx.Metric;
import org.melato.util.CircularList;


/** Computes rolling average speeds over multiple time intervals,
 * e.g. every 1 minute, every 5 minutes, every 1 and 5 minutes, etc. */
public class RollingSpeedManager {
  public class RollingSpeed {
    /** How many milliseconds to average over. */
    long    intervalTime;
    int     count; // the number of Samples
    double  distance; // the distance covered by our samples
    long    time; // the time covered by our samples
    
    RollingSpeed(long intervalTime) {
      super();
      this.intervalTime = intervalTime;
    }    
    public float getSpeed() {
      return (float) (distance * 1000 / time);
    }    
    public long getIntervalTime() {
      return intervalTime;
    }
    public int getCount() {
      return count;
    }
    public double getDistance() {
      return distance;
    }
    public long getTime() {
      return time;
    }
    void add(Sample sample) {
      count++;  // add the new sample
      distance += sample.distance;
      time += sample.timeMillis;
    }    
    
    @Override
    public String toString() {
      return distance + "/" + time + " (" + count + ")";
    }
    void recompute() {
      count = 0;
      distance = 0;
      time = 0;
      int size = samples.size();
      for( int i = 0; i < size; i++ ) {
        count++;
        Sample s = samples.get(i);
        distance += s.distance;
        time += s.getTime();
        if ( time > intervalTime ) {
          break;
        }
      }
    }
    void trim() {
      // find out which samples are no longer needed.
      if ( time > intervalTime ) {
        for( int last = count - 1; last > 0; last-- ) {
          Sample s = samples.get(last);
          long sampleTime = s.getTime();
          if ( time - sampleTime < intervalTime ) {
            break;
          }
          time -= sampleTime;
          distance -= s.getDistance();
          count--;
        }
      }      
    }
  }
  public class Sample {
    float distance;    // the traveled distance since the previous sample.
    long  timeMillis;  // the traveled time since the previous sample.
    
    public Sample(float distance, long timeMillis) {
      super();
      this.distance = distance;
      this.timeMillis = timeMillis;
    }
    public long getTime() {
      return timeMillis;      
    }
    public float getDistance() {
      return distance;
    }
    @Override
    public String toString() {
      return distance + "/" + timeMillis;
    }
    
  }
  private List<RollingSpeed> speeds = new ArrayList<RollingSpeed>();
  /** Recent samples.  The last sample is [0]. */
  private List<Sample> samples = new CircularList<Sample>();

  private RollingSpeed maxInterval;
  
  private Point  lastPoint;
  private Metric  metric;

  public RollingSpeedManager( Metric metric ) {
    this.metric = metric;
  }
  
  public RollingSpeed addRollingSpeed(int seconds) {
    RollingSpeed interval = new RollingSpeed(seconds * 1000L);
    speeds.add(interval);
    interval.recompute();
    if ( maxInterval == null || maxInterval.intervalTime < interval.intervalTime ) {
      maxInterval = interval;
    }
    return interval;    
  }
  
  public void addPoint(Point p) {
    if ( lastPoint == null ) {
      if ( metric == null ) {
        metric = new LocalDistance(p);
      }
      lastPoint = p;
      return;
    }
    Sample sample = new Sample(metric.distance(lastPoint,  p),
        Point.timeDifferenceMillis(lastPoint, p));
    lastPoint = p;
    
    // insert the new sample at the beginning of the list.
    samples.add(0, sample);
    
    // adjust all speeds
    for (RollingSpeed interval: speeds) {
      interval.add(sample);
      interval.trim();
    }

    // remove samples that are no longer needed.
    // These are the samples with index >= maxInterval.count.
    for( int i = samples.size() - 1; i >= maxInterval.count; i-- ) {
      samples.remove(i);
    }
  }  
}
