# Full text search app

This is a repository for a final project for Internet Application Design course. It's a full text search server and client application written in Java. The I used to enable full text search possibility and store the data was Elasticsearch.

## Technologies used

- Java, Maven
- Elasticsearch
- Docker
- Javascript (used for parsing and uploading data to Elastic)

## How to run

1. ```docker-compose up``` spins up a container with Elasticsearch and Kibana
2. ```cd scripts```  
    ```npm i```
    ```node ./es-migration.js``` 
    this script uploads data to elasticsearch
3. Run the Java server and client & enjoy