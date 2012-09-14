package org.melato.geometry;

import org.melato.matrix.Matrix;

/** Represents 2d transforms as 3x3 matrices.
 *  The transformation of (x, y) is done using matrix multiplication
 *  of the transform matrix with the vector (1, x, y).
 * */
public class MatrixTransform2d {
  public static void apply(Matrix matrix, Point2d p, Point2d result) {
    result.x = matrix.get(1, 0) + matrix.get(1,1) * p.x + matrix.get(1,2) * p.y;
    result.x = matrix.get(2, 0) + matrix.get(2,1) * p.x + matrix.get(2,2) * p.y;
  }
  
  public static Matrix translation(float x, float y) {
    return new Matrix(3,3,new float[] {
       1, 0, 0,
       x, 1, 0,
       y, 0, 1,
    });
  }
  public static Matrix scale(float x, float y) {
    return new Matrix(3,3,new float[] {
       1, 0, 0,
       0, x, 0,
       0, 0, y,
    });
  }
  public static Matrix rotation(float angle) {
    float cos = (float) Math.cos(angle);
    float sin = (float) Math.sin(angle);
    return new Matrix(3,3, new float[] {
       1, 0, 0,
       0, cos,   sin,
       0, -sin,  cos
    });
  }
  public static Matrix rotation(float x, float y) {
    float d = (float) Math.sqrt(x*x+y*y);
    float cos = x/d;
    float sin = y/d;
    return new Matrix(3,3, new float[] {
        1, 0, 0,
        0, cos,   sin,
        0, -sin,  cos
     });
  }
  
}
