package edu.haverford.mpp.mappingprogressivephiladelphia;

import org.w3c.dom.Text;

import java.security.acl.Group;

/**
 * Created by BrianG on 3/3/2015.
 */
public class PhillyOrg {

    int id;
    String Timestamp;
    String GroupName;
    String Website;
    String Facebook;
    String Address;
    String ZipCode;
    String SocialIssues;
    String Mission;
    String Twitter;
    boolean Subscribed;


    public PhillyOrg(int id, String Timestamp, String GroupName, String Website, String Facebook, String Address, String ZipCode,
                     String SocialIssues, String Mission, String Twitter, boolean Subscribed){
        this.id = id;
        this.Timestamp = Timestamp;
        this.GroupName = GroupName;
        this.Website = Website;
        this.Facebook = Facebook;
        this.Address = Address;
        this.ZipCode = ZipCode;
        this.SocialIssues = SocialIssues;
        this.Mission = Mission;
        this.Twitter = Twitter;
        //if (Subscribed == 1) this.Subscribed = true; else this.Subscribed = false;

    }

    public int getId() { return id; }

    public String getTimestamp() {
        return Timestamp;
    }

    public String getGroupName() {
        return GroupName;
    }

    public String getWebsite() {
        return Website;
    }

    public String getFacebook() { return Facebook; }

    public String getAddress() {
        return Address;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public String getSocialIssues() {
        return SocialIssues;
    }

    public String getMission() {
        return Mission;
    }

    public String getTwitter() {
        return Twitter;
    }

    public boolean getSubscribed() { return Subscribed; }

}
