package splProject;

import java.util.*;
//implicant er  representation , combine , compare,track rakhe
public class Term {
    private Set<Integer> minterms;
    private String state;

    public Term(Set<Integer> minterms, int value, int len) {
        this.minterms = new HashSet<>(minterms);
        this.state = getBinaryString(value, len);
    }

    public Term(Set<Integer> minterms, String state) {
        this.minterms = new HashSet<>(minterms);
        this.state = state;
    }

    public Set<Integer> getMinterms() {
        return minterms;
    }

    public String getState() {
        return state;
    }

    public int getDontCare() { //count - 
        int count = 0;
        String s = state;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '-') {
                count = count + 1;
            }
        }
        return count;
    }

    public boolean covers(int minterm) {
        String minBinary = getBinaryString(minterm, state.length());
        for (int i = 0; i < state.length(); i++) {
            if (state.charAt(i) != '-' && state.charAt(i) != minBinary.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public Term combine(Term other) {
        if (state.length() != other.state.length()) {
            return null;
        }
        
        int diffCount = 0;
        int diffPos = -1;
        
        for (int i = 0; i < state.length(); i++) {
            if (state.charAt(i) != other.state.charAt(i)) {
                if (state.charAt(i) == '-' || other.state.charAt(i) == '-') {
                    return null;
                }
                diffCount++;
                diffPos = i;
            }
        }

        if (diffCount != 1) { //diff 1 er beshi hole group hobe nah
            return null;
        }

        StringBuilder newState = new StringBuilder(state); //1010, 1000 → diff at position 1 → becomes 10-0
        newState.setCharAt(diffPos, '-'); // new string e same bit e - boshbe eibar
        Set<Integer> newMinterms = new HashSet<>(minterms);
        newMinterms.addAll(other.minterms);
        return new Term(newMinterms, newState.toString());
    }

    private static String getBinaryString(int value, int length) {
        String binary = Integer.toBinaryString(value);
        while (binary.length() < length) {
            binary = "0" + binary; //digit na mille extra 0 diye fill up
        }
        return binary;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Term otherTerm = (Term) obj;
        return this.state.equals(otherTerm.state);
    }

    @Override
    public int hashCode() { //object fast khujte use hoy  hashset, map e 
        return Objects.hash(state);
    }

    @Override
    public String toString() {
        return state + " -> " + minterms;
    }

    public int countOnes() {
        int count = 0;
        char[] characters = state.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            char current = characters[i];
            if (current == '1') {
                count++;
            }
        }
        return count;
    }
}
