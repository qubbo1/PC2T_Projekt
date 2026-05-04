package employeedb.model;

import java.util.List;

public class BezpecnostniSpecialista extends Zamestnanec {

    public BezpecnostniSpecialista(String jmeno, String prijmeni, int rokNarozeni) {
        super(jmeno, prijmeni, rokNarozeni);
    }

    public BezpecnostniSpecialista(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public String getSkupina() { return "Bezpečnostní specialista"; }

    @Override
    public String spustitDovednost(List<Zamestnanec> vsichni) {
        int pocet = getSpolurace().size();
        double prumer = prumernaKvalita();

        if (pocet == 0) {
            return "Žádní spolupracovníci – rizikové skóre nelze vypočítat.";
        }

        double skore = (pocet * 10.0) / (prumer * 3.0 + 1.0);
        String kategorie;
        if (skore <= 10) kategorie = "🟢 Nízké riziko";
        else if (skore <= 25) kategorie = "🟡 Střední riziko";
        else kategorie = "🔴 Vysoké riziko";

        return String.format(
                "Rizikové skóre: %.2f | Počet spolupracovníků: %d | Průměrná kvalita: %.2f | %s",
                skore, pocet, prumer, kategorie);
    }
}
