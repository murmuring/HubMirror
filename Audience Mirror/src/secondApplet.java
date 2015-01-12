
import processing.core.*;

public class secondApplet extends PApplet {


	
  public void setup() {
    size(300, 200);

    this.noLoop();
  }
  
  public void draw() {
	fill(200);
	noStroke();
	rect(0,0,width,height);
	
	
	
	
    fill(0);
    ellipse(mouseX, mouseY,20,20);
  }
  
  public void mousePressed() {
      // do something based on mouse movement

      // update the screen (run draw once)
      redraw();
  }
  
  
  
  
  /*
   * TODO: something like on Close set f to null, this is important if you need to 
   * open more secondapplet when click on button, and none secondapplet is open.
   */
}
