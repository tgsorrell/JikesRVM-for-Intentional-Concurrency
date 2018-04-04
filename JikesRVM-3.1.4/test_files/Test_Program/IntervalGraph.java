
import java.util.*;

public class IntervalGraph {
  
  public int intervals;
  public double width;
  public int numThreads;
  public int chunk;
  public int split;
  
  /*
   * Constructor for an interval graph
   * @param threads: the number of threads being used
   */
  public IntervalGraph(int threads){
    intervals = 50000000;
    width = 1.0/intervals;
    numThreads = threads;
    chunk = intervals/threads;
    split = intervals % threads;
    if(split == 0)
    {
      split = numThreads;
      chunk -= 1;
    }
      
  }
  
}