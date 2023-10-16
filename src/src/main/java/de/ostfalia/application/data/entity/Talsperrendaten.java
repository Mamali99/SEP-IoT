package de.ostfalia.application.data.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Talsperrendaten") // name der Datenbanktabelle
@IdClass(TalsperrendatenID.class)
public class Talsperrendaten {
    @Id
    @Column(name = "fkTalsperre")
    int fkTalsperre;

    @Id
    @Column(name = "Zeitpunkt")
    LocalDateTime tstamp;

    @Column(name = "Stauinhalt")
    double stauinhalt;

    @Column(name = "Zufluss")
    double zufluss;
    @Column(name = "Abgabe")
    double abgabe;

    public double getZufluss() {
        return zufluss;
    }

    public void setZufluss(double zufluss) {
        this.zufluss = zufluss;
    }

    public double getAbgabe() {
        return abgabe;
    }

    public void setAbgabe(double abgabe) {
        this.abgabe = abgabe;
    }



    public int getFkTalsperre() {
        return fkTalsperre;
    }

    public void setFkTalsperre(int fkTalsperre) {
        this.fkTalsperre = fkTalsperre;
    }

    public LocalDateTime getTstamp() {
        return tstamp;
    }

    public void setTstamp(LocalDateTime tstamp) {
        this.tstamp = tstamp;
    }

    public double getStauinhalt() {
        return stauinhalt;
    }

    public void setStauinhalt(double stauinhalt) {
        this.stauinhalt = stauinhalt;
    }


}
