package org.melato.geometry.gpx;

import java.io.IOException;

import org.melato.gpx.GPX;

public interface GpxSpecifier {
  GPX loadGpx() throws IOException;
  String getName();
}
