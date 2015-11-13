import java.util.EventObject;


public class DetailEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private String text;
	public DetailEvent(Object source, String intext){
		super(source);
		this.text = intext;
		
	}
	
	public String getText(){
		return text;
	}
	
	
}
