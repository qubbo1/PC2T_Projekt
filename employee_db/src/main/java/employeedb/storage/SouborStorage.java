package employeedb.storage;

import employeedb.model.*;
import employeedb.service.Databaza;

import java.io.*;
import java.util.*;

/**
 * Uložení/načtení jednoho zaměstnance do/ze souboru (.txt, vlastní formát).
 */
public class SouborStorage {

    public static void ulozitZamestnance(Zamestnanec z, String cesta) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(cesta))) {
            pw.println("ID=" + z.getId());
            pw.println("SKUPINA=" + z.getSkupina());
            pw.println("JMENO=" + z.getJmeno());
            pw.println("PRIJMENI=" + z.getPrijmeni());
            pw.println("ROK=" + z.getRokNarozeni());
            pw.println("SPOLUPRACE_COUNT=" + z.getSpolurace().size());
            for (Spoluprace s : z.getSpolurace()) {
                pw.println("SPOLUPRACE=" + s.getKolegaId() + ":" + s.getUroven().getPopis());
            }
        }
    }

    public static Zamestnanec nacistZamestnance(String cesta) throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        List<String[]> spoluprace = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(cesta))) {
            String radek;
            while ((radek = br.readLine()) != null) {
                if (radek.startsWith("SPOLUPRACE=")) {
                    String hodnota = radek.substring("SPOLUPRACE=".length());
                    spoluprace.add(hodnota.split(":", 2));
                } else {
                    String[] parts = radek.split("=", 2);
                    if (parts.length == 2) data.put(parts[0], parts[1]);
                }
            }
        }

        int id = Integer.parseInt(data.get("ID"));
        String skupina = data.get("SKUPINA");
        String jmeno = data.get("JMENO");
        String prijmeni = data.get("PRIJMENI");
        int rok = Integer.parseInt(data.get("ROK"));

        Zamestnanec z = switch (skupina) {
            case "Datový analytik" -> new DataovyAnalytik(id, jmeno, prijmeni, rok);
            case "Bezpečnostní specialista" -> new BezpecnostniSpecialista(id, jmeno, prijmeni, rok);
            default -> throw new IllegalArgumentException("Neznámá skupina: " + skupina);
        };

        for (String[] s : spoluprace) {
            int kolegaId = Integer.parseInt(s[0]);
            UrovenSpolurace uroven = UrovenSpolurace.zRetezce(s[1]);
            z.pridatSpolupraci(new Spoluprace(kolegaId, uroven));
        }

        return z;
    }
}
