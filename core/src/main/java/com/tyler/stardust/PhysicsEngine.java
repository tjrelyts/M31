package com.tyler.stardust;

import java.util.List;

public class PhysicsEngine {
    
    private double gravitationalConstant;
    private static final double RESTITUTION = 0.8; // Coefficient of restitution (bounciness)
    private static final double ABSORPTION_RATE = 0.25; // 10% mass transfer per collision

    public void update(List<Particle> particleList, double deltaTime) {
        // First calculate forces and update positions
        for (Particle particle : particleList) {
            Vector2D netForce = calculateGravitationalForce(particle, particleList);
            updateAcceleration(particle, netForce);
            updateVelocity(particle, deltaTime);
            updatePosition(particle, deltaTime);
        }

        // Then handle collisions and absorption
        handleCollisions(particleList);
        
        // Remove particles with no mass
        particleList.removeIf(p -> p.getMass() <= 0);
    }

    private void handleCollisions(List<Particle> particleList) {
        for (int i = 0; i < particleList.size(); i++) {
            for (int j = i + 1; j < particleList.size(); j++) {
                Particle p1 = particleList.get(i);
                Particle p2 = particleList.get(j);

                double distance = p1.getPosition().distanceTo(p2.getPosition());
                double minDistance = p1.getRadius() + p2.getRadius();

                if (distance < minDistance) {
                    // Handle absorption before collision
                    handleAbsorption(p1, p2);
                    
                    // Only process collision if both particles still exist
                    if (p1.getMass() > 0 && p2.getMass() > 0) {
                        resolveCollision(p1, p2);

                        // Move particles apart to prevent sticking
                        double overlap = minDistance - distance;
                        Vector2D normal = p2.getPosition().subtract(p1.getPosition()).normalize();
                        
                        double totalMass = p1.getMass() + p2.getMass();
                        double p1Move = (overlap * p2.getMass()) / totalMass;
                        double p2Move = (overlap * p1.getMass()) / totalMass;

                        p1.getPosition().add(normal.scale(-p1Move));
                        p2.getPosition().add(normal.scale(p2Move));
                    }
                }
            }
        }
    }

    private void handleAbsorption(Particle p1, Particle p2) {
        Particle larger = p1.getMass() > p2.getMass() ? p1 : p2;
        Particle smaller = p1.getMass() > p2.getMass() ? p2 : p1;
        
        double massToTransfer = smaller.getMass() * ABSORPTION_RATE;
        larger.absorbMass(massToTransfer);
        smaller.loseMass(massToTransfer);
    }

    private void resolveCollision(Particle p1, Particle p2) {
        Vector2D normal = p2.getPosition().subtract(p1.getPosition()).normalize();
        
        // Relative velocity
        Vector2D relativeVel = new Vector2D(
            p2.getVelocity().x - p1.getVelocity().x,
            p2.getVelocity().y - p1.getVelocity().y
        );

        // Normal velocity component
        double normalVel = relativeVel.dot(normal);

        // Don't resolve if objects are moving apart
        if (normalVel > 0) return;

        // Calculate impulse
        double j = -(1 + RESTITUTION) * normalVel;
        j /= (1 / p1.getMass()) + (1 / p2.getMass());

        // Apply impulse
        Vector2D impulse = normal.scale(j);
        p1.setVelocity(new Vector2D(
            p1.getVelocity().x - (impulse.x / p1.getMass()),
            p1.getVelocity().y - (impulse.y / p1.getMass())
        ));
        
        p2.setVelocity(new Vector2D(
            p2.getVelocity().x + (impulse.x / p2.getMass()),
            p2.getVelocity().y + (impulse.y / p2.getMass())
        ));
    }

    public void setGravitationalConstant(double value) {
        this.gravitationalConstant = value;
    }

    private Vector2D calculateGravitationalForce(Particle currentParticle, List<Particle> particleList) {
        Vector2D totalForce = new Vector2D(0, 0);
        for (Particle otherParticle : particleList) {
            if (currentParticle.equals(otherParticle)) continue;
            
            double distance = otherParticle.getPosition().distanceTo(currentParticle.getPosition());
            double force = gravitationalConstant * currentParticle.getMass() * otherParticle.getMass() / Math.pow(distance, 2);
            Vector2D forceVector = otherParticle.getPosition().subtract(currentParticle.getPosition()).normalize();
            totalForce.add(force * forceVector.x, force * forceVector.y);
        }
        return totalForce;
    }

    private void updateAcceleration(Particle particle, Vector2D netForce) {
        Vector2D acceleration = new Vector2D(
            netForce.x / particle.getMass(),
            netForce.y / particle.getMass()
        );
        particle.setAcceleration(acceleration);
    }

    private void updateVelocity(Particle particle, double deltaTime) {
        Vector2D velocity = particle.getVelocity();
        Vector2D acceleration = particle.getAcceleration();
        velocity.add(
            acceleration.x * deltaTime,
            acceleration.y * deltaTime
        );
        particle.setVelocity(velocity);
    }

    private void updatePosition(Particle particle, double deltaTime) {
        Vector2D position = particle.getPosition();
        Vector2D velocity = particle.getVelocity();
        position.add(
            velocity.x * deltaTime,
            velocity.y * deltaTime
        );
        particle.setPosition(position);
    }
}
