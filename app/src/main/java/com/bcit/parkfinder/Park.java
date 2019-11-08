package com.bcit.parkfinder;

public class Park
{
    private int parkId;

    private boolean washroom;

    private double latitude;

    private double longitude;

    private String name;

    private String neighbourhoodurl;

    private String streetNumber;

    private String neighbourhoodName;

    private String streetName;

    public void setParkId(int parkId){
        this.parkId = parkId;
    }
    public int getParkId(){
        return this.parkId;
    }
    public void setWashroom(boolean washroom){
        this.washroom = washroom;
    }
    public boolean getWashroom(){
        return this.washroom;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setNeighbourhoodurl(String neighbourhoodurl){
        this.neighbourhoodurl = neighbourhoodurl;
    }
    public String getNeighbourhoodurl(){
        return this.neighbourhoodurl;
    }
    public void setStreetNumber(String streetNumber){
        this.streetNumber = streetNumber;
    }
    public String getStreetNumber(){
        return this.streetNumber;
    }
    public void setNeighbourhoodName(String neighbourhoodName){
        this.neighbourhoodName = neighbourhoodName;
    }
    public String getNeighbourhoodName(){
        return this.neighbourhoodName;
    }
    public void setStreetName(String streetName){
        this.streetName = streetName;
    }
    public String getStreetName(){
        return this.streetName;
    }
}
