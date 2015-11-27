import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class App {

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){	
			public void run(){
				
				JFrame frame = new MainFrame("Injury Monitoring System"); 
				frame.setSize(500,500);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				// trying to make multiple frames 
				JFrame frame2 = new EmergencyFrame("Emergency Notification"); 
				frame2.setSize(500,500);
				frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame2.setVisible(true);
				
				
			
				
			}
			
		});
		
	}
}