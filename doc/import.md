## Importing Flow data

### Constraints

* We shouldn't need to redeploy a Flow instance to import data
* We should try to avoid (as much as possible) massive updates
  in Flow datastore
* Metadata about the _state_ of the process should be in Reflow database
* The import process should be _restable_
* We should be able to _interrupt_ the import process
* While importing we must _signal_ the Flow instance to stop pushing events

### Strategy

1. We'll have a `properties` table in Reflow database with known
   `keys` to configuration and/or state values.

   * This table could hold values like: the current state of the
     import process, the current `syncVersion` for an instance, etc

2. We set the flag in Flow datastore to stop pushing events to Reflow.

   * This will be a new _Kind_ holding some instance properties with
     known keys (e.g. `syncVersion`, `enablePushEvents`, etc)

3. We start the process by massively update `EventQueue` entities
   setting a property `syncVersion` to `0`.

   * This could be achieved using _RemoteAPI_ but implies, reading
   entities and putting them back again via HTTP.

   * Another approach is to do this processing directly in GAE runtime
   via a _Task Queue_ and the _backend_ `dataprocessor`. If we follow
   this approach, we need a way of sending a _signal_ back to Reflow
   that the process has finished (or Reflow will need to _poll_ for
   status).

     * To make  _status_ requests (via RemoteAPI), GAE code needs
	   to persist somewhere the state of the process.

     * We need to find a way to initiate the Task Queue process directly
	   from Reflow

	   * ~~[Task Queue REST API](https://cloud.google.com/appengine/docs/java/taskqueue/rest/about_auth)~~
	   The task Queue REST API only support _pull_ tasks. And we're
	   trying to do the less task management work possible.

	   * We could implement a servlet to launch the task, with custom
	   authentication via a shared secret (e.g. `apiKey`)

4. After the process in step 3 finishes (potentially a _no-op_) we
   continue and bump the `syncVersion` property in Reflow and Flow
   datastore.

5. We clean the tables in Reflow database (`answer`, `survey`, `form`,
   etc).

6. We read all defined _kinds_ and persist a JSON serialized version
   of the full entity.

   * Only the entities with `createdDateTime` before the first
     `EventQueue` entity are needed for the import.

7. [TBD]
