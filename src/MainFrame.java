import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;

public class MainFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private detailPanel dets;
	private playerInfoPane textp;
	public MainFrame(String title){
		super(title);
				
		// set layout manager 
		setLayout(new BorderLayout());
		
		// create swing s 
		textp = new playerInfoPane();
		textp.addToPane("text");

		
		// here i create a detail panel
		//textp.addToPane("charlie made it");
		dets = new detailPanel();
		
		dets.addDetailsListener(new DetailListener(){
			
			public void detailEventOccurred(DetailEvent event){
				System.out.println("An event has occered");
				String text = event.getText();
				
				textp.addToPane(text);
			}
			
			
			
		});
		
		
		
		// add swing components to content pane
		Container c = getContentPane();
		
		c.add(dets,BorderLayout.WEST);
		c.add(textp,BorderLayout.EAST);
		
		
	
		
	}
	
}