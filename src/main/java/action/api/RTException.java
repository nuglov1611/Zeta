/*
 * File: RTException.java
 * 
 * Created: Thu Apr 29 13:42:31 1999
 * 
 * 
 * 
 * Author: Alexey Chen
 */

package action.api;

public class RTException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String             type;

    public String             trap;

    public RTException() {
        super("calc.RTException");
    }

    public RTException(String type, String messag) {
        super(messag);
        this.type = type.trim().toUpperCase();
        trap = "";
    }

    public RTException(String type, String messag, String trap) {
        super(messag);
        this.type = type.trim().toUpperCase();
        this.trap = trap;
    }

    @Override
    public String toString() {
        return "RunTime:" + type + ":" + getMessage() + trap;
    }
}
