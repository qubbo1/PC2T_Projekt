package employeedb.storage;

import employeedb.model.*;
import employeedb.service.Databaza;

import java.sql.*;
import java.util.*;

public class SqlStorage {
    private static final String DB_URL = "jdbc:sqlite:employees.db";
    private static boolean driverDostupny = false;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            driverDostupny = true;
        } catch (ClassNotFoundException e) {
            System.err.println("[SQL] SQLite JDBC driver nenalezen – SQL záloha nebude dostupná.");
            System.err.println("[SQL] Pridajte sqlite-jdbc.jar do classpath pre aktiváciu SQL zálohy.");
        }
    }

    public static void inicializovat() {
        if (!driverDostupny) return;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS zamestnanci (
                    id INTEGER PRIMARY KEY,
                    skupina TEXT NOT NULL,
                    jmeno TEXT NOT NULL,
                    prijmeni TEXT NOT NULL,
                    rok_narozeni INTEGER NOT NULL
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS spoluprace (
                    zamestnanec_id INTEGER NOT NULL,
                    kolega_id INTEGER NOT NULL,
                    uroven TEXT NOT NULL,
                    PRIMARY KEY (zamestnanec_id, kolega_id),
                    FOREIGN KEY (zamestnanec_id) REFERENCES zamestnanci(id)
                )
            """);
            System.out.println("[SQL] Databáze inicializována.");
        } catch (SQLException e) {
            System.err.println("[SQL] Nepodařilo se inicializovat DB: " + e.getMessage());
        }
    }

    public static void ulozitVse(Databaza db) {
        if (!driverDostupny) {
            System.out.println("[SQL] Driver není k dispozici – data nebyla uložena do SQL.");
            return;
        }
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM spoluprace");
                stmt.execute("DELETE FROM zamestnanci");
            }
            String sqlZ = "INSERT INTO zamestnanci (id, skupina, jmeno, prijmeni, rok_narozeni) VALUES (?,?,?,?,?)";
            String sqlS = "INSERT INTO spoluprace (zamestnanec_id, kolega_id, uroven) VALUES (?,?,?)";
            try (PreparedStatement psZ = conn.prepareStatement(sqlZ);
                 PreparedStatement psS = conn.prepareStatement(sqlS)) {
                for (Zamestnanec z : db.getVsechni()) {
                    psZ.setInt(1, z.getId());
                    psZ.setString(2, z.getSkupina());
                    psZ.setString(3, z.getJmeno());
                    psZ.setString(4, z.getPrijmeni());
                    psZ.setInt(5, z.getRokNarozeni());
                    psZ.addBatch();
                    for (Spoluprace s : z.getSpolurace()) {
                        psS.setInt(1, z.getId());
                        psS.setInt(2, s.getKolegaId());
                        psS.setString(3, s.getUroven().getPopis());
                        psS.addBatch();
                    }
                }
                psZ.executeBatch();
                psS.executeBatch();
            }
            conn.commit();
            System.out.println("[SQL] Data úspěšně uložena do databáze.");
        } catch (SQLException e) {
            System.err.println("[SQL] Chyba při ukládání: " + e.getMessage());
        }
    }

    public static void nacistVse(Databaza db) {
        if (!driverDostupny) return;
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            db.clear();
            Map<Integer, Zamestnanec> mapa = new LinkedHashMap<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM zamestnanci ORDER BY id")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String skupina = rs.getString("skupina");
                    String jmeno = rs.getString("jmeno");
                    String prijmeni = rs.getString("prijmeni");
                    int rok = rs.getInt("rok_narozeni");
                    Zamestnanec z = switch (skupina) {
                        case "Datový analytik" -> new DataovyAnalytik(id, jmeno, prijmeni, rok);
                        case "Bezpečnostní specialista" -> new BezpecnostniSpecialista(id, jmeno, prijmeni, rok);
                        default -> throw new IllegalArgumentException("Neznámá skupina: " + skupina);
                    };
                    mapa.put(id, z);
                    db.pridatZamestnanceObjekt(z);
                }
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM spoluprace")) {
                while (rs.next()) {
                    int zId = rs.getInt("zamestnanec_id");
                    int kId = rs.getInt("kolega_id");
                    UrovenSpolurace uroven = UrovenSpolurace.zRetezce(rs.getString("uroven"));
                    Zamestnanec z = mapa.get(zId);
                    if (z != null) z.pridatSpolupraci(new Spoluprace(kId, uroven));
                }
            }
            System.out.println("[SQL] Načteno " + mapa.size() + " zaměstnanců z databáze.");
        } catch (SQLException e) {
            System.err.println("[SQL] Nelze načíst z DB: " + e.getMessage());
        }
    }
}
