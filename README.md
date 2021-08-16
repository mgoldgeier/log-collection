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
