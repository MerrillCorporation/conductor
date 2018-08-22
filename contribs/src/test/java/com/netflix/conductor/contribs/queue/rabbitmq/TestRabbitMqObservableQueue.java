package com.netflix.conductor.contribs.queue.rabbitmq;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestRabbitMqObservableQueue {

    @Test
    public void test() {
        String uri = "amqp://user:pass@rabbitmq.com";
        RabbitMqObservableQueue observableQueue = new RabbitMqObservableQueue(uri);
        assertEquals("foo", observableQueue.getName());
        assertEquals("amqp", observableQueue.getType());
        assertEquals(uri, observableQueue.getURI());
    }
}