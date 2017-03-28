package core.rml.dbi;

import java.io.InputStream;

public interface Packer {
    public Object unpack(InputStream is);

    public InputStream pack(Object o);
}
