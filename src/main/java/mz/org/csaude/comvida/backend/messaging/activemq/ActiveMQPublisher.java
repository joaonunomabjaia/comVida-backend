package mz.org.csaude.comvida.backend.messaging.activemq;

import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.messaging.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ActiveMQPublisher implements MessagePublisher {

    private static final Logger LOG = LoggerFactory.getLogger(ActiveMQPublisher.class);

    @Override
    public void publish(String queue, String message) {
        LOG.info("[ACTIVEMQ] Sending to queue '{}': {}", queue, message);
        // Implementação real do ActiveMQ vai aqui
    }
}
