import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVLoader {

    // String label → numeric class
    public static final Map<String, Integer> labelMap = Map.of(
        "ZU", 0,
        "PU", 1,
        "NU", 2,
        "RU", 3,
        "UU", 4,
        "OU", 5,
        "Uber", 6,
        "AG", 7
    );

    // Numeric class → String label (for printing predictions)
    public static final Map<Integer, String> reverseLabelMap = Map.of(
        0, "ZU",
        1, "PU",
        2, "NU",
        3, "RU",
        4, "UU",
        5, "OU",
        6, "Uber",
        7, "AG"
    );

    public static LoadedData loadCSV(String path) throws Exception {
        List<int[]> XList = new ArrayList<>();
        List<Integer> yList = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) continue;

            String[] tokens = line.split(",");

            int nFeatures = tokens.length - 1;
            int[] row = new int[nFeatures];

            // Convert feature columns → integers
            for (int i = 0; i < nFeatures; i++) {
                row[i] = Integer.parseInt(tokens[i]);
            }

            // Convert label column → numeric
            String labelStr = tokens[tokens.length - 1];
            if (!labelMap.containsKey(labelStr)) {
                throw new RuntimeException("Unknown label in CSV: " + labelStr);
            }
            int label = labelMap.get(labelStr);

            XList.add(row);
            yList.add(label);
        }
        br.close();

        int[][] X = XList.toArray(new int[0][]);
        int[] y = yList.stream().mapToInt(i -> i).toArray();

        return new LoadedData(X, y);
    }

    // Helper class to return both arrays
    public static class LoadedData {
        public int[][] X;
        public int[] y;

        public LoadedData(int[][] X, int[] y) {
            this.X = X;
            this.y = y;
        }
    }
}
