package com.netflix.conductor.core.events.amqp;

import com.netflix.conductor.contribs.queue.rabbitmq.RabbitMqObservableQueue;
import com.netflix.conductor.core.events.EventQueueProvider;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqEventQueueProvider implements EventQueueProvider {


    private ConnectionFactory connectionFactory;
    private String queueName;

    public RabbitMqEventQueueProvider(ConnectionFactory connectionFactory, String queueName) {
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
    }

    @Override
    public ObservableQueue getQueue(String queueURI) {
        return new RabbitMqObservableQueue(connectionFactory, queueURI, queueName);
    }
}
