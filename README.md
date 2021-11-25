# Sample-Tracking Service
Service that implements a sample tracking interface.

## Micronaut app
This service is build with [micronaut](https://micronaut.io):

```
mn create-app life.qbic.sampletracking --features=groovy --build maven
```

### Run locally

For the application to use the database the following information is read from environment variables:

| environment variable | description |
|---|---|
`TR_DB_HOST` | The sample tracking database host address
`TR_DB_USER` | The sample tracking database user
`TR_DB_PWD` | The sample tracking database user
`TR_DB_NAME` | The sample tracking database name

Furthermorethe a userrole definition is needed. By default a file is expected at `/etc/micronaut.d/userroles.yml`.
```
---
servicereader:
  token: 123!    // replace with your token
  roles:
    - READER
servicewriter:
  token: 123456! // replace with your token
  roles:
    - READER
    - WRITER
...
```
Once these configurations were made, you can run the sample-tracking-service locally by executing 
```
mvn 
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





