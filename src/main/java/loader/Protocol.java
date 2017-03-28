/*
 * File: Protocol.java
 * 
 * Created: Mon Mar 22 08:48:43 1999
 * 
 * Copyright(c) by Alexey Chen
 */

package loader;

public interface Protocol {
    public byte[] getByName_bytes(String path) throws Exception;

    public char[] getByName_chars(String path, boolean encoding)
            throws Exception;
}
