


package splProject;

import java.util.*;
import javax.swing.*;

public class Simplifier {
    private BooleanLogic parent;

    public Simplifier(BooleanLogic parent) {
        this.parent = parent;
    }

    public void simplify() { //core method
        String oriExp = parent.expField.getText().trim();

        if (oriExp.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Please enter a Boolean expression first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Set<String> variables = getVar(oriExp);
            int len = variables.size();
            String[] varNames = variables.toArray(new String[len]);
            Arrays.sort(varNames);

            if (1>len || len > 3) {
                JOptionPane.showMessageDialog(parent, "Simplification currently supports up to 3 variables only", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Set<Integer> minterms = solveExp(oriExp, varNames, len);
            String simpExp = simpExp(minterms, len, varNames);
            parent.setSimpExp(simpExp);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Error in simplification: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Set<String> getVar(String exp) {
        Set<String> variables = new HashSet<>();
        for (int i = 0; i < exp.length(); i++) {
            char ch = exp.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
                variables.add(String.valueOf(ch));
            }
        }
        return variables;
    }

    private Set<Integer> solveExp(String exp, String[] varNames, int length) {
        exp = exp.replaceAll("[·*]", "");
        String[] terms = exp.split("\\+");
        Set<Integer> minterms = new HashSet<>();

        for (String term : terms) {
            term = term.trim();
            if (!term.isEmpty()) {
                minterms.addAll(findCompliment(term, varNames, length));
            }
        }

        return minterms;
    }

    private static Set<Integer> findCompliment(String term, String[] variables, int length) {
        boolean[] isPresent = new boolean[length];
        boolean[] isComplement = new boolean[length];

        for (int i = 0; i < length; i++) {
            String var = variables[i];
            if (term.contains(var + "'")) {
                isPresent[i] = true;
                isComplement[i] = true;
            } else if (term.contains(var)) {
                isPresent[i] = true;
                isComplement[i] = false;
            }
        }

        return genMinterms(isPresent, isComplement, length);
    }

    private static Set<Integer> genMinterms(boolean[] varPresent, boolean[] varComplement, int numVars) {
        Set<Integer> minterms = new HashSet<>();
        Recursive(minterms, varPresent, varComplement, 0, 0, numVars);
        return minterms;
    }

    
     //ekta term theke possible all minterm collect korche
    public static void Recursive(Set<Integer> minterms, boolean[] present, boolean[] complement, int indx, int currMin, int len) {
    if (indx == len) {
        minterms.add(currMin);
        return;
    }

    if (present[indx]) {  //variable 3(A, B, C),len = 3 ,Expression: A B' .present = [true, true, false]complement = [false, true, false]C- 0 & 1 dhorle minterms :A=1, B=0, C=0 → 100 → 4 A=1, B=0, C=1 → 101 → 5minterms = {4, 5}
        // Variable is used in the term
        boolean isComplement = complement[indx];
        int bitPos = len - 1 - indx;

        if (!isComplement) {
            Recursive(minterms, present, complement, indx + 1, currMin | (1 << bitPos), len); // Variable = 1
        } else {
            Recursive(minterms, present, complement, indx + 1, currMin, len); // Variable = 0
        }
    } else {
        // Variable is not used both 0 and 1 possible
        int bitPos = len - 1 - indx;

        Recursive(minterms, present, complement, indx + 1, currMin, len); // 0
        Recursive(minterms, present, complement, indx + 1, currMin | (1 << bitPos), len); // 1  1 k oto ghor shorabe thene value tar shathe current er or korbe fole 1 hobe
    }
}

    private String simpExp(Set<Integer> minterms, int numVars, String[] varNames) {
        if (minterms.isEmpty()) 
            return "0";
        if (minterms.size() == Math.pow(2, numVars))
            return "1";//shob minterm e thake oitar solution hobe 2
                        //A·B + A·B' + A'·B + A'·B'

        List<Term> primeImplicants = findPrimeImpli(minterms, numVars);
        List<Term> minimalCover = findMinCover(primeImplicants, minterms);
        return termsToExpression(minimalCover, numVars, varNames);
    }

    private static List<Term> findPrimeImpli(Set<Integer> minterms, int numVars) {
        Map<Integer, List<Term>> groups = new HashMap<>();
        for (int minterm : minterms) {
            int ones = Integer.bitCount(minterm); //minterm er binary representation e  koyta 1 
            List<Term> group = groups.get(ones);
             if (group == null) {
               group = new ArrayList<>();
               groups.put(ones, group);//group
       }

          Set<Integer> mintermSet = Collections.singleton(minterm);
          Term newTerm = new Term(mintermSet, minterm, numVars);
          group.add(newTerm);
        }

        List<Term> primes = new ArrayList<>();
        while (!groups.isEmpty()) {
            Map<Integer, List<Term>> newGroups = new HashMap<>();
            Set<Term> used = new HashSet<>();
            List<Integer> keys = new ArrayList<>(groups.keySet());
            Collections.sort(keys);

            for (int i = 0; i < keys.size() - 1; i++) {
                List<Term> group1 = groups.get(keys.get(i));
                List<Term> group2 = groups.get(keys.get(i + 1));

                for (Term t1 : group1) {
                    for (Term t2 : group2) {
                        Term combined = t1.combine(t2);
                        if (combined != null) {
                            newGroups.computeIfAbsent(combined.countOnes(), k -> new ArrayList<>()).add(combined);
                            used.add(t1);
                            used.add(t2);
                        }
                    }
                }
            }

            for (List<Term> group : groups.values()) {
                for (Term t : group) {
                    if (!used.contains(t)) {
                        primes.add(t);
                    }
                }
            }

            groups = newGroups;
        }

        return primes;
    }

     //selects a minimal set of prime implicants that covers all given minterms using a greedy approach.
    private static List<Term> findMinCover(List<Term> primeImplicants, Set<Integer> minterms) {
        List<Term> cover = new ArrayList<>();
        Set<Integer> covered = new HashSet<>();

        while (!minterms.equals(covered)) {
            Term best = null;
            int maxCovered = -1;

            for (Term term : primeImplicants) {
                Set<Integer> uncovered = new HashSet<>(term.getMinterms());
                uncovered.removeAll(covered);

                if (uncovered.size() > maxCovered) {
                    maxCovered = uncovered.size();
                    best = term;
                }
            }

            if (best != null) {
                cover.add(best);
                covered.addAll(best.getMinterms());
            } else {
                break;
            }
        }

        return cover;
    }

    //final jei term gulu thake oigulu theke exp banay
    private static String termsToExpression(List<Term> terms, int numVars, String[] varNames) {
        if (terms.isEmpty()) return "0";

        List<String> exp = new ArrayList<>();
        for (Term term : terms) {
            StringBuilder sb = new StringBuilder();
            String mask = term.getState();

            for (int i = 0; i < numVars; i++) {
                if (mask.charAt(i) != '-') { //don't care na thakle variable boshbe
                    sb.append(varNames[i]);
                    if (mask.charAt(i) == '0') {
                        sb.append("'"); //add kore '
                    }
                }
            }

            if (sb.length() > 0) {
                exp.add(sb.toString());
            }
        }

        return exp.isEmpty() ? "1" : String.join(" + ", exp);
    }
}
