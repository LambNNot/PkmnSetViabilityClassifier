import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomForest {
    private List<DecisionTreeNode> trees;
    private int numTrees;
    private Random rand;

    public RandomForest(int numTrees) {
        this.numTrees = numTrees;
        this.trees = new ArrayList<>();
        this.rand = new Random();
    }

    /** Train the Random Forest on dataset X (features) and y (labels) */
    public void fit(int[][] X, int[] y) {
        int n = X.length;

        for (int t = 0; t < numTrees; t++) {
            // 1. Create bootstrap sample
            int[][] XSample = new int[n][];
            int[] ySample = new int[n];

            for (int i = 0; i < n; i++) {
                int idx = rand.nextInt(n); // random index with replacement
                XSample[i] = X[idx];
                ySample[i] = y[idx];
            }

            // 2. Build a decision tree from this sample
            DecisionTreeNode tree = buildTree(XSample, ySample);
            trees.add(tree);
        }
    }

    /** Predict the label for a new instance using majority vote */
    public int predict(int[] instance) {
        Map<Integer, Integer> votes = new HashMap<>();

        for (DecisionTreeNode tree : trees) {
            int pred = tree.predict(instance);
            votes.put(pred, votes.getOrDefault(pred, 0) + 1);
        }

        // Return the label with the most votes
        int bestClass = -1;
        int maxVotes = -1;
        for (int cls : votes.keySet()) {
            if (votes.get(cls) > maxVotes) {
                maxVotes = votes.get(cls);
                bestClass = cls;
            }
        }

        return bestClass;
    }

    /** Recursively build a decision tree from data */
    private DecisionTreeNode buildTree(int[][] X, int[] y) {
        // Base case 1: all labels are the same → leaf node
        if (allSameClass(y)) {
            return new DecisionTreeNode(y[0]);
        }

        // Base case 2: no attributes left → majority class leaf
        if (X[0].length == 0) {
            return new DecisionTreeNode(majorityClass(y));
        }

        // Randomly select an attribute to split on
        int numAttributes = X[0].length;
        int attribute = rand.nextInt(numAttributes);

        // Group examples by attribute value
        HashMap<Integer, List<Integer>> valueIndices = new HashMap<>();
        for (int i = 0; i < X.length; i++) {
            int val = X[i][attribute];
            valueIndices.computeIfAbsent(val, k -> new ArrayList<>()).add(i);
        }

        // Recursively build child nodes
        HashMap<Integer, DecisionTreeNode> children = new HashMap<>();
        for (int val : valueIndices.keySet()) {
            List<Integer> indices = valueIndices.get(val);
            int[][] subX = new int[indices.size()][numAttributes - 1];
            int[] subY = new int[indices.size()];

            for (int i = 0; i < indices.size(); i++) {
                int idx = indices.get(i);
                subY[i] = y[idx];

                // Remove the split attribute
                int[] newRow = new int[numAttributes - 1];
                int pos = 0;
                for (int j = 0; j < numAttributes; j++) {
                    if (j != attribute) {
                        newRow[pos++] = X[idx][j];
                    }
                }
                subX[i] = newRow;
            }

            children.put(val, buildTree(subX, subY));
        }

        return new DecisionTreeNode(attribute, children);
    }

    /** Helper: check if all labels are the same */
    private boolean allSameClass(int[] y) {
        int first = y[0];
        for (int val : y) {
            if (val != first) return false;
        }
        return true;
    }

    /** Helper: return majority class in y */
    private int majorityClass(int[] y) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int val : y) {
            counts.put(val, counts.getOrDefault(val, 0) + 1);
        }
        int majority = y[0];
        int max = 0;
        for (int k : counts.keySet()) {
            if (counts.get(k) > max) {
                max = counts.get(k);
                majority = k;
            }
        }
        return majority;
    }
}

