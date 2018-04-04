import java.util.*;


public class AssemblerTest{
  
  public String name;
  
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
      
      //System.out.println(Thread.currentThread().getPermissionState());
      //System.out.println(Thread.currentThread().getOwningThread());
      
    }
      
  }
  
  public static void main(String args[]){
    
    
    AssemblerTest at = new AssemblerTest();
    //at.setPermission(Object.ConcurrencyPermission.FROZEN);
    Test test = new Test(at);
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
