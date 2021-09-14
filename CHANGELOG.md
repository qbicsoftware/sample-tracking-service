# Changelog

## Release 1.3.0-SNAPSHOT (2021-09-14)

## Release 1.2.0 (2021-09-14)
- Log changes to the notification table (PR #41)

## Release 1.1.0 (2021-05-25)
- Add missing JavaDoc (Issue #15)
- the `/{email}` endpoint now searches by the newly introduced `user_id` instead.
  This is no change in the behaviour as of now since they are identical as of now.
- HttpResponses now contain the error messages in the status reason
  instead of the response body

## Release 1.0.7 (2021-03-18)
- Increase `data-model-lib:2.0.0` -> `2.4.0`

## Release 1.0.6 (2021-02-22)
- Increase `data-model-lib:1.6.0` -> `2.0.0`
  - The following imports were changed `life.qbic.datamodel.`
    * `services.Sample` -> `samples.Sample`
    * `services.Location` -> `samples.Location`
    * `services.Status` -> `samples.Status`
    * `services.Address` -> `people.Address`
    * `services.Contact` -> `people.Contact`
    * `services.Person` -> `people.Person`

## Release 1.0.5 (2021-01-28)

- Configures JDBC Driver to use a connection pool
- Explicitly checks the database connection for `null`

## Release 1.0.4

- Append new location to history even if old location is the same as
  current
- Fix #13 
