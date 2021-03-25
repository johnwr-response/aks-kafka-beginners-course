package no.responseweb.simplesteph.kafka.tutorial1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

        String bootstrapServers = "localhost:9092";

        // Create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 10; i++) {

            // Create a Producer Record
            ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", "Hello World! " + Integer.toString(i));

            // Send data
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // execute every time a record is successfully sent or an exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        logger.info(
                                "Received new metadata: \n" +
                                        "Topic: {} \n" +
                                        "Partition: {} \n" +
                                        "Offset: {} \n" +
                                        "Timestamp: {} \n"
                                , recordMetadata.topic()
                                , recordMetadata.partition()
                                , recordMetadata.offset()
                                , recordMetadata.timestamp()
                        );

                    } else {
                        // an exception is thrown
                        logger.error("Error while producing: ", e);
                    }
                }
            });

        }

        // Flush data
        producer.flush();
        producer.close();

    }
}
