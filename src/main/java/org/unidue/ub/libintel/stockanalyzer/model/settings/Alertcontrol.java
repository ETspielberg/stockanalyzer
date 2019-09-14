package org.unidue.ub.libintel.stockanalyzer.model.settings;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Eike on 08.07.2017.
 */
@Entity
public class Alertcontrol extends Profile {

    @Column(columnDefinition = "TEXT")
    private String description;

    private String notationgroup;

    @Column(name="threshold_ratio")
    private double thresholdRatio;

    @Column(name="threshold_requests")
    private int thresholdRequests;

    @Column(name="threshold_duration")
    private int thresholdDuration;

    @Column(name="for_elisa")
    private boolean forElisa;

    @Column(name="elisa_account")
    private String elisaAccount;

    private long timeperiod;

    public String getDescription() {
        return description;
    }

    public String getNotationgroup() {
        return notationgroup;
    }

    public double getThresholdRatio() {
        return thresholdRatio;
    }

    public int getThresholdRequests() {
        return thresholdRequests;
    }

    public int getThresholdDuration() {
        return thresholdDuration;
    }

    public long getTimeperiod() {
        return timeperiod;
    }

    public boolean isForElisa() {
        return forElisa;
    }

    public String getElisaAccount() {
        return elisaAccount;
    }
}
