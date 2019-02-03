# scala-protobuff

*A quick demo of a web server that persists the received data using Google protocol buffers for serialization*

scala-protobuff is a service that provides a single HTTP endpoint:
```
POST    /   - Temporarily persists a JSON message of the format {name: String, id: Int}
```
## Installing

Plain and simple: Clone the repository to your machine and _sbt run_ it!


## Configuring and Running

You might either run it via plain sbt or via docker-compose (`docker-compose up` or `docker-compose run --service-ports scala run`). There is also a _run.sh_ script that calls docker-compose on daemon mode.

There is a .conf file at _src/main/resources/application.conf_. By default, it is set to listen to the interface 0.0.0.0 using the port 9000. Note that you might need root privilegies if you choose to listen to ports under 1025. If you are deploying using docker-compose, do not forget to change the port at the docker-compose.yml as well.

Another important set of configuration is under the _persistence_ namespace. The _path_ configuration indicates the path to store the serialized information. The second configuration, <i>rollover_seconds</i>, sets the period of time after which the persistent file should be rolled over.

For now, running _sbt run_ in its own screen session should be a better call to keep track of its output and avoid problems with sighup if it is running in a remote machine. Do not forget to increase _akka.loglevel_ when running on production. 

When building requests, be aware you must use the Content-Type application/json (e.g., `curl localhost:9000 -H "Content-Type: application/json" --data '{"name":"mike","id":42}'`). Also, take a look at [Httpie](https://github.com/jakubroztocil/httpie) for a user-friendly http client.

## Decisions and assumptions

* I picked Scala as the development language (even though this is the second time I am using it), AkkaHttp as an actor-based web framework, and ScalaPB as a protocol buffer compiler.
* Persistence is merely appending the serialized bytes to the same file
* The rollover is a periodic event trigerred by simple timeouts. Considering the use case, it is not completely clear whether it is either a periodic timeout or a writing timeout.

## TO-DOs:

* Consider alternatives for deployment, e.g., packaging into a jar file (sbt-assembly?)
* Check whether [sbt-docker](https://github.com/marcuslonnberg/sbt-docker/) is a good option for deploying
