package org.unidue.ub.libintel.stockanalyzer.model.usage;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

public class DateAndCount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Date date;

    private long oldCount;

    private long newCount;

    private CollectionUsage collectionUsage;

    public DateAndCount() {
        date = new Date();
        oldCount = 0L;
        newCount = 0L;
    }

    public DateAndCount(Date date, long oldCount, long newCount) {
        this.date = date;
        this.oldCount = oldCount;
        this.newCount = newCount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getOldCount() {
        return oldCount;
    }

    public void setOldCount(long oldCount) {
        this.oldCount = oldCount;
    }

    public long getNewCount() {
        return newCount;
    }

    public void setNewCount(long newCount) {
        this.newCount = newCount;
    }
}
