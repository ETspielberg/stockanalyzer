package unidue.ub.stockanalyzer.model.lists;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="saved_item")
public class SavedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="item_id")
    private String itemId = "";

    private String barcode = "";

    private String shelfmark = "";

    @Column(columnDefinition = "TEXT")
    private String comment = "";

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String internal = "";

    private String collection = "";

    private String material = "";

    private UUID itemlist;

    public SavedItem() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getShelfmark() {
        return shelfmark;
    }

    public void setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getInternal() {
        return internal;
    }

    public void setInternal(String internal) {
        this.internal = internal;
    }

    public UUID getItemlist() {
        return itemlist;
    }


    public void setItemlist(UUID itemlist) {
        this.itemlist = itemlist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedItem )) return false;
        return id != null && id.equals(((SavedItem) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
