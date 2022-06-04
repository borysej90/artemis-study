package org.example.consumer;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.*;
import org.example.utils.Artemis;

public class Consumer {
    public static void main(String[] args) throws Exception {
        Artemis artemis = new Artemis("anyq");
        artemis.registerGracefulShutdown();

        ClientConsumer consumer = artemis.getSession().createConsumer(artemis.getQueueName());
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
