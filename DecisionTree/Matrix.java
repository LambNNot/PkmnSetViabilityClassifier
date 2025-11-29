package DecisionTree;

import java.util.*;


public class Matrix {
    private int[][] data;
    public int numRows = 0;
    public int numCols = 0;
    private ArrayList<Integer> rowIndices;

    public Matrix(int[][] matrix) {
        data = matrix;

        numRows = data.length;
        numCols = data[0].length;

        rowIndices = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            rowIndices.add(i);
        }
    }


    private int findFrequency(int attribute, int value, ArrayList<Integer> rows) {
        int count = 0;

        for (Integer r : rows) {
            if (data[r][attribute] == value) count++;
        }

        return count;
    }


    private HashSet<Integer> findDifferentValues(int attribute, ArrayList<Integer> rows) {
        HashSet<Integer> unique = new HashSet<>();
        for (Integer r : rows) {
            unique.add(data[r][attribute]);
        }
        return unique;
    }


    private double log2(double number) {
        return Math.log(number) / Math.log(2);
    }

    private double findEntropy(ArrayList<Integer> rows) {
        HashSet<Integer> categories = findDifferentValues(4, rows);

        double entropy = 0;
        for (Integer c : categories) {
            double pr_c = (1.0 * findFrequency(numCols - 1, c, rows)) / rows.size();
            entropy -= pr_c * log2(pr_c);
        }

        return entropy;
    }

    private double findEntropyA(int attribute, ArrayList<Integer> rows) {
        double entropy = 0;

        for (Integer value : findDifferentValues(attribute, rows)) {
            ArrayList<Integer> D_j = getRows(attribute, value, rows);
            entropy += ((D_j.size() * 1.0) / rows.size()) * findEntropy(D_j);
        }


        return entropy;
    }

    private double findGain(int attribute, ArrayList<Integer> rows) {
        return findEntropy(rows) - findEntropyA(attribute, rows);
    }


    public double computeIGR(int attribute, ArrayList<Integer> rows) {
        HashMap<Integer, ArrayList<Integer>> partition = split(attribute, rows);

        double total = 0;
        for (Map.Entry<Integer, ArrayList<Integer>> p : partition.entrySet()) {
            double proportion = (p.getValue()
                    .size() * 1.0) / rows.size();
            total -= proportion * log2(proportion);
        }

        return findGain(attribute, rows) / total;
    }



    public int findMostCommonCategory(ArrayList<Integer> rows) {
        HashMap<Integer, Integer> categoryCount = new HashMap<>();
        for (Integer r : rows) {
            int category = data[r][numCols - 1];
            categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
        }

        // Find the majority class
        int majorityCategory = -1;
        int maxCount = -1;
        for (Map.Entry<Integer, Integer> entry : categoryCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                majorityCategory = entry.getKey();
            }

            else if (entry.getValue() == maxCount && entry.getKey() < majorityCategory) {
                majorityCategory = entry.getKey();
            }
        }

        return majorityCategory;
    }



    public HashMap<Integer, ArrayList<Integer>> split(int attribute, ArrayList<Integer> rows) {
        HashMap<Integer, ArrayList<Integer>> attrValueMap = new HashMap<>();

        for (Integer r : rows) {
            int attrVal = data[r][attribute];

            ArrayList<Integer> currentRows = attrValueMap.getOrDefault(attrVal, new ArrayList<>());
            currentRows.add(r);
            attrValueMap.put(attrVal, currentRows);
        }

        return attrValueMap;
    }

    private ArrayList<Integer> getRows(int attribute, int value, ArrayList<Integer> rows) {
        ArrayList<Integer> rtn = new ArrayList<>();
        for (int r : rows) {
            if (data[r][attribute] == value) rtn.add(r);
        }
        return rtn;
    }


    public ArrayList<Integer> findAllRows() {
        return (ArrayList<Integer>) rowIndices.clone();
    }


    public int getCategoryAttribute() {
        return numCols - 1;
    }



    public double findProb(int[] row, int category) {
        double lambda = 1.0 / numRows;
        int n_j = findFrequency(getCategoryAttribute(), category, findAllRows());
        double prob = 1.0;

        for (int attrIndex = 0; attrIndex < getCategoryAttribute(); attrIndex++) {
            int n_ij = 0;
            int a_i = row[attrIndex];
            int m_i = findDifferentValues(attrIndex, findAllRows()).size();
            for (Map.Entry<Integer, ArrayList<Integer>> a_j : split(attrIndex, findAllRows()).entrySet()) {
                // if they have the same attribute value
                if (a_j.getKey() == a_i) {
                    // count the number of rows which also have the same class
                    for (Integer idx : a_j.getValue()) {
                        if (data[idx][getCategoryAttribute()] == category) {
                            n_ij++;
                        }
                    }



                }
            }
            prob *= (n_ij + lambda) / (n_j + lambda * m_i);
        }


        double p_ck =  0;
        for(Map.Entry<Integer, ArrayList<Integer>> c: split(getCategoryAttribute(), findAllRows()).entrySet()) {
            if (c.getKey() == category) {
                p_ck = (1.0 * c.getValue()
                        .size()) / numRows;
            }
        }



        return p_ck * prob;
    }



    public int findCategory(int[] row) {
        HashMap<Integer, Double> catMap = new HashMap<>();
        for (Integer category : findDifferentValues(getCategoryAttribute(), findAllRows())) {
            double p = findProb(row, category);
            catMap.put(category, p);
        }

        return Collections.max(catMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }




}