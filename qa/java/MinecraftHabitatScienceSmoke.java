import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class MinecraftHabitatScienceSmoke {
 private static int passed=0;
 private static void require(boolean condition,String why){
  passed++;if(!condition)throw new AssertionError(why);
 }
 public static void main(String[]args){
  var c=new MinecraftHabitatScience.Sample(-1,67,-17,15,true,true,true);
  var text=MinecraftHabitatScience.interpret(c);
  require(text.environmentTag().equals("CULTIVATED_RAIN"),"farmland + rain");
  require(text.areaBlocks()==16,"four-by-four grid = sixteen blocks");
  require(c.region().equals(PathogenSpatialFieldMath.fromBlock(-1,-17)),
      "negative coordinates floor divide, original world index unchanged");
  require(c.region().x()==-1&&c.region().z()==-5,
      "signed cell boundaries correct");
  require(MinecraftHabitatScience.BLOCK_METRES==1,"one block display metre");
  var shade=new MinecraftHabitatScience.Sample(0,64,0,2,false,false,true);
  require(MinecraftHabitatScience.interpret(shade).environmentTag().equals("SHADED"),
          "sky exposure tracked separately");
  var noChunk=new MinecraftHabitatScience.Sample(0,64,0,15,true,true,false);
  require(MinecraftHabitatScience.interpret(noChunk).areaBlocks()==0,
          "unloaded chunk is not sampled");
  for(int x=-9;x<9;x++)
   for(int z=-9;z<9;z++){
    var s=new MinecraftHabitatScience.Sample(x,65,z,0,false,true,true);
    require(s.region().x()==Math.floorDiv(x,4) &&
            s.region().z()==Math.floorDiv(z,4),"4x4 region mapping");
   }
  for(int tick=0;tick<64;tick++){
   var result=VanillaCropGameFeedback.growth(
       "minecraft:wheat",12,12,-8,tick);
   require((result==VanillaCropGameFeedback.Growth.SHORT_GAME_PAUSE)==
       ((tick+(long)12*17-8L*31)%4==0),
       "bounded deterministic game phase");
   require(VanillaCropGameFeedback.growth(
        "minecraft:wheat",0,12,-8,tick)==
        VanillaCropGameFeedback.Growth.UNCHANGED,
        "no game marker means original vanilla crop growth");
   require(VanillaCropGameFeedback.growth(
        "minecraft:potatoes",15,12,-8,tick)==
        VanillaCropGameFeedback.Growth.UNCHANGED,
        "fungal wheat art cannot affect unrelated potato blocks");
  }
  try{
   new MinecraftHabitatScience.Sample(0,0,0,16,true,true,true);
   throw new AssertionError("invalid vanilla skylight accepted");
  }catch(IllegalArgumentException expected){passed++;}
  System.out.println("GUGUGAGA_MINECRAFT_HABITAT_PASS checks="+passed+
       " realVanillaObservations=true notBiologicalRates=true");
 }
}
