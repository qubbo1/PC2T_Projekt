package employeedb.model;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class DataovyAnalytik extends Zamestnanec {

    public DataovyAnalytik(String jmeno, String prijmeni, int rokNarozeni) {
        super(jmeno, prijmeni, rokNarozeni);
    }

    public DataovyAnalytik(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public String getSkupina() { return "Datový analytik"; }

    /**
     * Dovednost: Najde spolupracovníka, s nímž má nejvíce společných spolupracovníků.
     */
    @Override
    public String spustitDovednost(List<Zamestnanec> vsichni) {
        Set<Integer> mojiKolegy = getSpolurace().stream()
                .map(Spoluprace::getKolegaId)
                .collect(Collectors.toSet());

        if (mojiKolegy.isEmpty()) {
            return "Žádní spolupracovníci – nelze vyhodnotit.";
        }

        int nejlepsiKolegaId = -1;
        int maxSpolecnych = -1;

        for (Zamestnanec z : vsichni) {
            if (z.getId() == getId()) continue;
            if (!mojiKolegy.contains(z.getId())) continue; // musí být můj kolega

            Set<Integer> jeho = z.getSpolurace().stream()
                    .map(Spoluprace::getKolegaId)
                    .collect(Collectors.toSet());

            Set<Integer> prusecik = new HashSet<>(mojiKolegy);
            prusecik.retainAll(jeho);
            prusecik.remove(z.getId()); // odeber sebe

            if (prusecik.size() > maxSpolecnych) {
                maxSpolecnych = prusecik.size();
                nejlepsiKolegaId = z.getId();
            }
        }

        if (nejlepsiKolegaId == -1) {
            return "Žádný spolupracovník nemá společné kontakty.";
        }

        int finalId = nejlepsiKolegaId;
        Zamestnanec kolega = vsichni.stream()
                .filter(z -> z.getId() == finalId)
                .findFirst().orElse(null);

        String kolegaJmeno = kolega != null
                ? kolega.getJmeno() + " " + kolega.getPrijmeni()
                : "ID " + nejlepsiKolegaId;

        return String.format(
                "Spolupracovník s nejvíce společnými kontakty: %s (ID=%d) — %d společných spolupracovníků.",
                kolegaJmeno, nejlepsiKolegaId, maxSpolecnych);
    }
}
