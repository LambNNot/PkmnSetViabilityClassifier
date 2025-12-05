import java.util.Random;




public class DataSplitter {

    public static TrainTestSplit trainTestSplit(int[][] X, int[] y, double testRatio) {
        int n = X.length;

        int[] indices = new int[n];
        for (int i = 0; i < n; i++) indices[i] = i;

        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            int j = rand.nextInt(n);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }

        int testSize = (int)(n * testRatio);
        int trainSize = n - testSize;

        int[][] Xtrain = new int[trainSize][];
        int[] ytrain = new int[trainSize];
        int[][] Xtest = new int[testSize][];
        int[] ytest = new int[testSize];

        for (int i = 0; i < trainSize; i++) {
            Xtrain[i] = X[indices[i]];
            ytrain[i] = y[indices[i]];
        }

        for (int i = 0; i < testSize; i++) {
            Xtest[i] = X[indices[trainSize + i]];
            ytest[i] = y[indices[trainSize + i]];
        }

        return new TrainTestSplit(Xtrain, ytrain, Xtest, ytest);
    }
}

