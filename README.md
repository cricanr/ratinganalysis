# Application that reads ratings for different products from a CSV data source and generates a ratings summary
Application is developed in Scala 13 as a console application. It receives as input the path to the CSV file containing
ratings in this format: 

```
Buyer Id,Shop Id,Product Id,Rating
buyer1,veloshop,chain-01,4
```

Validation rules: 
```
The input is described in a CSV formatted file, see an example in this section.
It contains 4 columns that identifies the buyer, shop, product and rating.
- The Buyer Id is sequence of alphanumeric characters that starts with a letter, e.g. buyer1
- The Shop Id is a sequence of alphanumeric characters that starts with a letter, e.g.
shop1
- The Product Id is a sequence of alphanumeric characters and hyphen (-) that starts with
a letter and ends with hyphen and a numeric value, the numeric value at the end is in a
range between 1 and 99, e.g. smart-tv-01, patagonia-32
- The Rating is numeric value between 1 and 5 as a whole number, e.g. 3 or 5
The CSV lines can be empty, or corrupted (not all the columns are present or formatted properly
- according to the rules described above).
```

The application reads all product lines, validates them (in case of validation failure it sends a business validation failure,
see validation rules above) and then it will create a summary of the ratings in this format:
```
{
"validLines" : 120,
"invalidLines" : 8,
"bestRatedProducts" : [
"lights-02",
"chain-01",
"widetv-03"
],
"worstRatedProducts" : [
"pandora-01",
"guess-01",
"wifi-projector-01"
],
"mostRatedProduct" : "smarttv-01",
"lessRatedProduct" : "saddle-01"
}
```
The generated summary is returned to the user and printed out to the console in JSON format.

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


#Architecture & code notes:
Application is written in Scala 13 as a console application. Code is organized in this way:
- Product:
 * it's a model, case class for transport of products in application
 * validation is done using CATS Validated
- CSV Parser: 
 * parses CSV files into products using: `com.github.tototoshi:scala-csv`
- ProductsService:
 * a small service class that contains the logic: calls the csv parser, validations and generates
 a rating summary
I am using dependency injection using Google Guice for Scala for ease of testing & configuration.
For JSON serialization I use Cats circe JSON parser.
For unit tests I use scalatest & mockito for Scala. 
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


# Further development:
As a minimal solution I have not considered for now some points that should be addressed:

* optimisation of the code that reads products from the CSV file. Currently just reading all lines. 
In case we have a huge input file this would not work. We would need to use a batch / stream approach
to read chunks of data at a time. The code that generates the rating summary needs to be 
optimised from a complexity point of view, for now it is not optimal. We could also consider a way
to generate the ratings summary as chunks of data come so that at any point of time we have a 
intermediate summary from the data read so far. For implementing this we have different tools and 
algorithms that we can consider using. 
* the logic used to calculate ratings is currently pretty limited. For a more mature system we should consider
adding some weighting mechanism so that a product with 10 ratings stands lower then one with say 3000 ratings
After a short search on the internet for this I found this: https://en.wikipedia.org/wiki/IMDb, more 
exactly: `Rankings` paragraph describing:
* logging should be added to our application
* add a log injection mechanism to collect logs for further monitoring
* add configuration entries
* add CI/CD


# Scalafmt:

In order to have well formatted, consistent, easy to maintain code approved by Scala community 
standards I use Scalafmt. It is configurable to work within IntelliJ or other IDEs, integrated with your favourite shortcuts
and also at build time when a file is saved code will be reformatted accordingly. 
Installation documentation: https://scalameta.org/scalafmt/

Useful sbt commands to run Scalafmt tasks:

```
scalafmt
scalafmtAll
scalafmtCheck
scalafmtCheckAll
scalafmtDoFormatOnCompile
scalafmtOnly
scalafmtSbt
scalafmtSbtCheck
```


Useful links:
1. Scalafmt: https://scalameta.org/scalafmt/
2. Scala CSV Parser: https://github.com/tototoshi/scala-csv
3. Cats Validated: https://typelevel.org/cats/datatypes/validated.html
4. JSON Circe: https://circe.github.io/circe/
5. Guice dependency injection for Scala: https://github.com/codingwell/scala-guice
6. Docker sbt plugin: https://www.scala-sbt.org/sbt-native-packager/formats/docker.html
7. Docker: https://www.docker.com/