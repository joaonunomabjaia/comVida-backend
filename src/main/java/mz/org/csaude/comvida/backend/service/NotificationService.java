package mz.org.csaude.comvida.backend.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.messaging.MessagePublisherFactory;

@Singleton
public class NotificationService {

    @Inject
    private MessagePublisherFactory publisherFactory;

    public void notifyExternalSystems(String eventJson) {
        publisherFactory.getPublisher().publish("comvida-events", eventJson);
    }
}
