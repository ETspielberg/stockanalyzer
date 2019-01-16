package unidue.ub.stockanalyzer.model.data;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class CaldRequest {

    @Column(columnDefinition = "TEXT")
    private String mab;

    @Id
    private String identifier;

    private String titleId;

    private String isbn;

    private String shelfmark;

    private String source;

    private String target;

    public int numberOfRequests;

    public int numberOfItemsSource;

    public int numberOfItemsTarget;

    private boolean isCald;

    public String status;

    public void setCald(boolean cald) {
        isCald = cald;
    }

    public CaldRequest() {
        mab = "";
        identifier = "";
        titleId = "";
        isbn = "";
        shelfmark = "";
        source = "";
        target = "";
        numberOfRequests = 0;
        numberOfItemsSource = 0;
        numberOfItemsTarget = 0;
        isCald = true;
        status = "";
    }

    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date date;

    public String getMab() {
        return mab;
    }

    public void setMab(String mab) {
        this.mab = mab;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitleId() {
        return titleId;
    }

    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getShelfmark() {
        return shelfmark;
    }

    public void setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public int getNumberOfItemsSource() {
        return numberOfItemsSource;
    }

    public void setNumberOfItemsSource(int numberOfItemsSource) {
        this.numberOfItemsSource = numberOfItemsSource;
    }

    public int getNumberOfItemsTarget() {
        return numberOfItemsTarget;
    }

    public void setNumberOfItemsTarget(int numberOfItemsTarget) {
        this.numberOfItemsTarget = numberOfItemsTarget;
    }

    public boolean isCald() {
        return isCald;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
