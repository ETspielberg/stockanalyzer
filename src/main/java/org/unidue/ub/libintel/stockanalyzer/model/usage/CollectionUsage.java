package org.unidue.ub.libintel.stockanalyzer.model.usage;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.*;

public class CollectionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private ManifestationUsage manifestationUsage;

    private HashMap<String, List<DateAndCount>> groupUsage = new HashMap<>();

    private List<DateAndCount> stock;

    private List<DateAndCount> requests;

    private List<DateAndCount> calds;

    public CollectionUsage() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ManifestationUsage getManifestationUsage() {
        return manifestationUsage;
    }

    public void setManifestationUsage(ManifestationUsage manifestationUsage) {
        this.manifestationUsage = manifestationUsage;
    }

    public HashMap<String, List<DateAndCount>> getGroupUsage() {
        return groupUsage;
    }

    public void setGroupUsage(HashMap<String, List<DateAndCount>> groupUsage) {
        this.groupUsage = groupUsage;
    }

    public void addGroupUsages(List<DateAndCount> usage, String group) {
        this.groupUsage.put(group, usage);
    }

    public void addGroupUsage(DateAndCount usage, String group) {
        if (groupUsage.get(group) == null) {
            List<DateAndCount> dateAndCounts = new ArrayList<>();
            dateAndCounts.add(usage);
            this.groupUsage.put(group, dateAndCounts);
        }
        this.groupUsage.get(group).add(usage);
    }

    public void addDeltaForGroup(Date date, long delta, String group) {
        if (groupUsage.get(group) == null) {
            List<DateAndCount> dateAndCounts = new ArrayList<>();
            dateAndCounts.add(new DateAndCount(date, 0, delta));
            this.groupUsage.put(group, dateAndCounts);
        } else {
            List<DateAndCount> dateAndCounts = groupUsage.get(group);
            long lastCount = dateAndCounts.get(dateAndCounts.size()-1).getNewCount();
            dateAndCounts.add(new DateAndCount(date, lastCount, lastCount + delta));
            groupUsage.put(group, dateAndCounts);
        }
    }

    public void addRequestsDelta(Date date, long delta) {
        if (requests.size() == 0) {
            requests.add(new DateAndCount(date, 0, delta));
        } else {
            long oldCounter = requests.get(requests.size()-1).getNewCount();
            requests.add(new DateAndCount(date, oldCounter, oldCounter + delta));
        }
    }

    public void addStockDelta(Date date, long delta) {
        if (stock.size() == 0) {
            stock.add(new DateAndCount(date, 0, delta));
        } else {
            long oldCounter = stock.get(stock.size()-1).getNewCount();
            stock.add(new DateAndCount(date, oldCounter, oldCounter + delta));
        }
    }

    public void addCaldDelta(Date date, long delta) {
        if (calds.size() == 0) {
            calds.add(new DateAndCount(date, 0, delta));
        } else {
            long oldCounter = calds.get(calds.size()-1).getNewCount();
            calds.add(new DateAndCount(date, oldCounter, oldCounter + delta));
        }
    }

    public List<DateAndCount> getUsageForGroup(String group) {
        if (groupUsage.get(group) == null)
            groupUsage.put(group, new ArrayList<>());
        return this.groupUsage.get(group);
    }

    public List<DateAndCount> getStock() {
        return stock;
    }

    public void setStock(List<DateAndCount> stock) {
        this.stock = stock;
    }

    public List<DateAndCount> getRequests() {
        return requests;
    }

    public void setRequests(List<DateAndCount> requests) {
        this.requests = requests;
    }

    public void addStockDateAndCount(DateAndCount dateAndCount) {
        stock.add(dateAndCount);
    }

    public void addRequestsDateAndCount(DateAndCount dateAndCount) {
        requests.add(dateAndCount);
    }

    public List<DateAndCount> getCalds() {
        return calds;
    }

    public void setCalds(List<DateAndCount> calds) {
        this.calds = calds;
    }
}
