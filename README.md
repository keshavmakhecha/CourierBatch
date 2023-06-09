# Getting Started

Courier is batch application which will process the orders according to country & will persist the order in h2 db.


Tables:

1) CAMEROON

2) ETHIOPIA

3) MOROCOO

4) MOZAMBIQUE

5) UGANDA

Reading:

To process bulk orders we have been processing the records by partioning the record limit considering the file size. CsvResourcePartitioner is the class which does partitioning.
By default the data will be read from data.csv file

FaultTolerance in Reading:

Invalid record which is not parsed will be skipped upto 5 records.
FileVerificationSkipper is the class which handles failure.

Processing:

Further the data will be processed in CourierItemProcessor class & country will be tagged to each record by identifying the country code in phonenumber. If no country is tagged, then it will result to job failure after skipping 8 records.


FaultTolerance in Processing:

UnIdentified country code for record will result in failure & it skips upto 8 records.
RecordProcessingSkipper is the class which handles failure.

Writing:

Data will be written to country table as per the country record while will be identified by ClassifierWriter.

Courier is a spring boot application which can be run by creating jar or running the CourierApplication class file.

Note: 

 1) Retry & Restart Policy is not implemented yet, which will be added in coming release.
 
 2) Scheduler is not implemented yet.
 
 3) Lombok is not used because of technical issue.

