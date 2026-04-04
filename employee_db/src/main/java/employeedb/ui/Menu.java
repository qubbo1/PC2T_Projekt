package employeedb.ui;

import employeedb.model.*;
import employeedb.service.Databaza;
import employeedb.storage.SouborStorage;
import employeedb.storage.SqlStorage;

import java.util.Scanner;

public class Menu {
    private final Databaza db;
    private final Scanner sc;

    public Menu(Databaza db) {
        this.db = db;
        this.sc = new Scanner(System.in);
    }

    public void spustit() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Databázový systém zaměstnanců      ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean bezi = true;
        while (bezi) {
            vypistMenu();
            String volba = sc.nextLine().trim();
            System.out.println();
            switch (volba) {
                case "1" -> pridatZamestnance();
                case "2" -> pridatSpolupraci();
                case "3" -> odebratZamestnance();
                case "4" -> vyhledatPodleId();
                case "5" -> spustitDovednost();
                case "6" -> System.out.println(db.abecedniVypisSkupiny());
                case "7" -> System.out.println(db.statistiky());
                case "8" -> System.out.println(db.poctyVeSkupinach());
                case "9" -> ulozitDoSouboru();
                case "10" -> nacistZeSouboru();
                case "0" -> { bezi = false; }
                default -> System.out.println("Neplatná volba.");
            }
        }

        System.out.println("\n[SQL] Ukládám data do databáze...");
        SqlStorage.ulozitVse(db);
        System.out.println("Nashledanou!");
    }

    private void vypistMenu() {
        System.out.println("\n──────────────────────────────────────");
        System.out.println(" 1) Přidat zaměstnance");
        System.out.println(" 2) Přidat spolupráci");
        System.out.println(" 3) Odebrat zaměstnance");
        System.out.println(" 4) Vyhledat zaměstnance dle ID");
        System.out.println(" 5) Spustit dovednost zaměstnance");
        System.out.println(" 6) Abecední výpis skupin");
        System.out.println(" 7) Statistiky");
        System.out.println(" 8) Počty ve skupinách");
        System.out.println(" 9) Uložit zaměstnance do souboru");
        System.out.println("10) Načíst zaměstnance ze souboru");
        System.out.println(" 0) Ukončit");
        System.out.print("─────────────────────────────────────\nVolba: ");
    }

    // ── Akce ─────────────────────────────────────────────────────────────

    private void pridatZamestnance() {
        System.out.print("Skupina (1 = Datový analytik / 2 = Bezpečnostní specialista): ");
        String volba = sc.nextLine().trim();
        String skupina = switch (volba) {
            case "1" -> "analytik";
            case "2" -> "specialista";
            default -> volba; // umožní aj priamy textový vstup
        };
        System.out.print("Jméno: ");
        String jmeno = sc.nextLine().trim();
        System.out.print("Příjmení: ");
        String prijmeni = sc.nextLine().trim();
        System.out.print("Rok narození: ");
        int rok;
        try { rok = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Neplatný rok."); return; }

        try {
            Zamestnanec z = db.pridatZamestnance(skupina, jmeno, prijmeni, rok);
            System.out.println("✔ Přidán: " + z.zakladniInfo());
        } catch (IllegalArgumentException e) {
            System.out.println("Chyba: " + e.getMessage());
        }
    }

    private void pridatSpolupraci() {
        System.out.print("ID zaměstnance: ");
        int idA = nacistInt();
        System.out.print("ID kolegy: ");
        int idB = nacistInt();
        System.out.print("Úroveň spolupráce (špatná / průměrná / dobrá): ");
        String uStr = sc.nextLine().trim();
        UrovenSpolurace uroven;
        try { uroven = UrovenSpolurace.zRetezce(uStr); }
        catch (IllegalArgumentException e) { System.out.println("Neplatná úroveň. Použijte: špatná / průměrná / dobrá"); return; }

        System.out.println(db.pridatSpolupraci(idA, idB, uroven));
    }

    private void odebratZamestnance() {
        System.out.print("ID zaměstnance k odebrání: ");
        int id = nacistInt();
        if (db.odebratZamestnance(id)) System.out.println("✔ Zaměstnanec odebrán.");
        else System.out.println("Zaměstnanec nenalezen.");
    }

    private void vyhledatPodleId() {
        System.out.print("ID: ");
        int id = nacistInt();
        Zamestnanec z = db.najitPodleId(id);
        if (z == null) System.out.println("Zaměstnanec nenalezen.");
        else System.out.println(z.detailniInfo());
    }

    private void spustitDovednost() {
        System.out.print("ID zaměstnance: ");
        int id = nacistInt();
        System.out.println(db.spustitDovednost(id));
    }

    private void ulozitDoSouboru() {
        System.out.print("ID zaměstnance: ");
        int id = nacistInt();
        Zamestnanec z = db.najitPodleId(id);
        if (z == null) { System.out.println("Zaměstnanec nenalezen."); return; }
        System.out.print("Cesta k souboru (napr. zamestnanec.txt): ");
        String cesta = sc.nextLine().trim();
        try {
            SouborStorage.ulozitZamestnance(z, cesta);
            System.out.println("✔ Uloženo do: " + cesta);
        } catch (Exception e) {
            System.out.println("Chyba při ukládání: " + e.getMessage());
        }
    }

    private void nacistZeSouboru() {
        System.out.print("Cesta k souboru: ");
        String cesta = sc.nextLine().trim();
        try {
            Zamestnanec z = SouborStorage.nacistZamestnance(cesta);
            // Pokud již existuje, přepíšeme (odebereme starý)
            db.odebratZamestnance(z.getId());
            db.pridatZamestnanceObjekt(z);
            System.out.println("✔ Načten: " + z.zakladniInfo());
        } catch (Exception e) {
            System.out.println("Chyba při načítání: " + e.getMessage());
        }
    }

    private int nacistInt() {
        try {
            String line = sc.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
