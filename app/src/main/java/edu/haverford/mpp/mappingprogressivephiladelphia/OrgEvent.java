package edu.haverford.mpp.mappingprogressivephiladelphia;

import io.realm.RealmObject;

/**
 * Created by evanhamilton on 4/27/15.
 */

public class OrgEvent extends RealmObject {

    private String name;
    private int eventID;
    private String startTime;



    public String getName() {
        return name;
    }

    public int getEventID() { return eventID;}

    public String getStartTime() { return startTime;}



    public void setName(String name) {
        this.name = name;
    }

    public void setEventID(int eventID) {this.eventID = eventID;}

    public void setStartTime(String startTime) {this.startTime = startTime;}


}
