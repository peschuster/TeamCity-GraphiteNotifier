TeamCity-GraphiteNotifier
=========================

This is a plugin for TeamCity to post metrics to graphite.

On every finished build (successful of failed) two metrics are send to the configured graphite server:

* a `1` for the metric key `[configured prefix].<ProjectExternalId>.<BuildTypeExternalId>.[success|failed]`
* the number of contained changes `[configured prefix].<ProjectExternalId>.<BuildTypeExternalId>.changes`

=== Releases ===

The latest release can be downloaded from the **Releases** section on GitHub: [Plugin download](https://github.com/peschuster/TeamCity-GraphiteNotifier/releases/download/untagged-c51d7f9a9ee0b7c813a5/GraphiteNotifier.zip)