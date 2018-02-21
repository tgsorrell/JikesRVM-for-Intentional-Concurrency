import java.util.*;


public class AssemblerTest{
  
  private String name;
  
  public AssemblerTest(){
    for(int i = 0; i < 5; i++)
      System.out.println("----------Create Object------------");
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
    for(int i = 0; i < 5; i++)
      System.out.println("--------Start Main-------");
    AssemblerTest at = new AssemblerTest();
    for(int i = 0; i < 5; i++)
      System.out.println("-----------Access Field----------");
    at.getName();
    System.out.println("DONE");
  }
}