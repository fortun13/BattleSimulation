package main.java.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Marek on 2014-11-15.
 *
 * That container gives possibility to fetch elements from given area
 */
public class KdTree<T, A> {
    private Tree tree;
    Comparator<T> vComp, hComp;
    ArrayList<Comparator<T>> comp;

    public KdTree(List<T> content, ArrayList<Comparator<T>> comp, Contains<Object, Object> con) throws KdTreeException {
        this.comp = comp;

        if (content.size() > 0 && comp.size() > 0) {
            ArrayList<T>[] sorted = new ArrayList[comp.size()];
            for (int i = 0; i < sorted.length; ++i) {
                sorted[i] = new ArrayList<>(content);
                sorted[i].sort(comp.get(i));
            }

            tree = buildTree(sorted, 0);
        }
    }

//    public KdTree(List<T> content, Comparator<T> vComp, Comparator<T> hComp) throws KdTreeException {
//        this.vComp = vComp; this.hComp = hComp;
//
//        ArrayList<T> vSorted = new ArrayList<>(content), hSorted = new ArrayList<>(content);
//
//        if (content.size() > 0) {
//            vSorted.sort(vComp);
//            hSorted.sort(hComp);
//
//            tree = buildTree(vSorted, hSorted, 0);
//        }
//    }

    public List<T> fetchElements(A area) {
        List<T> ret = new ArrayList<>();

        tree.getIntersection(area, ret);
        return ret;
    }

    private Tree buildTree(ArrayList<T>[] sorted, int deph) throws KdTreeException {
        if (sorted[0].size() == 1) {
            return new Leaf(sorted[0].get(0));
        }

        ArrayList<T> a = sorted[deph % sorted.length];
        T median = a.get((int) Math.floor((a.size() - .5)/2));

        Map<Boolean, List<T>>[] parts = new Map[sorted.length];
        for (int i = 0; i < sorted.length; i++) {
            final int j = i;
            parts[i] = sorted[i].parallelStream().collect(
                    Collectors.partitioningBy((T t) -> comp.get(j).compare(t, median) <= 0)
            );
            if (parts[i].get(true).size() == sorted.length) {
                throw new KdTreeException();
            }
        }

        Tree<T, A>[] children = new Tree[sorted.length];
        for (Tree<T, A> child : children) {
            child = buildTree(sorted, deph + 1);
        }

        return new Node(children, median);
    }

//    private Tree buildTree(List<T> vSorted, List<T> hSorted, int depth) throws KdTreeException {
//        if (vSorted.size() == 1) {
//            return new Leaf(vSorted.get(0));
//        }
//
//        T median;
//        Comparator<T> c;
//        if (depth % 2 == 0) {
//            median = vSorted.get((int) Math.floor((vSorted.size() - 0.5)/2));
//            c = vComp;
//        } else {
//            median = hSorted.get((int) Math.floor((hSorted.size() - 0.5)/2));
//            c = hComp;
//        }
//
//
//        Map<Boolean, List<T>> vParts = vSorted.parallelStream().collect(
//                Collectors.partitioningBy((T t) -> c.compare(t, median) <= 0)
//        );
//        Map<Boolean, List<T>> hParts = hSorted.parallelStream().collect(
//                Collectors.partitioningBy((T t) -> c.compare(t, median) <= 0)
//        );
//
//        if (vSorted.size() == vParts.get(true).size() || vSorted.size() == vParts.get(false).size())
//            throw new KdTreeException();
//
//        return new Node(
//                buildTree(vParts.get(true), hParts.get(true), depth + 1),
//                buildTree(vParts.get(false), hParts.get(false), depth + 1),
//                median
//        );
//    }

    private interface Tree<T, A> {
        boolean leaf();

        void getIntersection(A area, List ret);
    }

    private class Leaf<T, A> implements Tree<T, A> {
        private final T content;

        public Leaf(T t) {
            content = t;
        }

        @Override
        public boolean leaf() {
            return true;
        }

        @Override
        public void getIntersection(A area, List ret) {
//            if (area.contains(content)) {
//                ret.add(content);
//            }
        }
    }

    private class Node<T, A> implements Tree<T, A> {
        private final Tree<T, A>[] children;
//        private final Tree<T, A> left, right;
//        private final T val;
//
//        public Node(Tree<T, A> left, Tree<T, A> right, T val) {
//            this.left = left;
//            this.right = right;
//            this.val = val;
//        }

        public Node(Tree<T, A>[] children, T median) {

            this.children = children;
        }

        @Override
        public boolean leaf() {
            return false;
        }

        @Override
        public void getIntersection(A area, List ret) {

        }
    }

    public static void main(String[] args) {
        for (int i = 20; i < Integer.parseInt(args[0]); i++) {
            List<Point> l = new ArrayList<>();

            System.out.println("Dla i=" + i);

            for (int j = 0; j < i; j++) {
                l.add(new Point((int) (Math.random() * 10), (int) (Math.random() * 10)));
            }
//            l.add(new Point(1,1));
//            l.add(new Point(1,1));

            ArrayList<Comparator<Point>> a = new ArrayList<>();
            a.add((Point o1, Point o2) -> {
                int diff = o1.x - o2.x;
                return diff != 0 ? diff : o1.y - o2.y;
            });
            a.add((Point o1, Point o2) -> {
                int diff = o1.y - o2.y;
                return diff != 0 ? diff : o1.x - o2.x;
            });
//                    (Point o1, Point o2) -> {
//                int diff = o1.x - o2.x;
//                return diff != 0 ? diff : o1.y - o2.y;
//            },
//                    (Point o1, Point o2) -> {
//                        int diff = o1.y - o2.y;
//                        return diff != 0 ? diff : o1.x - o2.x;
//                    });
//            Comparator<Point>[] a = {(Point as, Point b) -> -1};

            try {
                new KdTree<>(l, a, (area,point) -> true);
            } catch (KdTreeException e) {
                e.printStackTrace();
            }
        }
    }

    public static class KdTreeException extends Throwable {
        public KdTreeException() {
            super("Two points with same coordinates occurred");
        }
    }

    private interface Contains<T, A> {
        boolean contains(A area, T point);
    }
}
