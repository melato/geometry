package org.melato.matrix;

public class Matrix {
  int rows;
  int cols;
  float[] data;
  
  public Matrix(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    data = new float[rows*cols];
  }
  public float get(int i, int j) {
    return data[i*cols+j];
  }
  public void set(int i, int j, float v) {
    data[i*cols+j] = v;
  }
  /**
   * @param rows
   * @param cols
   * @param data the matrix data as concatenated rows.
   */
  public Matrix(int rows, int cols, float[] data) {
    if ( data.length != rows * cols )
      throw new IllegalArgumentException();
    this.rows = rows;
    this.cols = cols;
    this.data = data;
  }
  /**
   * Compute the dot product between this matrix and another matrix.
   * @param b
   * @return
   */
  public Matrix dot(Matrix b) {
    if ( this.cols != b.rows )
      throw new IllegalArgumentException();
    Matrix c = new Matrix(this.rows, b.cols);
    for( int i = 0; i < this.rows; i++ ) {
      for( int j = 0; j < b.cols; j++ ) {
        double sum = 0;
        for( int k = 0; k < cols; k++) {
          sum += this.get(i, k) * b.get(k, j); 
        }
        c.set(i, j, (float) sum);
      }
    }
    return c;
  }

  /**
   * Compute the dot product between this matrix and a vector.
   * @param b
   * @return
   */
  public Matrix dot(float[] b) {
    return dot( new Matrix( b.length, 1, b));
  }
  
  /**
   * Compute the dot product between this matrix and a vector.
   * @param b
   * @return
   */
  public float[] dotVector(float[] b) {
    Matrix c = dot( new Matrix( b.length, 1, b));
    return c.data;
  }
  /**
   * Compute the dot product between this matrix and a vector.
   * @param b
   * @return
   */
  public void multiply(float[] vector, float[] result) {
    if ( this.cols != vector.length)
      throw new IllegalArgumentException();
    if ( this.rows != result.length)
      throw new IllegalArgumentException();
    for( int i = 0; i < result.length; i++ ) {
      double sum = 0;
      for( int j = 0; j < vector.length; j++ ) {
        sum += this.get(i, j) * vector[j];
      }
      result[i] = (float) sum;
    }
  }
  
}
