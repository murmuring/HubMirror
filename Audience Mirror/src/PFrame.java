import javax.swing.JFrame;
import processing.core.*;


public class PFrame extends JFrame {
	PApplet parent;
	secondApplet settingsWindow;

	
  public PFrame(PApplet p, int locX, int locY, int w, int h) {
	parent = p;
    setBounds(locX, locY, w, h);
    settingsWindow = new secondApplet();
    add(settingsWindow);
    settingsWindow.init();
    setVisible(true);
  }
}
