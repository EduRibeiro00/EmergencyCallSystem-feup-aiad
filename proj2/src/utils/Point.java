package utils;

import java.util.concurrent.ThreadLocalRandom;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Point implements java.io.Serializable {
    private static int MAX_COORDS_WIDTH = 100;
    private static int MAX_COORDS_HEIGHT = 100;
    private static final int PRECISION = 2;

    private double x;
    private double y;

    public static void setWidth(int widthValue) {
        MAX_COORDS_WIDTH = widthValue;
    }

    public static void setHeight(int heightValue) {
        MAX_COORDS_HEIGHT = heightValue;
    }

    public static Point genRandomPoint() {
        return new Point(
            ThreadLocalRandom.current().nextDouble(0.0, MAX_COORDS_WIDTH + 1),
            ThreadLocalRandom.current().nextDouble(0.0, MAX_COORDS_HEIGHT + 1)
        );
    }

    public Point(double x, double y) {
        this.x = roundDouble(x);
        this.y = roundDouble(y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getDistance(Point other) {
        double distance = Math.sqrt(
                Math.pow(this.getY() - other.getY(), 2)
                +
                Math.pow(this.getX() - other.getX(), 2)
        );
        return roundDouble(distance);
    }

    private static double roundDouble(double value) {
        return new BigDecimal(value).setScale(PRECISION, RoundingMode.HALF_EVEN).doubleValue();
    }

    @Override
    public String toString() {
        return "(" +
                "x=" + x +
                ", y=" + y +
                ')';
    }
}
