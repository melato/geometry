#!/bin/sh

for track in /main/dev/oasa/data/tracks/409/*.gpx
do
  ant path -Droute=/main/dev/oasa/data/target/gpx/409-1.gpx -Dtrack=$track
done

