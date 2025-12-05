import java.util.*;
import java.util.Map.Entry;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. Load CSV
        CSVLoader.LoadedData data = CSVLoader.loadCSV("src/main/cleaneddata.csv");
        int[][] X = data.X;
        int[] y = data.y;

        // 2. Print class distribution
        Map<Integer, Integer> countsTotal = new HashMap<>();
        for (int label : y) countsTotal.put(label, countsTotal.getOrDefault(label, 0) + 1);
        System.out.println("Class distribution: " + countsTotal);

        // 3. Shuffle data
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < X.length; i++) indices.add(i);
        Collections.shuffle(indices, new Random(42));

        int[][] Xshuffled = new int[X.length][];
        int[] yshuffled = new int[y.length];
        for (int i = 0; i < X.length; i++) {
            Xshuffled[i] = X[indices.get(i)];
            yshuffled[i] = y[indices.get(i)];
        }

        // 4. Split 80% train / 20% test
        int trainSize = (int) (X.length * 0.8);
        int[][] Xtrain = Arrays.copyOfRange(Xshuffled, 0, trainSize);
        int[] ytrain = Arrays.copyOfRange(yshuffled, 0, trainSize);
        int[][] Xtest = Arrays.copyOfRange(Xshuffled, trainSize, X.length);
        int[] ytest = Arrays.copyOfRange(yshuffled, trainSize, y.length);

        // 5. 10-fold CV to select best number of trees
        int[] treeOptions = {50, 100, 200, 300};
        int kFolds = 10, maxDepth = 20, minSamplesSplit = 5;
        Map<Integer, Double> avgAccuracy = new HashMap<>();

        // Generate shuffled indices for CV on training set
        List<Integer> trainIndices = new ArrayList<>();
        for (int i = 0; i < Xtrain.length; i++) trainIndices.add(i);
        Collections.shuffle(trainIndices, new Random(42));

        int foldSize = Xtrain.length / kFolds;

        for (int nTrees : treeOptions) {
            double totalAcc = 0.0;

            for (int fold = 0; fold < kFolds; fold++) {
                List<Integer> testIdx = trainIndices.subList(fold * foldSize,
                        fold == kFolds - 1 ? Xtrain.length : (fold + 1) * foldSize);
                List<Integer> cvTrainIdx = new ArrayList<>(trainIndices);
                cvTrainIdx.removeAll(testIdx);

                int[][] XcvTrain = new int[cvTrainIdx.size()][Xtrain[0].length];
                int[] ycvTrain = new int[cvTrainIdx.size()];
                int[][] XcvTest = new int[testIdx.size()][Xtrain[0].length];
                int[] ycvTest = new int[testIdx.size()];

                for (int i = 0; i < cvTrainIdx.size(); i++) {
                    XcvTrain[i] = Xtrain[cvTrainIdx.get(i)];
                    ycvTrain[i] = ytrain[cvTrainIdx.get(i)];
                }
                for (int i = 0; i < testIdx.size(); i++) {
                    XcvTest[i] = Xtrain[testIdx.get(i)];
                    ycvTest[i] = ytrain[testIdx.get(i)];
                }

                RandomForest rf = new RandomForest(nTrees, maxDepth, minSamplesSplit);
                rf.fit(XcvTrain, ycvTrain);

                int correct = 0;
                for (int i = 0; i < XcvTest.length; i++)
                    if (rf.predict(XcvTest[i]) == ycvTest[i]) correct++;

                totalAcc += correct / (double) XcvTest.length;
            }

            avgAccuracy.put(nTrees, totalAcc / kFolds);
            System.out.printf("Trees: %d | 10-fold CV Accuracy: %.4f%n", nTrees, avgAccuracy.get(nTrees));
        }

        // Best number of trees
        int bestTrees = avgAccuracy.entrySet().stream()
                .max(Entry.comparingByValue())
                .get()
                .getKey();
        System.out.println("\nBest number of trees: " + bestTrees);

        // 6. Train final RandomForest on all training data
        RandomForest finalRF = new RandomForest(bestTrees, maxDepth, minSamplesSplit);
        finalRF.fit(Xtrain, ytrain);
        System.out.println("Final model trained on all training data.");

        // 7. Evaluate on test set
        int numClasses = CSVLoader.reverseLabelMap.size();
        int[][] confusion = new int[numClasses][numClasses];

        for (int i = 0; i < Xtest.length; i++) {
            int pred = finalRF.predict(Xtest[i]);
            int actual = ytest[i];
            confusion[actual][pred]++;
        }

        int correctTotal = 0;
        for (int i = 0; i < numClasses; i++) correctTotal += confusion[i][i];
        double accuracy = correctTotal / (double) Xtest.length;
        System.out.println("\nTest Accuracy: " + accuracy);

        System.out.println("\nPrecision, Recall, F1 per class:");
        for (int c = 0; c < numClasses; c++) {
            int tp = confusion[c][c];
            int fp = 0, fn = 0;
            for (int i = 0; i < numClasses; i++) {
                if (i != c) {
                    fp += confusion[i][c];
                    fn += confusion[c][i];
                }
            }
            double precision = tp + fp == 0 ? 0 : tp / (double)(tp + fp);
            double recall = tp + fn == 0 ? 0 : tp / (double)(tp + fn);
            double f1 = precision + recall == 0 ? 0 : 2 * precision * recall / (precision + recall);
            System.out.printf("%s -> Precision: %.3f, Recall: %.3f, F1: %.3f%n",
                    CSVLoader.reverseLabelMap.get(c), precision, recall, f1);
        }

        // 8. Sample predictions
        System.out.println("\nSample test set predictions:");
        for (int i = 0; i < Math.min(5, Xtest.length); i++) {
            int pred = finalRF.predict(Xtest[i]);
            int actual = ytest[i];
            System.out.println("Sample " + i + " -> Predicted: "
                    + CSVLoader.reverseLabelMap.get(pred) + ", Actual: "
                    + CSVLoader.reverseLabelMap.get(actual));
        }
    }

    // Helper method to predict a single Pokémon set
    public static String predictSet(RandomForest rf, int[] features) {
        int pred = rf.predict(features);
        return CSVLoader.reverseLabelMap.get(pred);
    }
}
