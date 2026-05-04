package employeedb.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Zamestnanec {
    private static int pocitadloId = 1;

    private final int id;
    private final String jmeno;
    private final String prijmeni;
    private final int rokNarozeni;
    private final List<Spoluprace> spoluprace;

    public Zamestnanec(String jmeno, String prijmeni, int rokNarozeni) {
        this.id = pocitadloId++;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.spoluprace = new ArrayList<>();
    }

    public Zamestnanec(int id, String jmeno, String prijmeni, int rokNarozeni) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.spoluprace = new ArrayList<>();
        if (id >= pocitadloId) pocitadloId = id + 1;
    }

    public static void resetPocitadla(int novaHodnota) {
        pocitadloId = novaHodnota;
    }

    public int getId() { return id; }
    public String getJmeno() { return jmeno; }
    public String getPrijmeni() { return prijmeni; }
    public int getRokNarozeni() { return rokNarozeni; }
    public List<Spoluprace> getSpolurace() { return spoluprace; }

    public void pridatSpolupraci(Spoluprace s) {
        spoluprace.removeIf(x -> x.getKolegaId() == s.getKolegaId());
        spoluprace.add(s);
    }

    public void odebratSpolupraci(int kolegaId) {
        spoluprace.removeIf(x -> x.getKolegaId() == kolegaId);
    }

    public double prumernaKvalita() {
        if (spoluprace.isEmpty()) return 0;
        return spoluprace.stream()
                .mapToInt(s -> s.getUroven().getHodnota())
                .average()
                .orElse(0);
    }

    public UrovenSpolurace prevazujiciKvalita() {
        if (spoluprace.isEmpty()) return null;
        int[] pocty = new int[4];
        for (Spoluprace s : spoluprace) pocty[s.getUroven().getHodnota()]++;
        int max = 0, maxIdx = 1;
        for (int i = 1; i <= 3; i++) {
            if (pocty[i] > max) { max = pocty[i]; maxIdx = i; }
        }
        return switch (maxIdx) {
            case 1 -> UrovenSpolurace.SPATNA;
            case 2 -> UrovenSpolurace.PRUMERNA;
            default -> UrovenSpolurace.DOBRA;
        };
    }

    public abstract String getSkupina();
    public abstract String spustitDovednost(List<Zamestnanec> vsichni);

    public String zakladniInfo() {
        return String.format("ID: %d | %s %s | Rok nar.: %d | Skupina: %s | Spolupráce: %d",
                id, jmeno, prijmeni, rokNarozeni, getSkupina(), spoluprace.size());
    }

    public String detailniInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(zakladniInfo()).append("\n");
        sb.append("  Průměrná kvalita spolupráce: ").append(String.format("%.2f", prumernaKvalita())).append("\n");
        if (!spoluprace.isEmpty()) {
            sb.append("  Spolupracovníci:\n");
            for (Spoluprace s : spoluprace) {
                sb.append("    ").append(s).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() { return zakladniInfo(); }
}
