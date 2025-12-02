import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Matrix {
    private final int[][] matrix;
    public Matrix(int[][] matrix) {
        this.matrix = matrix;
    }
    //Examines only the specified rows of the array.
    // It returns the number of rows in which the element at position attribute (a number between 0 and 4) is equal to value.
    private int findFrequency(int attribute, int value, ArrayList<Integer> rows) {
        int frequency = 0;
        for (int row : rows) {
            int[] currRow = matrix[row];
            if (currRow[attribute] == value) {
                frequency++;
            }
        }
        return frequency;
    }

    //Examines only the specified rows of the array. It returns a HashSet of the different values for the specified attribute.
    private HashSet<Integer> findDifferentValues(int attribute, ArrayList<Integer> rows)  {
        HashSet<Integer> differentValues = new HashSet<>();
        for (int row : rows) {
            int[] currRow = matrix[row];
            differentValues.add(currRow[attribute]);
        }
        return differentValues;
    }

    //Examines only the specified rows of the array. Returns an ArrayList of the rows where the value for the attribute is equal to value.
    private ArrayList<Integer> findRows(int attribute, int value, ArrayList<Integer> rows) {
        ArrayList<Integer> specifiedRows = new ArrayList<>();
        for (int row : rows) {
            int[] currRow = matrix[row];
            if (currRow[attribute] == value) {
                specifiedRows.add(row);
            }
        }
        return specifiedRows;
    }

    //returns log2 of the input
    private double log2(double number) {
        return Math.log(number) / Math.log(2);
    }

    //finds the entropy of the dataset that consists of the specified rows.
    private double findEntropy(ArrayList<Integer> rows) {
        if (rows.isEmpty()) return 0.0;
        double entropy = 0.0;
        HashMap<Integer, Integer> classMap = new HashMap<>();
        for (int row : rows) {
            int[] currRow = matrix[row];
            int rowClass = currRow[4];
            if (classMap.containsKey(rowClass)) {
                classMap.replace(rowClass, classMap.get(rowClass) + 1);
            } else {
                classMap.put(rowClass, 1);
            }
        }

        int total = rows.size();
        for (int count : classMap.values()) {
            double p = (double) count / total;
            if (p > 0) {
                entropy -= p * log2(p);
            }
        }
        return entropy;
    }

    //finds the entropy of the dataset that consists of the specified rows after it is partitioned on the attribute.
    private double findEntropy(int attribute, ArrayList<Integer> rows) {
        if (rows.isEmpty()) return 0.0;
        double entropy = 0.0;
        int total = rows.size();
        HashSet<Integer> values = findDifferentValues(attribute, rows);

        for (int v : values) {
            ArrayList<Integer> subset = findRows(attribute, v, rows);
            double weight = (double) subset.size() / total;
            double subsetEntropy = findEntropy(subset);
            entropy += weight * subsetEntropy;
        }
        return entropy;
    }

    // finds the information gain of partitioning on the attribute. Considers only the specified rows.
    private double findGain(int attribute, ArrayList<Integer> rows) {
        double baseEntropy = findEntropy(rows);
        double gainEntropy = findEntropy(attribute, rows);
        return baseEntropy - gainEntropy;
    }

    // returns the Information Gain Ratio, where we only look at the data defined by the set of rows and we consider splitting on attribute.
    public double computeIGR(int attribute, ArrayList<Integer> rows) {
        if (rows.isEmpty()) return 0.0;
        double gain = findGain(attribute, rows);
        if (gain == 0) return 0.0;
        int total = rows.size();
        HashSet<Integer> values = findDifferentValues(attribute, rows);
        double splitInfo = 0.0;

        for (int v : values) {
            ArrayList<Integer> subset = findRows(attribute, v, rows);
            double p = (double) subset.size() / total;
            if (p > 0) {
                splitInfo -= p * log2(p);
            }
        }

        if (splitInfo == 0.0) {
            return 0.0;
        }

        return gain / splitInfo;
    }

    // returns the most common category for the dataset that is the defined by the specified rows.
    public int findMostCommonValue(ArrayList<Integer> rows) {
        HashMap<Integer, Integer> countMap = new HashMap<>();
        for (int r : rows) {
            int rowClass = matrix[r][4];
            countMap.put(rowClass, countMap.getOrDefault(rowClass, 0) + 1);
        }

        int bestClass = -1;
        int bestCount = -1;
        for (int c : countMap.keySet()) {
            int freq = countMap.get(c);
            if (freq > bestCount) {
                bestCount = freq;
                bestClass = c;
            }
        }
        return bestClass;
    }

    //Splits the dataset that is defined by rows on the attribute.
    // Each element of the HashMap that is returned contains the value for the attribute and an ArrayList of rows that have this value.
    public HashMap<Integer, ArrayList<Integer>> split(int attribute, ArrayList<Integer> rows) {
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();

        for (int r : rows) {
            int val = matrix[r][attribute];
            if (!map.containsKey(val)) {
                map.put(val, new ArrayList<>());
            }
            map.get(val).add(r);
        }

        return map;
    }

    public double getEntropy(ArrayList<Integer> rows) {
        return findEntropy(rows);
    }
}
