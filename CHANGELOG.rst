==========
Changelog
==========

This project adheres to `Semantic Versioning <https://semver.org/>`_.

1.2.2 (2021-12-13)
------------------

**Added**

**Fixed**

**Dependencies**

* org.apache.logging.log4j 2.13.2 -> 2.15.0 (addresses CVE-2021-44228)

**Deprecated**

1.2.1 (2021-11-22)
------------------

**Added**

**Fixed**

* Introduces proper HTTP response formatting for unauthorized requests (401) by providing the
WWW-Authentication header entry. This will enable clients that implement the HTTP specification strictly
to be able to make a successful request to this REST server

**Dependencies**

* Micronaut 1.2.5 -> 2.5.13
* Groovy 2.5.x -> 3.0.5

**Deprecated**

1.2.0 (2021-10-14)
------------------

**Added**

* Use Github actions

* Use new changelog style

* Log changes to the notification table (PR #41)

**Fixed**

**Dependencies**

**Deprecated**


1.1.0
-----

**Added**

* the `/{email}` endpoint now searches by the newly introduced `user_id` instead. This is no change in the behaviour as of now since they are identical as of now.
* HttpResponses now contain the error messages in the status reason instead of the response body

**Fixed**

* Add missing JavaDoc (Issue #15)

**Dependencies**

**Deprecated**


1.0.7
-----

**Added**

**Fixed**

**Dependencies**

* Increase `data-model-lib:2.0.0` -> `2.4.0`

**Deprecated**


1.0.6
-----

**Added**

**Fixed**

**Dependencies**

* Increase `data-model-lib:1.6.0` -> `2.0.0`

**Deprecated**


1.0.5
-----

**Added**

* Configures JDBC Driver to use a connection pool

**Fixed**

* Explicitly checks the database connection for `null`

**Dependencies**

**Deprecated**


1.0.4
-----

**Added**

* Append new location to history even if old location is the same as current

**Fixed**

* #13

**Dependencies**

**Deprecated**
