

package labs;

import DecisionTree.Matrix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class Lab7 {

    public static void main(String args[]) {
        int[][] data = process("src/files/data.txt");

        if (data == null) {
            System.exit(0);
        }

        ArrayList<Integer> attributes = new ArrayList<>();
        attributes.add(0);
        attributes.add(1);
        attributes.add(2);
        attributes.add(3);


        ArrayList<Integer> intialRows = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            intialRows.add(i);
        }


        printDecisionTree(data, attributes, intialRows,0, 100);
    }

    public static int[][] process(String filename) {
        ArrayList<ArrayList<Integer>> rowValues = new ArrayList<>();

        try {
            Iterator<String> lines = Files.lines(Paths.get(filename))
                    .collect(Collectors.toList())
                    .iterator();

            lines.forEachRemaining(l -> {
                ArrayList<Integer> rVals = new ArrayList<>();
                String[] data = l.split(",");
                for (String d : data) {
                    double val = Double.parseDouble(d);
                    rVals.add( (int) Math.floor(val));
                }
                rowValues.add(rVals);
            });

            int numCols = rowValues.get(0).size();
            int[][] data = new int[rowValues.size()][numCols];

            for (int r = 0; r < rowValues.size(); r++) {
                for (int c = 0; c < numCols; c++) {
                    data[r][c] = rowValues.get(r).get(c);
                }
            }

            return data;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void printDecisionTree(int[][] data, ArrayList<Integer> attributes, ArrayList<Integer> rows, int level, double currentIGR) {
        Matrix matrix = new Matrix(data);

        // If no rows left, stop
        if (rows == null || rows.isEmpty()) {
            return;
        }

        // Build indentation string
        String indent = "";
        for (int i = 0; i < level; i++) indent += "\t";

        // Leaf node condition: no attributes left or IGR too low
        if (attributes.isEmpty() || currentIGR < 0.01) {
            int value = matrix.findMostCommonCategory(rows);  // majority class

            System.out.println(indent + "value = " + value);
            return;
        }

        // Find the best attribute to split on
        int bestSplit = -1;
        double highestIGR = Double.NEGATIVE_INFINITY;
        for (int attribute : attributes) {
            double igr = matrix.computeIGR(attribute, rows);
            if (igr > highestIGR) {
                highestIGR = igr;
                bestSplit = attribute;
            }
        }

        // If the best split is not meaningful, print leaf node
        if (highestIGR < 0.01) {
            int value = matrix.findMostCommonCategory(rows);
            System.out.println(indent + "value = " + value);
            return;
        }

        // Split data by the best attribute
        HashMap<Integer, ArrayList<Integer>> splitData = matrix.split(bestSplit, rows);

        // Recurse for each branch
        for (int value : splitData.keySet()) {
            System.out.println(indent + "When attribute " + (bestSplit + 1) + " has value " + value);

            // Create a new copy of attributes for this branch to avoid affecting other branches
            ArrayList<Integer> remainingAttributes = new ArrayList<>(attributes);
            remainingAttributes.remove(Integer.valueOf(bestSplit));

            // Recursive call with increased level
            printDecisionTree(data, remainingAttributes, splitData.get(value), level + 1, highestIGR);
        }
    }




    }
