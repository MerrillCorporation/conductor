package com.netflix.conductor.contribs.queue.rabbitmq;

import com.netflix.conductor.core.events.queue.Message;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.rabbitmq.client.AMQP.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestRabbitMqObservableQueue {

    RabbitMqObservableQueue observableQueue;
    Channel mockChannel;
    ConnectionFactory mockConnectionFactory;

    @Before
    public void setUp() throws Exception {
        mockChannel = mock(Channel.class);
        mockConnectionFactory = mock(ConnectionFactory.class);
        Connection mockConnection = mock(Connection.class);
        when(mockConnectionFactory.newConnection()).thenReturn(mockConnection);
        when(mockConnection.createChannel()).thenReturn(mockChannel);

        observableQueue = new RabbitMqObservableQueue(
                mockConnectionFactory,
                "amqp://user:pass@rabbitmq.com",
                "exchange",
                "routingKey",
                "zebra");
    }

    @Test
    public void testName() {
        assertEquals("zebra", observableQueue.getName());
    }

    @Test
    public void testType() {
        assertEquals("amqp", observableQueue.getType());
    }

    @Test
    public void testUri() {
        assertEquals("amqp://user:pass@rabbitmq.com", observableQueue.getURI());
    }

    @Test
    public void testAckNoFailures() throws IOException {
        Message message = new Message();
        message.setReceipt("1234567");
        List failures = observableQueue.ack(Collections.singletonList(message));
        assertTrue(failures.isEmpty());
        verify(mockChannel).basicAck(1234567, false);
    }

    @Test
    public void testAckWithFailures() throws IOException {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("id1", "", "1"));
        messages.add(new Message("id2", "", "2"));
        messages.add(new Message("id3", "", "3"));

        doThrow(new IOException("bah"))
                .when(mockChannel).basicAck(1, false);
        doThrow(new IOException("gah"))
                .when(mockChannel).basicAck(3, false);

        List failures = observableQueue.ack(messages);

        verify(mockChannel).basicAck(1, false);
        verify(mockChannel).basicAck(2, false);
        verify(mockChannel).basicAck(3, false);

        assertEquals(2, failures.size());
    }

    @Test
    public void testPublishSuccess() throws Exception {
        List<Message> messages = new ArrayList<>();
        Message message1 = new Message("id1", "body1", "1");
        Message message2 = new Message("id2", "body2", "2");
        messages.add(message1);
        messages.add(message2);

        observableQueue.publish(messages);

        BasicProperties expectedProperties1 = new BasicProperties.Builder()
                .messageId(message1.getId())
                .build();

        BasicProperties expectedProperties2 = new BasicProperties.Builder()
                .messageId(message2.getId())
                .build();

        verify(mockChannel).basicPublish(
                eq("exchange"),
                eq("routingKey"),
                any(BasicProperties.class),
                eq(message1.getPayload().getBytes()));
        verify(mockChannel).basicPublish(
                eq("exchange"),
                eq("routingKey"),
                any(BasicProperties.class),
                eq(message2.getPayload().getBytes()));
    }

    @Test(expected = RuntimeException.class)
    public void testPublishFailure() throws Exception {
        List<Message> messages = new ArrayList<>();
        Message message1 = new Message("id1", "body1", "1");
        Message message2 = new Message("id2", "body2", "2");
        messages.add(message1);
        messages.add(message2);

        observableQueue.publish(messages);

        doThrow(new IOException())
                .when(mockChannel).basicPublish(
                        eq("exchange"),
                        eq("routingKey"),
                        any(BasicProperties.class),
                        eq(message1.getPayload().getBytes()));

        verify(mockChannel).basicPublish(
                eq("exchange"),
                eq("routingKey"),
                any(BasicProperties.class),
                eq(message1.getPayload().getBytes()));
        verify(mockChannel).basicPublish(
                eq("exchange"),
                eq("routingKey"),
                any(BasicProperties.class),
                eq(message2.getPayload().getBytes()));
    }

    @Test
    public void testSize() throws IOException {
        when(mockChannel.messageCount("zebra")).thenReturn(17L);
        assertEquals(17, observableQueue.size());
    }

    @Test
    public void testSizeException() throws IOException {
        when(mockChannel.messageCount("zebra"))
                .thenThrow(new IOException("blargh!"));

        assertEquals(-1, observableQueue.size());
    }

}