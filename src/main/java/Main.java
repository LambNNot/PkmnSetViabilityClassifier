import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



public class Main {
    public static void main(String[] args) throws Exception {

        // 1. Load CSV
        CSVLoader.LoadedData data = CSVLoader.loadCSV("src/main/cleaneddata.csv");
        int[][] X = data.X;
        int[] y = data.y;

        // 2. Print total counts per class
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

        // 5. Train RandomForest
        RandomForest rf = new RandomForest(300, 20, 5);
        rf.fit(Xtrain, ytrain);

        // 6. Evaluate predictions
        int numClasses = CSVLoader.reverseLabelMap.size();
        int[][] confusion = new int[numClasses][numClasses];

        for (int i = 0; i < Xtest.length; i++) {
            int pred = rf.predict(Xtest[i]); // majority-vote prediction
            int actual = ytest[i];
            confusion[actual][pred]++;
        }

        // 7. Compute overall accuracy
        int correctTotal = 0;
        for (int i = 0; i < numClasses; i++) correctTotal += confusion[i][i];
        double accuracy = correctTotal / (double) Xtest.length;
        System.out.println("\nAccuracy: " + accuracy);

        // 8. Compute precision, recall, F1 for each class
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

        // 9. Sample majority-vote predictions
        System.out.println("\nSample majority-vote predictions:");
        for (int i = 0; i < Math.min(5, Xtest.length); i++) {
            int pred = rf.predict(Xtest[i]);
            int actual = ytest[i];
            System.out.println("Sample " + i + " -> Predicted: " 
                + CSVLoader.reverseLabelMap.get(pred) + ", Actual: " 
                + CSVLoader.reverseLabelMap.get(actual));
        }

        // 10. Training set distribution
        Map<Integer, Integer> countsTrain = new HashMap<>();
        for (int label : ytrain) countsTrain.put(label, countsTrain.getOrDefault(label, 0) + 1);
        System.out.println("\nTraining set distribution: " + countsTrain);
    }
}
