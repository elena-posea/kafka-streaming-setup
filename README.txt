Setup

* Install Zookeeper first (I downloaded v3.3.6) zookeeper-3.3.6.tar.gz
* Extract Zookeeper and run this command in powershell/cmd \zookeeper-3.3.6\bin> .\zkServer.cmd Now this should up a Zookeeper instance on localhost:2181
* Download Kafka binary version (I downloaded v0.10.0.1)kafka_2.10-0.10.0.1.tgz
* Extract Kafka, time to modify some configs
* Inside Kafka extraction you can find .\config\server.properties
* In .\config\server.properties replace log.dirs=c:/kafka/kafka-logs

Note: Make sure to create those folders in relevant paths
Happy news: Now Kafka ships with windows .bat scripts, You can find these files inside ./bin/windows folder
Start powershell/cmd and run this command to start Kafka broker .\bin\windows\kafka-server-start.bat ..\config\server.properties

==== How to run
ZK: zookeeper-3.3.6\bin> .\zkServer.cmd
Kafka: 
\windows\kafka-server-start.bat ..\..\config\server.properties


======== Kafka scripts (windows)
https://www.cloudera.com/documentation/kafka/latest/topics/kafka_command_line.html
== create new topic
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 5 --topic elena
== list topics
C:\Users\Elena\Documents\kafka_2.11-1.1.0\kafka_2.11-1.1.0\bin\windows>kafka-topics.bat --list --zookeeper localhost
== produce a message
kafka-console-producer.bat --broker-list localhost:9092 --topic elena
== delete topic
kafka-topics.bat --zookeeper localhost:2181 --delete --topic elena