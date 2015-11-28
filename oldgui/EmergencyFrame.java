import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

public class EmergencyFrame extends JFrame {

	
	
	private static final long serialVersionUID = 1L;
	private PlayerEmergency emerg;
	
	
	public EmergencyFrame(String Title){
		
		//super(title);
		setLayout(new BorderLayout());
		
		emerg = new PlayerEmergency(); 
		
		
		Container c = getContentPane();
		c.add(emerg,BorderLayout.CENTER);
		
		
	}
}
