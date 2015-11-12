//import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.JButton;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;


public class detailPanel extends JPanel {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EventListenerList ListenerList = new EventListenerList();
	
	public detailPanel(){  
		Dimension size = getPreferredSize();
		size.width=250;
		setPreferredSize(size);
		setBorder(BorderFactory.createTitledBorder("Player Information"));
		
		// here I set up all my buttons and panel interactions 
		JButton button_Graph  = new JButton("Display Graph of Recent Hits ");
	
		final JTextField playerFeild = new JTextField(10);
		final JTextField graphField = new JTextField(10);
		
		JButton button = new JButton("Get player info");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String name = playerFeild.getText(); 
				
				fireDetailEvent(new DetailEvent(this,name));
				
				System.out.println(name);
				
			}
		});
			
	
		// here I set up the GridBagLayout
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints() ;
		// here I add the name text feild
		gc.fill =  GridBagConstraints.HORIZONTAL;
		gc.gridx = 0; 
		gc.gridy = 1; 
		add(playerFeild,gc);
		// here I add the get player info button 
		gc.gridx = 0; 
		gc.gridy = 0; 
		add(button,gc);
		// here I add the graph button 
		gc.gridx = 0; 
		gc.gridy = 2; 
		add(button_Graph,gc);
		//. Here I add the graph name feild
		gc.fill =  GridBagConstraints.HORIZONTAL;
		gc.gridx = 0; 
		gc.gridy = 3; 
		add(graphField,gc);
		
		
	}
	
	public void fireDetailEvent(DetailEvent event){
		
		Object[] listeners = listenerList.getListenerList();
		
		for(int i =0; i < listeners.length; i +=2){
			if(listeners[i] ==DetailListener.class ){
				((DetailListener)listeners[i+1]).detailEventOccurred(event);
			}
			
		}
		
	}
	public void addDetailsListener(DetailListener listener){
		ListenerList.add(DetailListener.class, listener);
		
		
	}
	public void removeDetailsListener(DetailListener listener){
		ListenerList.remove(DetailListener.class, listener);
	
	}

	
}