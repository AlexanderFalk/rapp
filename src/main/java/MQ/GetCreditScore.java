package MQ;

import MQ.CreditScore.CreditScoreService;
import MQ.CreditScore.CreditScoreService_Service;
import MQ.Model.Loan;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;


public class GetCreditScore {
    private final static String QUEUE_NAME = "loan";
    public byte[] xmlBytes;

    public static void main(String[] args) throws IOException, TimeoutException, SAXException {
        Loan loan = new Loan();
        GetCreditScore cds = new GetCreditScore();

        Scanner reader = new Scanner(System.in);
        System.out.println("Enter your social security number in the format ******-****:");
        loan.setSSN(reader.next());

        // Sets the credit score to loan.creditScore
        loan.setCreditScore(creditScore(loan.getSSN()));

        System.out.println("Enter how much you want to loan:");
        loan.setLoanAmount(reader.nextDouble());

        System.out.println("Enter the loan's duration in days:");
        loan.setLoanDuration(reader.next());
//


        reader.close();

        // Runs the writeXML method
        cds.writeXML(loan.getSSN(), loan.getCreditScore(), loan.getLoanAmount(), loan.getLoanDuration(), loan.getInterestRate(), loan.getRules());


    }

    // The method that makes the XML file
    public void writeXML(String ssnumber, int creditScore, double loanAmount, String loanDuration, double interestRate, String[] rules) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("loan");
            doc.appendChild(rootElement);

            Element ssn = doc.createElement("ssn");
            ssn.appendChild(doc.createTextNode(ssnumber));
            rootElement.appendChild(ssn);

            Element credScore = doc.createElement("creditscore");
            credScore.appendChild(doc.createTextNode(String.valueOf(creditScore)));
            rootElement.appendChild(credScore);

            Element loanAm = doc.createElement("loanamount");
            loanAm.appendChild(doc.createTextNode(String.valueOf(loanAmount)));
            rootElement.appendChild(loanAm);

            Element loanDu = doc.createElement("loanduration");
            loanDu.appendChild(doc.createTextNode(loanDuration));
            rootElement.appendChild(loanDu);

            Element interestRa = doc.createElement("interestrate");
            interestRa.appendChild(doc.createTextNode(String.valueOf(interestRate)));
            rootElement.appendChild(interestRa);

            Element ruls = doc.createElement("rules");
            ruls.appendChild(doc.createTextNode(String.valueOf(rules)));
            rootElement.appendChild(ruls);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);

            transformer.transform(source, result);

            byte[] array = bos.toByteArray();
            xmlBytes = array;

            System.out.println("=== File converted to byte array ===");

            // When all the above is done, then sendMesseage() begin
            sendMessage();


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // The messaging method, which in the moment sends a test string
    public void sendMessage() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("207.154.228.245");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicPublish("", QUEUE_NAME, null, xmlBytes);
        System.out.println(" [X] Sent '" + xmlBytes + "'");

        channel.close();
        connection.close();
    }


    private static int creditScore(java.lang.String ssn) {
        CreditScoreService_Service service = new CreditScoreService_Service();
        CreditScoreService port = service.getCreditScoreServicePort();
        return port.creditScore(ssn);
    }
}

