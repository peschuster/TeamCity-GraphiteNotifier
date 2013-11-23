TeamCity-GraphiteNotifier
=========================

This is a plugin for TeamCity to post metrics to graphite.

On every finished build (successful of failed) two metrics are send to the configured graphite server:

* a `1` for the metric key `[configured prefix].<ProjectExternalId>.<BuildTypeExternalId>.[success|failed]`
* the number of contained changes `[configured prefix].<ProjectExternalId>.<BuildTypeExternalId>.changes`