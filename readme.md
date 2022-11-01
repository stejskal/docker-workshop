## Table of Contents

* [Containerization Overview](#containerization-overview)
* [Factorial server](#factorial-server)
* [Dockerize Factorial Server](#dockerize)
* [Load Balancer](#load-balancer)
* [Map Reduce](#map-reduce)
* [Docker Compose](#docker-compose)

## Containerization Overview
Conceptually a container is a portable package of everything needed to run a single application. Much as a jar is 
executable by any JVM, a container can be executed by any instance of its engine running on any hardware or OS.
 The big difference is that a container encapsulates the entire required environment like JVM, dependent jars and
even OS specific binaries.

[See a much better overview here](https://medium.com/geekculture/introduction-to-containers-basics-of-containerization-bb60503df931)

### [Docker](https://docs.docker.com/get-started/overview/)

#### Minimum Terminology
1. engine - Known as the docker daemon, this runs continuously on a server (or your machine in this case) which handles
 running containers and fulfilling client requests.
2. client - the CLI used to invoke commands on the engine.
3. Image - A read-only template with instructions for creating a Docker container. An image is composed of multiple layers.
4. Container - A container is a runnable instance of an image. You can create, start, stop, move, or delete a container.
5. Dockerfile - A text file with a sequence of steps telling docker how to create an image.
6. layer - The filesystem change which was the result of a single Dockerfile step execution.  Each layer describes
 the change applied to the layer below it.

#### Creating a container from an existing image
```shell
docker run -p 80:80 nginx
```

## Factorial Server
A simple web server with no dependencies other than the JVM.  Once started it will listen on `localhost:8080\factorial\{int}`
and will calculate the factorial of any number < Integer.MAX_VALUE and return the value.

### Starting the server directly
The below commands are executed from the subdirectory factorial-sever
```shell
mvn package
```

```shell
java -jar target/factorial-server-1.0-SNAPSHOT.jar
```

## Dockerize
### Desired outcome 

An image that contains our jar file and will start our factorial-server when a container
 is created from the image.

High level steps required:
1. Create the Dockerfile in the subdirectory factorial-server
2. find appropriate base image to modify
2. inject our source files into the image
3. create our jar file
4. set up the default execution command

## Load Balancer

## Map Reduce

## Docker Compose