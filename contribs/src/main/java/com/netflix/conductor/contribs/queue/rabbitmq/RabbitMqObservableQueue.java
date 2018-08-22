package com.netflix.conductor.contribs.queue.rabbitmq;

import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import rx.Observable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RabbitMqObservableQueue implements ObservableQueue {
    private String uri;
    private String queueName;
    private Channel channel;

    public RabbitMqObservableQueue(ConnectionFactory connectionFactory, String uri, String queueName) {
        try {
            connectionFactory.setUri(uri);
            Connection connection = connectionFactory.newConnection();
            this.channel = connection.createChannel();
        } catch (URISyntaxException | GeneralSecurityException | IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        this.uri = uri;
        this.queueName = queueName;
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
        return queueName;
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public List<String> ack(List<Message> messages) {
        List<String> failedAckIds = new ArrayList<>();
        messages.forEach(message -> {
            try {
                Long deliveryTag = Long.valueOf(message.getReceipt());
                channel.basicAck(deliveryTag, false);
            } catch (IOException e) {
                failedAckIds.add(message.getReceipt());
            }
        });
        return failedAckIds;
    }

    @Override
    public void publish(List<Message> messages) {

    }

    @Override
    public void setUnackTimeout(Message message, long unackTimeout) {

    }

    @Override
    public long size() {
        try {
            return channel.messageCount(queueName);
        } catch (Exception e) {
            return -1;
        }
    }
}
