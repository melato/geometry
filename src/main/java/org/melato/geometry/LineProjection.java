package org.melato.geometry;

import org.melato.matrix.Matrix;

/**
 * Projects points to a straight line between two points p1, p2,
 * so that the new x coordinate is measured along the line from p1,
 * and the new y coordinate is perpendicular to the line.
 * y is positive to the left of the line as we go from p1 to p2. 
 * @author Alex Athanasopoulos
 *
 */
public class LineProjection {
  public static Matrix createMatrix( Point2d p1, Point2d p2) {
    Matrix translation = MatrixTransform2d.translation(-p1.x, -p2.x);
    Point2d t2 = new Point2d();
    MatrixTransform2d.apply(translation, p2, t2);
    Matrix rotation = MatrixTransform2d.rotation(t2.x, -t2.y);
    return rotation.dot(translation);
  }
}
