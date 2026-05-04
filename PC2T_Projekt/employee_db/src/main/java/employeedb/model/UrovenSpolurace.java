package employeedb.model;

public enum UrovenSpolurace {
    SPATNA("špatná", 1),
    PRUMERNA("průměrná", 2),
    DOBRA("dobrá", 3);

    private final String popis;
    private final int hodnota;

    UrovenSpolurace(String popis, int hodnota) {
        this.popis = popis;
        this.hodnota = hodnota;
    }

    public String getPopis() { return popis; }
    public int getHodnota() { return hodnota; }

    public static UrovenSpolurace zRetezce(String s) {
        for (UrovenSpolurace u : values()) {
            if (u.popis.equalsIgnoreCase(s)) return u;
        }
        throw new IllegalArgumentException("Neznámá úroveň: " + s);
    }

    @Override
    public String toString() { return popis; }
}
