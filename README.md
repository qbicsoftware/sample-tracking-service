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
The data model that holds sample tracking information is defined by attributes and relations shown in the following ER diagram.

![er-diagram](models/sample-tracking-er.svg)

## API design
The remote [RESTful API](https://app.swaggerhub.com/apis-docs/qbic/sample-tracking) is created with [swagger.io](https://swagger.io/).

## Authentication

The roles and user tokens must be provided in a file following the YAML format specification.
An exemplary entry looks like this:

```
servicereader:
    token: 123!
    roles:
        - READER
    servicewriter:
        token: 123456!
        roles:
            - READER
            - WRITER
```

### Retrieve sample information from sampleID
The accepted input formats are listed in the following
To obtain tracking information for a given sample id
```
{
"sampleId":"QABCD12AE"
}
### Set current location for a sample from sampleID
```
To set the current location for a sample with the given identifier
```
{
  "name":"Example Location Name",
  "responsible_person":"Max Mustermann",
  "responsible_person_email":"max.mustermann@uni-tubingen.de",
  "address":{
    "affiliation":"QBiC",
    "street":"Auf der Morgenstelle 6",
    "zip_code":72076,
    "country":"Germany"
  },
  "sample_status":"WAITING",
  "arrival_date":"2021-12-07T09:38Z",
  "forward_date":"2021-12-07T09:38Z"
}

NOTE: The provided location information should be stripped of all newlines("\n") even between the attributes, otherwise it can't be interpreted by the sample-tracking-service
}
```
### Retrieve contact Information from email address

**NOTE: This method is deprecated and will be removed in future versions** 
To retrieve contact information based on a given email address
```
"email": "max.mustermann@uni-tubingen.de"
```
### Retrieve location information for an userId

To retrieve location information for a given user id
```
"user_id": "qabcd04"
```





