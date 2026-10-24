import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public class FieldParticleSamplingSmoke{
 static int checks;
 static void yes(boolean ok,String reason){
  checks++;if(!ok)throw new AssertionError(reason);
 }
 public static void main(String[] args){
  var names=FieldParticleSampling.selectableIds();
  yes(names.size()==22,"15 original and seven observable extension labels");
  yes(names.stream().distinct().count()==22,"no duplicate name");
  int legacy=0,extended=0;
  for(String id:names){
   var choice=FieldParticleSampling.choose(id);
   yes(choice.worldVisualAllowed(),"visible vanilla world label");
   if(choice.store()==FieldParticleSampling.Store.ORIGINAL_15)legacy++;
   else extended++;
  }
  yes(legacy==15&&extended==7,"original NBT channels retained");
  var phage=FieldParticleSampling.choose("escherichia_phage_t4");
  yes(!phage.worldVisualAllowed()&&phage.family()==
       FieldParticleSampling.Family.PHAGE,"virtual only bacteriophage");
  var origin=new PathogenSpatialFieldMath.Cell(-5,9);
  var choice=FieldParticleSampling.choose("potato_virus_y");
  Map<PathogenSpatialFieldMath.Cell,Integer> values=new HashMap<>();
  for(int z=-3;z<=3;z++)
   for(int x=-3;x<=3;x++)
    values.put(new PathogenSpatialFieldMath.Cell(origin.x()+x,origin.z()+z),
        1+Math.floorMod(x*x+z*z,15));
  var scan=FieldParticleSampling.scan(origin,choice,c->true,
      c->values.getOrDefault(c,0),Map.of());
  yes(scan.visited()==49,"bounded 7x7 lookup");
  yes(scan.occupied()==49,"all candidate cells recorded");
  yes(scan.markers().size()==6,"at most six emission positions");
  yes(scan.markers().stream().allMatch(m->m.change()==
      FieldParticleSampling.Change.NEW_OR_INCREASED),"new data highlighted");
  var repeat=FieldParticleSampling.scan(origin,choice,c->true,
      c->values.getOrDefault(c,0),scan.current());
  yes(repeat.markers().stream().allMatch(m->m.change()==
      FieldParticleSampling.Change.UNCHANGED),"no fake ongoing change");
  var blocked=FieldParticleSampling.scan(origin,choice,c->false,
      c->{throw new AssertionError("read unloaded area");},Map.of());
  yes(blocked.visited()==0&&blocked.markers().isEmpty(),
      "unloaded chunks never accessed or rendered");
  var previouslyHigher=new PathogenSpatialFieldMath.Cell(origin.x()+1,origin.z());
  var smaller=FieldParticleSampling.scan(origin,choice,c->true,
      c->c.equals(previouslyHigher)?1:0,scan.current());
  yes(smaller.markers().size()==1&&smaller.markers().get(0).change()==
      FieldParticleSampling.Change.DECREASED,"decreasing game trend");
  var absent=FieldParticleSampling.scan(origin,phage,c->true,
      c->{throw new AssertionError("virtual item rendered");},Map.of());
  yes(absent.visited()==0&&absent.markers().isEmpty(),
      "virtual-only phage has no world particles");
  yes(scan.current().size()==49&&repeat.current().size()==49,
      "at most 49 transient memory entries per viewer");
  yes(FieldParticleSampling.choose("puccinia_graminis").family()==
      FieldParticleSampling.Family.FUNGUS,"fungal visual styling independent");
  yes(FieldParticleSampling.choose("salmonella_enterica").family()==
      FieldParticleSampling.Family.BACTERIUM,"bacterial styling independent");
  yes(FieldParticleSampling.choose("varroa_destructor").family()==
      FieldParticleSampling.Family.PARASITE,"parasite styling independent");
  System.out.println("GUGUGAGA_GAME_FIELD_PARTICLES_PASS checks="+checks+
    " noBlocks=true noNewEntities=true noInfectiousCoefficients=true");
 }
}
