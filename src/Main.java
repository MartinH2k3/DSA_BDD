import java.util.*;

public class Main {
    public static void main(String[] args) {
        DNFGenerator dnfGenerator = new DNFGenerator();
        BDDBuilder bddBuilder = new BDDBuilder();
        int numberOfDNFs = 100;
        int numberOfVariables = 18;
        boolean overall = true;

        for (int i = 0; i < numberOfDNFs; i++) {
            // Generate a unique DNF expression
            String dnfExpression = dnfGenerator.generateDNF(numberOfVariables);
            System.out.println("DNF Expression: " + dnfExpression);

            // Generate a random order of numberOfVariables 1s and 0s
            String input = DNFGenerator.generateRandomInput(numberOfVariables);
            System.out.println("Input: " + input);

            // Create a BDD, alphabetically ordered
            BDDBuilder.BDD bddDefault = bddBuilder.createBDD(dnfExpression);

            // Use the default BDD to evaluate the input
            char bddResult = bddDefault.use(input);
            System.out.println("BDD Result: " + bddResult);

            // Use the DNFVerifier to evaluate the input
            char verifierResult = Verifier.verifyUse(dnfExpression, input);
            System.out.println("Verifier Result: " + verifierResult);

            // Check if the results match
            if (bddResult != verifierResult) {
                overall = false;
            }
            System.out.println("Results match: " + (bddResult == verifierResult));
            System.out.println("---------------------------------------------");
        }

        System.out.println("Overall results match: " + overall);
        TestComplexity.test();
    }


}
