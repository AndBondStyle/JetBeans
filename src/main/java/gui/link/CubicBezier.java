package gui.link;

import java.util.ArrayList;
import java.awt.*;

public class CubicBezier {
    public static double eval(double a, double b, double c, double d, double t) {
        double tt = (1f - t);
        return a * (tt * tt * tt) + 3f * b * t * (tt * tt) + 3f * c * (t * t) * tt + d * (t * t * t);
    }

    public static Point eval(Point a, Point b, Point c, Point d, double t) {
        return new Point(
                (int) eval(a.x, b.x, c.x, d.x, t),
                (int) eval(a.y, b.y, c.y, d.y, t)
        );
    }

    public static double getStepSize(Point a, Point b, Point c, Point d) {
        Point p1 = eval(a, b, c, d, 0.1f);
        Point p2 = eval(a, b, c, d, 0.3f);
        Point p3 = eval(a, b, c, d, 0.7f);
        Point p4 = eval(a, b, c, d, 0.9f);
        double dist = a.distance(p1) + p1.distance(p2) + p2.distance(p3) + p3.distance(p4) + p4.distance(d);
        return 1 / dist * 5;  // <- adjust this to balance speed/accuracy
    }

    public static Point[] getPoints(Point a, Point b, Point c, Point d) {
        double step = getStepSize(a, b, c, d);
        ArrayList<Point> points = new ArrayList<>();
        for (double t = 0; t <= 1; t += step) points.add(eval(a, b, c, d, t));
        return points.toArray(Point[]::new);
    }
}
