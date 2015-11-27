import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

class PlayerEmergency extends JPanel {
	
	
	private static final long serialVersionUID = 1L;

	public PlayerEmergency(){  
		
		Dimension size = getPreferredSize();
		//size.width=200;
		setPreferredSize(size);
		setBorder(BorderFactory.createTitledBorder("PlayerEmergency"));
		
		JTextArea Severity = new JTextArea("Severity:");
		JTextArea PlayerName = new JTextArea("Player Name:");
		JTextArea NumberofPreviousHits = new JTextArea("Number of Previous Hits:");

		
		
		JButton DisplayWarning  = new JButton("Display Warning ");
		DisplayWarning.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Severity.append("10");
				PlayerName.append("Charlie");
				NumberofPreviousHits.append("3");
				
			}
		});
		
		JButton Emergencyreq = new JButton("Send Emergency  ");
		Emergencyreq.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.out.println("Emergency Notification sent");
				
			}
		});
		JButton Dismisswarn = new JButton("Dismiss  ");
		Dismisswarn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.out.println("Dismiss Notification sent");
				
			}
		});
		
		
		// here I set up the GridBagLayout
				setLayout(new GridBagLayout());
				GridBagConstraints gc = new GridBagConstraints() ;
				// here I add the name text feild
				gc.fill =  GridBagConstraints.HORIZONTAL;
				gc.gridx = 0; 
				gc.gridy = 0; 
				add(DisplayWarning,gc);
				gc.gridx = 0; 
				gc.gridy = 1; 
				add(Severity,gc);
				gc.gridx = 0; 
				gc.gridy = 2; 
				add(PlayerName,gc);
				gc.gridx = 0; 
				gc.gridy = 3; 
				add(NumberofPreviousHits,gc);
				gc.gridx = 1; 
				gc.gridy = 1; 
				add(Emergencyreq,gc);
				gc.gridx = 1; 
				gc.gridy = 2; 
				add(Dismisswarn,gc);
				
				
	}
	
	
}