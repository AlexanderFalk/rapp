package MQ;

import MQ.CreditScore.CreditScoreService;
import MQ.CreditScore.CreditScoreService_Service;

public class GetCreditScore {
    public static String ssn = "010192-1581";

    public static void main(String[] args){
        System.out.print(creditScore(ssn));
        }


    private static int creditScore(java.lang.String ssn) {
        CreditScoreService_Service service = new CreditScoreService_Service();
        CreditScoreService port = service.getCreditScoreServicePort();
        return port.creditScore(ssn);
    }
}

