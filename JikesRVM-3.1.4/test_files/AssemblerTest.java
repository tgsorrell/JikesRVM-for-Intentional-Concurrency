import java.util.*;


public class AssemblerTest{
  
  private String name;
  
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
  
  public static void main(String args[]){
    
    AssemblerTest at = new AssemblerTest();
    at.setPermissionStatePrivate();
    System.out.println(at.getName());
    at.setPermissionStateFrozen();
    System.out.println(at.getName());
    System.out.println("Owning thread ID: " + at.getOwningThread());
    System.out.println("DONE");
  }
}