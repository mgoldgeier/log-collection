# log-collection

## Initial plan/thoughts:

- Plan to use:
    - Kotlin (I'm most familiar with Kotlin currently)
    - Micronaut: lightweight DI, microservice framework
- Does this need some basic security/authentication?
- Will need to access the local file system in `/var/log`
- To return results in reverse time order, will need to read log files
  starting at the end of the file:
    - Will we need to handle very large files?
    - Will it be fast enough to read file from start to end?
    - Will we need to seek to the end of the file and work backwards?
    - If we worked backwards, would we have trouble locating line ends due to file encoding issues?
    - If we worked backwards, would it actually be faster, except for with really large files?
- Basic keyword filtering:
    - If returning the last `n` events, does the filtering happen over those `n` events only? Or does it mean we need to return `n` events that match the filter, even if has to search the entire log file?
    - Does `n` have a max upper bound?
    - If we have to search through a large number of log events, do we need to timeout?
    - Are we OK with filtering this data at query time, or would we actually want to index the log data in advance? (I'm assuming that's beyond the scope here.)
- Bonus: Master server:
    - Can have the REST API accept a list of machines in the request, which could then be retrieved from secondary machines via the same REST API.

## Overview

The application runs a web server on the localhost and provides 2 endpoints:

### Get events
- Endpoint: `GET /logs/{name}/events`
- Description: Returns a JSON object with an array of event strings from the machine.
- Path parameters:
  - `name`: The name of the log file (e.g., `system.log`)
- Query parameters:
  - `num`: Required; The number of events to retrieve from most recent first
  - `filter`: Optional; Search text to filter on

### Aggregate events from multiple machines
- Endpoint: `GET /logs/{name}/aggregates`
- Description: Returns a JSON object with arrays of matching events from multiple machines.
- Path parameters:
  - `name`: The name of the log file (e.g., `system.log`)
- Query parameters:
  - `num`: Required; The number of events to retrieve from most recent first
  - `remotes`: Required; A comma-separated list of remote servers to connect to (e.g., `http://some-host:12345,https://other-host`)
  - `filter`: Optional; Search text to filter on

When requested, the aggregate function works by calling the `events` endpoint on the machines provided and aggregating
the results back into a single response.

## Limitations

- Does not implement any authentication or security; obviously a concern when running since it exposes system logs.
- Provides query-time filtering of data, which is not necessarily a limitation, but may be slower than a system that indexes data in advance and then can search the indexes.
- Does not put limits on: number of events requested, number of remote servers, filter text, etc.
- Can only work with files directly in the specified directory; cannot retrieve files in subdirectories.

## Configuration

By default, application will start http server on port 8080 and will look in `/var/log`.
Settings can be changed by modifying `application.yml` file.

## Running

To build the application and run test suite:
```
./gradlew build
```

To run the application:
```
./gradlew run
```
