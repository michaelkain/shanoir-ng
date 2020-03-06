package org.shanoir.ng.shared.event;

import org.shanoir.ng.shared.configuration.RabbitMQConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Service to send every event created.
 * @author fli
 *
 */
@Service
public class ShanoirEventService {

	@Autowired
	RabbitTemplate rabbitTemplate;

	private static final Logger LOG = LoggerFactory.getLogger(ShanoirEventService.class);

	/**
	 * Publishes an event to user microservice.
	 * @param event
	 */
	public void publishEvent(ShanoirEvent event) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		StringBuilder builder = new StringBuilder("Event:\n")
			.append("Id: ").append(event.getId()).append("\n")
			.append("User ID: ").append(event.getUserId()).append("\n")
			.append("EventType: ").append(event.getEventType()).append("\n")
			.append("Object ID: ").append(event.getObjectId()).append("\n")
			.append("Message: ").append(event.getMessage()).append("\n")
			.append("Status: ").append(event.getStatus()).append("\n")
			.append("Progress: ").append(event.getProgress()).append("\n");
		LOG.info(builder.toString());
		try {
			String str = mapper.writeValueAsString(event);
			rabbitTemplate.convertAndSend(RabbitMQConfiguration.EVENTS_EXCHANGE, event.getEventType(), str);
		} catch (JsonProcessingException e) {
			LOG.error("Error while sending event: event {}, user: {}, reference: {}", event.getEventType(), event.getUserId(), event.getObjectId());
			LOG.error("Thrown exception: {}", e);
		}
	}
}
