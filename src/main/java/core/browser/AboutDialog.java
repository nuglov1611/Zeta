package core.browser;

import loader.ZetaProperties;
import org.jdesktop.swingx.JXHyperlink;
import properties.PropertyConstants;
import properties.Session;
import properties.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AboutDialog extends JDialog {
    static int width = 350;

    static int height = 315;

    JButton ok = new JButton("OK");

    JLabel l;

    public AboutDialog(String title, JFrame parent) {
        super(parent, title, true);

        if (UIManager.getLookAndFeel().getSupportsWindowDecorations()) {
            this.setUndecorated(true);
            this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        Session session = SessionManager.getIntance().getCurrentSession();
        String s = session.getProperty(PropertyConstants.DB_SERVER) + ":"
                + session.getProperty(PropertyConstants.DB_NAME);

        s = s.substring(s.indexOf("@") + 1);

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.getCalendar().setTimeInMillis(System.currentTimeMillis());
        l = new JLabel("© Zeta Core " + sdf.getCalendar().get(Calendar.YEAR) + " Current DB:" + s);
        JLabel zeta_version = new JLabel("Версия программы: "
                + ZetaProperties.CORE_VERSION);
        JLabel oracle_version = new JLabel("Версия Oracle: "
                + ZetaProperties.ORACLE_VERSION);
        Toolkit t = Toolkit.getDefaultToolkit();
        JLabel lblLogo = new JLabel();
        lblLogo.setVerticalAlignment(JLabel.CENTER);
        lblLogo.setHorizontalAlignment(JLabel.CENTER);
        lblLogo.setIcon(new ImageIcon(getClass().getClassLoader()
                .getResource(ZetaProperties.IMAGE_LOGO))); // NOI18N

        int sw = t.getScreenSize().width;
        int sh = t.getScreenSize().height;
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ok.addActionListener(new AL());
        JPanel p0 = new JPanel(new BorderLayout());
        JPanel p_labels = new JPanel(new GridLayout(4, 1));
        p_labels.add(l);
        p_labels.add(zeta_version);
        p_labels.add(oracle_version);
        JXHyperlink site = new JXHyperlink();
        try {
            site.setURI(new URI("http://zetacore.ru"));
        } catch (URISyntaxException e) {
        }
        site.setText("Zeta Core");
        p_labels.add(site);
        p0.add("South", p_labels);
        // p0.add("North",l1);
        p0.add("Center", lblLogo);
        JPanel p = new JPanel();
        p.add(ok);

        add("South", p);
        add("Center", p0);
    }

    class AL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(ok)) {
                dispose();
            }
        }
    }
}
