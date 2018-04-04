
import java.util.*;


public class Pi {
  
  public static LinkedList<Sum> sums = new LinkedList<Sum>();
  public static Object monitor = new Object(); // For signaling the main thread
  
  private static class IntervalGraph {
  
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
  
  private static class Integration implements Runnable{
    
    private IntervalGraph graph;
    private int ID;
    
    public Integration(int id, IntervalGraph g){
      graph = g;
      ID = id;
    }
    
    public void run(){
      
      int i;
      int low;
      int high;
      //Sum is created as a private object. Only the current thread can access it.
      Sum local = new Sum();
      double midpoint;
      
      if(ID < graph.split)
      {
        //A getfield on the graph is fine, since it is a frozen object
        low = (ID * (graph.chunk + 1));
        high =(low + (graph.chunk + 1));
      }
      else
      {
        low = (graph.split * (graph.chunk + 1)) + ((ID - graph.split) * graph.chunk);
        high = low + graph.chunk;
      }
      
      midpoint = (low + 0.5) * graph.width;
      for(i = low; i < high; i++)
      {
        local.addTo( (4.0/(1.0 + midpoint * midpoint)) );
        midpoint += graph.width;  
      }
      
      //Need to transfer the Sum object to the main thread.
      //Next attempt to access its sum field will transfer 
      //ownership to the main thread.
      local.setPermission(Object.ConcurrencyPermission.TRANSFER);
      
      synchronized(sums){
        sums.add(local);
      }
      synchronized(monitor){
          monitor.notify();
      }
    }
  }
  
  private static class Sum {
    
    private double sum;
    
    public Sum(){
      sum = 0;
    }
    
    public void addTo(double d){
      sum += d;
    }
    
    public double getSum(){
      return sum;
    }
    
  }
  
  public static void main(String[] args){
    
    int numThreads = 4;
    //Setting the graph to frozen allows its fields to be read, but not written to.
    IntervalGraph graph = new IntervalGraph(numThreads);
    graph.setPermission(Object.ConcurrencyPermission.FROZEN);
    Thread workers[] = new Thread[numThreads];
    int sumsRecieved = 0;
    Sum s;
    double PI = 0.0;
    
    
    for(int i = 0; i < numThreads; i++)
    {
      workers[i] = new Thread(new Integration(i, graph));
      workers[i].start();
    }
    
    while(sumsRecieved < numThreads)
    {
      try{
        synchronized(monitor){
          monitor.wait();
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
      
      synchronized(sums){
        while((s = sums.poll()) != null){
          
          //s.getSum() can be called since ownership
          //of the sum has been transfered.
          System.out.println("Recieved sum " + s.getSum());
          PI += s.getSum();
          sumsRecieved++;
        }
      }
    }
    
    PI *= graph.width;
    
    System.out.println("Approximation of PI: " + PI);
    
  }
}