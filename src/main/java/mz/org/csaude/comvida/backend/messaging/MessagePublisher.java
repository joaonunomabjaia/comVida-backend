package mz.org.csaude.comvida.backend.messaging;

public interface MessagePublisher {
    void publish(String topicOrQueue, String message);
}
