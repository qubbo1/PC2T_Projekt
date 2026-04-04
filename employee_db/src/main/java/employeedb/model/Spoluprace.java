package employeedb.model;

public class Spoluprace {
    private final int kolegaId;
    private final UrovenSpolurace uroven;

    public Spoluprace(int kolegaId, UrovenSpolurace uroven) {
        this.kolegaId = kolegaId;
        this.uroven = uroven;
    }

    public int getKolegaId() { return kolegaId; }
    public UrovenSpolurace getUroven() { return uroven; }

    @Override
    public String toString() {
        return "Kolega ID=" + kolegaId + " [" + uroven + "]";
    }
}
