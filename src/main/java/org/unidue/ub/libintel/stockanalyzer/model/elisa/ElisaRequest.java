package org.unidue.ub.libintel.stockanalyzer.model.elisa;

public class ElisaRequest {

    private String notepadName;

    private String userID;

    private Title[] titles;

    public ElisaRequest() {
    }

    public String getNotepadName() {
        return notepadName;
    }

    public void setNotepadName(String notepadName) {
        this.notepadName = notepadName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Title[] getTitles() {
        return titles;
    }

    public void setTitles(Title[] titles) {
        this.titles = titles;
    }
}
