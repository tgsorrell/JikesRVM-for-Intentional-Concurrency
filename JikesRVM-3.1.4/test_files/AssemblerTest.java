import java.util.*;


public class AssemblerTest{
  
  private String name;
  //private Integer concurrent;
  
  public AssemblerTest(){
    name = "Assembler Test";
    //concurrent = 42;
  }
  
  @Override
  public String toString()
  {
    return name;
  }
  
  public String getName()
  {
    return name;
  }
  
  private static class Test implements Runnable{
    
    private AssemblerTest mine;
    
    private Integer one;
    private Integer two;
    private Integer three;
    private Integer four;
    
    
    public Test(AssemblerTest at){
      mine = at;
      /*
      one = 1;
      two = 2;
      three = 3;
      four = 4;
      */
    }
    
    public void countToFour(){
      one = 1;
      two = 2;
      three = 3;
      four = 4;
    }
    
    public void run(){
      
      for(int i = 0; i < 3; i++)
        System.out.println("Should be an invalid access: " + mine.getName());
      
      //System.out.println(Thread.currentThread().getPermissionState());
      //System.out.println(Thread.currentThread().getOwningThread());
      
    }
      
  }
  
  public static void main(String args[]){
    
    
    AssemblerTest at = new AssemblerTest();
    Test test = new Test(at);
    test.countToFour();
    test.setPermission(Object.ConcurrencyPermission.FROZEN);
    Thread t = new Thread(test);
    t.start();
    try{
      t.join();
    }
    catch(Exception e){
      e.printStackTrace();
    }
    System.out.println("Valid access: " + at.getName());
    System.out.println("DONE");
  }
}
