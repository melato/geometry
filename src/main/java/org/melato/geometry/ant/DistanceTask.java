package org.melato.geometry.ant;

import java.io.File;
import java.io.IOException;

import org.melato.ant.FileTask;
import org.melato.common.util.Filenames;
import org.melato.export.CsvWriter;
import org.melato.export.TableWriter;
import org.melato.geometry.gpx.PathLength;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.GlobalDistance;
import org.melato.gpx.LocalDistance;
import org.melato.gpx.Waypoint;

public class DistanceTask extends FileTask {
  protected TableWriter tableWriter;

  public DistanceTask() {
    super();
  }

  public void setOutputDir(File outputDir) {
    tableWriter = new CsvWriter(outputDir);
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
    System.out.println( file );
    GPX gpx = readGPX(file);
    PathLength global = new PathLength(new GlobalDistance());
    PathLength local = null;
    tableWriter.tableOpen(Filenames.getBasename(file));
    try {
      String[] headers = new String[] { "global", "local", "global-local" };
      Object[] row = new Object[headers.length];
      tableWriter.tableHeaders(headers);
      for( Waypoint p: GPXIterators.trackWaypoints(gpx.getTracks())) {
        if ( local == null ) {
          local = new PathLength(new LocalDistance(p));
        }
        global.add(p);
        local.add(p);
        row[0] = global.getLength();
        row[1] = local.getLength();
        row[2] = global.getLength() - local.getLength();
        tableWriter.tableRow(row);
      }
   } finally {
      tableWriter.tableClose();
    }
  }
  
}