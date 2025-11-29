package ItemsetClasses;

public record Rule(ItemSet left, ItemSet right) {

    public static Rule parseRule(String str) {
        String[] split = str.split("->");
        ItemSet left = ItemSet.parseItemSet(split[0]);
        ItemSet right = ItemSet.parseItemSet(split[1]);
        return new Rule(left, right);
    }

    public ItemSet getUnion() {
        return left().union(right());
    }

    public String toString() {
        return left.toString() + "->" + right.toString();
    }

}