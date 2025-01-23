package com.tyler.stardust;

import com.badlogic.gdx.graphics.Color;

public class Particle {
    
    private double mass;
    private Vector2D position;
    private Vector2D velocity;
    private Vector2D acceleration;

    private double radius;
    private Color bodyColor;

    private static final double RADIUS_SCALE_FACTOR = 2;

    public Particle(Builder builder) {
        this.mass = builder.mass;
        this.position = builder.position;
        this.velocity = builder.velocity;
        this.acceleration = builder.acceleration;
        this.radius = calculateRadiusFromMass(builder.mass);
        this.bodyColor = builder.bodyColor;
    }

    private double calculateRadiusFromMass(double mass) {
        return RADIUS_SCALE_FACTOR * Math.cbrt(mass);
    }

    public static class Builder {

        private double mass;
        private Vector2D position;

        private Color bodyColor = Color.WHITE;
        private Vector2D velocity = new Vector2D(0, 0);
        private Vector2D acceleration = new Vector2D(0, 0);

        public Builder(double mass, Vector2D position) {
            this.mass = mass;
            this.position = position;
        }
        
        public Builder velocity(Vector2D velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder acceleration(Vector2D acceleration) {
            this.acceleration = acceleration;
            return this;
        }
        
        public Builder bodyColor(Color bodyColor) {
            this.bodyColor = bodyColor;
            return this;
        }
        
        public Particle build() {
            return new Particle(this);
        }
    }

    public double getMass() { return mass; }
    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public Vector2D getAcceleration() { return acceleration; }
    public double getRadius() { return radius; }
    public Color getBodyColor() { return bodyColor; }

    public void setPosition(Vector2D position) { this.position = position; }
    public void setVelocity(Vector2D velocity) { this.velocity = velocity; }
    public void setAcceleration(Vector2D acceleration) { this.acceleration = acceleration; }

    public void absorbMass(double amount) {
        this.mass += amount;
        this.radius = calculateRadiusFromMass(this.mass);
    }

    public void loseMass(double amount) {
        this.mass = Math.max(0, this.mass - amount);
        this.radius = calculateRadiusFromMass(this.mass);
    }
}
