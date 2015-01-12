import java.util.ArrayList;
import processing.core.*;

public class Flock {

		  ArrayList<Boid> boids; // An ArrayList for all the boids
		  float maxBoids;
		  PVector mouse;
		  
		    Flock(float num) {
		    boids = new ArrayList<Boid>(); // Initialize the ArrayList
		    maxBoids = num;
		  }

			  void run() {
				  killBoids();
			    for (Boid b : boids) {
			      b.run(boids);  // Passing the entire list of boids to each boid individually
			    }
			  }
		    
		  void runMouse(PVector mouse) {
			  killBoids();
			  this.mouse = mouse; // passing the mouse location
		    for (Boid b : boids) {
		      b.runMouse(boids, this.mouse);  // Passing the entire list of boids to each boid individually
		    }
		  }
		  
		// run the flock based on ?local? grid attractions
		  void runGrid(Point[] grid) {
			  killBoids();
			  // here should pass the location of valid detection gridboxes
		    for (Boid b : boids) {
		      b.runGrid(boids, grid);  // Passing the entire list of boids to each boid individually
		    }
		  }
		  

		  void addBoid(Boid b) {
			    if (boids.size() > maxBoids) {      
			      boids.remove(0);
			      boids.add(b);
			    } else {
			      boids.add(b);
			    }
			  }



		  void killBoids() {
		    for (int i= (boids.size () -1); i>=0; i--) {
		      Boid b0 = (Boid) boids.get(i);
		      if ( b0.finished()) {
		        boids.remove(i);
		      }
		    }
		  }
		  
		  void killAll(){
			  for (int i= (boids.size () -1); i>=0; i--) {
				  boids.remove(i);
			  }
		  }
		

}
