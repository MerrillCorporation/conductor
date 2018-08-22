package com.netflix.conductor.core.events.amqp;

import com.netflix.conductor.contribs.queue.rabbitmq.RabbitMqObservableQueue;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestRabbitMqEventQueueProvider {

    @Test
    public void returnsRabbitMQEventQueue() throws Exception {
        ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
        Connection mockConnection = mock(Connection.class);
        Channel mockChannel = mock(Channel.class);

        when(mockConnectionFactory.newConnection()).thenReturn(mockConnection);
        when(mockConnection.createChannel()).thenReturn(mockChannel);

        RabbitMqEventQueueProvider provider = new RabbitMqEventQueueProvider(mockConnectionFactory, "exchange", "#", "queue");
        ObservableQueue observableQueue = provider.getQueue("");
        assertNotNull(observableQueue);
        assertTrue(observableQueue instanceof RabbitMqObservableQueue);
    }
}