package MQ;

import MQ.CreditScore.CreditScoreService;
import MQ.CreditScore.CreditScoreService_Service;
import MQ.Model.Loan;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import javafx.scene.shape.Path;
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
import java.util.concurrent.TimeoutException;


public class GetCreditScore {
    private final static String QUEUE_NAME = "loanqueue";

    public static byte[] readAllBytes(Path path) throws IOException {
        return new byte[0];
    }


    public static void main(String[] args) throws IOException, TimeoutException, SAXException {
        Loan loan = new Loan();
        GetCreditScore cds = new GetCreditScore();

        loan.setSSN("010192-1581");

        // Sets the credit score to loan.creditScore
        loan.setCreditScore(creditScore(loan.getSSN()));

        // Runs the writeXML method
        cds.writeXML(loan.getSSN(),loan.getCreditScore(), loan.getLoanAmount(), loan.getLoanDuration(), loan.getInterestRate(), loan.getRules());

        // The messaging method, which in the moment sends a test string
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//
//        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//        String message = "test";
//        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//        System.out.println(" [X] Sent '" + message + "'");
//
//        channel.close();
//        connection.close();

    }

    // The method that makes the XML file
    public void writeXML(String ssnumber, int creditScore, double loanAmount, Date loanDuration, double interestRate, String[] rules) throws IOException, SAXException {
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
            loanDu.appendChild(doc.createTextNode(String.valueOf(loanDuration)));
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

            byte[]array = bos.toByteArray();
            

            System.out.println("=== File converted to byte array ===");



              // Not used. But this method takes all from loan.xml, and puts it on a string
//            BufferedReader br = new BufferedReader(new FileReader(new File("D:\\loan.xml")));
//            String line;
//            StringBuilder sb = new StringBuilder();
//
//            while ((line=br.readLine())!= null){
//                sb.append(line.trim());
//            }
//            System.out.println(sb);

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

