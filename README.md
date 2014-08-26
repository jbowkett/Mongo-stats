Mongo-stats
===========

Introduction
============

Coding assignment submission by James Bowkett - `james@bowkett.info`.

The three tasks are written in Java 8 and utilise the Gradle 2.0 build system.  
Gradle can be installed by following the instructions [here](http://www.gradle.org/installation). 

The source can be compiled by executing the enclosed gradle build script by 
running from the project root directory:
`gradle build`

This will create an executable jar (aka a fat jar, i.e. includes all 
dependencies) jar in `build/libs/Mongo-stats-1.0.jar`

For convenience, a pre-built Mongo-stats-1.0.jar is supplied in `build/libs`

Please note, the main code is held in `java/src/` and the accompanying test code 
in `java/test`.

The main program entry point is in `java/src/info/bowkett/mongostats/tasks/Main.java`, 
the sections below outline how to navigate to the entry point for each separate task.

When using the gradle tasks, the default database used is adthena hosted on 
localhost, listening on port 27017.  

The program uses a collection named regions.

The junit tests may be run by executing:
 `gradle test`
 
All tasks may be run in order
`java  -jar build/libs/Mongo-stats-1.0.jar -h <mongo host> -pt <port> -d <mongo database> -t ALL`
or
`gradle build allTasks`


Task One
========

This implementation uses Java 8 Streams to read the file in (the input data was 
turned it into comma-delimited for parsing convenience).  This task will read 
the data in from `input-data/data.txt` (this could obviously be parameterised, 
I chose not to for the sake of brevity)

Task 1 can be run as follows:

`java  -jar build/libs/Mongo-stats-1.0.jar -h <mongo host> -pt <port>> -d <mongo database> -t 1`
or
`gradle taskOne`

The main algorithm can be followed in `Task1.demonstrate()`.

The resulting document schema is as follows:

> db.regions.findOne();
{
	"_id" : ObjectId("53fa3dac03649aab50a4718c"),
	"country" : "Scotland",
	"populations" : [
		{
			"year" : 2008,
			"population" : 5062011,
			"growth" : 0
		}...
  ],
  "region" : "Edinburgh"
}

The population growth is calculated prior to inserting the document into Mongo, 
as this statistic helps in determining the output for the following tasks. 


Task Two
========

The implementation of this can be inspected in `Task2.demonstrate()`.  The code 
uses the Mongo aggregation framework to select the year 2012 for each 
area, then sorts by the amount of population growth and then selects the top 2 
regions.

Task 2 can be run as follows:

java  -jar build/libs/Mongo-stats-1.0.jar -h <mongo host> -pt <port>> -d <mongo database> -t 2
or
`gradle taskTwo`


Task Three
==========

The implementation for this part of the assignment can be viewed in 
`Task3.demonstrate()`.

First the (arithmetic) mean rate of population growth is calculated and 
persisted for each region (2008 is removed during this average calculation 
process, as there are no growth statistics for 2008 as it is the first year we 
have data for - taking an average with a zero statistic present in the set, 
would skew the average figure).
  
The average is pre-calculated as it requires an aggregation over all years for 
the region.  This could not be completed in one mongo operation in the 
aggregation framework, as once this aggregation is complete to compute the 
average, the individual year statistics would already have been aggregated 
together and so would not be available for comparison to the mean.  Hence it is 
persisted back to the region document.

Next the Mongo aggregation framework is used to list for each region, for each 
year how much the population growth for that year differs from the mean for the 
region.  Next, the absolute deviation value is extracted. 

The results are then scrolled within the Java DAO code as this cannot be extracted 
from within the aggregation framework as the top 2 for each region are required, 
and mongo limits work on the entire dataset, they would not limit selectively 
for each region.

Task 3 can be run as follows:

java  -jar build/libs/Mongo-stats-1.0.jar -h <mongo host> -pt <port>> -d <mongo database> -t 3
or
`gradle taskThree`


Suggested improvements
======================
I have not used a logging framework to keep the dependencies down, although one 
would use one in production code (either log4j or slf4j with log4j backing it).

Note, there are a number of points where the data points are loaded into memory, 
and then scrolled, if this became a performance bottleneck, then this could be 
rectified by perhaps only persisting the results into mongo when conducting the 
aggregation and reading them back as required.


Output from running all tasks
=============================

running `gradle alltasks` gives the following output:

Connecting to : mongodb://localhost:27017/adthena
Loading file...
File parsed.  Loading into DB...
All documents loaded into DB.  Task 1 complete.

Top 2 regions in order of greatest growth for 2012:
1 - London, England
2 - Edinburgh, Scotland
Task 2 complete.

4 documents updated with average population growth
In 2012, in London, England the population grew by 1000 which is an absolute deviation of 290.0 from the arithmetic mean population for the region for all years of 710.0
In 2009, in London, England the population grew by 600 which is an absolute deviation of 110.0 from the arithmetic mean population for the region for all years of 710.0
In 2009, in Belfast, Northern Ireland the population grew by 20 which is an absolute deviation of 45.5 from the arithmetic mean population for the region for all years of 65.5
In 2012, in Belfast, Northern Ireland the population grew by 102 which is an absolute deviation of 36.5 from the arithmetic mean population for the region for all years of 65.5
In 2012, in Edinburgh, Scotland the population grew by 300 which is an absolute deviation of 68.25 from the arithmetic mean population for the region for all years of 231.75
In 2009, in Edinburgh, Scotland the population grew by 200 which is an absolute deviation of 31.75 from the arithmetic mean population for the region for all years of 231.75
In 2012, in Cardiff, Wales the population grew by -200 which is an absolute deviation of 200.0 from the arithmetic mean population for the region for all years of 0.0
In 2011, in Cardiff, Wales the population grew by 100 which is an absolute deviation of 100.0 from the arithmetic mean population for the region for all years of 0.0
Task 3 complete.

It also leaves the database with an adthena database containing a regions 
collection.