# Local Kafka and Kafka UI Setup with Docker

In total, you will create three Docker containers:
- `zookeeper`
- `kafka`: runs Kafka with a topic called `quickstart`
- `kafka-ui`: runs [Kafka UI](https://github.com/provectus/kafka-ui)

## 1. Create Docker containers for Kafka

You'll first need to create a Docker network so that the Kafka and Kafka UI containers can communicate:
```zsh
docker network create kafka-net
```

Next, run a Zookeeper instance (as Kafka depends on it for cluster management):
```zsh
docker run -d --name zookeeper --network kafka-net zookeeper:latest
```

Now, start the Kafka broker:
```zsh
docker run -d \
  --name kafka \
  --network kafka-net \
  -p 9092:9092 \
  -p 9093:9093 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT_INTERNAL:PLAINTEXT,PLAINTEXT_EXTERNAL:PLAINTEXT \
  -e KAFKA_LISTENERS=PLAINTEXT_INTERNAL://kafka:9093,PLAINTEXT_EXTERNAL://0.0.0.0:9092 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT_INTERNAL://kafka:9093,PLAINTEXT_EXTERNAL://localhost:9092 \
  -e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT_INTERNAL \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:latest
```

In this command:
- `--network kafka-net` attaches the container to the kafka-net network.
- `-p 9092:9092`, `-p 9093:9093` exposes ports 9092 and 9093 to the host.
- `-e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181` tells Kafka the address of its Zookeeper.
- `-e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP` sets security protocols for listeners.
- `-e KAFKA_LISTENERS` defines Kafka's internal and external listeners.
- `-e KAFKA_ADVERTISED_LISTENERS` defines how Kafka broker announces itself to the world.
- `-e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT_INTERNAL` sets the listener for inter-broker communication.
- `-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1` is necessary for a single Kafka broker setup.

## 2. Create a Kafka topic

First, access the Kafka container:
```zsh
docker exec -it kafka bash
```

Inside the container, create a topic:
```zsh
kafka-topics --create --topic quickstart --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
```

## 3. Create Docker container running Kafka UI

Now, you will set up Kafka UI in another Docker container. The key is to ensure this container can access the Kafka broker.
```zsh
docker run -d \
  --name kafka-ui \
  --network kafka-net \
  -p 8080:8080 \
  -e KAFKA_CLUSTERS_0_NAME=local \
  -e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka:9093 \
  provectuslabs/kafka-ui:latest
```

In this command:
- `--network kafka-net` ensures that the Kafka UI is in the same network as Kafka.
- `-p 8080:8080` exposes ports 9092 and 9093 to the host.
- `-e KAFKA_CLUSTERS_0_NAME` sets a name for your Kafka cluster in the UI.
- `-e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS` specifies the Kafka broker URL within the Docker network.

## Accessing Kafka UI & Kafka

- Now, Kafka UI should be running and accessible at [http://localhost:8080](http://localhost:8080)
- Kafka UI and other internal Docker services should connect to `kafka:9093`
- External connections (like those from your local machine) should use `localhost:9092`
