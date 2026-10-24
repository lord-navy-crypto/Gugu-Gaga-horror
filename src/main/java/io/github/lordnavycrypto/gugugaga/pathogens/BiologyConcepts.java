package io.github.lordnavycrypto.gugugaga.pathogens;
import java.util.List;
/** Source-linked educational definitions; no experimental or transport parameters. */
public final class BiologyConcepts {
  private BiologyConcepts() {}
  public record Concept(String category,String fact,String sourceUrl) {}
  public static final List<Concept> ENTRIES=List.of(
    new Concept("VIRUS","Viruses depend on host cells to replicate; viruses do not undergo binary fission.","https://research.nih.gov/genetics-glossary/Virus"),
    new Concept("BACTERIA","Most bacteria reproduce by binary fission.","https://www.ncbi.nlm.nih.gov/books/NBK20370/"),
    new Concept("FUNGI","Fungal plant disease is distinct from bacterial and viral plant disease.","https://www.ars.usda.gov/midwest-area/stpaul/cereal-disease-lab/docs/cereal-rusts/wheat-stem-rust/"),
    new Concept("ONE_HEALTH","Animal, plant, environmental and human health are interconnected.","https://www.who.int/health-topics/one-health"));
}
