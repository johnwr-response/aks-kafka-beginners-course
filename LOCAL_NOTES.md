# Log

### Section 18: Annex - Starting Kafka Differently
- 117. Start Kafka Development environment using Docker
```
docker-compose -f zk-single-kafka-single.yml up
docker-compose -f zk-single-kafka-single.yml up -d
docker-compose -f zk-single-kafka-single.yml down
docker-compose -f zk-single-kafka-single.yml down -v

docker-compose -f zk-single-kafka-single.yml up kafka1 -d
docker-compose -f zk-single-kafka-single.yml up kibana -d
docker-compose -f zk-single-kafka-single.yml down kafka1
docker-compose -f zk-single-kafka-single.yml down kibana

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
  - `acks` = 0 (no acks)
    - No response is requested
    - If a broker goes offline, or an exception happens, we won't know and will lose data
    - Useful for data where it's okay to potentially lose messages
      - Metrics collection
      - Log collection
  - `acks` = 1 (leader acks) {Default}
    - Leader response is requested, but replication is not a guarantee (happens in the background)
    - If an ack is not received, the producer may retry
    - If the leader broker goes offline, but the replicas haven't replicated the data yet, the data is lost
  - `acks` = all (replicas acks)
    - Leader + replicas ack requested
    - Added both latency and safety
    - No data loss (if enough replicas)
    - Necessary setting if you don't want to lose data
    - Must be used in conjuction with `min.insync.replicas` (set at the broker or topic (override) level)
  - `min.insync.replicas` = 2 (implies that at least 2 ISR brokers (including the leader) must respond)
  - This means that using the following setup will only tolerate loss of 1 broker, with more the producer will receive an exception on send
    - `replication.factor`=3
    - `min.insync.replicas`=2
    - `acks`=all
- retries, delivery.timeout.ms & max.in.flight.requests.per.connection
  - retries
    - In case of transient failures like NotEnoughReplicasException, developers are expected to handle the exceptions for data not to be lost
    - For Kafka >= 2.1 the default number of retries are 2147483647, earlier versions has no retries by default
      - `retry.backoff.ms` configures the time between retries. Default is 100 ms
      - `delivery.timeout.ms` configures a timeout for retries. Default is 120000 ms
        - records will be failed if not acknowledged within this timeout. This must be handled by the producer for data not to be lost
    - Warning: retries can be sent out of order. With key-based ordering this can be an issue.
      - for this the setting `max.in.flight.requests.per.connection` can control number of parallel produce requests. 
        - Set this to 1 to ensure strict ordering (may impact throughput). The default is 5.
  - NB! Note, all this should instead be solved by using idempotent producers.
- Idempotent Producer
  - Kafka detects duplicated produce.
  - Enable by setting this property which will set the following properties
    - `enable.idempotence` = true
      - `retries` = Integer.MAX
      - `max.in.flight.requests` = 5
      - `acks` = all
- Producer Part 3 - Safe Producer
- Producer Compression
  - Enabled on the producer level. No configuration required for neither brokers nor consumers
  - `compression.type` = [none,gzip,lz4,snappy]
    - gzip = slowest, but highest compression ratio
  - ALWAYS use compression in production!
  - For high throughput producers, consider tweaking the batching
- Producer Batching
  - Kafka tries to send records as soon as possible in parallel by up to 5
  - If more messages is to be sent while others are in flight, Kafka will start batching them to send them all at once.  
  - This mechanism is controlled by two properties:
    - `linger.ms` | Number of ms a producer is willing to wait before sending a batch out. Default is 0.
      - By introducing some lag, i.e. 5 ms, the chances of messages being sent in batches increases
        - this small delay can significantly increase throughput, compression and efficiency of producer
      - If `batch.size` is reached, the batch will be sent regardless
    - `batch.size` | Maximum number of bytes to be included in a batch. Default is 16 KB.
      - Increasing the batch size to 32 KB or 64 KB can significantly increase compression, throughput and efficiency. 
      - Any message bigger than the batch size will not be batched
      - A batch is allocated pr. partition, make sure not to set it too high to avoid waste of memory.
      - The average batch size metrics can be monitored using Kafka Producer Metrics.
- Producer Part 4 - High Throughput Producer
- Producer Default Partitions and Key Hashing
  - keys are hashed using the murmur2 algorithm
- [Advanced] `max.block.ms` and `buffer.memory`
  If the producer is producing faster than the broker can receive, the records will be buffered in memory
  - `buffer.memory` The size of the sender buffer. Default is 32 MB
    - If this buffer is full, the .send() method will block. (Not return)
  - `max.block.ms` The time in ms the .send() method will block before throwing an exception. Default 60000.
- Refactoring the Project

### Section 10: Kafka ElasticSearch Consumer & Advanced Configurations
- Consumer and Advanced Configuration Overview
- Setting up ElasticSearch in the Cloud
- ElasticSearch 101
  - [About elasticSearch cluster](http://localhost:9200 "GET localhost:9200")
  - [elasticSearch cluster health info](http://localhost:9200/_cat/health?v "GET localhost:9200/_cat/health?v")
  - [elasticSearch cluster nodes info](http://localhost:9200/_cat/nodes?v "GET localhost:9200/_cat/nodes?v")
  - [elasticSearch indices status](http://localhost:9200/_cat/indices?v "GET localhost:9200/_cat/indices?v")
  - [Create an index for twitter](http://localhost:9200/twitter "PUT /twitter")
  - [Add a document to elasticSearch](http://localhost:9200/twitter/tweets/1 "PUT /twitter/tweets/1") Payload some json
  - [Get a document from elasticSearch](http://localhost:9200/twitter/tweets/1 "GET /twitter/tweets/1")
  - [Delete a document from elasticSearch](http://localhost:9200/twitter/tweets/1 "DELETE /twitter/tweets/1")
  - [Delete an index from elasticSearch](http://localhost:9200/twitter "DELETE /twitter")
- Consumer Part 1 - Setup Project
- Consumer Part 2 - Write the Consumer & Send to ElasticSearch
- Delivery Semantics for Consumers
  - At most once: Offsets are committed as soon as the batch is received. Might lose messages from error to end of batch
  - At least once (default): Offsets are committed after the message is committed. Can result in duplicates if processing is not idempotent
  - Exactly once: Can be achieved for Kafka => Kafka workflows using Kafka Streams API
- Consumer Part 3 - Idempotence
- Consumer Poll Behaviour
  - Kafka consumers have  a "poll" model. Other messaging systems typically has a "push" model 
    - This allows consumers control over where and how fast in the topic to consume, this also gives the ability to replay events
    - `fetch.min.bytes` (default 1) Controls how much data to pull at least on each request
      - Helps to improve throughput and decrease number of requests at the cost of latency
    - `max.poll.records` (default 500) Controls how many records to receive per poll request
      - Increase if your messages are very small, and you have a lot of RAM
    - `max.partitions.fetch.bytes` (default 1 MB) Maximum data returned by the broker per partition
      - If you read from a 100 partitions, you'll need a lot of RAM
    - `fetch.max.bytes` (default 50 MB) Maximum data returned for each fetch request (multiple partitions)
      - The consumer performs multiple fetches in parallel
- Consumer Offset Commit Strategies
  - (easy) `enable.auto.commit` = *true* & synchronous processing of batches
  - (medium) `enable.auto.commit` = *false* & manual commit of offsets
- Consumer Part 4 - Manual Commit of Offsets
- Consumer Part 5 - Performance Improvement using Batching
- Consumer Offsets Reset Behaviour
  - A consumer is expected to read logs continuously
  - `auto.offset.reset` Behaviour for consumer
    - `latest` will read from the end of the log
    - `earliest` will read from start of the log
    - `none` will throw exception if no offset is found
  - Consumer offsets can be lost if consumer hasn't read data for 7 days
    - `offset.retention.minutes` controls this
  - To replay data for  a consumer group:
    - Take down all consumers in group
    - use `kafka-consumer-groups` command to offset to what you want
    - restart consumers
- Consumer Part 6 - Replaying Data
```
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group kafka-demo-elasticsearch
kafka-consumer-groups --bootstrap-server localhost:9092 --group kafka-demo-elasticsearch --reset-offsets --execute --to-earliest --topic twitter_tweets
```
- Consumer Internal Threads
  - `session.timeout.ms` (Default 10 seconds) If no heartbeat is sent during that period the consumer is considered dead 
    - Set this value lower to enable faster consumer re-balancing
  - `heartbeat.interval.ms` (Default 3 seconds) How often heartbeats are to be sent
    - Generally this should be set to 1/3 of `session.timeout.ms`
  - `max.poll.interval.ms` (Default 5 minutes) Maximum time between two polls before consumer is declared dead

# Github setup
```
git remote add origin https://github.com/johnwr-response/aks-kafka-beginners-course.git
```
