package MQ;

import MQ.CreditScore.CreditScoreService;
import MQ.CreditScore.CreditScoreService_Service;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.sun.jndi.url.corbaname.corbanameURLContextFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class GetCreditScore {
    private final static String QUEUE_NAME = "loanqueue";

    public double userLoanAount;
    public Date date = new Date();
    public String ssn;

    public static void main(String[] args) throws IOException, TimeoutException {
        GetCreditScore cds = new GetCreditScore();
        cds.ssn = "010192-1581";
        System.out.println(creditScore(cds.ssn));


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = cds.ssn;
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [X] Sent '" + message + "'");

        channel.close();
        connection.close();

    }


    private static int creditScore(java.lang.String ssn)
    {
        CreditScoreService_Service service = new CreditScoreService_Service();
        CreditScoreService port = service.getCreditScoreServicePort();
        return port.creditScore(ssn);
    }
}

