public class TestComplexity {
    public static void test() {
        DNFGenerator dnfGenerator = new DNFGenerator();
        BDDBuilder bddBuilder = new BDDBuilder();
        int numberOfDNFs = 20000;
        int numberOfVariables = 18;
        long nodes = 0;
        BDDBuilder.BDD bdd;

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfDNFs; i++) {
            bdd = bddBuilder.createBDD(dnfGenerator.generateDNF(numberOfVariables));
            nodes += bdd.nOfNodes;
        }
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println("Test for " + numberOfDNFs + " formulas with " + numberOfVariables + "variables.\n" + "Time is " + duration + " milliseconds.\nReduced:NonReduced ratio of nodes number:\t1:"  + (int)(numberOfDNFs*(Math.pow(2, numberOfVariables))/nodes));
    }
}
