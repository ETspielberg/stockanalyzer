package org.unidue.ub.libintel.stockanalyzer.model.lists;

import org.unidue.ub.libintel.stockanalyzer.model.settings.Stockcontrol;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Memorylist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private Date created;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Date lastChange;

    private String username;

    @OneToMany(mappedBy = "memorylist", cascade = CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval = true)
    List<MemorylistItem> memorylistItems;

    public Memorylist() {
        description = "";
        name = "";
        lastChange = new Date();
        this.lastChange = new Date();
        this.memorylistItems = new ArrayList<>();
    }

    public Memorylist(Stockcontrol stockcontrol) {
        this.description = stockcontrol.getIdentifier();
        this.name = stockcontrol.getDescription();
        this.created = new Date();
        this.lastChange = new Date();
        this.memorylistItems = new ArrayList<>();
    }

    public List<MemorylistItem> getMemorylistItems() {
        return memorylistItems;
    }

    public void setMemorylistItems(List<MemorylistItem> memorylistItems) {
        this.memorylistItems = memorylistItems;
    }

    public void addMemorylistItem(MemorylistItem memorylistItem) {
        this.memorylistItems.add(memorylistItem);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLastChange() {
        return lastChange;
    }

    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
