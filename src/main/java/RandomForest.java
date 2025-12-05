import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomForest {
    private List<DecisionTreeNode> trees;
    private int numTrees;
    private int maxDepth;
    private int minSamplesSplit;
    private Random rand;

    public RandomForest(int numTrees, int maxDepth, int minSamplesSplit) {
        this.numTrees = numTrees;
        this.maxDepth = maxDepth;
        this.minSamplesSplit = minSamplesSplit;
        this.trees = new ArrayList<>();
        this.rand = new Random(42); // fixed seed for reproducibility
    }

    public void fit(int[][] X, int[] y) {
        int n = X.length;
        int numAttributes = X[0].length;

        for (int t = 0; t < numTrees; t++) {
            // Bootstrap sample
            int[][] XSample = new int[n][];
            int[] ySample = new int[n];
            for (int i = 0; i < n; i++) {
                int idx = rand.nextInt(n);
                XSample[i] = X[idx];
                ySample[i] = y[idx];
            }

            // Build tree
            DecisionTreeNode tree = buildTree(XSample, ySample, 0, numAttributes);
            trees.add(tree);
        }
    }

    public int predict(int[] instance) {
        Map<Integer, Integer> votes = new HashMap<>();
        for (DecisionTreeNode tree : trees) {
            int pred = tree.predict(instance);
            votes.put(pred, votes.getOrDefault(pred, 0) + 1);
        }
        // Return class with most votes
        return votes.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
    }

    private DecisionTreeNode buildTree(int[][] X, int[] y, int depth, int numAttributes) {
        if (allSameClass(y) || depth >= maxDepth || y.length < minSamplesSplit)
            return new DecisionTreeNode(majorityClass(y));

        // Random subset of attributes (sqrt rule)
        int m = (int) Math.ceil(Math.sqrt(numAttributes));
        List<Integer> attrs = new ArrayList<>();
        for (int i = 0; i < numAttributes; i++) attrs.add(i);
        Collections.shuffle(attrs, rand);
        List<Integer> subset = attrs.subList(0, m);

        // Pick best attribute by Gini
        int bestAttr = -1;
        double bestGini = Double.MAX_VALUE;
        for (int attr : subset) {
            double gini = giniForAttribute(X, y, attr);
            if (gini < bestGini) {
                bestGini = gini;
                bestAttr = attr;
            }
        }

        // Group by attribute value
        Map<Integer, List<Integer>> groups = new HashMap<>();
        for (int i = 0; i < X.length; i++)
            groups.computeIfAbsent(X[i][bestAttr], k -> new ArrayList<>()).add(i);

        Map<Integer, DecisionTreeNode> children = new HashMap<>();
        for (int val : groups.keySet()) {
            List<Integer> rows = groups.get(val);
            int[][] subX = new int[rows.size()][X[0].length];
            int[] subY = new int[rows.size()];
            for (int i = 0; i < rows.size(); i++) {
                int idx = rows.get(i);
                subX[i] = X[idx];
                subY[i] = y[idx];
            }
            children.put(val, buildTree(subX, subY, depth + 1, numAttributes));
        }

        return new DecisionTreeNode(bestAttr, children);
    }

    private boolean allSameClass(int[] y) {
        for (int i = 1; i < y.length; i++)
            if (y[i] != y[0]) return false;
        return true;
    }

    private int majorityClass(int[] y) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int v : y) counts.put(v, counts.getOrDefault(v, 0) + 1);
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();
    }

    private double giniForAttribute(int[][] X, int[] y, int attr) {
        Map<Integer, Map<Integer, Integer>> valueCounts = new HashMap<>();
        for (int i = 0; i < X.length; i++) {
            int val = X[i][attr];
            valueCounts.putIfAbsent(val, new HashMap<>());
            Map<Integer, Integer> counts = valueCounts.get(val);
            counts.put(y[i], counts.getOrDefault(y[i], 0) + 1);
        }

        double total = X.length;
        double gini = 0.0;
        for (Map<Integer, Integer> counts : valueCounts.values()) {
            int sum = counts.values().stream().mapToInt(Integer::intValue).sum();
            double score = 1.0;
            for (int c : counts.values()) {
                double p = (double) c / sum;
                score -= p * p;
            }
            gini += sum / total * score;
        }
        return gini;
    }
}
