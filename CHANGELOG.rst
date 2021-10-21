==========
Changelog
==========

This project adheres to `Semantic Versioning <https://semver.org/>`_.

1.3.0-SNAPSHOT (2021-10-21)
------------------

**Added**

**Fixed**

**Dependencies**

**Deprecated**


1.2.0 (2021-10-21)
------------------

**Added**

* Use Github actions

* Use new changelog style

* Log changes to the notification table (PR #41)

**Fixed**

**Dependencies**

* Increase `data-model-lib:2.4.0` -> `2.13.0`

* Increase `mariadb-java-client:2.0.2` -> `2.7.3`

* Increase `logback-classic:1.2.3` -> `1.2.6`

* Increase `gmavenplus-plugin:1.6.3` -> `1.12.1`

* Introduce `groovy-bom:2.5.7`

* Introduce `micronaut-bom:1.2.7`

* Introduce `maven-shade-plugin:3.2.1`

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
