package model;

public class Candidate {
    public String unitCd;
    public String sailPerno;
    public String unitPerno;
    public String yyyymm;
    public String name;

    public Candidate(String unitCd, String sailPerno, String unitPerno, String yyyymm, String name) {
        this.unitCd = unitCd;
        this.sailPerno = sailPerno;
        this.unitPerno = unitPerno;
        this.yyyymm = yyyymm;
        this.name = name;
    }

    public String uniqueId() {
        return this.sailPerno; // Use sailPerno as the unique key
    }

    @Override
    public String toString() {
        return String.format("Candidate[unitCd=%s, sailPerno=%s, unitPerno=%s, yyyymm=%s, name=%s]", unitCd, sailPerno, unitPerno, yyyymm, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Candidate)) return false;
        Candidate c = (Candidate) obj;
        return this.unitCd.equals(c.unitCd)
            && this.sailPerno.equals(c.sailPerno)
            && this.unitPerno.equals(c.unitPerno)
            && this.yyyymm.equals(c.yyyymm)
            && this.name.equalsIgnoreCase(c.name);
    }
}
