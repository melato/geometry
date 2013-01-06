package org.melato.geometry.gpx;

import org.melato.gps.PointTime;


/**
 * Listens to PointTime updates.
 * Like LocationListener but uses PointTime.
 * */
public interface PointTimeListener {
  void setLocation(PointTime point);
}
