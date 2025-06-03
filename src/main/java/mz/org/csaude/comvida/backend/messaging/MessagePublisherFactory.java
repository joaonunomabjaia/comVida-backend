package mz.org.csaude.comvida.backend.messaging;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.entity.Setting;
import mz.org.csaude.comvida.backend.messaging.activemq.ActiveMQPublisher;
import mz.org.csaude.comvida.backend.messaging.kafka.KafkaPublisher;
import mz.org.csaude.comvida.backend.repository.SettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MessagePublisherFactory {

    private static final Logger LOG = LoggerFactory.getLogger(MessagePublisherFactory.class);

    private final KafkaPublisher kafkaPublisher;
    private final ActiveMQPublisher activeMQPublisher;
    private final SettingRepository settingRepository;

    @Inject
    public MessagePublisherFactory(KafkaPublisher kafkaPublisher,
                                   ActiveMQPublisher activeMQPublisher,
                                   SettingRepository settingRepository) {
        this.kafkaPublisher = kafkaPublisher;
        this.activeMQPublisher = activeMQPublisher;
        this.settingRepository = settingRepository;
    }

    public MessagePublisher getPublisher() {
        Setting setting = settingRepository.findByDesignation(Setting.MESSAGING_BROKER)
                .orElseThrow(() -> new IllegalStateException("Messaging broker setting not found"));

        String broker = setting.getValue().trim().toUpperCase();
        LOG.info("Messaging broker selected: {}", broker);

        return switch (broker) {
            case "KAFKA" -> kafkaPublisher;
            case "ACTIVEMQ" -> activeMQPublisher;
            default -> throw new IllegalStateException("Unsupported broker type: " + broker);
        };
    }
}
