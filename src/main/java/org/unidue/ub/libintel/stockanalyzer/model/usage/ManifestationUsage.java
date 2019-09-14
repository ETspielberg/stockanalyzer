package org.unidue.ub.libintel.stockanalyzer.model.usage;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ManifestationUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private HashMap<String, CollectionUsage> collectionUsage = new HashMap<>();

    private String titleId = "";

    private List<String> shelfmarks = new ArrayList<>();

    private List<String> barcodes = new ArrayList<>();

    public ManifestationUsage() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public List<String> getShelfmarks() {
        return shelfmarks;
    }

    public void setShelfmarks(List<String> shelfmarks) {
        this.shelfmarks = shelfmarks;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
    }

    public HashMap<String, CollectionUsage> getCollectionUsage() {
        return collectionUsage;
    }

    public void setCollectionUsage(HashMap<String, CollectionUsage> collectionUsage) {
        this.collectionUsage = collectionUsage;
    }

    public void addCollectionUsages(CollectionUsage collectionUsage, String collection) {
        this.collectionUsage.put(collection, collectionUsage);
    }

    public CollectionUsage getUsageForCollection(String collection) {
        if (collectionUsage.get(collection) == null) {
            CollectionUsage collectionUsage = new CollectionUsage();
            collectionUsage.setManifestationUsage(this);
            this.collectionUsage.put(collection, collectionUsage);
        }
        return this.collectionUsage.get(collection);
    }
}
