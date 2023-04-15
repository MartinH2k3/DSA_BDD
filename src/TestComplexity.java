public class TestComplexity {
    public static void test(int numberOfDNFs, int numberOfVariables) {
        DNFGenerator dnfGenerator = new DNFGenerator();
        BDDBuilder bddBuilder = new BDDBuilder();
        long nodesAlph = 0;
        long nodesOrd = 0;
        BDDBuilder.BDD bdd;

        long startTimeAlph = System.currentTimeMillis();
        for (int i = 0; i < numberOfDNFs; i++) {
            bdd = bddBuilder.createBDD(dnfGenerator.generateDNF(numberOfVariables));
            nodesAlph += bdd.nOfNodes;
        }
        long endTimeAlph = System.currentTimeMillis();
        long durationAlph = (endTimeAlph - startTimeAlph);

        long startTimeOrd = System.currentTimeMillis();
        for (int i = 0; i < numberOfDNFs; i++) {
            bdd = bddBuilder.createWithBestOrder(dnfGenerator.generateDNF(numberOfVariables));
            nodesOrd += bdd.nOfNodes;
        }
        long endTimeOrd = System.currentTimeMillis();
        long durationOrd = (endTimeOrd - startTimeOrd);

        System.out.println("Test for " + numberOfDNFs + " formulas with " + numberOfVariables + " variables.\n");
        System.out.println("Test for alphabetically ordered BDDs:");
        System.out.println("Time is " + durationAlph + " milliseconds.\nReduced:NonReduced ratio of nodes number:\n1:"  + (int)(numberOfDNFs*(Math.pow(2, numberOfVariables))/nodesAlph));
        System.out.println("Test for BDDs with best order:");
        System.out.println("Time is " + durationOrd + " milliseconds.\nReduced:NonReduced ratio of nodes number:\n1:"  + (int)(numberOfDNFs*(Math.pow(2, numberOfVariables))/nodesOrd));

    }
}
