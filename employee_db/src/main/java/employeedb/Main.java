package employeedb;

import employeedb.service.Databaza;
import employeedb.storage.SqlStorage;
import employeedb.ui.Menu;

public class Main {
    public static void main(String[] args) {
        Databaza db = new Databaza();

        // Inicializácia SQL DB a načtení dat
        SqlStorage.inicializovat();
        SqlStorage.nacistVse(db);

        // Spustit menu
        Menu menu = new Menu(db);
        menu.spustit();
    }
}
