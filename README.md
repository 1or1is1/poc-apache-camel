
# Poc for Apache Camel

This is a poc based on apache camel, to read messages from a kafka topic and aggreate it based on the `id` present in kafka header and then finally saving the data into a file.


## Run Locally

Clone the project

```bash
  git clone https://github.com/1or1is1/poc-apache-camel.git
```

Go to the project directory

```bash
  cd poc-apache-camel
```

### Steps to run Kafka Locally
- There is a `docker-compose.yml` file present in the directory.
- Open Terminal into the same directory, where docker-compose file is present.
- Run command `docker-compose up -d`
- It will start kafka with 1 broker, and a kafka-ui to see the details in a GUI form.
- Navigate to `http://localhost:8080` to access the Kafka GUI
- On need basis please create a topic with name `poc-topic`

### After Local Kafka Setup is completed
Once the kafka is up and running, we are ready to start our application, just run the application as any Spring Boot application.

- There is a mock producer, which will automatically start to push data to kafka topic `poc-topic`
- Apache camel route will start listening to the events happening on `poc-topic`, and start aggregating the messages.
- Once the message count reaches a threshold it will save the data into a file in the directory `output/<filename>.txt`
