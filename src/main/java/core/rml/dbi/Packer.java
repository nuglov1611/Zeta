package core.rml.dbi;

import java.io.InputStream;

public interface Packer {
    Object unpack(InputStream is);

    InputStream pack(Object o);
}
