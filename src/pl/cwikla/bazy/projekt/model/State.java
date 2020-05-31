package pl.cwikla.bazy.projekt.model;

import javax.persistence.*;

@Entity
public class State {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO)
    private int ID;

    private String name;

    public State() {
    }

    public State(String name) {
        this.name = name;
    }
}
