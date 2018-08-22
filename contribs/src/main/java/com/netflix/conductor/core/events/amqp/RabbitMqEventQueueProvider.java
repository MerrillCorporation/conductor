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
package com.netflix.conductor.core.events.amqp;

import com.netflix.conductor.contribs.queue.rabbitmq.RabbitMqObservableQueue;
import com.netflix.conductor.core.events.EventQueueProvider;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMqEventQueueProvider implements EventQueueProvider {


    private ConnectionFactory connectionFactory;
    private String queueName;
    private String exchangeName;
    private String routingKey;

    public RabbitMqEventQueueProvider(ConnectionFactory connectionFactory,
                                      String exchangeName, String routingKey, String queueName) {
        this.connectionFactory = connectionFactory;
        this.queueName = queueName;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    @Override
    public ObservableQueue getQueue(String queueURI) {
        return new RabbitMqObservableQueue(connectionFactory, queueURI, exchangeName, routingKey, queueName);
    }
}
