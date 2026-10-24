import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class EcologyExtensionToyFieldSmoke {
 static int checks;
 static void ok(boolean yes,String message){checks++;if(!yes)throw new AssertionError(message);}
 public static void main(String[]args){
  var origin=new PathogenSpatialFieldMath.Cell(-1,-1);
  int wheat=EcologyExtendedTaxa.index("puccinia_graminis");
  int[] values=new int[8];values[wheat]=12;
  var start=Map.of(origin,values);
  var locked=EcologyExtensionToyField.advance(start,0,0,c->c.equals(origin));
  ok(locked.cells().size()==1,"unloaded neighbors receive no new values");
  var first=EcologyExtensionToyField.advance(start,0,0,c->true);
  ok(first.changed()==1 &&first.cells().size()==2,"game-only spatial art extends to neighbor");
  ok(start.get(origin)[wheat]==12,"input remains independent");
  ok(first.cells().get(origin)[wheat]==12,"only decorative value in destination is updated");
  var map=first.cells();
  for(int f=1;f<60;f++){
    var next=EcologyExtensionToyField.advance(map,0,f,c->true);
    ok(next.visited()<=24,"bounded source samples");
    ok(next.cells().size()<=256,"bounded memory");
    map=next.cells();
  }
  int[] virtual=new int[8];
  virtual[EcologyExtendedTaxa.index("escherichia_phage_t4")]=12;
  var phage=EcologyExtensionToyField.advance(Map.of(origin,virtual),0,7,c->true);
  ok(phage.changed()==0,"no physical world phage artifact");
  ok(phage.cells().size()==1,"no phage animal/block");
  ok(EcologyExtendedTaxa.BLOCK_EDGE_METERS==1,"one game block one metre");
  System.out.println("GUGUGAGA_ECOLOGY_TOY_FIELD_PASS checks="+checks);
 }
}
