package org.example.producer;

import org.apache.activemq.artemis.api.core.*;
import org.apache.activemq.artemis.api.core.client.*;
import org.example.utils.Artemis;

public class Producer {
    public static void main(String[] args) throws Exception {
        final String address = "anyq.addr";
        Artemis artemis = new Artemis();
        artemis.registerGracefulShutdown();
        artemis.createAnycastQueue(address, "anyq");

        ClientProducer producer = artemis.getSession().createProducer(address);
        try (ClientSession session = artemis.getSession()) {
            int counter = 0;
            while (!session.isClosed()) {
                ClientMessage msg = session.createMessage(true);
                ActiveMQBuffer buffer = msg.getBodyBuffer();
                buffer.writeString("The Message - " + counter);
                producer.send(msg);
                System.out.println("Sent \"" + buffer.readString() + '"');
                counter++;
            }
        }
    }
}
