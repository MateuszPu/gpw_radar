package com.gpw.radar.domain.database;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "FILLED_DATA_STATUS")
public class FillDataStatus {

    @Id
    @Column(name = "data_type")
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull
    @Column(name = "filled", columnDefinition = "boolean default 'false'", nullable = false)
    private boolean filled;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }
}
