package com.tyler.stardust;

import java.util.List;

public class PhysicsEngine {
    
    private static final double GRAVITATIONAL_CONSTANT = 6.67430e-11;

    public void update(List<Particle> particleList, double deltaTime) {
        for (Particle particle : particleList) {
            Vector2D netForce = calculateGravitationalForce(particle, particleList);
            updateAcceleration(particle, netForce);
            updateVelocity(particle, deltaTime);
            updatePosition(particle, deltaTime);
        }
    }

    private Vector2D calculateGravitationalForce(Particle currentParticle, List<Particle> particleList) {
        Vector2D totalForce = new Vector2D(0, 0);
        for (Particle otherParticle : particleList) {
            if (currentParticle.equals(otherParticle)) continue;
            
            double distance = otherParticle.getPosition().distanceTo(currentParticle.getPosition());
            double force = GRAVITATIONAL_CONSTANT * currentParticle.getMass() * otherParticle.getMass() / Math.pow(distance, 2);
            Vector2D forceVector = otherParticle.getPosition().subtract(currentParticle.getPosition()).normalize();
            totalForce.add(force * forceVector.x, force * forceVector.y);
        }
        return totalForce;
    }

    private void updateAcceleration(Particle particle, Vector2D netForce) {
        // F = ma -> a = F/m
        Vector2D acceleration = new Vector2D(
            netForce.x / particle.getMass(),
            netForce.y / particle.getMass()
        );
        particle.setAcceleration(acceleration);
    }

    private void updateVelocity(Particle particle, double deltaTime) {
        // Update velocity: v = v0 + at
        Vector2D velocity = particle.getVelocity();
        Vector2D acceleration = particle.getAcceleration();
        velocity.add(
            acceleration.x * deltaTime,
            acceleration.y * deltaTime
        );
        particle.setVelocity(velocity);
    }

    private void updatePosition(Particle particle, double deltaTime) {
        // Update position: p = p0 + vt
        Vector2D position = particle.getPosition();
        Vector2D velocity = particle.getVelocity();
        position.add(
            velocity.x * deltaTime,
            velocity.y * deltaTime
        );
        particle.setPosition(position);
    }
}
