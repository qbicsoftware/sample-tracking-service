# Sample-Tracking Service
Service that implements a sample tracking interface.

## Micronaut app
This service is build with [micronaut](https://micronaut.io):

```
mn create-app life.qbic.sampletracking --features=groovy --build maven
```

### Run locally

```
./mvnw compile
./mvnw exec:exec
```

### Execute tests

```
./mvnw test 
```

## Data model
The data model that holds sample tracking information is denfined by attributes and relations shown in the following ER diagram.

![er-diagram](models/sample-tracking-er.svg)

## API design
The remote [RESTful API](https://app.swaggerhub.com/apis-docs/qbic/sample-tracking) is created with [swagger.io](https://swagger.io/).





