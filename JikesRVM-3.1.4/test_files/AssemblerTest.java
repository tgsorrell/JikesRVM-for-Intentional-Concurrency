import java.util.*;


public class AssemblerTest{
  
  private String name;
  private int concurrent;
  
  public AssemblerTest(){
    name = "Assembler Test";
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
    
    public Test(AssemblerTest at){
      mine = at;
    }
    
    public void run(){
      
      for(int i = 0; i < 3; i++)
        System.out.println("Should be an invalid access: " + mine.getName());
    }
      
  }
  
  public static void main(String args[]){
    
    AssemblerTest at = new AssemblerTest();
    Thread t = new Thread(new Test(at));
    t.start();
    System.out.println("Valid access: " + at.getName());
    try{
    t.join();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    System.out.println("DONE");
  }
}