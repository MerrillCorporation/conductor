package com.netflix.conductor.contribs.queue.rabbitmq;

import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import rx.Observable;

import java.util.List;

public class RabbitMqObservableQueue implements ObservableQueue {
    private String uri;

    public RabbitMqObservableQueue(String uri) {
        this.uri = uri;
    }

    @Override
    public Observable<Message> observe() {
        return null;
    }

    @Override
    public String getType() {
        return "amqp";
    }

    @Override
    public String getName() {
        return "foo";
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public List<String> ack(List<Message> messages) {
        return null;
    }

    @Override
    public void publish(List<Message> messages) {

    }

    @Override
    public void setUnackTimeout(Message message, long unackTimeout) {

    }

    @Override
    public long size() {
        return 0;
    }
}
