import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class testLab7 {
    private DecisionTreeNode tree;

    @BeforeEach
    void setUp() {
        String file = "src/test/resources/files/asgn4.csv";
        int[][] data = Lab7.process(file);  // <-- static call

        ArrayList<Integer> attributes = new ArrayList<>();
        int attributeSize = data[0].length - 1;

        for (int i = 0; i < attributeSize; i++) {
            attributes.add(i);
        }

        ArrayList<Integer> rows = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            rows.add(i);
        }

        tree = Lab7.buildDecisionTree(new Matrix(data), attributes, rows);
    }

    @Test
    void row3_shouldFail() {
        // Row 3 from your table:
        // Labs = Some(1), Assignments = Poor(0), Final = Poor(0), Project = Good(2)
        int[] test = {1,0,0,2};
        int result = tree.predict(test);
        assertEquals(0, result); // 0 = Fail
    }

    @Test
    void row4_shouldPass() {
        // Labs = All(2), Assignments = Good(2), Final = Good(2), Project = Good(2)
        int[] test = {2, 2, 2, 2};
        int result = tree.predict(test);
        assertEquals(1, result); // 1 = Pass
    }

    @Test
    void finalGood_alwaysPasses() {
        // Any case where Final = 2 (Good) should pass
        int[] test = {1, 0, 2, 0};

        int result = tree.predict(test);
        assertEquals(1, result);
    }
}
