import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree{
    private Node root;
    private final Queue<Point2D> queuePoints = new Queue<>();
    private double nearestNeighbour;
    private Point2D nearestNeighbourPoint;


    // construct an empty set of points
    public KdTree() {
    }

    // is the set empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the set
    public int size() {
        return size(root);
    }

    private int size(Node n) {
        if (n == null) return 0;
        else return n.count;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("You cannot pass a null argument");
        }
        root = insert(root, p, true, new RectHV(0, 0, 1, 1));
    }

    private Node insert(Node n, Point2D p, boolean toCompareNodeUsingXCoordinate,
                        RectHV rect) {
        if (!contains(p)) { // if the set of nodes doesn't contain this point then insert it
            if (n == null)
                return new Node(p, toCompareNodeUsingXCoordinate, 1,
                                rect); // TODO: find another way
            if (n.toCompareNodeUsingXCoordinate) {
                double compare = p.x() - n.point.x();
                // TODO: how to change from x cordinate to y cordinate
                if (compare < 0) {
                    RectHV rectN = new RectHV(n.rect.xmin(), n.rect.ymin(), n.point.x(),
                                              n.rect.ymax());
                    n.left = insert(n.left, p,
                                    !n.toCompareNodeUsingXCoordinate, rectN);
                }
                else {
                    RectHV rectN = new RectHV(n.point.x(), n.rect.ymin(), n.rect.xmax(),
                                              n.rect.ymax());
                    n.right = insert(n.right, p, !n.toCompareNodeUsingXCoordinate, rectN);
                }
            }
            else {
                double compare = p.y() - n.point.y();
                // TODO: how to change from x cordinate to y cordinate
                if (compare < 0) {
                    RectHV rectN = new RectHV(n.rect.xmin(), n.rect.ymin(), n.rect.xmax(),
                                              n.point.y());
                    n.left = insert(n.left, p, !n.toCompareNodeUsingXCoordinate, rectN);
                }
                else {
                    RectHV rectN = new RectHV(n.rect.xmin(), n.point.y(), n.rect.xmax(),
                                              n.rect.ymax());
                    n.right = insert(n.right, p, !n.toCompareNodeUsingXCoordinate, rectN);
                }
            }
            n.count = 1 + size(n.left) + size(n.right);
        }

        if (nearestNeighbourPoint == null) nearestNeighbourPoint = p;
        return n;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("You cannot pass a null argument");
        }
        Node n = root;

        while (n != null) {
            if (n.toCompareNodeUsingXCoordinate) {
                if (p.x() < n.point.x()) n = n.left;
                else if (p.x() > n.point.x()) n = n.right;
                else if (p.y() == n.point.y()) return true;
                else n = n.right;
            }
            else {
                if (p.y() < n.point.y()) n = n.left;
                else if (p.y() > n.point.y()) n = n.right;
                else if (p.x() == n.point.x()) return true;
                else n = n.right;
            }
        }
        return false;
    }

    // draw all points to standard draw
    public void draw() {
        Node n = root;
        draw(n);
    }

    private void draw(Node n) {
        if (n == null) return;
        draw(n.left);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        n.point.draw();
        StdDraw.setPenRadius();
        if (n.toCompareNodeUsingXCoordinate) {
            StdDraw.setPenColor(StdDraw.RED);
            n.rect.draw();
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            n.rect.draw();
        }
        draw(n.right);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("You cannot pass a null argument");
        }
        Node n = root;
        range(rect, n);
        return queuePoints;
    }

    private void range(RectHV rect, Node n) {
        if (n == null) return;
        if (rect.contains(n.point)) queuePoints.enqueue(n.point);
        if (n.toCompareNodeUsingXCoordinate) {
            if (rect.xmin() < n.point.x()) {
                range(rect, n.left);
            }
            if (rect.xmax() >= n.point.x()) {
                range(rect, n.right);
            }
        }

        else {
            if (rect.ymin() < n.point.y()) {
                range(rect, n.left);
            }
            if (rect.ymax() >= n.point.y()) {
                range(rect, n.right);
            }
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("You cannot pass a null argument");
        }
        Node n = root;
        if (n == null) return null;
        nearestNeighbour = p.distanceSquaredTo(n.point);
        nearestNeighbourPoint = n.point;
        nearest(p, n);
        return nearestNeighbourPoint;
    }

    private void nearest(Point2D p, Node n) {
        if (n == null) return;
        double distance = p.distanceSquaredTo(n.point);
        if (distance < nearestNeighbour) {
            nearestNeighbour = distance;
            nearestNeighbourPoint = n.point;
        }
        // dividing using the x coordinate
        if (n.toCompareNodeUsingXCoordinate) {
            // is the queryPoint to the left of the current point in the set
            if (p.x() < n.point.x()) {
                // we are certainly going to examine all the points to the left
                nearest(p, n.left);
                // is the current minimum distance less than the distance from
                // the query point to the left border of the rectangle formed
                // using the current point being examined in the set. If it is
                // not less than, then we need to also examine the points on the
                // right
                if (n.right != null && n.right.rect.distanceSquaredTo(p) < distance) {
                    nearest(p, n.right);
                }
            }
            else { // if the query point is to the right of the current point in the set
                nearest(p, n.right);
                if (n.left != null && n.left.rect.distanceSquaredTo(p) < distance) {
                    nearest(p, n.left);
                }
            }
        }
        else {
            if (p.y() < n.point.y()) {
                nearest(p, n.left);
                if (n.right != null && n.right.rect.distanceSquaredTo(p) < distance) {
                    nearest(p, n.right);
                }
            }
            else {
                nearest(p, n.right);
                if (n.left != null && n.left.rect.distanceSquaredTo(p) < distance) {
                    nearest(p, n.left);
                }
            }
        }
    }

    public static void main(String[] args) {

    }

    private class Node {
        private final Point2D point;
        private Node left, right;
        private final RectHV rect;
        private final boolean toCompareNodeUsingXCoordinate;
        private int count;

        public Node(Point2D point, Boolean toCompareNodeUsingXCoordinate, int count, RectHV rect) {
            this.point = point;
            this.toCompareNodeUsingXCoordinate = toCompareNodeUsingXCoordinate;
            this.count = count;
            this.rect = rect;
        }


    }
}
