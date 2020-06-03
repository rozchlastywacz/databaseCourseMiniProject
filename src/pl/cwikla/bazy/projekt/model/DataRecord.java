package pl.cwikla.bazy.projekt.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"province_ID", "date"})}
)
public class DataRecord {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO)
    private int ID;

    @ManyToOne
    private Province province;

    private LocalDate date;
    private int numberOfCases;

    public DataRecord() {
    }

    public DataRecord(LocalDate date, int numberOfCases) {
        this.date = date;
        this.numberOfCases = numberOfCases;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public LocalDate getDate() {
        return date;
    }

    public Province getProvince() {
        return province;
    }

    public int getNumberOfCases() {
        return numberOfCases;
    }

    @Override
    public String toString() {
        return "DataRecord{" +
                "date=" + date +
                ", numberOfCases=" + numberOfCases +
                "}\n";
    }
}
