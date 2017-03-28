/*
 * MainWindow.java
 * 
 * Created on 25 Март 2009 г., 16:04
 */

package boot;

import java.awt.Toolkit;

import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import loader.ZetaProperties;

import org.apache.log4j.Logger;

/*
 * Стартовое окно системы. Через него осуществляется вход в
 * доступные информационные системы, а также настройки.
 * @author uglov
 * @author mmylnikova
 */
public class MainWindow extends JFrame {

    private static final Logger log = Logger.getLogger(MainWindow.class);

    private JPanel panelControl;

    protected JButton bEnter;

    private JPanel panelImage;

    protected JTextField txtName;

    private JLabel lblName;

    protected JPasswordField txtPassword;

    protected JButton bProperties;

    private JLabel lblPassword;

    protected JComboBox cbSessions;

    private JLabel lblSystem;

    private JLabel lblLogo;

    private Icon imageLogo;

    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();
    }

    private void initComponents() {
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                    getClass().getClassLoader().getResource(ZetaProperties.IMAGE_ICON)));
            imageLogo = new ImageIcon(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_LOGO));
        }
        catch (Exception e) {
            log.error("Shit happens", e);
        }

        panelImage = new JPanel();
        lblLogo = new JLabel();
        panelControl = new JPanel();
        lblName = new JLabel("Имя:");
        lblPassword = new JLabel("Пароль:");
        lblSystem = new JLabel("Сиcтема:");
        txtName = new JTextField();
        txtPassword = new JPasswordField();
        cbSessions = new JComboBox();
        bEnter = new JButton("Вход в систему");
        bProperties = new JButton("Настройки");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Вход в систему");
        setResizable(false);

        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setIcon(imageLogo);
        //lblLogo.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

        GroupLayout panelControlLayout = new GroupLayout(panelControl);
        panelControl.setLayout(panelControlLayout);
        panelControlLayout.setHorizontalGroup(
            panelControlLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelControlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelControlLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(bProperties, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(bEnter, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.LEADING, panelControlLayout.createSequentialGroup()
                        .addGroup(panelControlLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblPassword)
                            .addComponent(lblSystem)
                            .addComponent(lblName))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelControlLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbSessions, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        panelControlLayout.setVerticalGroup(
            panelControlLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelControlLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelControlLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelControlLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelControlLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSystem)
                    .addComponent(cbSessions, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bEnter, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bProperties, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addContainerGap())
        );

        GroupLayout panelImageLayout = new GroupLayout(panelImage);
        panelImage.setLayout(panelImageLayout);
        panelImageLayout.setHorizontalGroup(
            panelImageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(lblLogo)
        );
        panelImageLayout.setVerticalGroup(
            panelImageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(lblLogo)
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelImage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelControl, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(panelControl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelImage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }
}