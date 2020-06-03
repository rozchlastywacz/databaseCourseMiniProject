package pl.cwikla.bazy.projekt.model;

import javax.persistence.*;

@Entity
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"region_ID", "code"})}
)
public class Province {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO)
    private int ID;

    @ManyToOne
    private Region region;

    private String name;
    private String code;

    public Province() {
    }

    public Province(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return name;
    }
}
