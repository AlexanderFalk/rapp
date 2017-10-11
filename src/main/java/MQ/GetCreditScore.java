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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;


public class GetCreditScore {
    private static final String HOST_NAME = "207.154.228.245";
    private final static String PUBLISH_QUEUE_NAME = "Loan_Request_Queue";

    public static void main(String[] args) throws IOException, TimeoutException, SAXException, ParseException {
        Loan loan = new Loan();

        Scanner reader = new Scanner(System.in);
        System.out.println("Enter your social security number in the format ******-****:");
        loan.setSSN(reader.next());

        System.out.println("Enter how much you want to loan:");
        loan.setLoanAmount(reader.nextDouble());

        System.out.println("Enter the loan's duration in days:");
        loan.setLoanDuration(reader.nextInt());

        reader.close();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse("1970-01-01");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, loan.getLoanDuration());
        String output = dateFormat.format(calendar.getTime());

        String finalLoanDuration = output + " 01:00:00.0 CET";

        // Runs the writeXML method
        writeXML(loan.getSSN(), loan.getLoanAmount(), finalLoanDuration);


    }

    // The method that makes the XML file
    private static void writeXML(String ssnumber, double loanAmount, String loanDuration) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("LoanRequest");
            doc.appendChild(rootElement);

            Element ssn = doc.createElement("ssn");
            ssn.appendChild(doc.createTextNode(ssnumber));
            rootElement.appendChild(ssn);

            Element loanAm = doc.createElement("loanAmount");
            loanAm.appendChild(doc.createTextNode(String.valueOf(loanAmount)));
            rootElement.appendChild(loanAm);

            Element loanDu = doc.createElement("loanDuration");
            loanDu.appendChild(doc.createTextNode(loanDuration));
            rootElement.appendChild(loanDu);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);

            transformer.transform(source, result);

            byte[] xmlByteArray = bos.toByteArray();

            System.out.println("=== File converted to byte array ===");

            // When all the above is done, then sendMesseage() begin
            sendMessage(xmlByteArray);


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
    private static void sendMessage(byte[] message) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(PUBLISH_QUEUE_NAME, false, false, false, null);
        channel.basicPublish("", PUBLISH_QUEUE_NAME, null, message);
        System.out.println(" [X] Sent '" + message + "'");

        channel.close();
        connection.close();
    }

}

