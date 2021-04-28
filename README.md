# Run Kafka

```
git clone git@github.com:apache/kafka.git
cd kafka
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

# create a new topic
./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --partitions 3 --replication-factor 1 --topic tes
```

# Go client

To run producer

```
cd client
BROKERS=127.0.0.1:29092 TOPIC=test  go run cmd/producer/main.go
```

To run consumer

```
BROKERS=localhost:9092 TOPICS=test go run cmd/consumer/main.go
```

# Connect

```
cd connect
sbt assembly

# back to kafka directory
cd <path to kafka repo>

CLASSPASS=<Path to this repo>/connect/target/scala-2.13/kafka-connect-example.jar ./bin/connect-standalone.sh config/connect-standalone.properties <Path to this repo>/connect/config/source.properties
```