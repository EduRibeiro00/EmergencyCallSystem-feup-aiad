package utils;

import java.util.concurrent.ThreadLocalRandom;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Point implements java.io.Serializable {
    private static final double MIN_COORDS_VALUE = 1.0;
    private static final double MAX_COORDS_VALUE = 190.0;
    private static final int PRECISION = 2;

    private double x;
    private double y;

    public static Point genRandomPoint() {
        return new Point(
            ThreadLocalRandom.current().nextDouble(MIN_COORDS_VALUE, MAX_COORDS_VALUE + 1),
            ThreadLocalRandom.current().nextDouble(MIN_COORDS_VALUE, MAX_COORDS_VALUE + 1)
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
