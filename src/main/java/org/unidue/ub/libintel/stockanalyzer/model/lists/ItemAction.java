package org.unidue.ub.libintel.stockanalyzer.model.lists;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="item_action")
@Getter
@Setter
@NoArgsConstructor
public class ItemAction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(name="item_id", unique=true)
    private String itemId;

    private String shelfmark;

    @JoinColumn
    @ManyToOne
    @JsonIgnore
    private MemorylistItem memorylistItem;

    @Column(name="action_type")
    private String actionType;

    @Column(name="collection_from")
    private String collectionFrom;

    @Column(name="collection_to")
    private String collectionTo;

    private long number;

    private Date created = new Date();

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setCollectionFrom(String collectionFrom) {
        this.collectionFrom = collectionFrom;
    }

    public void setCollectionTo(String collectionTo) {
        this.collectionTo = collectionTo;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public void setMemorylistItem(MemorylistItem memorylistItem) {
        this.memorylistItem = memorylistItem;
    }
}
