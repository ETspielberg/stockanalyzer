package unidue.ub.stockanalyzer.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;

/**
 * Created by Eike on 22.06.2017.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stockcontrol extends Profile {

    private String description = "";

    private String subjectID ="";

    private String systemCode="";

    private String collections="";

    private String materials="";

    private int yearsToAverage=0;

    private int minimumYears=0;

    private double staticBuffer=0.0;

    private double variableBuffer=0.0;

    private String deletionMailBcc="";

    private int yearsOfRequests=0;

    private int minimumDaysOfRequest=0;

    private double blacklistExpire=0.0;

    private boolean groupedAnalysis=false;
    
    private boolean persistEmptyAnalysis=false;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID.trim();
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode.trim();
    }

    public String getCollections() {
        return collections;
    }

    public void setCollections(String collections) {
        this.collections = collections.trim();
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials.trim();
    }

    public int getYearsToAverage() {
        return yearsToAverage;
    }

    public void setYearsToAverage(int yearsToAverage) {
        this.yearsToAverage = yearsToAverage;
    }

    public int getMinimumYears() {
        return minimumYears;
    }

    public void setMinimumYears(int minimumYears) {
        this.minimumYears = minimumYears;
    }

    public double getStaticBuffer() {
        return staticBuffer;
    }

    public void setStaticBuffer(double staticBuffer) {
        this.staticBuffer = staticBuffer;
    }

    public double getVariableBuffer() {
        return variableBuffer;
    }

    public void setVariableBuffer(double variableBuffer) {
        this.variableBuffer = variableBuffer;
    }

    public String getDeletionMailBcc() {
        return deletionMailBcc;
    }

    public void setDeletionMailBcc(String deletionMailBcc) {
        this.deletionMailBcc = deletionMailBcc.trim();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier.trim();
    }

    public int getYearsOfRequests() {
        return yearsOfRequests;
    }

    public void setYearsOfRequests(int yearsOfRequests) {
        this.yearsOfRequests = yearsOfRequests;
    }

    public int getMinimumDaysOfRequest() {
        return minimumDaysOfRequest;
    }

    public void setMinimumDaysOfRequest(int minimumDaysOfRequest) {
        this.minimumDaysOfRequest = minimumDaysOfRequest;
    }

    public double getBlacklistExpire() {
        return blacklistExpire;
    }

    public void setBlacklistExpire(double blacklistExpire) {
        this.blacklistExpire = blacklistExpire;
    }

    public boolean isGroupedAnalysis() {
        return groupedAnalysis;
    }

    public void setGroupedAnalysis(boolean groupedAnalysis) {
        this.groupedAnalysis = groupedAnalysis;
    }

    public boolean isPersistEmptyAnalysis() {
        return persistEmptyAnalysis;
    }

    public void setPersistEmptyAnalysis(boolean persistEmptyAnalysis) {
        this.persistEmptyAnalysis = persistEmptyAnalysis;
    }
}
