package edu.haverford.mpp.mappingprogressivephiladelphia;

import io.realm.RealmObject;

/**
 * Created by evanhamilton on 4/27/15.
 */

public class OrgEvent extends RealmObject {

    private String eventName;
    private String orgName;
    private String eventDescription;
    private int eventID;
    private String startTime;



    public String getEventName(){ return eventName; }

    public String getOrgName() {return orgName;}

    public int getEventID() { return eventID;}

    public String getStartTime() { return startTime;}

    public String getEventDescription() {return eventDescription; }



    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventDescription(String eventDescription) {this.eventDescription = eventDescription; }

    public void setorgName(String orgName) {this.orgName = orgName; }

    public void setEventID(int eventID) {this.eventID = eventID;}

    public void setStartTime(String startTime) {this.startTime = startTime;}


}
