package main.java.utils;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marek on 2014-11-15.
 *
 * That container gives possibility to fetch elements from given area in O(sqrt(n) + k),
 * where n is an count of elements and k is a count of results
 */
public class KdTree<T, A> {
    private final Contains<T, A> contains;
    private Tree<T, A> tree;
    ArrayList<Comparator<T>> c;

    public KdTree(List<T> content, ArrayList<Comparator<T>> comp, Contains<T, A> contains) throws KdTreeException {
        c = comp;
        this.contains = contains;

        if (content.size() > 0 && comp.size() > 0) {
            ArrayList<List<T>> sorted = new ArrayList<>();
            for (Comparator<T> aComp : comp) {
                ArrayList<T> s = new ArrayList<>(content);
                s.sort(aComp);
                sorted.add(s);
            }

            tree = buildTree(sorted, 0);
        }
    }

    public void addPoint(T point) {
        tree.addPoint(point);
    }
    public void rmPoint(T point) { tree.rmPoint(point); }
    public List<T> fetchElements(A area) {
        List<T> ret = new ArrayList<>();
        points = new ArrayList<>();
        ascending = new ArrayList<>();
        tree.getIntersection(area, ret);
        return ret;
    }

    public static class KdTreeException extends Throwable {

        public KdTreeException() {
            super("Two points with same coordinates occurred");
        }
    }
    public interface Contains<T, A> {

        boolean contains(A area, T point);
        boolean contains(A area, ArrayList<T> points);
        boolean intersects(A area, ArrayList<T> points, ArrayList<Boolean> ascending);
    }
    public static class CircleComparator implements Contains<Point2D, Circle> {

        @Override
        public boolean contains(Circle area, Point2D point) {
            return area.contains(point);
        }
        @Override
        public boolean contains(Circle area, ArrayList<Point2D> points) {
            Point2D A = points.get(0), B = points.get(1), C = points.get(2), D = points.get(3);
            Point2D[] corners = {new Point2D(A.getX(), B.getY()), new Point2D(B.getX(), C.getY()), new Point2D(C.getX(), D.getY()), new Point2D(D.getX(), A.getY())};
            for (Point2D corner : corners) {
                if (!area.contains(corner)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean intersects(Circle area, ArrayList<Point2D> points, ArrayList<Boolean> ascending) {
            ArrayList<Point2D> candidates = new ArrayList<>();
            ArrayList<Point2D> bad = new ArrayList<>();

            for (int j = 0; j < points.size(); j++) {
                for (Point2D candidate : candidates) {
                    if (!(j % 2 == 0 &&
                            (ascending.get(j) &&
                                    candidate.getX() > points.get(j).getX() ||
                                    candidate.getX() < points.get(j).getX()
                            ) ||
                            (ascending.get(j) &&
                                    candidate.getY() > points.get(j).getY() ||
                                    candidate.getY() < points.get(j).getY()
                            )))
                        bad.add(candidate);
                }

                double x = points.get(j).getX(), ox = area.getCenterX(), r = area.getRadius();
                double y = points.get(j).getY(), oy = area.getCenterY();
                if (j % 2 == 0) {
                    double dx = Math.abs(ox - x);
                    if (dx<=r) {
                        double dy = Math.sqrt(r * r - dx * dx);
                        candidates.add(new Point2D(x, area.getCenterY() + dy));
                        candidates.add(new Point2D(x, area.getCenterY() - dy));
                    } else if (ascending.get(j) && ox > x || ox < x) candidates.add(new Point2D(ox, oy));
                } else {
                    double dy = Math.abs(oy - y);
                    if (dy<=r) {
                        double dx = Math.sqrt(r * r - dy * dy);
                        candidates.add(new Point2D(area.getCenterX() + dx, y));
                        candidates.add(new Point2D(area.getCenterX() - dx, y));
                    } else if (ascending.get(j) && oy > y || oy < y) candidates.add(new Point2D(ox, oy));
                }
            }

            return candidates.size() > bad.size();
        }
    }
    public static class StdKd extends KdTree<Point2D, Circle> {
        StdKd(List<Point2D> l) throws KdTreeException {
            super(l, new ArrayList<>(planeComparator), new CircleComparator());
        }

        private static final List<Comparator<Point2D>> planeComparator = Arrays.asList(
                (Point2D o1, Point2D o2) -> {
                    int diff = (int) (o1.getX() - o2.getX());
                    return diff != 0 ? diff : (int)(o1.getY() - o2.getY());
                },

                (Point2D o1, Point2D o2) -> {
                    int diff = (int) (o1.getY() - o2.getY());
                    return diff != 0 ? diff : (int)(o1.getX() - o2.getX());
                }
        );
    }

    public static void main(String[] args) {
        try {
            StdKd t = new StdKd(Arrays.asList(new Point2D(10,10), new Point2D(-10, -10)));
            System.out.println(t.fetchElements(new Circle(14,14,30)));
            t.addPoint(Point2D.ZERO);
            System.out.println(t.fetchElements(new Circle(14, 14, 30)));
            t.addPoint(Point2D.ZERO);
            System.out.println(t.fetchElements(new Circle(14, 14, 30)));
            List<Point2D> res = t.fetchElements(new Circle(14, 14, 30));
            t.rmPoint(res.get(1));
            System.out.println(t.fetchElements(new Circle(14, 14, 30)));

        } catch (KdTreeException e) {
            e.printStackTrace();
        }
    }
//========================= P R I V A T E ==============================================================================
    private Tree<T, A> buildTree(ArrayList<List<T>> sorted, int depth) throws KdTreeException {
        int dimension = depth % sorted.size();

        if (sorted.get(0).size() == 1) {
            return new Leaf(sorted.get(0).get(0), depth);
        }

        List<T> a = sorted.get(dimension);
        int index = (int) Math.floor((a.size() - .5) / 2);
        T median = a.get(index);

        ArrayList<Map<Boolean, List<T>>> parts = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            final int j = dimension;
            parts.add(sorted.get(i).parallelStream().collect(
                    Collectors.partitioningBy((T t) -> c.get(j).compare(t, median) <= 0)
            ));
            if (parts.get(i).get(true).size() == sorted.get(i).size()) {
                throw new KdTreeException();
            }
        }

        ArrayList<List<T>> l = new ArrayList<>();
        ArrayList<List<T>> r = new ArrayList<>();

        for (Map<Boolean, List<T>> part : parts) {
            l.add(part.get(true));
            r.add(part.get(false));
        }

        return new Node(buildTree(l, depth + 1), buildTree(r, depth + 1), median, depth);
    }

    private interface Tree<T, A> {
        public void getIntersection(A area, List<T> ret);
        void pullValues(List<T> ret);
        Tree<T, A> addPoint(T point);
        Tree<T, A> rmPoint(T point);
        void decDepth();
    }

    private class Leaf implements Tree<T, A> {
        private final T content;
        private int depth;

        public Leaf(T t, int depth) {
            content = t;
            this.depth = depth;
        }

        @Override
        public void getIntersection(A area, List<T> ret) {
            if (contains.contains(area, content)) {
                ret.add(content);
            }
        }

        @Override
        public void pullValues(List<T> ret) {
            ret.add(content);
        }

        @Override
        public Tree<T, A> addPoint(T point) {
            int comparison = c.get(depth % c.size()).compare(point, content);
            if (comparison == 0) return this;

            ++depth;
            Leaf l, r = new Leaf(point, depth);
            if (comparison > 0) {
                l = this;
            } else {
                l = r;
                r = this;
            }
            return new Node(l, r, l.content, depth - 1);
        }

        @Override
        public Tree<T, A> rmPoint(T point) {
            int comparison = c.get(depth % c.size()).compare(point, content);

            if (comparison == 0) return null;
            else return this;
        }

        @Override
        public void decDepth() {
            depth--;
        }

    }

    private ArrayList<T> points = new ArrayList<>(); // awful workaround
    private ArrayList<Boolean> ascending = new ArrayList<>();

    private class Node implements Tree<T, A> {
        private Tree<T, A> left, right;
        private final T val;
        private int depth;

        public Node(Tree<T, A> left, Tree<T, A> right, T median, int depth) {
            this.left = left;
            this.right = right;
            val = median;
            this.depth = depth;
        }

        @Override
        public void getIntersection(A area, List<T> ret) {
            doThat(area, ret);
        }

        @Override
        public void pullValues(List<T> ret) {
            left.pullValues(ret);
            right.pullValues(ret);
        }

        @Override
        public Tree<T, A> addPoint(T point) {
            int comparison = c.get(depth % c.size()).compare(point, val);

            if (comparison < 0) {
                left = left.addPoint(point);
            } else if (comparison > 0){
                right = right.addPoint(point);
            }
            return this;
        }

        @Override
        public Tree<T, A> rmPoint(T point) {
            int comparison = c.get(depth % c.size()).compare(point, val);

            Tree<T, A> next = comparison <= 0 ? left : right, ret;
            ret = next.rmPoint(point);
            if (ret == null) {
                next = comparison <= 0 ? right : left;
                next.decDepth();
                return next;
            } else {
                if (comparison <= 0) {
                    left = ret;
                } else {
                    right = ret;
                }
            }
            return this;
        }

        @Override
        public void decDepth() {
            --depth;
        }

        private void doThat(A area, List<T> ret) {
            int part = depth % (c.size() * 2);
            if (points.size() <= part) {
                points.add(val);
                ascending.add(false);
            } else {
                points.set(part, val);
                ascending.set(part, false);
            }

            if (points.size() == c.size()*2 && contain(area, points, ascending)) {
                left.pullValues(ret);
            } else if (contains.intersects(area, points, ascending)) {
                left.getIntersection(area, ret);
            }

            ascending.set(part, true);
            if (points.size() == c.size()*2 && contain(area, points, ascending)) {
                right.pullValues(ret);
            } else if (contains.intersects(area, points, ascending)) {
                right.getIntersection(area, ret);
            }
        }

        private boolean contain(A area, ArrayList<T> points, ArrayList<Boolean> dirs) {
            int half = dirs.size() / 2;
            for (int i = 0; i < half; i++) {
                if (dirs.get(i) == dirs.get(i + half)) {
                    return false;
                }
            }

            return  contains.contains(area, points);
        }
    }
}
