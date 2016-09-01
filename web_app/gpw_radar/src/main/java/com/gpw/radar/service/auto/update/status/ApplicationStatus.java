package com.gpw.radar.service.auto.update.status;

import org.springframework.stereotype.Component;

@Component
public class ApplicationStatus {
    private boolean updating;
    private int step;

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
