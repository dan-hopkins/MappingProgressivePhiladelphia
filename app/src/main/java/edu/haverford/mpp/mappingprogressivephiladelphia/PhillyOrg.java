package edu.haverford.mpp.mappingprogressivephiladelphia;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;

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
    double Longitude;
    double Latitude;
    String FacebookID;

    public String getFacebookID() {
        return FacebookID;
    }

    /**
     * @param id
     * @param Timestamp
     * @param GroupName
     * @param Website
     * @param Facebook
     * @param Address
     * @param ZipCode
     * @param SocialIssues
     * @param Mission
     * @param Twitter
     * @param Longitude
     * @param Latitude
     * @param Subscribed
     */

    public PhillyOrg(int id, String Timestamp, String GroupName, String Website, String Facebook, String Address, String ZipCode,
                     String SocialIssues, String Mission, String Twitter, double Longitude, double Latitude, boolean Subscribed, String FacebookID) {
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
        this.Longitude = Longitude;
        this.Latitude = Latitude;
        this.Subscribed = Subscribed;
        this.FacebookID = FacebookID;
    }

    PhillyOrg(int id,String GroupName, String Website, String Facebook, String Address, String ZipCode,
            String SocialIssues, String Mission, String Twitter, Boolean isDeleted) {
        this.id = id;
        this.GroupName = GroupName;
        this.Website = Website;
        this.Facebook = Facebook;
        this.Address = Address;
        this.ZipCode = ZipCode;
        this.SocialIssues = SocialIssues;
        this.Mission = Mission;
        this.Twitter = Twitter;
        //this.Subscribed = ; TODO
    }

    public PhillyOrg(){}

    public Location getLocation(){
        Location location = new Location("");
        location.setLatitude(this.Latitude);
        location.setLongitude(this.Longitude);
        return location;
    }

    public LatLng getLatLng(){
        return new LatLng(this.Latitude, this.Longitude);
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

    public double getLongitude() { return Longitude; }

    public double getLatitude() { return Latitude; }

    public boolean getSubscribed() { return Subscribed; }

    public void setSubscribed(Boolean subscribed) {this.Subscribed = subscribed;}

    @Override
    public String toString(){
        return this.GroupName;
    }

}
