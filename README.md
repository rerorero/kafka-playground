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

vi config/connect-standalone.properties
# edit plugin.path to include the jar file
# -#plugin.path=
# +plugin.path=<path to this repo>/connect/target/

./bin/connect-standalone.sh config/connect-standalone.properties <Path to this repo>/connect/config/source.properties
```

# Debezium

```
cd debezium
docker-compose up

cd <path to debezium repo>
mvn install -Passembly -DskipITs -DskipTests
cd debezium-connector-mysql/target/
tar xzvf debezium-connector-mysql-1.5.0.Final-plugin.tar.gz

cd <path to kafka repo>
vi config/connect-standalone.properties
# edit plugin.path to include the jar file
# -#plugin.path=
# +plugin.path=<path to debezium repo>/debezium-connector-mysql/target

CLASSPATH=/Users/rerorero/go/src/github.com/rerorero/debezium/debezium-connector-mysql/target/ ./bin/connect-standalone.sh config/connect-standalone.properties <Path to this repo>/debezium/mysql-connector.properties
```
