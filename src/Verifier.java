import java.util.*;

public class Verifier {

    static class Literal {
        String variable;
        boolean negated;

        public Literal(String variable, boolean negated) {
            this.variable = variable;
            this.negated = negated;
        }
    }

    static class Clause {
        ArrayList<Literal> literals;

        public Clause(ArrayList<Literal> literals) {
            this.literals = literals;
        }
    }

    public static char verifyUse(String dnfExpression, String input) {
        ArrayList<Clause> clauses = parseDNF(dnfExpression);

        Map<Character, Boolean> variableValues = new HashMap<>();
        for (int i = 0; i < input.length(); i++) {
            variableValues.put((char) ('A' + i), input.charAt(i) == '1');
        }

        for (Clause clause : clauses) {
            boolean clauseResult = true;

            for (Literal literal : clause.literals) {
                boolean variableValue = variableValues.get(literal.variable.charAt(0));
                boolean literalValue = literal.negated ? !variableValue : variableValue;

                if (!literalValue) {
                    clauseResult = false;
                    break;
                }
            }

            if (clauseResult) {
                return '1';
            }
        }

        return '0';
    }

    private static ArrayList<Clause> parseDNF(String input) {
        String[] clausesStr = input.split("\\+");
        ArrayList<Clause> clauses = new ArrayList<>();
        for (String clauseStr : clausesStr) {
            clauses.add(parseClause(clauseStr));
        }
        return clauses;
    }

    private static Clause parseClause(String input) {
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
}
