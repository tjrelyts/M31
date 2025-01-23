package com.tyler.stardust;

public class Vector2D {

    public double x;
    public double y;

    public Vector2D() { }
    
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other) {
        return this.add(other.x, other.y);
    }

    public Vector2D add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public double calculateMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public double distanceTo(Vector2D other) {
        return Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));
    }

    public Vector2D normalize() {
        double length = calculateMagnitude();
        if (length == 0) {
            System.out.println("Cannot normalize a zero vector.");
            return this;
        }
        return scale(1.0 / length);
    }

    public Vector2D scale(double scalar) {
        return new Vector2D(this.x * scalar, this.y * scalar);
    }

    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    public double dot(Vector2D other) {
        return this.x * other.x + this.y * other.y;
    }
}
