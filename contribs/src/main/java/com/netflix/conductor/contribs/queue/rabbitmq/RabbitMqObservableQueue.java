/**
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.contribs.queue.rabbitmq;

import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import rx.Observable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RabbitMqObservableQueue implements ObservableQueue {
    private String uri;
    private String queueName;
    private String exchangeName;
    private String routingKey;
    private Channel channel;

    public RabbitMqObservableQueue(ConnectionFactory connectionFactory,
                                   String uri,
                                   String exchangeName,
                                   String routingKey,
                                   String queueName) {
        try {
            connectionFactory.setUri(uri);
            Connection connection = connectionFactory.newConnection();
            this.channel = connection.createChannel();
        } catch (URISyntaxException | GeneralSecurityException | IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        this.uri = uri;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
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
        for (Message message : messages) {
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .messageId(message.getId())
                    .build();
            try {
                channel.basicPublish(this.exchangeName, this.routingKey, properties, message.getPayload().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
