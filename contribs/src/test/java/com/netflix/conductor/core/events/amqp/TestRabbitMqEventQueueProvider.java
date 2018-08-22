package com.netflix.conductor.core.events.amqp;

import com.netflix.conductor.contribs.queue.rabbitmq.RabbitMqObservableQueue;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestRabbitMqEventQueueProvider {

    @Test
    public void returnsRabbitMQEventQueue() {
        RabbitMqEventQueueProvider provider = new RabbitMqEventQueueProvider();
        ObservableQueue observableQueue = provider.getQueue("");
        assertNotNull(observableQueue);
        assertTrue(observableQueue instanceof RabbitMqObservableQueue);
    }
}