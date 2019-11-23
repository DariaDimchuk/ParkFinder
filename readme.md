
# VanParks

The city of Vancouver has more than 200 parks. However, there is no easy solution to find a park with the specific combinations of amenities you want; Features such as dog off-leash area, tennis court, playground etc. VanParks offers a way to search for parks with over 40 specific features to choose from. In addition, a user can search a park by name and neighborhood, and add them to their favourites! VanParks helps people to find parks they want easily and effectively and aims to promote active community and involvement in the city of Vancouver.

![ic_launcher](./app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)


## Built With

* [Vancouver Open Data] 
1. Parks (https://opendata.vancouver.ca/api/records/1.0/search/?dataset=parks&rows=300)
2. Facilities (https://opendata.vancouver.ca/api/records/1.0/search/?dataset=parks-facilities&rows=1000)  
3. Features (https://opendata.vancouver.ca/api/records/1.0/search/?dataset=parks-special-features&rows=100)

* [SQLite]
  - DB Version 1.0: When launching the app for the first time, it will create the following four tables off of the Vancouver Open Data(JSON).
    - PARK
    - PARK_FACILITY
    - PARK_FEATURE
    - FAV_PARK

* [Google Maps API]
  - com.google.android.gms:play-services-maps:16.1.0

## Features

- **Search By Feature** - Arraylist of over 40+ features to choose from. These features are selected from PARK_FEATURE and PARK_FACILITY tables and displayed to the user in a ListView. Features can be clicked to update a checkbox. A clear all button unchecks all chosen features. A search button sends the chosen features to a ParksList activity
- **Search by Name** - EditText that takes a user input. 'Like' where query was used to query for all parks with similar names to the user input.
- **Search by Location** - Spinner of all the neighborhoods available within Vancouver. These neighborhoods are selected from the PARK table. One neighborhood can be chosen at a time. A search button sends the chosen neighborhood to a ParksList activity which displays all parks that are located in the chosen neighborhood.
- **Search All** - Takes the user directly to the ParksList activity without any filters. All Parks in Vancouver are shown here.
- **See Favourites** - Takes user directly to the ParksList activity that is populated from the FAV_PARK table.
- **Park Detail Page** - Shows the name, favourited status, neighborhood and all the features and facilities of the chosen park. Allows the user to toggle the favourite status which inserts or updates the deleted column of the FAV_PARK table to 0 or 1. Includes a hotlink to the Vancouver neighborhood page the park exists in.
- **Google Maps Fragment** - Shows the named label + markers of all the parks rendered on the map. Directions to the park can be had by pressing a link which takes the user to the Google Maps application.

## Android Version

- Minimum SDK version: 23 (Lollipop)
- Target SDK version: 29 (Q)


## Authors

* **Leeseul Kim** - (https://github.com/usop7)
* **Daria Dimchuk** - (https://github.com/DariaDimchuk)
* **Robert Ozdoba** - (https://github.com/rozdoba)
