package mz.org.csaude.comvida.backend.messaging.kafka;

import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.messaging.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class KafkaPublisher implements MessagePublisher {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaPublisher.class);

    @Override
    public void publish(String topic, String message) {
        LOG.info("[KAFKA] Sending to topic '{}': {}", topic, message);
        // Implementação real de Kafka vai aqui
    }
}
