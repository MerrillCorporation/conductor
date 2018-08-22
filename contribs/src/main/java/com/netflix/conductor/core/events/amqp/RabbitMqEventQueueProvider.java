package com.netflix.conductor.core.events.amqp;

import com.netflix.conductor.contribs.queue.rabbitmq.RabbitMqObservableQueue;
import com.netflix.conductor.core.events.EventQueueProvider;
import com.netflix.conductor.core.events.queue.ObservableQueue;

public class RabbitMqEventQueueProvider implements EventQueueProvider {
    @Override
    public ObservableQueue getQueue(String queueURI) {
        return new RabbitMqObservableQueue(queueURI);
    }
}
