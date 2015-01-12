import java.util.ArrayList;

import processing.core.*;

public class Boid {
	PApplet parent; // The parent PApplet that we will render ourselves onto

	PVector location;
	PVector velocity;
	PVector acceleration;

	float r; // boid size
	float maxforce; // Maximum steering force
	float maxspeed; // Maximum speed
	float life; // boids die!
	int boidColor;

	Boid(PApplet p, float x, float y, float lfe, float cnt, int clr) {
		parent = p;
		acceleration = new PVector(0, 0);

		// This is a new PVector method not yet implemented in JS
		// velocity = PVector.random2D();

		// Leaving the code temporarily this way so that this example runs in JS
		// float angle = parent.random(PConstants.TWO_PI);
		velocity = new PVector(PApplet.cos(cnt * PConstants.TWO_PI),
				PApplet.sin(cnt * PConstants.TWO_PI));

		location = new PVector(x, y);
		r = 1.0f;
		maxspeed = 5;
		maxforce = 0.03f;
		life = lfe * 40;
		boidColor = clr;
	}

	void run(ArrayList<Boid> boids) {
		flock(boids, null, null);
		update();
		borders();
		render();
	}

	void runMouse(ArrayList<Boid> boids, PVector mouse) {
		flock(boids, mouse, null);
		update();
		borders();
		render();
	}

	// run the boid based on ?local? grid attractions
	void runGrid(ArrayList<Boid> boids, Point[] grid) {
		flock(boids, null, grid);
		update();
		borders();
		render();
	}

	void applyForce(PVector force) {
		// We could add mass here if we want A = F / M
		acceleration.add(force);
	}

	// We accumulate a new acceleration each time based on three rules
	void flock(ArrayList<Boid> boids, PVector mouse, Point[] grid) {
		PVector sep = separate(boids); // Separation
		PVector ali = align(boids); // Alignment
		PVector coh = cohesion(boids); // Cohesion
		if (mouse != null) {
			PVector att = attraction(mouse); // Attraction by mouse
			att.mult(1.0f);
			applyForce(att);
		}
		if (grid != null) {
			PVector gridForce = gridAvoider(grid); // gridAvgAttractor(grid);
			gridForce.mult(10.0f);
			applyForce(gridForce);
		}
		// Arbitrarily weight these forces
		sep.mult(1.5f);
		ali.mult(1.0f);
		coh.mult(1.0f);

		// Add the force vectors to acceleration
		applyForce(sep);
		applyForce(ali);
		applyForce(coh);

	}

	// Method to update location
	void update() {
		// Update velocity
		velocity.add(acceleration);

		// Limit speed
		velocity.limit(maxspeed);
		location.add(velocity);
		// Reset accelertion to 0 each cycle
		acceleration.mult(0);
	}

	// A method that calculates and applies a steering force towards a target
	// STEER = DESIRED MINUS VELOCITY
	PVector seek(PVector target) {
		PVector desired = PVector.sub(target, location); // A vector pointing
															// from the location
															// to the target
		// Scale to maximum speed
		desired.normalize();
		desired.mult(maxspeed);

		// Above two lines of code below could be condensed with new PVector
		// setMag() method
		// Not using this method until Processing.js catches up
		// desired.setMag(maxspeed);

		// Steering = Desired minus Velocity
		PVector steer = PVector.sub(desired, velocity);
		steer.limit(maxforce); // Limit to maximum steering force
		return steer;
	}

	void render() {
		// Draw a triangle rotated in the direction of velocity
		float theta = velocity.heading() + PApplet.radians(90);
		// heading2D() above is now heading() but leaving old syntax until
		// Processing.js catches up
		
		boidColor= (int) PApplet.degrees(theta);
		int B = 80+ (int) (velocity.mag() / maxspeed * 20);
		
		parent.fill(boidColor, 70,B,80);
		parent.stroke(boidColor,90,B,90);
		
		parent.pushMatrix();
		parent.translate(location.x, location.y);
		parent.rotate(theta);
		parent.beginShape(PConstants.TRIANGLES);
		parent.vertex(0, -r * 2);
		parent.vertex(-r, r * 2);
		parent.vertex(r, r * 2);
		parent.endShape();
		parent.popMatrix();
	}

	// Wraparound
	void borders() {
		if (location.x < -r)
			location.x = parent.width + r;
		if (location.y < -r)
			location.y = parent.height + r;
		if (location.x > parent.width + r)
			location.x = -r;
		if (location.y > parent.height + r)
			location.y = -r;
	}

	// Separation
	// Method checks for nearby boids and steers away
	PVector separate(ArrayList<Boid> boids) {
		float desiredseparation = 15.0f;
		PVector steer = new PVector(0, 0, 0);
		int count = 0;
		// For every boid in the system, check if it's too close
		for (Boid other : boids) {
			float d = PVector.dist(location, other.location);
			// If the distance is greater than 0 and less than an arbitrary
			// amount (0 when you are yourself)
			if ((d > 0) && (d < desiredseparation)) {
				// Calculate vector pointing away from neighbor
				PVector diff = PVector.sub(location, other.location);
				diff.normalize();
				diff.div(d); // Weight by distance
				steer.add(diff);
				count++; // Keep track of how many
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steer.div((float) count);
		}

		// As long as the vector is greater than 0
		if (steer.mag() > 0) {
			// First two lines of code below could be condensed with new PVector
			// setMag() method
			// Not using this method until Processing.js catches up
			// steer.setMag(maxspeed);

			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
		}
		return steer;
	}

	// Alignment
	// For every nearby boid in the system, calculate the average velocity
	PVector align(ArrayList<Boid> boids) {
		float neighbordist = 100;
		PVector sum = new PVector(0, 0);
		int count = 0;
		for (Boid other : boids) {
			float d = PVector.dist(location, other.location);
			if ((d > 0) && (d < neighbordist)) {
				sum.add(other.velocity);
				count++;
			}
		}
		if (count > 0) {
			sum.div((float) count);
			// First two lines of code below could be condensed with new PVector
			// setMag() method
			// Not using this method until Processing.js catches up
			// sum.setMag(maxspeed);

			// Implement Reynolds: Steering = Desired - Velocity
			sum.normalize();
			sum.mult(maxspeed);
			PVector steer = PVector.sub(sum, velocity);
			steer.limit(maxforce);
			return steer;
		} else {
			return new PVector(0, 0);
		}
	}

	// Cohesion
	// For the average location (i.e. center) of all nearby boids, calculate
	// steering vector towards that location
	PVector cohesion(ArrayList<Boid> boids) {
		float neighbordist = 200;
		PVector sum = new PVector(0, 0); // Start with empty vector to
											// accumulate all locations
		int count = 0;
		for (Boid other : boids) {
			float d = PVector.dist(location, other.location);
			if ((d > 0) && (d < neighbordist)) {
				sum.add(other.location); // Add location
				count++;
			}
		}
		if (count > 0) {
			sum.div(count);
			return seek(sum); // Steer towards the location
		} else {
			return new PVector(0, 0);
		}
	}

	// Grid center of mass Attraction

	PVector gridAvgAttractor(Point[] grid) {

		PVector sum = new PVector(0, 0); // Start with empty vector to
											// accumulate all locations
		int count = 0;
		for (Point other : grid) {
			if (other.life > 98) {
				float d = PVector.dist(location, other.location);
				if (d > 0) {
					sum.add(other.location); // Add location
					count++;
				}
			}
		}
		if (count > 0) {
			sum.div(count);
			return seek(sum); // Steer towards the location
		} else {
			return new PVector(0, 0);
		}
	}

	PVector gridAvoider(Point[] grid) {
		float desiredseparation = 40.0f;
		PVector steer = new PVector(0, 0);
		int count = 0;
		// For every gridbox in the detection map, check if it's too close
		for (Point other : grid) {
			if (other.life > 98) {
				float d = PVector.dist(location, other.location);
				// If the distance is greater than 0 and less than an arbitrary
				// amount (0 when you are on the gridpoint)
				if ((d > 0) && (d < desiredseparation)) {
					// Calculate vector pointing away from gridbox
					PVector diff = PVector.sub(location, other.location);
					diff.normalize();
					diff.div(d); // Weight by distance
					steer.add(diff);
					count++; // Keep track of how many
				}
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steer.div((float) count);
		}

		// As long as the vector is greater than 0
		if (steer.mag() > 0) {
			// First two lines of code below could be condensed with new PVector
			// setMag() method
			// Not using this method until Processing.js catches up
			// steer.setMag(maxspeed);

			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
		}
		return steer;
	}

	// Attraction
	// the location of the mouse attracts each boid

	PVector attraction(PVector mouse) {
		return seek(mouse);
	}

	boolean finished() {
		life -= 2.0;
		if (life < 0)
			return true;
		else
			return false;
	}

}
