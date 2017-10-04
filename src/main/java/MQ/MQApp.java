package MQ;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MQApp {
    private static final String QUEUE_NAME = "loan";

    public static void main(String[] args) throws IOException, TimeoutException {

        loanChannel();
    }

    public static void loanChannel() throws IOException, TimeoutException {
        // Connect
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("http://94.130.57.246:9001");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
