package unidue.ub.stockanalyzer.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroup {

    @Id
    private String name;

    private String[] userCategories;

    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean relevantForAnalysis;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getUserCategories() {
        return userCategories;
    }

    public void setUserCategories(String[] userCategories) {
        this.userCategories = userCategories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserCategoriesAsString() {
        String userCategoriesAsString = "";
        if (userCategories != null){
            for (String userCategory : userCategories) {
                userCategoriesAsString += userCategory + " ";
            }
        }
        return userCategoriesAsString.trim();
    }

    public boolean isRelevantForAnalysis() {
        return relevantForAnalysis;
    }

    public void setRelevantForAnalysis(boolean relevantForAnalysis) {
        this.relevantForAnalysis = relevantForAnalysis;
    }
}
