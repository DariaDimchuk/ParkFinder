package com.bcit.parkfinder;

class Feature {

    private String featureName;
    private boolean checked;

    /**
     * Creates a new feature object.
     * @param featureName name of the feature
     */
    public Feature(String featureName) {
        this.featureName = featureName;
        this.checked = false;
    }

    /* Getters */

    public String getFeatureName() {
        return featureName;
    }

    public boolean isChecked() {
        return checked;
    }

    /* Setters */

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
}