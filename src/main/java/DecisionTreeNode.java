import java.util.HashMap;
import java.util.Map;

public class DecisionTreeNode {
    private final boolean isLeaf;
    private int classValue; // use if isLeaf = true
    private int attribute; // use if decision node
    private HashMap<Integer, DecisionTreeNode> children; // attribute value -> child

    // leaf node
    public DecisionTreeNode(int classValue) {
        this.isLeaf = true;
        this.classValue = classValue;
        this.children = null;
    }

    // decision node
    public DecisionTreeNode(int attribute, Map<Integer, DecisionTreeNode> children) {
        this.isLeaf = false;
        this.attribute = attribute;
        this.children = new HashMap<>(children);
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public int getClassValue() {
        return classValue;
    }

    public int getAttribute() {
        return attribute;
    }

    public HashMap<Integer, DecisionTreeNode> getChildren() {
        return children;
    }

    public int predict(int[] instance) {
        if (isLeaf) {
            return classValue;
        }
        int attributeValue = instance[attribute];
        if (children.containsKey(attributeValue)) {
            return children.get(attributeValue).predict(instance);
        }
        return fallbackClass();
    }

    private int fallbackClass() {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (DecisionTreeNode child : children.values()) {
            int c = child.classValue;
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }
        int best = -1, bestCount = -1;
        for (int c : counts.keySet()) {
            if (counts.get(c) > bestCount) {
                bestCount = counts.get(c);
                best = c;
            }
        }
        return best;
    }
}
