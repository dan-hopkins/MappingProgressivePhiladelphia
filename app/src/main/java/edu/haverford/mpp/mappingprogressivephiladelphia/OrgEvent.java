package edu.haverford.mpp.mappingprogressivephiladelphia;

import io.realm.RealmObject;

/**
 * Created by evanhamilton on 4/27/15.
 */

public class OrgEvent extends RealmObject {

    private String eventName;
    private String orgName;
    private String eventDescription;
    private String eventID;
    private String startTime;
    private String facebookID;

    public OrgEvent(String eventName, String orgName, String eventDescription, String eventID, String startTime, String facebookID) {
        this.eventName = eventName;
        this.orgName = orgName;
        this.eventDescription = eventDescription;
        this.eventID = eventID;
        this.startTime = startTime;
        this.facebookID = facebookID;
    }

    public OrgEvent() {}


    public String getEventName(){ return eventName; }

    public String getOrgName() {return orgName;}

    public String getEventID() { return eventID;}

    public String getStartTime() { return startTime;}

    public String getEventDescription() {return eventDescription; }

    public String getFacebookID() {return facebookID;}

    public void setFacebookID(String facebookID) {this.facebookID = facebookID;}

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDescription(String eventDescription) {this.eventDescription = eventDescription; }

    public void setorgName(String orgName) {this.orgName = orgName; }

    public void setEventID(String eventID) {this.eventID = eventID;}

    public void setStartTime(String startTime) {this.startTime = startTime;}


}
