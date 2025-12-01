import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Lab7 {
    public static void main(String[] args) {
        String file = "./src/test/resources/files/asgn4.csv";
        int[][] data = process(file);

        ArrayList<Integer> attributes = new ArrayList<>();
        int attributeSize = data[0].length-1;
        for (int i = 0; i < attributeSize; i++) {
            attributes.add(i);
        }

        ArrayList<Integer> rows = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            rows.add(i);
        }

        DecisionTreeNode tree = buildDecisionTree(new Matrix(data), attributes, rows);
        printDecisionTree(tree, 0);

    }

    //creates a two-dimensional array from the input file.
    public static int[][] process(String filename) {
        ArrayList<int[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                int[] row = new int[parts.length];
                //truncate decimal
                for (int i = 0; i < parts.length; i++) {
                    double d = Double.parseDouble(parts[i]);
                    row[i] = (int) d;
                }
                //class
                row[parts.length-1] = Integer.parseInt(parts[parts.length-1].trim());

                rows.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        int[][] data = new int[rows.size()][5];
        for (int i = 0; i < rows.size(); i++) {
            data[i] = rows.get(i);
        }
        return data;
    }

    //recursive method that builds the decision tree.
    //params: data matrix, the set of attributes that have not been used so far in this branch of the tree, the set of rows to examine
    public static DecisionTreeNode buildDecisionTree(Matrix dataMatrix,
                                                     ArrayList<Integer> attributes,
                                                     ArrayList<Integer> rows) {
        double entropy = dataMatrix.getEntropy(rows);
        if (rows.isEmpty() || entropy < 0.01 || attributes.isEmpty()) {
            int value = dataMatrix.findMostCommonValue(rows);
            return new DecisionTreeNode(value);
        }
        else {
            //find best attribute to split on
            int bestAttribute = -1;
            double bestIGR = -0.01;
            for (int attribute : attributes) {
                double IGR = dataMatrix.computeIGR(attribute, rows);
                if (IGR > bestIGR) {
                    bestIGR = IGR;
                    bestAttribute = attribute;
                }
            }
            //if there are no good attributes to split on, make leaf node
            if (bestIGR < 0.01) {
                return new DecisionTreeNode(dataMatrix.findMostCommonValue(rows));
            }
            //if there is significant improvement make decision node with split on the attribute
            else {
                HashMap<Integer, ArrayList<Integer>> newSplit = dataMatrix.split(bestAttribute, rows);
                HashMap<Integer, DecisionTreeNode> children = new HashMap<>();
                for (int attributeVal : newSplit.keySet()) {
                    ArrayList<Integer> newRows = newSplit.get(attributeVal);
                    ArrayList<Integer> newAttributes = new ArrayList<>(attributes);
                    newAttributes.remove((Integer) bestAttribute);
                    if (!newRows.isEmpty()) {
                        DecisionTreeNode child = buildDecisionTree(dataMatrix, newAttributes,newRows);
                        children.put(attributeVal, child);
                    }
                }
                return new DecisionTreeNode(bestAttribute, children);
            }
        }
    }

    public static void printDecisionTree(DecisionTreeNode node, int level) {
        if (node.isLeaf()) {
            printIndent(level);
            System.out.println("value = " + node.getClassValue());
            return;
        }

        // for each decision node, print the "When attribute ..." line and recurse
        ArrayList<Integer> values = new ArrayList<>(node.getChildren().keySet());
        Collections.sort(values);

        for (int v : values) {
            printIndent(level);
            System.out.println("When attribute " + (node.getAttribute() + 1) + " has value " + v);
            printDecisionTree(node.getChildren().get(v), level + 1);
        }
    }

    private static void printIndent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
    }
}
