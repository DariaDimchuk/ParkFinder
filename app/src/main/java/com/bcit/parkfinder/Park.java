package com.bcit.parkfinder;

import java.util.ArrayList;

public class Park {

    private String name;
    private double[] coordinates;

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
