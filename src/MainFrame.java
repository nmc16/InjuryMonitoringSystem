import java.awt.BorderLayout;
import java.awt.Container;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;

//import javax.swing.JButton;
import javax.swing.JFrame;
//import javax.swing.JTextArea;




public class MainFrame extends JFrame {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private detailPanel dets;
	public MainFrame(String title){
		super(title);
		
		// set layout manager 
		setLayout(new BorderLayout());
		
		// create swing componant 
		//final JTextArea textArea = new JTextArea();
	//	JButton button = new JButton("Click here!");
		
		dets = new detailPanel();
		
		dets.addDetailsListener(new DetailListener(){
			
			public void detailEventOccurred(DetailEvent event){
				
				String text = event.getText();
				System.out.println(text);
			}
			
			
			
		});
		
		
		
		// add swing componants to content pane
		Container c = getContentPane();
		//c.add(textArea,BorderLayout.CENTER);
		//c.add(button,BorderLayout.NORTH); 
		c.add(dets,BorderLayout.WEST);
		//add button behavior 
		//button.addActionListener(new ActionListener(){

			//@Override
			//public void actionPerformed(ActionEvent arg0) {
			//	textArea.append("Charlieisawesome\n");
			//}
			
			
			
	///	});
	
		
	}
	
}