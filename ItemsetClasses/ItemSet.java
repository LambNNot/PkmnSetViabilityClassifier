
rule
package ItemsetClasses;
import java.util.*;

public class ItemSet {

    private final TreeSet<Integer> items;

    public ItemSet() {
        this.items = new TreeSet<>();
    }


    public ItemSet(List<Integer> items) {
        this.items = new TreeSet<>();
        this.items.addAll(items);
    }


    public static ItemSet parseItemSet(String str) {
        String[] split = str.split("[\\[\\], ]");
        List<Integer> items = Arrays.stream(split)
                .filter(s -> !s.isEmpty())
                .mapToInt(Integer::parseInt)
                .boxed().toList();
        return new ItemSet(items);
    }


    public ItemSet copy() {
        ItemSet copy = new ItemSet();
        copy.addAll(this);
        return copy;
    }


    public ItemSet union(ItemSet other) {
        ItemSet u = this.copy();
        u.addAll(other);
        return u;
    }

    public ItemSet withoutAny(ItemSet other) {
        ItemSet copy = this.copy();
        copy.removeAll(other);
        return copy;
    }


    public ArrayList<Integer> getItems() {
        return new ArrayList<>(items);
    }


    public TreeSet<Integer> getItemSet() {
        return items;
    }


    public boolean contains(Integer item) {
        return items.contains(item);
    }


    public boolean contains(ItemSet other) {
        return items.containsAll(other.items);
    }

    public int size() {
        return items.size();
    }


    public void addItem(Integer item) {
        items.add(item);
    }


    public void addAll(ItemSet other) {
        items.addAll(other.items);
    }


    public void removeItem(Integer item) {
        items.remove(item);
    }

    public void removeAll(ItemSet other) {
        items.removeAll(other.items);
    }


    public ArrayList<ItemSet> subsets() {
        ArrayList<ItemSet> subsets = new ArrayList<>();
        for (int s = 0; s < items.size(); s++) {
            subsets.addAll(this.subsetsOfSize(s));
        }
        return subsets;
    }



    public ArrayList<ItemSet> subsetsOfSize(int size) {
        if (size == 0) {
            return new ArrayList<>();
        }
        ArrayList<ItemSet> subsets = new ArrayList<>();
        if (size == 1) {
            for (Integer item : items) {
                ItemSet singleton = new ItemSet(Collections.singletonList(item));
                subsets.add(singleton);
            }
            return subsets;
        }
        ItemSet copy = this.copy();
        for (Integer item : items) {
            copy.removeItem(item);
            for (ItemSet subset : copy.subsetsOfSize(size - 1)) {
                ItemSet newSubset = subset.copy();
                newSubset.addItem(item);
                subsets.add(newSubset);
            }
        }
        return subsets;
    }

    @Override
    public String toString() {
        return Arrays.toString(items.toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemSet itemSet)) return false;
        return Objects.equals(items, itemSet.items);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getItems());
    }

}