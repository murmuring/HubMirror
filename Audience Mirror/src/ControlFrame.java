import java.awt.Frame;
import java.awt.BorderLayout;
import controlP5.*;
import processing.core.*;


// the ControlFrame class extends PApplet, so we 
// are creating a new processing applet inside a
// new frame with a controlP5 object loaded
public class ControlFrame extends PApplet {
	
	
	
	
	public ControlP5 settingsCP5;
	RadioButton r;

  int w, h;

  int abc = 100;
  
  public void setup() {
	  
	  
    size(w, h);
    frameRate(5);
    settingsCP5 = new ControlP5(this);

    settingsCP5.addToggle("gridToggle").plugTo(parent,"gridToggle").setPosition(10,10).setSize(10,10) .setValue(false).setId(1);
    settingsCP5.addToggle("blankToggle").plugTo(parent,"blankToggle").setPosition(10,30).setSize(10,10) .setValue(false).setId(2);
    settingsCP5.addSlider("debugToggle").plugTo(parent,"debugToggle").setPosition(10,50).setWidth(100).setRange(0,3).setValue(0)
    .setNumberOfTickMarks(3).setId(3);
    
    settingsCP5.addToggle("colorMode").plugTo(parent,"colorMode").setPosition(10,70).setSize(10,10) .setValue(false).setId(4);
    
    settingsCP5.addSlider("ballSpeed").plugTo(parent,"originalBallSpeed").setRange(5, 15).setNumberOfTickMarks(10).setPosition(10,90).setId(5);
    settingsCP5.addSlider("bounceFactor").plugTo(parent,"bounceFactor").setRange(0, 2.0f).setPosition(10,110).setId(6);
    
    settingsCP5.addSlider("flockSize").plugTo(parent,"flockSize").setRange(1, 100).setPosition(10,130).setId(7);
     
    
    
    
    // display Modes: 
    // gridToggle (Boolean)
    // debug Toggle (3 states)
    // blanking Toggle (Boolean)
    // blanking color (color)
    
    // detection color (set from big Mirror class)
    
    //	Dots
    // dot scaling (?)
    
    //	Pong
    // ballSpeed	(float)
    // bounce factor (float)
    
    //	Swarm
    // max num of Boids (int)
    // separation Factor (float)
    // alignment Factor (float)
    // cohesion Factor (float)
    // grid attract Factor (float)
    
    //	Rhizomic
    // Growth speed (float)
    // Growth spread (float)
    // number of max branchings (float)
    //	Stellar
    // MaxSpeed of particles (float)
    // Gravitational Force (float)
    
    // show frame rate
    
    
  }

  public void draw() {
      background(abc);
  }
  
  private ControlFrame() {
  }

  public ControlFrame(Object theParent, int theWidth, int theHeight) {
    parent = theParent;
    w = theWidth;
    h = theHeight;
  }


  public ControlP5 control() {
    return settingsCP5;
  }
  
  
  ControlP5 cp5;

  Object parent;

  public void controlEvent(ControlEvent theEvent) {
	  println("got a control event from controller with id "+theEvent.getController().getLabel());
	  
	  
	  switch(theEvent.getController().getId()) {
	    case(1):
	println(theEvent.getController().getValue());
	    break;
	    case(2):
	 println(theEvent.getController().getValue());
	    break;
	    case(3):
	    println(theEvent.getController().getStringValue());
	    case(4):
	    println(theEvent.getController().getStringValue());
	    break;
	  }
	}
  
  
  
}