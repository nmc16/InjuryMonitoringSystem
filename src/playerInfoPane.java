import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class playerInfoPane extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	playerInfoPane(){
		Dimension size = getPreferredSize();
		size.width=250;
		setPreferredSize(size);
		setBorder(BorderFactory.createTitledBorder("Player Information"));
		
		textArea = new JTextArea("Player Info",250,100);
		textArea.setLineWrap(true);
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints() ;
		gc.fill =  GridBagConstraints.HORIZONTAL;
		gc.gridx = 0; 
		gc.gridy = 0; 
		add(textArea,gc);
	}
	
	public void addToPane(String add){
		
		textArea.append(add);
		
		
	}
}
