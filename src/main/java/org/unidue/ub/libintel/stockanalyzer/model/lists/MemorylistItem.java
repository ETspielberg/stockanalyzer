package org.unidue.ub.libintel.stockanalyzer.model.lists;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unidue.ub.media.monographs.Item;
import org.unidue.ub.libintel.stockanalyzer.model.data.Eventanalysis;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="memorylist_item")
@Getter
@Setter
@NoArgsConstructor
public class MemorylistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="item_id")
    private String itemId = "";

    private String shelfmark = "";

    private String barcode = "";

    @Column(columnDefinition = "TEXT")
    private String comment = "";

    @OneToMany(mappedBy = "memorylistItem", cascade = CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval = true)
    private List<TitleAction> titleActions = new ArrayList<>();

    @OneToMany(mappedBy = "memorylistItem", cascade = CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval = true)
    private List<ItemAction> itemActions = new ArrayList<>();

    private Date lastChanged = new Date();

    private Date created = new Date();

    @JoinColumn
    @ManyToOne
    @JsonIgnore
    private Memorylist memorylist;

    public MemorylistItem(Item item) {
        this.itemId = item.getItemId();
        this.shelfmark = item.getShelfmark();

    }

    public MemorylistItem(Eventanalysis eventanalysis) {
        this.itemId = eventanalysis.getTitleId();
        this.shelfmark = eventanalysis.getShelfmark();
        this.created = eventanalysis.getDate();
        this.comment = eventanalysis.getComment();
        if (eventanalysis.getProposedDeletion() > 0) {
            TitleAction titleAction = new TitleAction();
            titleAction.setNumber(eventanalysis.getProposedDeletion());
            titleAction.setCollectionFrom(eventanalysis.getCollection());
            titleAction.setActionType("deletion");
            titleAction.setCollectionTo("basar");
            titleAction.setTitleId(eventanalysis.getTitleId());
            titleAction.setMemorylistItem(this);
            this.titleActions.add(titleAction);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemorylistItem)) return false;
        return id != null && id.equals(((MemorylistItem) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void setMemorylist(Memorylist memorylist) {
        this.memorylist = memorylist;
    }
}
