package de.ostfalia.application.data.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class TalsperrendatenID implements Serializable {

    int fkTalsperre;

    LocalDateTime tstamp;

    public TalsperrendatenID(int fkTalsperre, LocalDateTime tstamp) {
        this.fkTalsperre = fkTalsperre;
        this.tstamp = tstamp;
    }
    public TalsperrendatenID(){}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TalsperrendatenID that = (TalsperrendatenID) o;

        if (fkTalsperre != that.fkTalsperre) return false;
        return Objects.equals(tstamp, that.tstamp);
    }

    @Override
    public int hashCode() {
        int result = fkTalsperre;
        result = 31 * result + (tstamp != null ? tstamp.hashCode() : 0);
        return result;
    }
}
