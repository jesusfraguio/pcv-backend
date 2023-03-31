package es.udc.pcv.backend.model.exceptions;

@SuppressWarnings("serial")
public class DuplicateInstanceException extends InstanceException {

    public DuplicateInstanceException(String name, Object key) {
    	super(name, key); 	
    }
    
}
