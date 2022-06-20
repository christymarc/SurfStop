# SurfStop:

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Schema](#Schema)

## Overview
### Description
Allows users to get tips, learn about current surf conditions, and join a surfing community, all through providing: a short-term timeline based on their favorited beaches which describes current conditions and recent posts about the beach from other users (which deletes all posts more than 24 hrs old) and affinity groups/beach groups with associated group timelines. The app also uses the user's selected tags to push notifications to the user when other's post with that tag on the short-term timeline.

### App Evaluation
- **Category:** Social Networking / Lifestyle
- **Mobile:** Mobile is essential for the accessibility of the app; posting real-time, camera usage, real-time notifications, etc.
- **Story:** Surfing is a largely male-dominated sport with decreased accessibility to women, POC, and those of lower socio-economic status. There are two issues regarding this participation disparity that this app aims to address, price inaccessibility and social inaccessibility; not only is surf gear expensive, but if you don't know the right people or enough people who surf, it is difficult to know what gear to buy, where to buy it, learn about surf beaches, learn about surf etiquette, etc. Therefore, surfing becomes very intimidating. This app aims to break down this barrier to entry in the surfing world through democratizing resources to the sport: providing affinity groups to find community and real-time surf condition updates from forecasters and other users.
- **Market:** Any individual could choose to use this app. It is aimed towards surfers of all levels who want to find community and lean on fellow surfers for information and accurate beach conditions/updates.
- **Habit:** Surfers will be able to use this frequently, daily/weekly. Daily notifications based on tags will draw users to the app in order to catch optimal beach surf times. An average consumer is prompted to use and create via the app.
- **Scope:** At its core, this is a social app based on community mutual support whether that's through a short-term timeline or a group space. Building a short-term timeline and a system where users are notified when posts are made that use tags they're subscribed to will be difficult problem spaces. Building out features such as an image classification system, use google comments for beach detail data collection, post bookmarking, a used gear shop, or a world map feature that connects to the groups is more of a technical stretch.

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User logs in to access personal posts, groups, tags, and favorited beaches
* User can favorite a beach 
* User's short term timeline is of a selected favorite beach, and it includes a forecast and timeline for that beach
* Users can join affinity groups or beach groups that connect to long-term group timelines
* User can post on short-term (removed after 24 hrs) and long-term timelines (text and photos) -> these are different types of post objects
  * Posts with photos will be able to be pinched-to-scale
* Profile pages for each user -> which includes their groups and tags
* Settings (Notification, General, etc.)

**Optional Nice-to-have Stories**

* A description page for each group including photos, name, details
* image classification for image filtering of the timeline (see beach images option)
* Users can rate the accuracy of the surf conditions
* Used surf shop with filters by price, location, or common items
* Posts can be bookmarked by a user and there is a bookmarked page on the user's profile
* The user's profile shows their posts
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
* Community/Beach group timelines
    * displays long term posts
* Shop Screen
    * displays shop posts of used merchandise
    * posts include price, location, contact info, description, photo
    * (Potentially) filters posts
* Profile Screen 
   * Allows user to upload a photo and give a simple description
* Settings Screen
   * Lets people change app notification settings.
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
* Shop
* Profile

**Flow Navigation** (Screen to Screen)
* Forced Log-in -> Account creation if no log in is available
* Temporary Feed -> Short Post Details
* Groups -> Community/Beach Timeline -> Long Post Details
        -> (Potentially) Beach Details
        -> (Potentially) WorldMap
* Profile -> Settings
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
   | favoritedBeaches | Array of Pointer to groups | user's favorited beach groups (optional)|
   | myGroups | Array of Pointer to groups | user's favorited groups (optional)|
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
   | beach  | Boolean   | if true, group is a beach group; if false, group is not a beach group|
   | createdAt     | DateTime | date when group is created (default field) |
   | updatedAt     | DateTime | date when group is last updated (default field) |
   
#### shortPost
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for post|
   | author        | Pointer to User | post author |
   | group         | Pointer to Group | group that the post is apart of |
   | image         | Image    | image that user adds to post (optional) |
   | textContent   | String   | text content of the post by author |
   | surfHeight    | Number   | user's estimate of current average wave height (optional) |
   | surfRating    | Number   | user's assessment of the current surf (optional)|
   | tag           | String   | user's assessment described through a word tag (optional)|
   | likesCount    | Number   | likes count of the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   
#### longPost
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for post|
   | author        | Pointer to User | post author |
   | group         | Pointer to Group | group that the post is apart of |
   | image         | Image    | image that user adds to post (optional) |
   | imageExists   | Boolean  | indicates if there is an image in the post; true = yes, false = no (optional) |
   | textContent   | String   | text content of the post by author |
   | likesCount    | Number   | likes count of the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

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
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
   - Create shortPost Screen
      - (Create/POST) Create a new shortPost object
- Groups Screen
    - (Read/GET) Query all groups
- Group Feed
    - Query selected group object
    - (Read/GET) Query all long posts associated with the current group
- Profile Screen
    - (Read/GET) Query logged in user object
    - (Update/PUT) Update user profile image
    - (Update/PUT) user's description
