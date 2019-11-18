package com.bcit.parkfinder;


import java.io.Serializable;
import java.util.Arrays;

public class Park implements Serializable
{
    private int parkId;
    private String name;
    private double latitude;
    private double longitude;
    private String washroom;
    private String neighbourhoodName;
    private String neighbourhoodurl;
    private String streetNumber;
    private String streetName;

    private String[] facility;
    private String[] feature;

    private boolean isFavourite = false;

    public Park() {}


    /**
     * Creates a new park object
     * @param parkId
     * @param name - park name
     * @param latitude
     * @param longitude
     * @param washroom - Y or N for if there is an available washroom
     * @param neighName - neighbourhood name
     * @param neighURL - URL to neighbourhood news / details
     * @param streetNumber - part of address
     * @param streetName - part of address
     */
    public Park(int parkId, String name, double latitude, double longitude, String washroom,
                String neighName, String neighURL, String streetNumber, String streetName) {
        this.parkId = parkId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.washroom = washroom;
        this.neighbourhoodName = neighName;
        this.neighbourhoodurl = neighURL;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
    }

    /**
     * Creates a new park object
     * @param parkId
     * @param name - park name
     * @param latitude
     * @param longitude
     * @param washroom - Y or N for if there is an available washroom
     * @param neighName - neighbourhood name
     * @param neighURL - URL to neighbourhood news / details
     * @param streetNumber - part of address
     * @param streetName - part of address
     * @param facility - description of facilities. May be null.
     * @param feature - description of features. May be null.
     */
    public Park(int parkId, String name, double latitude, double longitude, String washroom,
                String neighName, String neighURL, String streetNumber, String streetName,
                String[] facility, String[] feature) {
        this.parkId = parkId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.washroom = washroom;
        this.neighbourhoodName = neighName;
        this.neighbourhoodurl = neighURL;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.facility = facility;
        this.feature = feature;
    }


    /* Getters */

    public int getParkId(){
        return this.parkId;
    }

    public String getWashroom(){
        return this.washroom;
    }

    public String getWashroomFormattedString(){
        if(this.washroom.equalsIgnoreCase("Y")){
            return "Washroom available";
        } else{
            return "No washroom available";
        }
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public String getName(){
        return this.name;
    }

    public String getNeighbourhoodName(){
        return this.neighbourhoodName;
    }

    public String getNeighbourhoodurl(){
        return this.neighbourhoodurl;
    }

    public String getStreetNumber(){
        return this.streetNumber;
    }

    public String getStreetName(){
        return this.streetName;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public String[] getFacility() { return facility; }

    public String[] getFeature() { return feature; }

    public String[] getCombinedFeaturesFacilities(){
        if(this.facility == null && this.feature == null){
            return null;
        } else if(this.facility == null){
            Arrays.sort(this.feature);
            return this.feature;
        } else if (this.feature == null){
            Arrays.sort(this.facility);
            return this.facility;
        }

        String[] both = Arrays.copyOf(this.facility, this.facility.length + this.feature.length);
        System.arraycopy(this.feature, 0, both, this.facility.length, this.feature.length);
        Arrays.sort(both);

        return both;
    }


    /* Setters */

    public void setParkId(int parkId){
        this.parkId = parkId;
    }
    public void setWashroom(String washroom){
        this.washroom = washroom;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setNeighbourhoodurl(String url){ this.neighbourhoodurl = url; }
    public void setStreetNumber(String streetNumber){
        this.streetNumber = streetNumber;
    }
    public void setNeighbourhoodName(String neighbourhoodName){ this.neighbourhoodName = neighbourhoodName; }
    public void setStreetName(String streetName){
        this.streetName = streetName;
    }
    public void setFacility(String[] facility) {
        this.facility = facility;
    }
    public void setFeature(String[] feature) {
        this.feature = feature;
    }
    public void setFavourite(boolean f) {
        this.isFavourite = f;
    }


    public String toString() {
        return "" + this.parkId + ", " + this.name + ", " + this.latitude + ", " + this.longitude;
    }

}
