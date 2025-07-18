package gift.common.config;

import gift.common.event.EventPublisher;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {

    private final EventPublisher eventPublisher;

    public EventConfig(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
