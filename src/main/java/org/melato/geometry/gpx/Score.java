package org.melato.geometry.gpx;



public class Score implements Comparable<Score> {
  private Object  id;
  private int     count;
  
  
  /**
   * The id of the score, as passed in the constructor.
   * Used for identification.
   * @return
   */
  public Object getId() {
    return id;
  }
  
  public void setId(Object id) {
    this.id = id;
  }
  
  public Score() {
  }

  public Score(Object id) {
    super();
    this.id = id;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  @Override
  public int compareTo(Score score) {
    return score.count - count;
  }
  @Override
  public String toString() {
    return getId() + " " + count;
  }  
}
