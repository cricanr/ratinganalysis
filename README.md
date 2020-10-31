# Running application
## Using sbt
Prerequisites:
- install java, minimum JDK 8, or anything up to JDK 15: https://openjdk.java.net/install/
- install sbt: https://www.scala-sbt.org/1.x/docs/Setup.html
Sbt is the industry standard used Scala build tool

Compile test & run application locally:
Open sbt in the repo root folder: 
Compile & run tests:
 
 `sbt clean compile test`
 
Run application: 
 ```sbt run```

## Docker Version:
You can also create a docker image of the application and run it using docker
Prerequisites: 
Install docker: https://docs.docker.com/get-docker/

Create docker image locally

```sbt docker:publishLocal```

Check images:
```docker images -a```
Output example: 
```
radu@Radu-Desktop:~/sources/ratinganalysis$ docker images -a
ratinganalysis      0.1.0-SNAPSHOT      3768014b8511        4 minutes ago       545MB
```

Running docker image created (`ratingssummary`):

```docker run ratinganalysis:0.1.0-SNAPSHOT```

