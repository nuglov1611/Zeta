package views.printing;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RPrintJob extends PrintJob {
    // BufferedOutp
    DataOutputStream dos = null;

    RGraphics g = null;

    String pname = "DEFAULT";

    String orientation = "PORTRAIT";

    protected boolean first = true;      // =true до первого запроса getGraphics()

    protected Socket sok = null;

    public RPrintJob(String host, int port, String pname, String or, int bsize)
            throws Exception {

        try {
            sok = new Socket(InetAddress.getByName(host), port);
            dos = new DataOutputStream(new BufferedOutputStream(sok
                    .getOutputStream(), bsize));
            g = new RGraphics(dos);
            if (pname != null) {
                this.pname = pname;
            }
            if (or != null) {
                orientation = or;
            }
        } catch (Exception e) {
            throw new Exception("can't create RRrintJob");
        }
    }

    @Override
    public void end() {
        try {
            dos.writeUTF(PrintConstants.END_PRINTING + "," + "1" + "," + "1");
        } catch (Exception e) {
        }
        // на всякий случай

        if (g != null) {
            g.flush();
        }
        try {
            dos.close();
            sok.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void finalize() {
    }

    @Override
    public Graphics getGraphics() {
        if (first) {
            first = false;
            try {
                dos.writeUTF(PrintConstants.BEGIN_PRINTING + "," + "1" + ","
                        + "1" + ",\"" + pname + "\"" + orientation);
            } catch (Exception e) {
            }
        }
        return g;
    }

    @Override
    public Dimension getPageDimension() {
        return null;
    }

    @Override
    public int getPageResolution() {
        return 0;
    }

    @Override
    public boolean lastPageFirst() {
        return false;
    }
}
