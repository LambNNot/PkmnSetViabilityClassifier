import java.util.HashMap;

public class DecisionTreeNode {
    private final boolean isLeaf;
    private int classValue; // use if isLeaf = true

    private int attribute; // use if decision node, split on this attribute
    private HashMap<Integer, DecisionTreeNode> children; // attribute value, list of children

    // leaf node
    public DecisionTreeNode(int classValue) {
        this.isLeaf = true;
        this.classValue = classValue;
        this.children = null;
    }

    // decision node
    public DecisionTreeNode(int attribute, HashMap<Integer, DecisionTreeNode> children) {
        this.isLeaf = false;
        this.attribute = attribute;
        this.children = children;
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
        //if leaf node return class
        if (isLeaf) {
            System.out.println("prediction: " + classValue);
            return classValue;
        }

        // else get attributes value
        int attributeValue = instance[attribute];

        // if there is a child with the value follow it
        if (children.containsKey(attributeValue)) {
            return children.get(attributeValue).predict(instance);
        }

        // if the value was never seen during training,
        // fall back to the most common child or majority class
        return fallbackClass();
    }

    private int fallbackClass() {
        // majority vote among children
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (DecisionTreeNode child : children.values()) {
            int c = child.classValue;
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }

        int best = -1;
        int bestCount = -1;
        for (int c : counts.keySet()) {
            if (counts.get(c) > bestCount) {
                bestCount = counts.get(c);
                best = c;
            }
        }
        return best;
    }

}

