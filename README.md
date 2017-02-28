Openthings
==========
Openthings is an open-source platform for sharing open design and building things together. The project is based on git.

The official Openthings instance is running here: https://openthinhs.wiki
Everyone can run their own instance when it's shared alike.

### How to start
- Open the OpenthingsImplementation folder within your terminal
```
./sbt_reload-on-codechanges
```
- Start without rebuilding on html and css changes

```
./sbt
container:start
```

### Working on CSS
- Open the OpenthingsImplementation/src/main/webapp in your terminal
```
npm install
gulp start
```

## Openthings API V1
The Openthings api makes it possible for people to load Openthing projects on the personal website through a GET API. This way people can create there own showcase of open design project but make use of the bigger Openthings system and database. For V2 we are planning to create a POST api so people can post project onto Openthings from their personal website.

### Openthings TODO
===================
We are keeping track of the Openthings TODOS in Trello:
https://trello.com/b/ztcZRp8n/openthings

### API Documentation
=====================

#### API search options overview:
- title= text search
- description= text search
- difficulty= single number 1...6 or list of numbers 1,2, ...
- state= single number 1...6 or list of numbers 1,2, ...
- creator= single id of a creator or a list of ids  1,2, ...
- tag= single id of a tag or a list of tags  1,2, ...
- rating= integer 4 or decimal 4.1; defines the minimal rating of projects
- licence= all, com or deriv
- nr_of_items= number of items for pagination
- current_page= number o the current page

#### Example string
https://www.openthings.wiki/api/projects/search?tag=51,48&nr_of_items=8

##### Search for title:
https://www.openthings.wiki/api/projects/search?title=zelfgemaakt

##### Search for tags:
With the API you can search for (multiple)tag(s). You can find the tag id simply in the url on Openthings when you click on a specific tag you would like to search for. For example:

https://www.openthings.wiki/api/projects/search?tag=51,48

##### Search-Values for difficulty:
You can search for the difficulty based on a nr from 1 to 6. For example:

https://www.openthings.wiki/api/projects/search?tag=51,48&difficulty=3

##### Search-Values for project state:
You can search for the projects with a specific state based on a number from 1 to 6. For example:

https://www.openthings.wiki/api/projects/search?tag=51,48&state=2

##### Search-Values for project licence:
You can search for the projects with a specific creative commons licence. For example:

- all
- com
- deriv

https://www.openthings.wiki/api/projects/search?tag=51,48&licence=deriv

#### Search return amount:
Specify how much items are returned:

https://www.openthings.wiki/api/projects/search?tag=51,48&licence=deriv&nr_of_items=2


#### Search page
Move through search result pages:

https://www.openthings.wiki/api/projects/search?tag=51,48&licence=deriv&nr_of_items=2&current_page=2

#### General info
##### Websites that make use of the Openthings API
- Fablab Amsterdam https://fablab.waag.org
- Platform Maker Education https://makereducation.nl
