package MQ;

import MQ.CreditScore.CreditScoreService;
import MQ.CreditScore.CreditScoreService_Service;
import MQ.Model.Loan;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class GetCreditScore {
    private final static String QUEUE_NAME = "loanqueue";

    public double userLoanAount;
    public String userSsn;
    public Integer csResult;

    public static void main(String[] args) throws IOException, TimeoutException {
        Loan loan = new Loan();

        GetCreditScore cds = new GetCreditScore();
        cds.userSsn = "010192-1581";
        cds.csResult = creditScore(cds.userSsn);
        cds.writeXML();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = cds.userSsn;
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [X] Sent '" + message + "'");

        channel.close();
        connection.close();

    }

    public void writeXML(){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("loan");
            doc.appendChild(rootElement);

            Element ssn = doc.createElement("ssn");
            ssn.appendChild(doc.createTextNode(csResult.toString()));
            rootElement.appendChild(ssn);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("D:\\file.xml"));

            transformer.transform(source, result);

            System.out.println("=== File created ===");


        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerConfigurationException tfce) {
            tfce.printStackTrace();
        } catch (TransformerException te) {
            te.printStackTrace();
        }
    }


    private static int creditScore(java.lang.String ssn)
    {
        CreditScoreService_Service service = new CreditScoreService_Service();
        CreditScoreService port = service.getCreditScoreServicePort();
        return port.creditScore(ssn);
    }
}

