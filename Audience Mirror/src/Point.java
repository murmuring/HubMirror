import processing.core.*;

public class Point {
	PApplet parent; // The parent PApplet that we will render ourselves onto
	float x, y;
	float count, life;
	int pointColor;
	boolean bump = false;
    PVector location;

	Point(PApplet p) {
		parent = p;
	}

	Point(PApplet p, float xpos, float ypos, float lfe, float cnt, int clr) {
		parent = p;
		x = xpos;
		y = ypos;
		location = new PVector(xpos+(Mirror.videoScale/2), ypos+(Mirror.videoScale/2));
		count = cnt;
		pointColor = clr;
		life = lfe;
		

	}

	void set(float xpos, float ypos, float lfe, float cnt) {
		if (life <= 0){
			bump = true;
		}
		
		x = xpos;
		y = ypos;
		location = new PVector(xpos+(Mirror.videoScale/2), ypos+(Mirror.videoScale/2));
		count = cnt;
		life = lfe;



	}




	boolean finished() {
		if (life > 0){
		life -= 1.0;
		return false;
		}
		else{			
			return true;
		}
	}

}
