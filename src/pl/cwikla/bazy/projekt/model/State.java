package pl.cwikla.bazy.projekt.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
