## Flow config

We need to load the information in the akvo-flow-server-config repo and use it to be able to enumerate and connect to
the Flow instances in GAE.

#### Components needed to update and load the Flow config

##### Endpoint webhook

Data needed: path to the repo checkout

Responsible for updating the checked out instance of the akvo-flow-server-config. Restarts the system after the update.

##### Component flow-config

Data needed: path to the repo checkout

Responsible for reading the config from the file system, parse and transform it into a cloure data strucure

### Workflow

Call the webhook to update the local checkout of the akvo-flow-server-config repo. This should trigger a restart of the
system.

flow-config reads from the repo on (re)start and populates a data structure available for the rest of the app.