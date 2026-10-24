import io.github.lordnavycrypto.gugugaga.pathogens.*;
import java.util.*;
public final class EcologyExtendedTaxaSmoke{
 static int checks;
 static void ok(boolean x,String m){checks++;if(!x)throw new AssertionError(m);}
 public static void main(String[]args){
  var all=EcologyExtendedTaxa.ENTRIES;
  ok(all.size()==8,"8 additions: 4 fungi, 3 parasites, one phage");
  ok(EcologyExtendedTaxa.ORIGINAL_CHANNELS==15,"original field save remains compatible");
  ok(EcologyExtendedTaxa.BLOCK_EDGE_METERS==1,"one game block per physical-scale meter");
  ok(EcologyExtendedTaxa.FIELD_CELL_EDGE_BLOCKS==4,"four by four blocks per field cell");
  int fungi=0,parasites=0,phage=0;
  for(var item:all){
   switch(item.group()){
    case FUNGUS -> fungi++;
    case ECTOPARASITE,HELMINTH,PROTOZOAN -> parasites++;
    case BACTERIOPHAGE -> phage++;
   }
   ok(item.sourceUrl().startsWith("https://"),"source-backed identity");
   ok(EcologyExtendedTaxa.byId(item.id())==item,"consistent lookup");
  }
  ok(fungi==4 &&parasites==3 &&phage==1,"classification counts");
  ok(EcologyExtendedTaxa.byId("puccinia_graminis").matchesBlock("minecraft:wheat"),
     "genuine wheat host category");
  ok(EcologyExtendedTaxa.byId("varroa_destructor").matchesEntity("minecraft:bee"),
     "bee mapping");
  ok(!EcologyExtendedTaxa.byId("varroa_destructor").matchesEntity("minecraft:chicken"),
     "never infer an unrelated game host");
  ok(EcologyExtendedTaxa.byId("botrytis_cinerea").artProxy(),
     "sweet berries are documented Minecraft art proxies");
  ok(EcologyExtendedTaxa.byId("escherichia_phage_t4").virtualOnly(),
     "bacteriophages never become animal hosts or phage blocks");
  System.out.println("GUGUGAGA_EXTENDED_TAXA_PASS checks="+checks);
 }
}
