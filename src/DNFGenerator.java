import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DNFGenerator {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random random = new Random();

    public static String generateDNF(int n) {
        if (n > ALPHABET.length()) {
            throw new IllegalArgumentException("The number of variables must be less than or equal to the length of the alphabet.");
        }
        List<Character> variables = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            variables.add(ALPHABET.charAt(i));
        }
        StringBuilder dnf = new StringBuilder();
        List<Character> unusedVariables = new ArrayList<>(variables);

        while (!unusedVariables.isEmpty()) {
            int numLiterals = random.nextInt(n) + 1;
            List<Character> clauseVariables = new ArrayList<>(variables);
            clauseVariables = clauseVariables.subList(0, numLiterals);

            char newVar = unusedVariables.remove(0);
            if (!clauseVariables.contains(newVar)) {
                clauseVariables.set(0, newVar);
            }

            dnf.append(generateClause(clauseVariables));
            if (!unusedVariables.isEmpty()) {
                dnf.append("+");
            }
        }

        return dnf.toString();
    }

    public static String generateRandomInput(int size) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(random.nextInt(2));
        }
        return sb.toString();
    }

    private static String generateClause(List<Character> clauseVariables) {
        StringBuilder clause = new StringBuilder();
        for (char var : clauseVariables) {
            if (random.nextBoolean()) {
                clause.append("!");
            }
            clause.append(var);
        }
        return clause.toString();
    }
}
