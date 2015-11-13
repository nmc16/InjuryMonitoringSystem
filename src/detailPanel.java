//import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;


public class detailPanel extends JPanel {
	/**
	 * This is my details pannel;
	 */
	private static final long serialVersionUID = -7574758752829411693L;
	String[] playerString = { "Player1", "player2", "player2", "player3", "player4" };
	private EventListenerList ListenerList = new EventListenerList();
	
	public detailPanel(){  
		
		// Here I set up the 
		Dimension size = getPreferredSize();
		size.width=200;
		setPreferredSize(size);
		setBorder(BorderFactory.createTitledBorder("Select Player"));
		
		// here I set up all my buttons and panel interactions 
		
		
		// Here  I Create the drop down box of player names
		final JComboBox playerDrop = new JComboBox(playerString);
		
		
		JButton playerInfoButton = new JButton("Get player info");
		playerInfoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				 String playerName =(String)playerDrop.getSelectedItem();
				//Object PlayerName = playerDrop.getSelectedItem();
				//fireDetailEvent(new DetailEvent(this,name));
				
				System.out.println(playerName);
				
			}
		});
			
		JButton button_Graph  = new JButton("Display Graph of Recent Hits ");
		button_Graph.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				 String playerName =(String)playerDrop.getSelectedItem();
				//Object PlayerName = playerDrop.getSelectedItem();
				//fireDetailEvent(new DetailEvent(this,name));
				
				System.out.println(playerName);
				
			}
		});
		
		
		JButton SendEmerg  = new JButton("Send Emergency Notification ");
		SendEmerg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				 String playerName =(String)playerDrop.getSelectedItem();
				//Object PlayerName = playerDrop.getSelectedItem();
				 fireDetailEvent(new DetailEvent(this,"charlie"));
				// fireDetialEvent2(e,playerName);
				
			//	System.out.println("THERE IS AN EMERGENCY");
				
			}
		});
		
		
		
		
		// here I set up the GridBagLayout
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints() ;
		// here I add the name text feild
		gc.fill =  GridBagConstraints.HORIZONTAL;
		gc.gridx = 0; 
		gc.gridy = 0; 
		add(playerDrop,gc);
		// here I add the get player info button 
		gc.gridx = 0; 
		gc.gridy = 1; 
		add(playerInfoButton,gc);
		// here I add the graph button 
		gc.gridx = 0; 
		gc.gridy = 2; 
		add(button_Graph,gc);
		// here i add the Send emergency button 
		gc.gridx = 0; 
		gc.gridy = 3; 
		add(SendEmerg,gc);
	
		
		
	}
	
	public void fireDetailEvent(DetailEvent event){
		System.out.println("made it here");
		Object[] listeners = listenerList.getListenerList();
		
		for(int i =0; i < listeners.length; i +=2){
			if(listeners[i]==DetailListener.class ){
				System.out.println("made it here2");
				((DetailListener) listeners[i+1]).detailEventOccurred(event);
				
			}
		
		}
		
	}
	
		

	public void addDetailsListener(DetailListener listener){
		ListenerList.add(DetailListener.class, listener);
		
		System.out.println("ListenerAdded");
		
	}
	public void removeDetailsListener(DetailListener listener){
		ListenerList.remove(DetailListener.class, listener);
	
	}
	
		     
		  
		

		   
	
}