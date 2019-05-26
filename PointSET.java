import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {

    private final SET<Point2D> points;
    private Point2D closestNeighbour;

    // construct an empty set of points
    public PointSET() {
        points = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("You cannot pass a null argument");
        }
        if (!contains(
                p)) // if this points does not contain this point p, then add point p; though this check might not really be necessary
            points.add(p);
        if (closestNeighbour == null) {
            closestNeighbour = p;
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("You cannot pass a null argument");
        }
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D point : points) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius();
            point.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("You cannot pass a null argument");
        }
        if (points.size() == 0) return null;
        Queue<Point2D> pointsInRect = new Queue<>();
        for (Point2D point : points) {
            if (rect.contains(point)) {
                pointsInRect.enqueue(point);
            }
        }
        return pointsInRect;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("You cannot pass a null argument");
        }
        if (points.size() == 0) return null;
        double distance = p.distanceSquaredTo(closestNeighbour);
        double min = distance;
        for (Point2D point : points) {
            distance = p.distanceSquaredTo(point);
            if (distance < min) {
                closestNeighbour = point;
                min = distance;
            }
        }
        return closestNeighbour;
    }

    public static void main(String[] args) {

    }
}
