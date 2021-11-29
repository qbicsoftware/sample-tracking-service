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

## Output Formats

### Common Response Codes 
The Response codes in the sample-tracking API follow the [REST API status code](https://restfulapi.net/http-status-codes/) terminology: 

| RESPONSE CODE | TEXT                  | Purpose   | 
| -----------   | -----------           | --------- |
| 200           | OK                    | For successful GET and PUT requests.| 
| 201           | Created               | The request has created a new resource| 
| 400           | Bad Request           | Issued when a malformed request was sent.| 
| 401           | Unauthorized          | Sent when the client provided invalid credentials| 
| 404           | Not Found             | The accessed resource doesn't exist or couldn't be found.| 
| 500           | Internal Server Error | When an error has occurred within the API.| 


### For samples
Providing a sampleId to the samples API 
By providing a sampleId the sample-tracking service returns the tracking information in the JSON Format: 

#### Response 
```
{
  "id": "QTEST123AE",
  "current_location": {
    "name": "QBiC",
    "responsible_person": "Max Mustermann",
    "address": {
      "affiliation": "Quantitative Biology Center",
      "street": "Auf der Morgenstelle",
      "number": 10,
      "zip_code": 72076,
      "country": "Germany"
    },
    "sample_status": "processed",
    "date_of_receipt": "2019-01-31T10:00:00Z",
    "delivered_to": "Department for Human Genetics",
    "date_of_delivery": "2019-02-01T09:00:00Z"
  },
  "passed_locations": [
    {
      "name": "QBiC",
      "responsible_person": "Max Mustermann",
      "address": {
        "affiliation": "Quantitative Biology Center",
        "street": "Auf der Morgenstelle",
        "number": 10,
        "zip_code": 72076,
        "country": "Germany"
      },
      "sample_status": "processed",
      "date_of_receipt": "2019-01-31T10:00:00Z",
      "delivered_to": "Department for Human Genetics",
      "date_of_delivery": "2019-02-01T09:00:00Z"
    }
  ]
}
```

### For locations
//ToDo Add GET Methods from LocationsController
```
{
  "full_name": "Max Mustermann",
  "address": {
    "affiliation": "Quantitative Biology Center",
    "street": "Auf der Morgenstelle",
    "number": 10,
    "zip_code": 72076,
    "country": "Germany"
  },
  "email": "max.mustermann@example.com"
}
```

