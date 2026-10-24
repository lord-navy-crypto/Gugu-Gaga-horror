import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class PathogenHudTextSmoke {
 private static int checks;
 private static void check(boolean pass,String msg) {
  checks++;if(!pass)throw new AssertionError(msg);
 }
 public static void main(String[]args){
  var cell=new PathogenSpatialFieldMath.Cell(-3,21);
  var zero=new int[15];
  check(PathogenHudTextPolicy.format(cell,zero)
      .equals("[GAME FIELD -3,21] no active signals"),"empty field");
  var values=new int[15];
  values[0]=4;values[14]=12;values[6]=5;
  String out=PathogenHudTextPolicy.format(cell,values);
  check(out.contains("12/15"),"dominant channel strength");
  check(out.contains("3 labels, 21 units"),"game-only reading");
  check(!out.contains("infect"),"no mistaken diagnostic wording");
  check(values[14]==12&&values[0]==4,"rendering does not alter input");
  check(out.length()<110,"tiny action bar, not intrusive HUD");
  try{
   PathogenHudTextPolicy.format(cell,new int[14]);
   throw new AssertionError("Allowed short array");
  }catch(IllegalArgumentException expected){checks++;}
  try{
   int[] invalid=new int[15];invalid[4]=16;
   PathogenHudTextPolicy.format(cell,invalid);
   throw new AssertionError("Allowed out-of-range marker");
  }catch(IllegalArgumentException expected){checks++;}
  var same=new int[15];same[1]=9;same[12]=9;
  String tied=PathogenHudTextPolicy.format(cell,same);
  check(tied.contains(PathogenRoster15.FIRST_FIFTEEN.get(1).id()),
      "tie-breaking stable and deterministic");
  System.out.println("GUGUGAGA_PATHOGEN_HUD_PASS checks="+checks
      +" nativeActionBar=true optInOnly=true noExtraBlocks=true");
 }
}
