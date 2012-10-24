package org.melato.geometry.gpx;

import java.io.File;
import java.io.IOException;

import org.melato.common.util.Filenames;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;

public class FileGpxSpecifier implements GpxSpecifier {
  private File file;
  
  public void setFile(File file) {
    this.file = file;
  }

  
  public FileGpxSpecifier() {
  }

  public FileGpxSpecifier(File file) {
    this.file = file;
  }

  @Override
  public GPX loadGpx() throws IOException {
    GPXParser parser = new GPXParser();
    return parser.parse(file);
  }
  
  @Override
  public String getName() {
    return Filenames.getBasename(file);
  }
}
