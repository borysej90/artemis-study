package org.example.consumer;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.*;
import org.example.utils.Artemis;

public class Consumer {
    public static void main(String[] args) throws Exception {
        Artemis artemis = new Artemis("tcp://192.168.56.12:61616", "consumer-queue");
        artemis.registerGracefulShutdown();

        ClientConsumer consumer = artemis.getSession().createConsumer(artemis.getQueueName());
        artemis.startSession();
        while (!artemis.getSession().isClosed()) {
            Exception e = consumer.getLastException();
            if (e != null) {
                artemis.stop();
                break;
            }
            ClientMessage msg = consumer.receive();
            System.out.println("Received \"" + msg.getBodyBuffer().readString() + '"');
        }
    }
}
