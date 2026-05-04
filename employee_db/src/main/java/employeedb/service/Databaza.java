package employeedb.service;

import employeedb.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class Databaza {
    private final LinkedList<Zamestnanec> zamestnanci = new LinkedList<>();

    public Zamestnanec pridatZamestnance(String skupina, String jmeno, String prijmeni, int rok) {
        Zamestnanec z = switch (skupina.toLowerCase()) {
            case "analytik", "datový analytik" -> new DataovyAnalytik(jmeno, prijmeni, rok);
            case "specialista", "bezpečnostní specialista" -> new BezpecnostniSpecialista(jmeno, prijmeni, rok);
            default -> throw new IllegalArgumentException("Neznámá skupina: " + skupina);
        };
        zamestnanci.add(z);
        return z;
    }

    public void pridatZamestnanceObjekt(Zamestnanec z) {
        zamestnanci.add(z);
    }

    public boolean odebratZamestnance(int id) {
        Zamestnanec target = najitPodleId(id);
        if (target == null) return false;
        zamestnanci.remove(target);
        for (Zamestnanec z : zamestnanci) {
            z.odebratSpolupraci(id);
        }
        return true;
    }

    public Zamestnanec najitPodleId(int id) {
        return zamestnanci.stream()
                .filter(z -> z.getId() == id)
                .findFirst().orElse(null);
    }

    public String pridatSpolupraci(int idA, int idB, UrovenSpolurace uroven) {
        Zamestnanec a = najitPodleId(idA);
        Zamestnanec b = najitPodleId(idB);
        if (a == null) return "Zaměstnanec ID=" + idA + " neexistuje.";
        if (b == null) return "Zaměstnanec ID=" + idB + " neexistuje.";
        if (idA == idB) return "Nelze přidat spolupráci sám se sebou.";
        a.pridatSpolupraci(new Spoluprace(idB, uroven));
        return "Spolupráce přidána.";
    }

    public String spustitDovednost(int id) {
        Zamestnanec z = najitPodleId(id);
        if (z == null) return "Zaměstnanec nenalezen.";
        return z.spustitDovednost(zamestnanci);
    }

    public String abecedniVypisSkupiny() {
        Map<String, List<Zamestnanec>> skupiny = new LinkedHashMap<>();
        skupiny.put("Datový analytik", new ArrayList<>());
        skupiny.put("Bezpečnostní specialista", new ArrayList<>());

        for (Zamestnanec z : zamestnanci) {
            skupiny.get(z.getSkupina()).add(z);
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Zamestnanec>> entry : skupiny.entrySet()) {
            sb.append("\n=== ").append(entry.getKey()).append(" ===\n");
            entry.getValue().stream()
                    .sorted(Comparator.comparing(Zamestnanec::getPrijmeni)
                            .thenComparing(Zamestnanec::getJmeno))
                    .forEach(z -> sb.append("  ").append(z.zakladniInfo()).append("\n"));
        }
        return sb.toString();
    }

    public String poctyVeSkupinach() {
        long analytici = zamestnanci.stream()
                .filter(z -> z instanceof DataovyAnalytik).count();
        long specialiste = zamestnanci.stream()
                .filter(z -> z instanceof BezpecnostniSpecialista).count();
        return String.format("Datový analytik: %d | Bezpečnostní specialista: %d | Celkem: %d",
                analytici, specialiste, zamestnanci.size());
    }

    public String statistiky() {
        if (zamestnanci.isEmpty()) return "Žádní zaměstnanci.";

        int[] pocty = new int[4];
        for (Zamestnanec z : zamestnanci) {
            for (Spoluprace s : z.getSpolurace()) {
                pocty[s.getUroven().getHodnota()]++;
            }
        }
        int celkem = pocty[1] + pocty[2] + pocty[3];
        String prevazujici = "žádná";
        if (celkem > 0) {
            int max = Math.max(pocty[1], Math.max(pocty[2], pocty[3]));
            if (max == pocty[3]) prevazujici = UrovenSpolurace.DOBRA.getPopis();
            else if (max == pocty[2]) prevazujici = UrovenSpolurace.PRUMERNA.getPopis();
            else prevazujici = UrovenSpolurace.SPATNA.getPopis();
        }

        Zamestnanec nejviceVazeb = zamestnanci.stream()
                .max(Comparator.comparingInt(z -> z.getSpolurace().size()))
                .orElse(null);

        StringBuilder sb = new StringBuilder();
        sb.append("Převažující kvalita spolupráce: ").append(prevazujici).append("\n");
        sb.append(String.format("  (špatná: %d, průměrná: %d, dobrá: %d)\n", pocty[1], pocty[2], pocty[3]));
        if (nejviceVazeb != null) {
            sb.append("Zaměstnanec s nejvíce vazbami: ")
              .append(nejviceVazeb.getJmeno()).append(" ")
              .append(nejviceVazeb.getPrijmeni())
              .append(" (ID=").append(nejviceVazeb.getId()).append(")")
              .append(" — ").append(nejviceVazeb.getSpolurace().size()).append(" vazeb");
        }
        return sb.toString();
    }

    public LinkedList<Zamestnanec> getVsechni() { return zamestnanci; }

    public void clear() { zamestnanci.clear(); }
}
