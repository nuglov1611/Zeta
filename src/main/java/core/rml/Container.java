package core.rml;

import java.sql.SQLException;
import java.util.ArrayList;

import publicapi.RetrieveableAPI;
import publicapi.RmlContainerAPI;
import core.connection.BadPasswordException;
import core.document.Document;
import core.parser.Parser;
import core.parser.Proper;
import core.rml.dbi.exception.UpdateException;

public class Container {
	private ArrayList<RmlObject> children = new ArrayList<RmlObject>(); 
	
	private RmlContainerAPI parent = null;
	
	public Container(RmlContainerAPI rml_object){
		parent = rml_object;
	}
	
	
	public boolean addChildToCollection(RmlObject child) {
		if(child == null)
			return false;
		if(!children.contains(child)){
			child.addToContainer(parent);
			children.add(child);
		}
		return true;
	}
	
	public void addChild(RmlObject child) {
		if(addChildToCollection(child))
			parent.addChild(child);
	}
	
	public void addChildren(Proper prop, Document doc) throws Exception{
		addChildren(new Parser(doc).getContent(prop));
	}
	
	public void addChildren(RmlObject[] objs) throws Exception{
		for(RmlObject child : objs){
			addChild(child);
		}
		
		parent.initChildren();
	}

	public RmlObject[] getChildren() {
		final RmlObject[] ret = new RmlObject[children.size()];
		children.toArray(ret);
		return children.toArray(ret);
	}
	
	public boolean removeChild(RmlObject obj) {
		return children.remove(obj);
	}

    public void retrieveAll() throws Exception {
    	for(RmlObject child : children){
    		if(child instanceof RetrieveableAPI){
    			((RetrieveableAPI) child).retrieve();
    		}
    	}
    }

    public void toDSAll() {
    	for(RmlObject child : children){
    		if(child instanceof RetrieveableAPI){
    			((RetrieveableAPI) child).toDS();
    		}
    	}
    }
    
    public void fromDSAll() {
    	for(RmlObject child : children){
    		if(child instanceof RetrieveableAPI){
    			((RetrieveableAPI) child).fromDS();
    		}
    	}
    }


    public void updateAll() throws UpdateException, BadPasswordException, SQLException {
    	for(RmlObject child : children){
    		if(child instanceof RetrieveableAPI){
    			((RetrieveableAPI) child).update();
    		}
    	}
    }
}
