# Sample-Tracking Service
Service that implements a sample tracking interface.

Overview:

- [Micronaut app](#micronaut-app)
  * [Run locally](#run-locally)
  * [Execute tests](#execute-tests)
- [Data model](#data-model)
- [Authentication](#authentication)
- [Api design](#api-design)
  * [Common response codes](#common-response-codes)
  * [Endpoint format](#endpoint-format)
  * [Retrieve sample information from sampleID](#retrieve-sample-information-from-sampleid)
  * [Update Sample Status of current location from sampleId and sample status](#update-sample-status-of-current-location-from-sampleid-and-sample-status)
  * [Retrieve location information for an userId](#retrieve-location-information-for-an-userid)
  * [Update current location of sampleId](#update-current-location-of-sampleid)
  * [Retrieve complete location to user linked information](#retrieve-complete-location-to-user-linked-information)
  * [Retrieve contact Information from email address](#retrieve-contact-information-from-email-address)

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

## API design

### Common response codes
The Response codes in the sample-tracking API follow the [REST API status code](https://restfulapi.net/http-status-codes/) terminology:

| RESPONSE CODE | TEXT                  | Purpose   | 
| -----------   | -----------           | --------- |
| 200           | OK                    | For successful GET and PUT requests.| 
| 201           | Created               | The request has created a new resource| 
| 400           | Bad Request           | Issued when a malformed request was sent.| 
| 401           | Unauthorized          | Sent when the client provided invalid credentials| 
| 404           | Not Found             | The accessed resource doesn't exist or couldn't be found.| 
| 500           | Internal Server Error | When an error has occurred within the API.| 


### Endpoint format
The endpoints formatting follows the [OpenAPI Specifications](https://swagger.io/specification/)

### Retrieve sample information from sampleID
Gets the sample information including current and past locations for a specific sample ID in JSON format

#### Endpoint
```
  /samples/{sampleId}:
    get:
      summary: "GET samples/{sampleId}"
      parameters:
      - name: "sampleId"
        in: "path"
      responses:
        "200":
          description: "OK"
```

#### Example Request

```
/sample/QMUJW064AW
```

#### Example Response
```
{
  "code": "QMUJW064AW",
  "current_location": {
    "name": "QBiC",
    "responsible_person": "John Doe",
    "responsible_person_email": "John.Doe@Templa.te",
    "address": {
      "affiliation": "QBiC",
      "street": "Auf der Morgenstelle 10",
      "zip_code": 72076,
      "country": "Germany"
    },
    "sample_status": "METADATA_REGISTERED",
    "arrival_date": "2019-12-03T10:32Z"
  }
}
```

### Update Sample Status of current location from sampleId and sample status

Updates the status set in the current location of the provided sampleId with the provided sample status.

#### Endpoint
```
  /samples/{sampleId}/currentLocation/{status}:
    put:
      summary: "PUT samples/{sampleId}/currentLocation/{status}"
      parameters:
      - name: "sampleId"
        in: "path"
      - name: "status"
        in: "path"
      responses:
        "201":
          description: "OK"
```
#### Example Request

```
/samples/QMUJW064AW/currentLocation/data_available/
``` 

#### Example Response

``` 
Response code: 201 (Sample status updated to DATA_AVAILABLE.)
``` 

### Retrieve location information for an userId

Gets the location information for a given user id in JSON Format:

#### Endpoint

```
  /locations/{user_id}:
    get:
      summary: "GET locations/{user_id}"
      parameters:
      - name: "user_id"
        in: "path"
      responses:
        "200":
          description: "OK"
```

#### Example Request

```
/locations/John.Doe@Templa.te
```

#### Example Response
```
[
  {
    "name": "QBiC",
    "responsible_person": "John Doe",
    "responsible_person_email": "John.Doe@Templa.te",
    "address": {
      "affiliation": "QBiC",
      "street": "Auf der Morgenstelle 10",
      "zip_code": 72076,
      "country": "Germany"
    }
  },
  {
    "name": "Awesome Partner Lab",
    "responsible_person": "John Doe",
    "responsible_person_email": "John.Doe@Templa.te",
    "address": {
      "affiliation": "Partner Labs",
      "street": "Example Street 5",
      "zip_code": 12345,
      "country": "ImaginationLand"
    }
  },
  {
    "name": "Splendid Facility",
    "responsible_person": "John Doe",
    "responsible_person_email": "John.Doe@Templa.te",
    "address": {
      "affiliation": "Partner Facilities",
      "street": "Example Lane 10",
      "zip_code": 12345,
      "country": "ImaginationLand"
    }
  }
]
```

### Update current location of sampleId

Updates the current location of a provided sampleId with the provided location information. 

#### Endpoint
```
  /samples/{sampleId}/currentLocation/:
    post:
      summary: "POST samples/{sampleId}/currentLocation/"
      parameters:
      - name: "sampleId"
        in: "path"
      responses:
        "200":
          description: "OK"
```

#### Example Request

```
/samples/QMUJW064AW/currentLocation/
```

Additionally, the location information has to be provided in the JSON Format: 

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
```

**NOTE: The provided location information should be stripped of all newlines("\n"), otherwise it can't be interpreted by the sample-tracking-service**

#### Example Response

The sample-tracking-service will return the set location information: 

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
```

### Retrieve complete location to user linked information

Gets all the location to users linked information in JSON format

#### Endpoint

```
  /locations/:
    get:
      summary: "GET locations/"
      responses:
        "200":
          description: "OK"
```

#### Example Request

```
/locations/
```

#### Example Response
```
[
  {
    "name": "QBiC",
    "responsible_person": "John Doe",
    "responsible_person_email": "John.Doe@Templa.te",
    "address": {
      "affiliation": "QBiC",
      "street": "Auf der Morgenstelle 10",
      "zip_code": 72076,
      "country": "Germany"
    }
  },
  {
    "name": "Awesome Partner Lab",
    "responsible_person": "John Doe",
    "responsible_person_email": "John.Doe@Templa.te",
    "address": {
      "affiliation": "Partner Labs",
      "street": "Example Street 5",
      "zip_code": 12345,
      "country": "ImaginationLand"
    }
  },
  {
    "name": "QBiC",
    "responsible_person": "Erika Musterfrau",
    "responsible_person_email": "Erika@MusterFr.au",
    "address": {
      "affiliation": "QBiC",
      "street": "Auf der Morgenstelle 10",
      "zip_code": 72076,
      "country": "Germany"
    }
  },
  {
    "name": "Splendid Facility",
    "responsible_person": "Erika Musterfrau",
    "responsible_person_email": "Erika@MusterFr.au",
    "address": {
      "affiliation": "Partner Facilities",
      "street": "Example Lane 10",
      "zip_code": 12345,
      "country": "ImaginationLand"
    }
  },
  }]
```

### Retrieve contact Information from email address

**NOTE: This method is deprecated and will be removed in future versions**

Gets the linked affiliation and person information for an email address in JSON Format:

#### Endpoint
```
  /locations/contacts/{email}:
    get:
      summary: "GET locations/contacts/{email}"
      deprecated: true
      parameters:
      - name: "email"
        in: "path"
      responses:
        "200":
          description: "OK"
```

#### Example Request

```
/locations/contacts/John.Doe@Templa.te
```

#### Example Response
```
{
  "full_name": "John Doe",
  "address": {
    "affiliation": "QBiC",
    "street": "Auf der Morgenstelle 10",
    "zip_code": 72076,
    "country": "Germany"
  },
  "email": "John.Doe@Templa.te"
}
```

