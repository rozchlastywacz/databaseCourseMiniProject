package pl.cwikla.bazy.projekt.model;

import javax.persistence.*;
@Entity
@Table(
        uniqueConstraints ={@UniqueConstraint(columnNames = {"state_ID", "code"})}
)
public class Region {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO)
    private int ID;

    @ManyToOne
    private State state;

    private String name;
    private String code;

    public Region() {
    }

    public Region(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public void setState(State state) {
        this.state = state;
    }

}
