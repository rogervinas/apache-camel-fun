# Fun with Apache Camel

![fun-with-apache-camel](etc/fun-with-apache-camel.jpg)

## Before Having Fun

What is Camel? *- Jonathan Anstey at [DZone](https://dzone.com/articles/open-source-integration-apache)*

Apache Camel is an open source Java framework that focuses on making integration easier and more accessible to developers. It does this by providing:

* Concrete implementations of all the widely used EIPs.
* Connectivity to a great variety of transports and APIs.
* Easy to use Domain Specific Languages (DSLs) to wire EIPs and transports together.

All is about Routes, Components and Exchanges:

* Routes:
  * A route defines the workflow an Exchange will have to follow.
  * A route starts using a Component consumer (the **from**) and then can apply any set of EIP's and invoke any set of Component producers (usually the **to**'s).

* Components:
  * A Component may provide two implementations: a Consumer and a Producer.
  * A Component consumer is placed at the **from** of a route and generates Exchanges every time its own logic is triggered, usually receiving data from an external source.
  * A Component producer is placed at a **to** endpoint and usually sends the Exchange to an external sink.

* Exchanges:
  * An Exchange is the data flowing across a route, containing:
    * An **in** message: a body (anything) and some headers (list of key+value)
    * An **out** message: a body (anything) and some headers (list of key+value)
    * Properties: a list of key+value only set/get by us and available along the route.
  * At any step of a route the **in** message is transformed to an **out** message which will be the **in** message for the following step.
  * Type converters will automatically convert a body to whatever type we want to read it as.

Useful links:

* [What is Camel](http://camel.apache.org/what-is-camel.html)
* [Integration patterns](http://camel.apache.org/enterprise-integration-patterns.html)
* [Components](http://camel.apache.org/components.html)
* [Languages](http://camel.apache.org/languages.html)
* [Type Converters](http://camel.apache.org/type-converter.html)
* [Error handling](http://camel.apache.org/error-handling-in-camel.html)
* [Testing](http://camel.apache.org/testing.html)
* [Examples](https://github.com/apache/camel/tree/master/examples)
* [Source Code](https://github.com/apache/camel/tree/master)

## Fun Use Case #1

![apache-camel-fun-1](etc/apache-camel-fun-1.png)

* An external system periodically creates files in a local directory.
* The external system sends them ZIP compressed.
* The original content is XML:

```xml
<UserQuotes>
  <UserQuote Id="nnn" Name="xxx" Surname="yyy">
    <Quote>zzzzzz</Quote>
  </UserQuote>
  <UserQuote/>
  <UserQuote/>
</UserQuotes>
```

* Generate one JSON for each UserQuote:

```javascript
{ "id" : nnn, "user" : "xxx yyy", "quote" : "zzzzzz" }
```

* Send it both to an FTP server and to a KAFKA topic.

## Fun Use Case #2

![apache-camel-fun-2](etc/apache-camel-fun-2.png)

* Receive messages published to KAFKA by [Fun Use Case #1](#fun-use-case-1).
* Send them to an AWS-S3 bucket if the id is even.
* Send them to a POSTGRES table if the id is odd.

## Fun Use Case #3

![apache-camel-fun-3](etc/apache-camel-fun-3.png)

* Poll POSTGRES table rows inserted by [Fun Use Case #2](#fun-use-case-2).
* Aggregate them in groups of 5.
* Convert each group to a CSV file.
* Send the file to an email address.

## Fun Use Case #4

![apache-camel-fun-4](etc/apache-camel-fun-4.png)

* Poll files uploaded to:
  * an FTP server by [Fun Use Case #1](#fun-use-case-1).
  * an AWS-S3 bucket by [Fun Use Case #2](#fun-use-case-2).
* Enrich them executing a query to a POSTGRES table.
* Convert to a line of text and append to a file in a local directory.

## Let the Fun Begin

### Code!

Make all the test pass!

```bash
./gradlew test --info
./gradlew integrationTest --info
```

### Run!

Start all servers and applications:

```bash
docker-compose up
cd apache-camel-fun-1 ; ./gradlew bootRun
cd apache-camel-fun-2 ; ./gradlew bootRun
cd apache-camel-fun-3 ; ./gradlew bootRun
cd apache-camel-fun-4 ; ./gradlew bootRun
```

... and test manually:

```bash
cd apache-camel-fun-1/input_samples
zip fun0.zip fun0.xml && mv fun0.zip ../input
zip fun1.zip fun1.xml && mv fun1.zip ../input
zip fun2.zip fun2.xml && mv fun2.zip ../input
zip fun5.zip fun5.xml && mv fun5.zip ../input
zip fun10.zip fun10.xml && mv fun10.zip ../input
```
