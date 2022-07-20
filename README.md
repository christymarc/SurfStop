# SurfStop:

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Schema](#Schema)

## Overview
### Description
SurfStop is a surf social media app catering towards surfers of all levels and all involvement. The app allows users to get tips, learn about current surf/weather conditions, and join a surfing community. Features include a temporary main feed feature -- based on a user's favorited beaches -- which describes the current weather/surf conditions and contains a timeline of recent posts about the beach from other users (posts delete after 12 hours from the server and the local datastore) and a Groups feature where users can favorite Beaches and/or other social groups with associated group timelines. The app also includes access to the current user's personal profile, which shows their personal posts to Beach groups and a list of their followed groups.

### App Evaluation
- **Category:** Social Networking / Lifestyle
- **Mobile:** Mobile is essential for the accessibility of the app; posting real-time, camera usage, offline mode (offline timelines, group access, and posting capabilities) etc.
- **Story:** Surfing is a largely male-dominated sport with decreased accessibility to women, POC, and those of lower socio-economic status. There are two issues regarding this participation disparity that this app aims to address, price inaccessibility and social inaccessibility; not only is surf gear expensive, but if you don't know the right people or enough people who surf, it is difficult to know what gear to buy, where to buy it, learn about surf beaches, learn about surf etiquette, etc. Therefore, surfing becomes very intimidating. This app aims to break down this barrier to entry in the surfing world through democratizing resources to the sport: providing groups to find community and real-time surf condition updates from forecasters and other users.
- **Market:** Any individual could choose to use this app. It is aimed towards surfers of all levels who want to find community and lean on fellow surfers for information and accurate beach conditions/updates.
- **Habit:** Surfers will be able to use this frequently, daily/weekly. Offline mode allows for users to use the app, see posts and create their own posts, in areas where they don't have internet access. An average consumer is prompted to use and create via the app.
- **Scope:** At its core, this is a social app based on community mutual support whether that's through a short-term timeline or a group space. The short-term timeline that auto-purges post data (online and offline) and the app's offline mode, managing data persistence through a local database, are the primary complex technical spaces of this app. Building out features such as a beach image classification system, web scraping for beach detail data collection, post bookmarking, or a world map feature are more of technical stretches.

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User logs in to access posts, groups, and favorited beaches
* User can favorite a beach 
* User's short-term timeline is of a selected favorite beach, and it includes a forecast and timeline for that beach
* Short-term posts expand into a larger detailed view upon clicking
* Users can join social groups that connect to long-term group timelines
* User can post on short-term (removed after 12 hrs) and long-term timelines (posts including text and photos) -> these are different types of post objects
  * Posts with photos will be able to be pinched-to-scale
* Profile pages for each user -> which includes their groups and beach posts
* Users can post, access past posts, and group timelines offline

**Optional Nice-to-have Stories**

* A description page for each group including photos, name, details
* image classification to classify current beach conditions
* Users can rate the accuracy of the surf conditions
* Used surf shop with filters by price, location, or common items
* Posts can be bookmarked by a user and there is a bookmarked page on the user's profile
* WorldMap that allows user to select a location on the map then it gives them the groups in that area

### 2. Screen Archetypes

* Login 
* Register - User signs up or logs into their account
   * Upon Download/Reopening of the application, the user is prompted to log in to gain access to their profile information
* Temporary Feed Screen
   * all posts delete after 24 hrs
   * displays the current surf conditions of the selected beach
   * (Potentially) a user-informed accuracy rating
* Groups Screen
    * displays community groups and beach groups
* Community group timelines
    * displays long term posts
* Profile Screen 
   * Allows user to see their posts to BeachGroups and Groups they've favorited
* (Potentially) Settings Screen
   * Lets people change app notification settings.
* (Potentially) Shop Screen
    * displays shop posts of used merchandise
    * posts include price, location, contact info, description, photo
    * filters posts
* (Potentially) Beach Details Screen
    * displays photos, name, description
* (Potentially) BookMarked Screen
   * displays all bookmarked posts of user  
* (Potentially) WorldMap
    * shows a map of the world and connects to groups

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Temporary Feed
* Groups
* Profile

**Flow Navigation** (Screen to Screen)
* Forced Log-in -> Account creation if no log in is available
* Temporary Feed -> Short Post Details
* Groups -> Community Timeline 
        -> (Potentially) Long Post Details
        -> (Potentially) Beach Details
        -> (Potentially) WorldMap
* Profile -> (Potentially) Settings
    -> (Potentially) Bookmarked

## Wireframes
![thumbnail_IMG_4166](https://user-images.githubusercontent.com/70297740/174128601-d0747dff-e4ae-4078-9507-e9de9ff8f718.jpg)

## Schema
### Models
#### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for user identity|
   | username      | String   | user's name identifier for login |
   | password      | String   | password required for login |
   | profilePhoto  | Image    | user's profile image (default until updated) |
   | followedTags  | Array    | user's followed tags for notification (optional)|
   | description   | String   | user description (self-written) (optional) |
   | createdAt     | DateTime | date when user is created (default field) |
   | updatedAt     | DateTime | date when user is last updated (default field) |
   
#### Group

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for user identity|
   | groupName     | String   | group's name identifier |
   | groupCoverPhoto| Image   | group's cover image (default until updated) |
   | description   | String   | group's description|
   | createdAt     | DateTime | date when group is created (default field) |
   | updatedAt     | DateTime | date when group is last updated (default field) |
   
#### BeachGroup

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for user identity|
   | groupName     | String   | group's name identifier |
   | groupCoverPhoto| Image   | group's cover image (default until updated) |
   | group         | Pointer to Group | group associated with BeachGroup |
   | description   | String   | group's description|
   | maxBreak      | String   | predicted max break height of waves currently |
   | minBreak      | String   | predicted min break height of waves currently |
   | locationId    | String   | beach's location Id |
   | createdAt     | DateTime | date when group is created (default field) |
   | updatedAt     | DateTime | date when group is last updated (default field) |
   
#### ShortPost
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for post|
   | username      | Pointer to User | post author |
   | group         | Pointer to Group | group that the post is apart of |
   | beachGroup    | Pointer to BeachGroup | beach group that the post is apart of |
   | image         | Image    | image that user adds to post (optional) |
   | textContent   | String   | text content of the post by author |
   | surfHeight    | Number   | user's estimate of current average wave height (optional) |
   | tag           | String   | user's assessment described through a word tag (optional)|
   | likesCount    | Number   | likes count of the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   
#### longPost
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for post|
   | username      | Pointer to User | post author |
   | group         | Pointer to Group | group that the post is apart of |
   | image         | Image    | image that user adds to post (optional) |
   | textContent   | String   | text content of the post by author |
   | likesCount    | Number   | likes count of the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   
#### FavoriteGroups
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for favorited group |
   | username      | Pointer to User | user associated with the favorite |
   | group         | Pointer to Group | group that the user favorited |

### Networking
#### List of network requests by screen
   - Temporary Timeline Screen
       -  (Read/Get) Query forecast data from Weather/Forecasting API
       - (Read/GET) Query all the current user's favorited beaches 
      - (Read/GET) Query all short posts where group is a user's favorited beach
         ```java
        favorited_beach = ParseUser.currentUser()
            .getFavoritedBeaches.get(position);
         query = ParseQuery.getQuery(shortPost.class)
         query.whereKey("group", equalTo: favorited_beach)
         query.order(byDescending: "createdAt")
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Query posts error", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Description: " + post.getKeyDescription() +
                            "\nUser: " + post.getKeyUser().getUsername());
                }
                // save received posts to list and notify adapter of new data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
         ```
   - Create ShortPost Screen
      - (Create/POST) Create a new ShortPost object
- Groups Screen
    - (Read/GET) Query all groups
    - (Create/POST) Create a FavoritedGroups object
    - (DELETE) Delete a FavoritedGroups object
- Group Feed
    - (Read/GET) Query all long posts associated with the current group
- Profile Screen
    - (Read/GET) Query logged in user object
    - (Read/GET) Query current user's short posts
    - (Read/GET) Query current user's groups
    - (Create/POST) Create a FavoritedGroups object
    - (DELETE) Delete a FavoritedGroups object
