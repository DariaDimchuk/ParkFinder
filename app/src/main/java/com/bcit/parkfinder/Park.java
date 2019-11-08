package com.bcit.parkfinder;

public class Park {

    private String name;
    private double[] coordinates;

    public Park(String name, double[] coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getName(){
        return this.name;
    }

    public double[] getCoordinates(){
        return this.coordinates;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

}
