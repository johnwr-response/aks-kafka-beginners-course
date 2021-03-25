# Log

### Section 18: Annex - Starting Kafka Differently
- 117. Start Kafka Development environment using Docker
```
docker-compose -f zk-single-kafka-single.yml up
docker-compose -f zk-single-kafka-single.yml up -d
docker-compose -f zk-single-kafka-single.yml down
```

### Section 6: CLI (Command Line Interface) 101
```
kafka-topics --bootstrap-server localhost:9092 --list
kafka-topics --bootstrap-server localhost:9092 --topic first_topic --create --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server localhost:9092 --topic first_topic --describe
# DO-NOT-RUN-DELETE-ON-WINDOWS # kafka-topics --bootstrap-server localhost:9092 --topic second_topic --create --partitions 3 --replication-factor 1
# DO-NOT-RUN-DELETE-ON-WINDOWS # kafka-topics --bootstrap-server localhost:9092 --topic second_topic --delete

kafka-console-producer --broker-list localhost:9092 --topic first_topic
kafka-console-producer --broker-list localhost:9092 --topic first_topic --producer-property acks=all
kafka-console-producer --broker-list localhost:9092 --topic new_topic
kafka-topics --bootstrap-server localhost:9092 --topic new_topic --describe

kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --from-beginning

kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group my-first-application
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group my-second-application --from-beginning # from-beginning not applicable when using group

kafka-consumer-groups --bootstrap-server localhost:9092 --list
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my-first-application

kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-application --reset-offsets --to-earliest --execute --topic first_topic
kafka-consumer-groups --bootstrap-server localhost:9092 --group my-first-application --reset-offsets --shift-by -2 --execute --topic first_topic

kafka-console-producer --broker-list localhost:9092 --topic first_topic --property parse.key=true --property key.separator=,
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --from-beginning --property print.key=true --property key.separator=

```

### Section 7: Kafka Java Programming 101
- Creating Kafka Project
- Java Producer
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group my-third-application
```
- Java Producer Callbacks
- Java Producer with Keys
- Java Consumer
- Java Consumer inside Consumer Group
```
Tip: To allow parallell run in IntelliJ, enable "Allow multiple instances" it in run configuration 
```
- Java Consumer with Threads
- Java Consumer Seek and Assign
```
Tip: Assign and seek are mostly used to replay data or fetch a specific message
```
- Client Bi-Directional Compatibility
  - [Upgrading Apache Kafka Clients Just Got Easier](https://www.confluent.io/blog/upgrading-apache-kafka-clients-just-got-easier "Link to Confluent Homepage")
- Configuring Producers and Consumers
  - [Configure producers](https://kafka.apache.org/documentation/#producerconfigs "Link to Kafka Documentation")
  - [configure consumers](https://kafka.apache.org/documentation/#consumerconfigs "Link to Kafka Documentation")


# Github setup
```
git remote add origin https://github.com/johnwr-response/aks-kafka-beginners-course.git
```
