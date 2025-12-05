



public class TrainTestSplit {
    public int[][] Xtrain;
    public int[] ytrain;
    public int[][] Xtest;
    public int[] ytest;

    public TrainTestSplit(int[][] Xtrain, int[] ytrain,
                          int[][] Xtest, int[] ytest) {
        this.Xtrain = Xtrain;
        this.ytrain = ytrain;
        this.Xtest = Xtest;
        this.ytest = ytest;
    }
}




