import java.util.*;

public class BDDBuilder {

    // logic

    class Literal {
        String variable;
        boolean negated;

        public Literal(String variable, boolean negated) {
            this.variable = variable;
            this.negated = negated;
        }
    }

    class Clause {
        ArrayList<Literal> literals;

        public Clause(ArrayList<Literal> literals) {
            this.literals = literals;
        }
    }

    class DNF{
        public DNF(ArrayList<Clause> clauses) {
            this.clauses = clauses;
        }
        public DNF() {
            clauses = new ArrayList<>();
        }
        ArrayList<Clause> clauses;
        boolean isZero;
        boolean isOne;

        // used so I can compare these two based on content, not reference
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            DNF dnf = (DNF) o;
            if (isZero != dnf.isZero || isOne != dnf.isOne) {
                return false;
            }
            return Objects.equals(clauses, dnf.clauses);
        }

        @Override
        public int hashCode() {
            int result = clauses != null ? clauses.hashCode() : 0;
            result = 31 * result + (isZero ? 1 : 0);
            result = 31 * result + (isOne ? 1 : 0);
            return result;
        }
    }

    private ArrayList<ArrayList<String>> generatePermutations(ArrayList<String> variables) {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        int size = variables.size();

        for (int i = 0; i < size; i++) {
            ArrayList<String> permutation = new ArrayList<>(size);
            for (int j = 0; j < size; j++) {
                permutation.add(variables.get((j + i) % size));
            }
            result.add(permutation);
        }

        return result;
    }


    // tree
    public class Node {
        public Node(String variable){
            this.variable = variable;
            this.left = null;
            this.right = null;
        }
        String variable;
        Node left;
        Node right;
    }

    // parsing functions
    private ArrayList<Clause> parseDNF(String input) {
        String[] clausesStr = input.split("\\+");
        ArrayList<Clause> clauses = new ArrayList<>();
        for (String clauseStr : clausesStr) {
            clauses.add(parseClause(clauseStr));
        }
        return clauses;
    }

    private Clause parseClause(String input) {
        ArrayList<Literal> literals = new ArrayList<>();
        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);
            boolean negated = false;
            if (c == '!') {
                negated = true;
                i++;
                c = input.charAt(i);
            }
            literals.add(new Literal(Character.toString(c), negated));
            i++;
        }
        return new Clause(literals);
    }

    private String[] parseVariables(String variablesOrder) {
        return variablesOrder.split(",");
    }

    // BDD functions
    public class BDD {
        public BDD(String expression, String variablesOrder){
            dnf = new DNF(parseDNF(expression));
            variables.addAll(List.of(parseVariables(variablesOrder)));
            nOfVar = variables.size();
            one = new Node(null);
            zero = new Node(null);
            one.variable = "1";
            zero.variable = "0";

            if (dnf.isZero) {
                root = zero;
            } else if (dnf.isOne) {
                root = one;
            } else {
                root = buildBDD(this, dnf, 0);
                dnfCache.put(dnf, root);
            }

            nOfNodes = dnfCache.size();
        }
        DNF dnf;
        ArrayList<String> variables = new ArrayList<>();
        Integer nOfVar;
        Integer nOfNodes;
        Node root, one, zero;
        private HashMap<DNF, Node> dnfCache = new HashMap<>();

        public char use(String sequence) { // uses alphabetical order
            if (sequence == null || sequence.isEmpty()) {
                return '\0';
            }

            Map<Character, Boolean> variableValues = new HashMap<>();
            for (int i = 0; i < sequence.length(); i++) {
                variableValues.put((char) ('A' + i), sequence.charAt(i) == '1');
            }

            Node currentNode = root;
            while (!(currentNode == one || currentNode == zero)) {
                if (variableValues.get(currentNode.variable.charAt(0))) {
                    currentNode = currentNode.right;
                } else {
                    currentNode = currentNode.left;
                }
            }

            return currentNode.variable.charAt(0);
        }

    }

    public BDD createBDD(String expression, String variableOrder) {
        BDD output = new BDD(expression, variableOrder);
        return output;
    }

    public BDD createBDD(String expression) {
        return createBDD(expression, "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z");
    }

    private Node buildBDD(BDD bdd, DNF dnf, int variableIndex) {
        if (variableIndex == bdd.variables.size() || dnf.clauses.isEmpty()) {
            return dnf.isOne ? bdd.one : bdd.zero;
        }

        String variable = bdd.variables.get(variableIndex);

        DNF reducedDNFWhenTrue = reduce(dnf, variable); // The variable is false
        DNF reducedDNFWhenFalse = reduce(dnf, "!" + variable); // The variable is true

        Node falseBranch = bdd.dnfCache.get(reducedDNFWhenFalse);
        if (falseBranch == null) {
            falseBranch = buildBDD(bdd, reducedDNFWhenFalse, variableIndex + 1);
            bdd.dnfCache.put(reducedDNFWhenFalse, falseBranch);
        }

        Node trueBranch = bdd.dnfCache.get(reducedDNFWhenTrue);
        if (trueBranch == null) {
            trueBranch = buildBDD(bdd, reducedDNFWhenTrue, variableIndex + 1);
            bdd.dnfCache.put(reducedDNFWhenTrue, trueBranch);
        }

        Node node = new Node(variable);
        node.left = falseBranch;
        node.right = trueBranch;

        return node;
    }

    private DNF reduce(DNF inputDNF, String variable) {
        ArrayList<Clause> reducedClauses = new ArrayList<>();
        boolean negated = false;
        if (variable.startsWith("!")) {
            negated = true;
            variable = variable.substring(1);
        }

        for (Clause clause : inputDNF.clauses) {
            boolean variableFound = false;
            ArrayList<Literal> reducedLiterals = new ArrayList<>();

            for (Literal literal : clause.literals) {
                if (literal.variable.equals(variable)) {
                    variableFound = true;
                    if (literal.negated != negated) {
                        reducedLiterals = null;
                        break;
                    }
                } else {
                    reducedLiterals.add(literal);
                }
            }

            if (!variableFound) {
                reducedClauses.add(clause);
            } else {
                if (reducedLiterals == null) {
                    continue;
                }
                if (reducedLiterals.isEmpty()) {
                    DNF reducedDNF = new DNF();
                    reducedDNF.isOne = true;
                    return reducedDNF;
                }
                reducedClauses.add(new Clause(reducedLiterals));
            }
        }

        DNF reducedDNF = new DNF(reducedClauses);
        if (reducedClauses.isEmpty()) {
            reducedDNF.isZero = true;
        }
        return reducedDNF;
    }

    public BDD createWithBestOrder(String dnfExpression) {
        ArrayList<String> uniqueVariables = getUniqueVariablesFromDNF(dnfExpression);
        ArrayList<ArrayList<String>> permutations = generatePermutations(uniqueVariables);
        BDD bestBDD = null;
        int minNodes = Integer.MAX_VALUE;

        for (ArrayList<String> permutation : permutations) {
            String variableOrder = String.join(",", permutation);
            BDD currentBDD = createBDD(dnfExpression, variableOrder);

            if (currentBDD.nOfNodes < minNodes) {
                bestBDD = currentBDD;
                minNodes = currentBDD.nOfNodes;
            }
        }

        return bestBDD;
    }

    private ArrayList<String> getUniqueVariablesFromDNF(String dnfExpression) {
        DNF dnf = new DNF(parseDNF(dnfExpression));
        LinkedHashSet<String> uniqueVariables = new LinkedHashSet<>();

        for (Clause clause : dnf.clauses) {
            for (Literal literal : clause.literals) {
                uniqueVariables.add(literal.variable);
            }
        }

        return new ArrayList<>(uniqueVariables);
    }

    public BDD createWithTotallyBestPermutations(String dnf) {
        ArrayList<String> variables = getUniqueVariablesFromDNF(dnf);
        int n = variables.size();
        int[] c = new int[n];
        BDD bestBDD = new BDD(dnf, String.join(",", variables));
        int minNodes = bestBDD.nOfNodes;

        // Heap's Algorithm
        int i = 0;
        while (i < n) {
            if (c[i] < i) {
                if (i % 2 == 0) {
                    swap(variables, 0, i);
                } else {
                    swap(variables, c[i], i);
                }
                BDD currentBDD = new BDD(dnf, String.join(",", variables));
                if (currentBDD.nOfNodes < minNodes) {
                    bestBDD = currentBDD;
                    minNodes = currentBDD.nOfNodes;
                }
                c[i]++;
                i = 0;
            } else {
                c[i] = 0;
                i++;
            }
        }

        return bestBDD;
    }

    private void swap(ArrayList<String> variables, int i, int j) {
        String temp = variables.get(i);
        variables.set(i, variables.get(j));
        variables.set(j, temp);
    }

}
