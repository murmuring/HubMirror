import java.util.Random;

import processing.core.*;
import processing.video.*;

import java.awt.Frame;
import java.awt.BorderLayout;
import controlP5.*;




public class Mirror extends PApplet {
	/**
	 * 
	 */

	
	private static final long serialVersionUID = 1L;
	
	public static int videoScale = 32; // size of detection squares

	public enum states {
		squares, dots, swarm, simplePong, halfPong;

	}

	states displayMode = states.squares;

	Capture video;
	
	public Point detectMap[];

	PVector mouse;
	public int cols, rows;								

	public int trackColor, blankColor, fillColor;		// accessible from outside

	public static Random rand = new Random();

	public boolean blankToggle = false;
	public int debugToggle = 0;
	public boolean gridToggle = false;



	public boolean colorMode = true; // true=RGB, false=HSB // accessible from outside
	private float d; // distance for color detection		
	public int limit = 20; // sensitivity limit for color detection // accessible from outside

	private int trackCount; // store number of detections in gridbox

//	public float cSize;
	public int lifeSpan = 100; // lifespan of boids or points /// accessible from outside

	// for flocking mode
	public Flock flock;

	// for pong modes
	public float originalBallSpeed = 5;			// accessible from outside
	private float ballSpeed = originalBallSpeed;
	public float ballSize = 20;					// accessible from outside
	private float dirX, dirY;
	private float ballX, ballY;
	private float scoreR, scoreL = 0;
	private float scored = 0;
	private float scoreDelay, scoreTime = 60;
	private int ballRow, ballCol, ballPos;
	public float bounceFactor = 1.01f; 			// accessible from outside

	private float rightCount;
	private float rightBatY;

	private float leftCount;
	private float leftBatY;
	private float rateTrack;
	private float frameCount;




	public void setup() {

		size(1280, 960,P2D); // logitech 1280,960 // creative 1280,720
		// get camera list and print to console
		
		colorMode(HSB, 360, 100, 100, 100);
		
		blankColor=color(0,0,10,90);
		
		
		String[] cameras = Capture.list();
		if (cameras.length == 
				0) {
			println("There are no cameras available for capture.");
			exit();
		} else {
			println("Available cameras:");
			for (int i = 0; i < cameras.length; i++) {
				println(cameras[i]);
			}
		}

		
		// Initialize detection map and flock
		resetDetectMap();

		// start the video capturing
		video = new Capture(this, cameras[0]);

		video.start();

		trackColor = color(255, 221, 0);
	}

	public void draw() {
		
		rateTrack+= frameRate;
		frameCount ++;
		
		if (frameCount >9){
			rateTrack=rateTrack/frameCount;
			println(rateTrack);
			frameCount=0;
		}
		
		
		
		// Read image from the camera
		if (video.available()) {
			video.read();
		}
		// reverse the camera image
		pushMatrix();
		scale(-1, 1);
		image(video, -width, 0);
		popMatrix();

		// load the pixel array
		loadPixels();
		trackCount = 0;
		// Begin loop for columns
		for (int i = 0; i < cols; i++) {
			// Begin loop for rows
			for (int j = 0; j < rows; j++) {
				trackCount = 0;
				int ploc = (i * rows + j);
				int hor = i * videoScale;
				int ver = j * videoScale;

				// begin loop for sub columns
				for (int k = 0; k < videoScale; k++) {
					// begin loop for sub rows
					for (int l = 0; l < videoScale; l++) {
						int loc = (((j * videoScale) + k) * width) + l
								+ (i * videoScale);
						// What is the color at this location
						int currentColor = pixels[loc];

						if (colorMode) {
							float r1 = red(currentColor);
							float g1 = green(currentColor);
							float b1 = blue(currentColor);
							float r2 = red(trackColor);
							float g2 = green(trackColor);
							float b2 = blue(trackColor);
							d = dist(r1, g1, b1, r2, g2, b2);
						} else {
							float h1 = hue(currentColor);
							float s1 = saturation(currentColor);
							// float b1 = brightness(currentColor);
							float h2 = hue(trackColor);
							float s2 = saturation(trackColor);
							// float b2 = brightness(trackColor);

							// Using euclidean distance to compare colors,
							// ignoring brightness
							d = dist(h1, s1, h2, s2);
						}

						if (d < limit) {
							trackCount++;
						}
					}
				}
				// ///////////////
				// do something based on how many pixels you found
				// in this square of videoScale size
//				r = red(trackColor);
//				g = green(trackColor);
//				b = blue(trackColor);

				// fill array with truth values based on pixels found

				if (trackCount > videoScale / 4) {

					detectMap[ploc].set(hor, ver, lifeSpan, trackCount);
				} else {
					if (detectMap[ploc].finished()) {
						// here we could do something when lifetime in detect
						// grid has expired
					}
				}
			}
		}

		if (blankToggle) {
			noStroke();
			fill(blankColor);
			rect(0, 0, width, height);
		}
		
		if (gridToggle) {
			// rectangle grid
			// iterate through detectMap
			for (int i = 0; i < (cols * rows); i++) {
				// draw rectangles
				if (detectMap[i].life > 98) {
					stroke(trackColor, 100);
					fill(trackColor, 50);
			rect(detectMap[i].x, detectMap[i].y, videoScale, videoScale);
					

					
					if (debugToggle==1) {
						// print the life count in the gridbox
						fill(0);
						textSize(8);
						text((int) (detectMap[i].life), 
								detectMap[i].x + (videoScale / 3),
								detectMap[i].y + (videoScale / 2));
					} else if (debugToggle==2){
						// print the index number of the gridbox
						fill(0);
						textSize(8);
						text(i, detectMap[i].x + videoScale / 3,
								detectMap[i].y + videoScale / 2);
					}
				}

			}
		}
		

		switch (displayMode) {
		case squares:


			break;
		case dots:
			// circles size depends on trackCount
			// iterate through detectMap
			for (int i = 0; i < (cols * rows); i++) {
				// draw circles, size depends on detection count, gridsize and lifecount
				if (detectMap[i].life > 10) {
					stroke(detectMap[i].pointColor);

					fill(trackColor, 100);

					float cSize = map((detectMap[i].life * detectMap[i].count),
							0, (videoScale * videoScale * lifeSpan), 0,
							videoScale);
					ellipse(detectMap[i].x + videoScale / 2, detectMap[i].y
							+ videoScale / 2, cSize, cSize);
				}
			}
			break;
		case swarm:
			// generates a swarm of boids, new boid in a specific grid point
			// only after lifetime has elapsed
			for (int i = 0; i < (cols * rows); i++) {
				// Boids generation
				if (detectMap[i].bump) {
					flock.addBoid(new Boid(this, detectMap[i].x +videoScale/2,
							detectMap[i].y+ +videoScale/2, detectMap[i].life,
							detectMap[i].count / videoScale,
							detectMap[i].pointColor));
					detectMap[i].bump = false;
				}
			}

			// run the flock simply attracted by itself
			// flock.run();
			
			// run the flock, attracted by mouse
			//flock.runMouse(new PVector(mouseX, mouseY));
			
// run the flock based on grid attraction
			 flock.runGrid(detectMap);

			break;

		case simplePong:
			// automatic ball, bat position depends on number of detected grid
			// points in half the screen.

			rectMode(CENTER);
			// count number of detection boxes in the right side of the screen
			rightCount = 0;
			for (int c = (cols / 2); c < cols; c++) {
				for (int r = 0; r < rows; r++) {
					if (detectMap[c * rows + r].life > 98) {
						rightCount++;
					}
				}
			}
			rightBatY = map(rightCount, 0, (cols * rows) / 3, 0, height);

			// draw a 10 x 80 rectangle 10 pixels away from the right of the
			// screen, in a position relative to the number of gridboxes in the
			// right half of the screen
			stroke(trackColor,100);
			fill(trackColor,70);
			
			rightBatY=mouseY;
			rect(width - 10, rightBatY, 10, 80);

			// count number of detection boxes in the left side of the screen
			leftCount = 0;
			for (int c = 0; c < (cols / 2); c++) {
				for (int r = 0; r < rows; r++) {
					if (detectMap[c * rows + r].life > 98) {
						leftCount++;
					}
				}
			}
			leftBatY = map(leftCount, 0, (cols * rows) / 3, 0, height);
			// draw a 10 x 80 rectangle 10 pixels away from the left of the
			// screen, in a position relative to the number of gridboxes in the
			// left half of the screen
			leftBatY=mouseY;
			rect(10, leftBatY, 10, 80);


			// display score
			fill(trackColor);
			textSize(40);
			text((int)scoreL + " - " + (int)scoreR, (width / 2) - 40, 60);

			if (scored > 0) {
				if (scoreDelay > 0) {
					fill(trackColor);
					textSize(40);

					text("SCORED", scored * (width / 3) - 50, (height / 2) + 20);
					scoreDelay--;
				} else {
					scored = 0;
				}
			} else {

				// update ball position
				ballY += dirY;
				ballX += dirX;
				// create the ball
				fill(trackColor);
				rect(ballX, ballY, ballSize, ballSize);

				// bounce off the top and bottom walls
				if (ballY >= height-(ballSize/2)) {
					dirY = -dirY;
					dirX*=bounceFactor;
					dirY*=bounceFactor;
					
					ballY = height-(ballSize/2);
				}
				if (ballY <= ballSize/2) {
					dirY = -dirY;
					dirX*=bounceFactor;
					dirY*=bounceFactor;
					
					ballY = ballSize/2;
				}

				// bounce of the left Bat
				if (ballX <= ballSize && ballY <= (leftBatY + 40) && ballY >= leftBatY - 40) {
					// if its less than center make it go upwards
					if (ballY < leftBatY) {
						dirX = -dirX;
						dirY = -dirY;
					}
					// if its more than center make it go downwards
					if (ballY > leftBatY) {
						dirX = -dirX;
						dirY = +dirY;
					}
					// if its dead on make it shoot dead on
					if (ballY == leftBatY) {
						dirX = -dirY;
						dirY = 0;
					}
					dirX*=bounceFactor;
					dirY*=bounceFactor;
				}
				// bounce off the right Bat
				if (ballX >= width - ballSize && ballY <= rightBatY + 40
						&& ballY >= rightBatY - 40) {
					// if its less than center make it go upwards
					if (ballY < rightBatY) {
						dirX = -dirX;
						dirY = -dirY;
					}
					// if its more than center make it go downwards
					if (ballY > rightBatY) {
						dirX = -dirX;
						dirY = +dirY;
					}
					// if its dead on make it shoot dead on
					if (ballY == rightBatY) {
						dirX = -dirX;
						dirY = 0;
					}
					dirX*=bounceFactor;
					dirY*=bounceFactor;
				}
				// account for score
				if (ballX < 0) {
					// if the left paddle doesn't hit the ball then add one to
					// the right players score
					scoreR++;

					ballX = width / 2;
					ballY = height / 2;
					scored = 2;
					scoreDelay = scoreTime;

				}
				if (ballX > width) {
					// if the right paddle doesn't hit the ball then add one to
					// the left players score
					scoreL++;

					ballX = width / 2;
					ballY = height / 2;
					scored = 1;
					scoreDelay = scoreTime;

				}
			}

			// set rectangle mode back to default
			rectMode(CORNER);
			break;

		case halfPong:
			// automatic ball, bounces off any detected grid
			// display score
			fill(trackColor);
			textSize(40);
			text((int)scoreL + " - " + (int)scoreR, (width / 2) - 40, 60);

			if (scored > 0) {
				if (scoreDelay > 0) {
					fill(trackColor);
					textSize(40);

					text("SCORED", scored * (width / 3) - 50, (height / 2) + 20);
					scoreDelay--;
				} else {
					scored = 0;
					ballX = width / 2;
					ballY = height / 2;
					dirX*=bounceFactor*bounceFactor;
					dirY*=bounceFactor*bounceFactor;
				}
			} else {

				
				// update ball position
				ballY += dirY;
				ballX += dirX;
				// create the ball
				rectMode(CENTER);
				fill(trackColor);
				rect(ballX, ballY, ballSize, ballSize);

				// account for score
				if (ballX <= 0) {
					// if the the ball exits left then add one to the right score
					// players score
					scoreR++;
					scored = 2;
					scoreDelay = scoreTime;
					// limit x values (for other checkings)
					ballX=0;
				}
				if (ballX >= width) {
					// if the the ball exits right then add one to the left score
					// players score
					scoreL++;
					scored = 1;
					scoreDelay = scoreTime;
					// limit x values (for other checkings)
					ballX=width-1;
				}				
				
				// bounce off the top and bottom walls
				if (ballY >= height-(ballSize/2)) {
					dirY = -dirY;
					dirX*=bounceFactor;
					dirY*=bounceFactor;
					
					ballY = height-(ballSize/2);
				}
				if (ballY <= ballSize/2) {
					dirY = -dirY;
					dirX*=bounceFactor;
					dirY*=bounceFactor;
					
					ballY = ballSize/2;
				}

				// check grid detection collisions
				// calculate grid position of ball
				ballCol = (int)ballX / videoScale;
				ballRow = (int)ballY / videoScale;
				ballPos = (ballCol * rows) + ballRow;
				//println("ballX="+ ballX + " ballCol= " + ballCol + " ballY="+ ballY + " ballRow= " + ballRow + " ballPos" + ballPos);
				

				// based on ball direction, check collisions horizontal

				if (dirX < 0) { // if going left
					// check if we are at the leftmost
					if (ballCol >= 1) {
						// check if the left adjacent detection grid is on
						if (detectMap[ballPos - rows].life >98) {
							// check if the left side of the ball touches right
							// side that gridbox
							 if (((ballX-(ballSize/2)) - (detectMap[ballPos - rows].x + videoScale)) <= ballSpeed/2 ){
							//if (ballX - (ballSize / 2) == detectMap[ballPos - rows].x + videoScale) {
								// reverse x direction
								dirX = -dirX;
								dirX*=bounceFactor;
								dirY*=bounceFactor;
							}
						}
					}
				} else { // it's going right, check gridbox on the right
					// check if we are at the rightmost
					if (ballCol <= cols - 2) {
						// check if the right adjacent box is on
						if (detectMap[ballPos + rows].life >98) {
							// check if the right side of the ball touches left
							// side that gridbox
							if ( detectMap[ballPos + rows].x - (ballX + (ballSize/2))  <= ballSpeed/2 ){
							//if (ballX + (ballSize / 2) == detectMap[ballPos + rows].x) {
								// reverse x direction
								dirX = -dirX;
								dirX*=bounceFactor;
								dirY*=bounceFactor;
							}
						}
					}
				}

				// based on ball direction, check collisions vertical

				if (dirY > 0) { // if the ball is going down (increasing y)
					// check if we are at the bottom
					if (ballRow <= rows - 2) {
						// check if the gridbox under is on
						if (detectMap[ballPos + 1].life > 98) {

							// check if the bottom of the ball touches the top
							// of that gridbox
							 if ( (detectMap[ballPos + 1].y - (ballY+(ballSize/2)) ) <= ballSpeed/2 ){
							//if (ballY + (ballSize / 2) == detectMap[ballPos + 1].y) {
								// reverse y direction
								dirY = -dirY;
								dirX*=bounceFactor;
								dirY*=bounceFactor;
							}
						}
					}
				} else { // ball is going up (decreasing y)
					// check if we are at the top
					if (ballRow >= 1) {
						// check if the gridbox under is on
						if (detectMap[ballPos - 1].life > 98) {
							// check if the top side of the ball touches the
							// bottom of that gridbox
							 if ( ((ballY - (ballSize/2)) - (detectMap[ballPos - 1].y + videoScale) ) <= ballSpeed/2  ){
							//if (ballY - (ballSize / 2) == detectMap[ballPos - 1].y) {
								// reverse y direction
								dirY = -dirY;
								dirX*=bounceFactor;
								dirY*=bounceFactor;
							}
						}
					}
				}

			}

			// set rectangle mode back to default
			rectMode(CORNER);

			break;
		}
	}

	public void keyReleased() {
		if (key == CODED) {
			if (keyCode == UP) {
				videoScale = videoScale * 2;
				flock.killAll();
				resetDetectMap();
			} else if (keyCode == DOWN) {
				if (videoScale > 2) {
					videoScale = videoScale / 2;
					flock.killAll();
					resetDetectMap();
				}
			}

		} else if (key == 'b') {
			blankToggle = !blankToggle;
			println("blankToggle=" + blankToggle);
		} else if (key == 'd') {
			debugToggle= (debugToggle+1) % 3;
			println("debugToggle=" + debugToggle);
		} else if (key == 'g') {
			gridToggle = !gridToggle;
			println("gridToggle=" + gridToggle);
		} else if (key == 'b') {
			trackColor = color(255, 221, 0);
		} else if (key == 'c') {
			colorMode = !colorMode;
			if (colorMode) {
				limit = 20;
				println("RGB(" + limit + ")");
			} else {
				limit = 10;
				println("HSB (" + limit + ")");
			}
		} else if (key == '1') {
			displayMode = states.squares;
			println("display mode= " + displayMode);
			println("squares of tracking color on video");
		} else if (key == '2') {
			displayMode = states.dots;
			println("display mode= " + displayMode);
			println("circles related to tracking color on video");
		} else if (key == '3') {
			displayMode = states.swarm;
			gridToggle=true;
			blankToggle=true;
			debugToggle=0;
			println("display mode= " + displayMode);
		} else if (key == '4') {
			displayMode = states.simplePong;
			resetPong();
			gridToggle = false;
			debugToggle=0;
			println("display mode= " + displayMode);
		} else if (key == '5') {
			displayMode = states.halfPong;
			resetPong();
			gridToggle = true;
			debugToggle=0;
			println("display mode= " + displayMode
					+ " (t will toggle black background)");
		} else if (key == '+') {
			limit += 1;
			println("limit= " + limit);
		} else if (key == '0') {
			limit -= 1;
			println("limit= " + limit);
		} else if (key == 'h') {
			println("keys:");
			println("h : this list");
			println("1 : display modes: 1-squares; 2-circles; 3-flock ; 4-bat pong ; 5- grid pong");
			println("b : toggle blanking= " + debugToggle);
			println("d : toggle debug (various)= " + debugToggle);
			println("g : toggle detection grid= " + gridToggle);
					
			println("c : toggle color-mode between RGB (20) to HSB (40) and reset limit to value (n)");
			println("arrow up: bigger squares, videoScale= " + videoScale);
			println("arrow dwn: smaller squares");
			println("mouse click : selects color for detection= " + trackColor);
			println("b : default color selected (yellow-ish)");
			println("0 : decrease color distance for positive color detection (limit) = "
					+ limit);
			println("+ : increase color distance for positive color detection (limit)");
		}

	}

	private void resetDetectMap() {

		cols = width / videoScale;
		rows = height / videoScale;

		println("videoScale=" + videoScale + ", # detection squares=" + cols
				* rows);
		detectMap = new Point[cols * rows];
		// make new detection map
		for (int i = 0; i < (cols * rows); i++) {
			detectMap[i] = new Point(this);
			detectMap[i].pointColor = color(180 + map((i / cols), 0, rows, -180, 180),80, 90);
		}

		// generate a new flock
		flock = new Flock(50);
		flock.maxBoids = 50;
		println("flock reset to size " + flock.maxBoids);
	}

	private void resetPong() {

		ballX = width / 2;
		ballY = height / 2;
		dirX = dirY = ballSpeed;
		scoreL = scoreR = 0;
		scoreDelay = scoreTime;
		ballSpeed=originalBallSpeed;
	}

	public void mousePressed() {
		// Save color where the mouse is clicked in trackColor variable
		int loc = mouseX + mouseY * width;
		trackColor = pixels[loc];
		flock.killAll();
		
		println("trackColor=" + (red(trackColor)) + "," + (green(trackColor))
				+ "," + (blue(trackColor)));
	}

	public void stop() {
		video.stop();
		video.dispose();
		super.exit();
	}
	

	
	
}
