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

### Section 8: Real World Project Overview
- [Twitter Producer: Twitter Java Client](https://github.com/twitter/hbc "The Twitter Producer gets data from Twitter based on some keywords and put them in a Kafka topic of your choice")
- [Twitter Producer: Twitter API Credentials](https://developer.twitter.com/ "The Twitter Producer gets data from Twitter based on some keywords and put them in a Kafka topic of your choice")
- [ElasticSearch Consumer: ElasticSearch Java Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.4/java-rest-high.html "The ElasticSearch Consumer gets data from your twitter topic and inserts it into ElasticSearch")
- [ElasticSearch Consumer: ElasticSearch setup #1](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html "The ElasticSearch Consumer gets data from your twitter topic and inserts it into ElasticSearch")
- [ElasticSearch Consumer: ElasticSearch setup #2](https://bonsai.io/ "The ElasticSearch Consumer gets data from your twitter topic and inserts it into ElasticSearch")

### Section 9: Kafka Twitter Producer & Advanced Configurations
- Producer and Advanced Configurations Overview
- Twitter Setup
- Producer Part 1 - Writing Twitter Client
- Producer Part 2 - Writing the Kafka Producer
```
kafka-topics --bootstrap-server localhost:9092 --create --topic twitter_tweets --partitions 6 --replication-factor 1
kafka-console-consumer --bootstrap-server localhost:9092 --topic twitter_tweets
```
- Producer Configurations Introduction
- acks & min.insync.replicas
  - acks = 0 (no acks)
    - No response is requested
    - If a broker goes offline, or an exception happens, we won't know and will lose data
    - Useful for data where it's okay to potentially lose messages
      - Metrics collection
      - Log collection
  - acks = 1 (leader acks) {Default}
    - Leader response is requested, but replication is not a guarantee (happens in the background)
    - If an ack is not received, the producer may retry
    - If the leader broker goes offline, but the replicas haven't replicated the data yet, the data is lost
  - acks = all (replicas acks)
    - Leader + replicas ack requested
    - Added both latency and safety
    - No data loss (if enough replicas)
    - Necessary setting if you don't want to lose data
    - Must be used in conjuction with "min.insync.replicas" (set at the broker or topic (override) level)
  - min.insync.replicas = 2 (implies that at least 2 ISR brokers (including the leader) must respond)
  - This means that using the following setup will only tolerate loss of 1 broker, with more the producer will receive an exception on send
    - replication.factor=3
    - min.insync.replicas=2
    - acks=all


# Github setup
```
git remote add origin https://github.com/johnwr-response/aks-kafka-beginners-course.git
```
