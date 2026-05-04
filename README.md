# Databázový systém zaměstnanců

## Popis projektu
Konzolová Java aplikace pro správu zaměstnanců technologické firmy.

## Struktura projektu
```
employee_db/
├── src/main/java/employeedb/
│   ├── Main.java                          ← vstupní bod programu
│   ├── model/
│   │   ├── Zamestnanec.java               ← abstraktní třída (OOP)
│   │   ├── DataovyAnalytik.java           ← rozšíření Zamestnanec
│   │   ├── BezpecnostniSpecialista.java   ← rozšíření Zamestnanec
│   │   ├── Spoluprace.java                ← záznam spolupráce
│   │   └── UrovenSpolurace.java           ← enum (špatná/průměrná/dobrá)
│   ├── service/
│   │   └── Databaza.java                  ← hlavní logika (LinkedList)
│   ├── storage/
│   │   ├── SouborStorage.java             ← uložení/načtení do souboru
│   │   └── SqlStorage.java                ← záloha do SQLite DB
│   └── ui/
│       └── Menu.java                      ← konzolové menu
├── lib/                                   ← sem vložte sqlite-jdbc.jar
├── out/                                   ← zkompilované .class soubory
├── spustit.sh                             ← spuštění Linux/Mac
└── spustit.bat                            ← spuštění Windows
```

## Požadavky
- Java 17 nebo novější
- (volitelné) SQLite JDBC driver pro SQL zálohu

## Spuštění

### Linux / macOS
```bash
chmod +x spustit.sh
./spustit.sh
<img width="386" height="528" alt="image" src="https://github.com/user-attachments/assets/ed3b2eb5-2f0a-405f-8007-b517df6ed50e" />

```

### Windows
Dvojklik na `spustit.bat`

### Manuální kompilace a spuštění
```bash
mkdir out
find src -name "*.java" | xargs javac --release 17 -d out
java -cp out -Dfile.encoding=UTF-8 employeedb.Main
```

## SQL záloha (volitelné)
Pro aktivaci SQL zálohy:
1. Stáhněte `sqlite-jdbc-*.jar` z https://github.com/xerial/sqlite-jdbc/releases
2. Vložte JAR soubor do složky `lib/`
3. Program automaticky detekuje driver a aktivuje SQL zálohu

Bez driveru program funguje normálně – SQL záloha je pouze přeskočena.

## OOP koncepty použité v projektu
| Požadavek               | Implementace                                              |
|-------------------------|-----------------------------------------------------------|
| Abstraktní třída        | `Zamestnanec` – abstraktní metody `getSkupina()`, `spustitDovednost()` |
| Dědičnost               | `DataovyAnalytik`, `BezpecnostniSpecialista` rozšiřují `Zamestnanec` |
| Polymorfismus           | `spustitDovednost()` volána přes referenci `Zamestnanec` |
| Dynamická datová struktura | `LinkedList<Zamestnanec>` v třídě `Databaza`         |
| Enum                    | `UrovenSpolurace`                                         |

## Funkce programu
| Č. | Funkce                                      |
|----|---------------------------------------------|
| 1  | Přidání zaměstnance (výběr skupiny)         |
| 2  | Přidání spolupráce (ID + ID + úroveň)       |
| 3  | Odebrání zaměstnance (včetně všech vazeb)   |
| 4  | Vyhledání dle ID + statistiky               |
| 5  | Dovednost – Analytik: společní kolegové     |
|    | Dovednost – Specialista: rizikové skóre     |
| 6  | Abecední výpis dle příjmení ve skupinách    |
| 7  | Globální statistiky + zaměstnanec s nejvíce vazbami |
| 8  | Počty zaměstnanců ve skupinách              |
| 9  | Uložení zaměstnance do .txt souboru         |
| 10 | Načtení zaměstnance ze souboru              |
| –  | SQL záloha při ukončení / načtení při startu|

## Algoritmy dovedností

### Datový analytik
Najde spolupracovníka, s nímž má daný analytik **nejvíce společných spolupracovníků**
(průnik množin kontaktů).

### Bezpečnostní specialista
Vypočítá rizikové skóre podle vzorce:

```
riziko = (počet_kolegů × 10) / (průměrná_kvalita × 3 + 1)
```

- Více spolupracovníků → vyšší riziko
- Vyšší průměrná kvalita → nižší riziko

Kategorie: 🟢 Nízké (≤10) | 🟡 Střední (11–25) | 🔴 Vysoké (26+)
