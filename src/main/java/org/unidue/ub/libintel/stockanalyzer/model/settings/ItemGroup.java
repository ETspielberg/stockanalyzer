package org.unidue.ub.libintel.stockanalyzer.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name="item_group")
public class ItemGroup {

    @Id
    @Column(unique = true)
    private String name;

    @Column(name="item_categories")
    private String[] itemCategories;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name="relevant_for_analysis")
    private boolean relevantForAnalysis;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getItemCategories() {
        return itemCategories;
    }

    public void setItemcategories(String[] itemCategories) {
        this.itemCategories = itemCategories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemCategoriesAsString() {
        String itemCategoriesAsString = "";
        if (itemCategories != null) {
            for (String itemCategory : itemCategories) {
                itemCategoriesAsString += itemCategory + " ";
            }
        }
        return itemCategoriesAsString.trim();
    }

    public boolean isRelevantForAnalysis() {
        return relevantForAnalysis;
    }

    public void setRelevantForAnalysis(boolean relevantForAnalysis) {
        this.relevantForAnalysis = relevantForAnalysis;
    }
}
