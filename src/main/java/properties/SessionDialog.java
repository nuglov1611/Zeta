package properties;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import loader.ZetaProperties;

public class SessionDialog extends JDialog implements FocusListener, ActionListener, WindowListener, CaretListener {

    private JButton cancelButton;
    private JButton saveButton;
    private JButton browseServerButton;
    private JButton browseFileButton;
    private JComboBox cbServerType;
    private JPanel dbPanel;
    private JLabel lblDbLogin;
    private JLabel lblDbPort;
    private JLabel lblDbServer;
    private JLabel lblDbname;
    private JLabel lblPassword;
    private JLabel lblRmlLogin;
    private JLabel lblRmlPassword;
    private JLabel lblRmlServer;
    private JLabel lblServerType;
    private JLabel lblSession;
    private JLabel lblStartDoc;
    private JPanel rmlPanel;
    private JTextField txtDbName;
    private JPasswordField txtDbPassword;
    private JTextField txtDbPort;
    private JTextField txtDbServer;
    private JTextField txtDbUsername;
    private JTextField txtRmlLogin;
    private JPasswordField txtRmlPassword;
    private JTextField txtRmlServer;
    private JTextField txtSession;
    private JTextField txtStartDoc;
    private JCheckBox cbAutoDownload;

    JFileChooser pathChooser;
    private Session session;

    private SettingsDialog dialog;

    /**
     * Creates new form SessionDialog
     */
    public SessionDialog(JFrame parent, SettingsDialog dialog, boolean modal) {
        super(parent, modal);
        this.dialog = dialog;
        initComponents(parent);
    }

    private void initComponents(JFrame parent) {
        ResourceBundle bundle = ResourceBundle
                .getBundle("properties/Bundle"); // NOI18N
        cancelButton = new JButton(bundle.getString("SessionDialog.cancelButton.text"));
        saveButton = new JButton(bundle.getString("SessionDialog.saveButton.text"));
        dbPanel = new JPanel();
        lblDbname = new JLabel(bundle.getString("SessionDialog.lblDbname.text"));
        txtDbName = new JTextField();
        lblDbLogin = new JLabel(bundle.getString("SessionDialog.lblDbLogin.text"));
        txtDbUsername = new JTextField();
        lblDbServer = new JLabel(bundle.getString("SessionDialog.lblDbServer.text"));
        txtDbServer = new JTextField();
        lblDbPort = new JLabel(bundle.getString("SessionDialog.lblDbPort.text"));
        txtDbPort = new JTextField();
        lblPassword = new JLabel(bundle.getString("SessionDialog.lblPassword.text"));
        txtDbPassword = new JPasswordField();
        rmlPanel = new JPanel();
        lblRmlServer = new JLabel(bundle.getString("SessionDialog.lblRmlServer.text"));
        txtRmlServer = new JTextField();
        lblStartDoc = new JLabel(bundle.getString("SessionDialog.lblStartDoc.text"));
        txtStartDoc = new JTextField();
        browseServerButton = new JButton();
        browseFileButton = new JButton();
        lblServerType = new JLabel(bundle.getString("SessionDialog.lblServerType.text"));
        cbServerType = new JComboBox();
        lblSession = new JLabel(bundle.getString("SessionDialog.lblSession.text"));
        txtSession = new JTextField();
        txtRmlLogin = new JTextField();
        txtRmlPassword = new JPasswordField();
        lblRmlLogin = new JLabel(bundle.getString("SessionDialog.lblRmlLogin.text"));
        lblRmlPassword = new JLabel(bundle.getString("SessionDialog.lblRmlPassword.text"));
        cbAutoDownload = new JCheckBox(bundle.getString("SessionDialog.cbAutoDownload.text"));

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
        setTitle(bundle.getString("SessionDialog.title")); // NOI18N
        setLocationByPlatform(true);
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_ICON)));

        cancelButton.addActionListener(this);
        saveButton.addActionListener(this);

        dbPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(204, 204, 204), 2, true), bundle
                .getString("SessionDialog.dbPanel.border.title"))); // NOI18N

        txtDbName.setName(PropertyConstants.DB_NAME);
        txtDbName.addFocusListener(this);

        txtDbUsername.setName(PropertyConstants.DB_USERNAME);
        txtDbUsername.addFocusListener(this);

        txtDbServer.setName(PropertyConstants.DB_SERVER);
        txtDbServer.addFocusListener(this);

        txtDbPort.setName(PropertyConstants.DB_PORT);
        txtDbPort.addFocusListener(this);

        txtDbPassword.setName(PropertyConstants.DB_PASSWORD);
        txtDbPassword.addFocusListener(this);

        GroupLayout dbPanelLayout = new GroupLayout(dbPanel);
        fillDbPanelLayout(dbPanelLayout);
        dbPanel.setLayout(dbPanelLayout);

        rmlPanel.setBorder(BorderFactory.createTitledBorder(bundle
                .getString("SessionDialog.rmlPanel.border.title")));

        txtRmlServer.setName(PropertyConstants.RML_SERVER);
        txtRmlServer.addFocusListener(this);

        txtStartDoc.setName(PropertyConstants.RML_START_DOC);
        txtStartDoc.addFocusListener(this);

        browseServerButton.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_BROWSE))));
        browseServerButton.addActionListener(this);

        browseFileButton.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_BROWSE))));
        browseFileButton.addActionListener(this);

        cbAutoDownload.addActionListener(this);

        cbServerType.setModel(new DefaultComboBoxModel(new String[]{PropertyConstants.FILE_PROTO, PropertyConstants.HTTP_PROTO}));
        cbServerType.setName(PropertyConstants.RML_SERVER_TYPE);
        cbServerType.addActionListener(this);

        txtRmlLogin.setName(PropertyConstants.RML_USERNAME);
        txtRmlLogin.addFocusListener(this);

        txtRmlPassword.setName(PropertyConstants.RML_PASSWORD);
        txtRmlPassword.addFocusListener(this);

        fillRmlPanelLayout();

        txtSession.setName(PropertyConstants.NAME);
        txtSession.addFocusListener(this);
        txtSession.addCaretListener(this);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        fillMainPanelLayout(layout);

        pack();

        pathChooser = new JFileChooser();
        pathChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    }

    private void fillMainPanelLayout(final GroupLayout layout) {
        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(lblSession)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSession, GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                        .addComponent(dbPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rmlPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(saveButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 204, Short.MAX_VALUE)
                .addComponent(cancelButton)
                .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(lblSession).addComponent(txtSession, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(dbPanel, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(rmlPanel, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(saveButton).addComponent(cancelButton))
                .addContainerGap()));
    }

    private void fillDbPanelLayout(final GroupLayout dbPanelLayout) {
        dbPanelLayout
                .setHorizontalGroup(dbPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(dbPanelLayout
                                .createSequentialGroup()
                                .addGroup(dbPanelLayout
                                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lblDbServer)
                                        .addComponent(txtDbServer, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(dbPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(txtDbPort, GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                                .addComponent(lblDbPort)))
                        .addComponent(lblDbname)
                        .addGroup(dbPanelLayout
                                .createSequentialGroup()
                                .addGroup(dbPanelLayout
                                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(txtDbUsername, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                        .addComponent(lblDbLogin))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(dbPanelLayout
                                .createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(dbPanelLayout
                                        .createSequentialGroup()
                                        .addComponent(lblPassword)
                                        .addGap(120, 120, 120))
                                .addComponent(txtDbPassword, GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)))
                        .addComponent(txtDbName, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE));
        dbPanelLayout
                .setVerticalGroup(dbPanelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(dbPanelLayout
                        .createSequentialGroup()
                        .addGroup(dbPanelLayout
                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblDbServer)
                                .addComponent(lblDbPort))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dbPanelLayout
                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDbServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDbPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDbname)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDbName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dbPanelLayout
                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblDbLogin)
                                .addComponent(lblPassword))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(dbPanelLayout
                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDbUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDbPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)));
    }

    private void fillRmlPanelLayout() {
        GroupLayout rmlPanelLayout = new GroupLayout(rmlPanel);
        rmlPanel.setLayout(rmlPanelLayout);
        rmlPanelLayout.setHorizontalGroup(
            rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(rmlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(cbAutoDownload)
                    .addGroup(rmlPanelLayout.createSequentialGroup()
                        .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblServerType)
                            .addComponent(lblRmlServer))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addGroup(rmlPanelLayout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbServerType, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                                .addComponent(lblRmlLogin)
                                .addGap(10, 10, 10))
                            .addGroup(rmlPanelLayout.createSequentialGroup()
                                .addComponent(lblRmlPassword)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRmlPassword, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRmlLogin, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblStartDoc)
                    .addGroup(rmlPanelLayout.createSequentialGroup()
                        .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtRmlServer, GroupLayout.PREFERRED_SIZE, 315, GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStartDoc, GroupLayout.PREFERRED_SIZE, 315, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(browseFileButton, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                            .addComponent(browseServerButton, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        rmlPanelLayout.setVerticalGroup(
            rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(rmlPanelLayout.createSequentialGroup()
                .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServerType)
                    .addComponent(txtRmlLogin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRmlLogin)
                    .addComponent(cbServerType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRmlPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRmlPassword)
                    .addComponent(lblRmlServer))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(browseServerButton)
                    .addComponent(txtRmlServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStartDoc)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rmlPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStartDoc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseFileButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbAutoDownload)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        JTextComponent component = (JTextComponent) e.getSource();
        session.setProperty(component.getName(), component.getText().trim());
    }

    public void initProperties(Session session) {
        if (session != null) {
            this.session = session;
            txtSession.setText(session.getProperty(PropertyConstants.NAME));
            txtDbServer.setText(session
                    .getProperty(PropertyConstants.DB_SERVER));
            txtDbPort.setText(session.getProperty(PropertyConstants.DB_PORT));
            txtDbName.setText(session.getProperty(PropertyConstants.DB_NAME));
            txtDbUsername.setText(session
                    .getProperty(PropertyConstants.DB_USERNAME));
            txtDbPassword.setText(session
                    .getProperty(PropertyConstants.DB_PASSWORD));
            txtRmlServer.setText(session
                    .getProperty(PropertyConstants.RML_SERVER));
            txtStartDoc.setText(session
                    .getProperty(PropertyConstants.RML_START_DOC));
            txtRmlLogin.setText(session
                    .getProperty(PropertyConstants.RML_USERNAME));
            txtRmlPassword.setText(session
                    .getProperty(PropertyConstants.RML_PASSWORD));
            cbServerType.setSelectedItem(getRmlServerType(session
                    .getProperty(PropertyConstants.RML_SERVER_TYPE)));
            session.setProperty(PropertyConstants.RML_SERVER_TYPE, cbServerType
                    .getSelectedItem().toString());
            cbAutoDownload.setSelected(PropertyManager.getBooleanProperty(session
                    .getProperty(PropertyConstants.RML_AUTO_DOWNLOAD)));
        }
    }

    private String getRmlServerType(String server) {
        if (server == null || "".equals(server)) {
            return PropertyConstants.FILE_PROTO;
        }
        return server;
    }

    public void resetProperties() {
        session = new Session();
        txtSession.setText("");
        txtDbServer.setText("");
        txtDbPort.setText("");
        txtDbName.setText("");
        txtDbUsername.setText("");
        txtDbPassword.setText("");
        txtRmlServer.setText("");
        txtStartDoc.setText("");
        txtRmlPassword.setText("");
        txtRmlLogin.setText("");
        cbServerType.setSelectedItem(0);
        cbAutoDownload.setSelected(false);
        session.setProperty(PropertyConstants.RML_SERVER_TYPE, cbServerType
                .getSelectedItem().toString());
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == saveButton) {
            SessionManager.getIntance().save(session);
            SettingsDialog.getInstance().updateSessions();
            setVisible(false);
            dialog.setVisible(true);
        } else if (source == cancelButton) {
            setVisible(false);
            dialog.setVisible(true);
        } else if (source == browseServerButton) {
            //получаем директорию откуда было запущено приложение
            URL startDirUrl = SettingsDialog.class.getProtectionDomain().getCodeSource().getLocation();
            File startDir = new File(startDirUrl.getPath());
            pathChooser.setCurrentDirectory(startDir);
            pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int selectedOption = pathChooser.showOpenDialog(this);
            if (selectedOption == JFileChooser.APPROVE_OPTION) {
//                 txtRmlServer.setText(pathChooser.getSelectedFile()
//                 .getAbsolutePath());
                txtRmlServer.setText(pathChooser.getSelectedFile().toURI()
                        .getPath().trim());
                session.setProperty(PropertyConstants.RML_SERVER, txtRmlServer
                        .getText());
            }
        } else if (source == browseFileButton) {
            //получаем директорию откуда было запущено приложение
            File startDir = new File(txtRmlServer.getText());
            if (!startDir.isDirectory()) {
                URL startDirUrl = SettingsDialog.class.getProtectionDomain().getCodeSource().getLocation();
                startDir = new File(startDirUrl.getPath());
            }
            pathChooser.setCurrentDirectory(startDir);
            pathChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int selectedOption = pathChooser.showOpenDialog(this);
            String fileName = "";
            if (selectedOption == JFileChooser.APPROVE_OPTION) {
//                 txtRmlServer.setText(pathChooser.getSelectedFile()
//                 .getAbsolutePath());
//                txtStartDoc.setText(pathChooser.getSelectedFile().toURI()
//                        .getPath().trim());
                fileName = pathChooser.getSelectedFile().getName().trim();
            }
            if (fileName != null && !"".equals(fileName)) {
                txtStartDoc.setText(fileName);
                session.setProperty(PropertyConstants.RML_START_DOC, fileName);
            }
        } else if (source == cbServerType) {
            if (cbServerType.getSelectedItem() != null) {
                session.setProperty(PropertyConstants.RML_SERVER_TYPE, cbServerType.getSelectedItem().toString());
                checkServerType();
            }
        } else if (source == cbAutoDownload) {
            String autoDownloadRml = PropertyManager.getXmlBooleanProperty(cbAutoDownload.isSelected());
            session.setProperty(PropertyConstants.RML_AUTO_DOWNLOAD, autoDownloadRml);
            setAutoDownload(cbAutoDownload.isSelected());
        }
    }

    private void checkServerType() {
        if (cbServerType.getSelectedItem().equals(PropertyConstants.FILE_PROTO)) {
            browseServerButton.setEnabled(true);
            browseFileButton.setEnabled(true);
            txtRmlLogin.setEnabled(false);
            txtRmlPassword.setEnabled(false);
        }
        else {
            browseServerButton.setEnabled(false);
            browseFileButton.setEnabled(false);
            txtRmlLogin.setEnabled(true);
            txtRmlPassword.setEnabled(true);
        }
    }

    private void setAutoDownload(final boolean isAuto) {
        if (!isAuto) {
            checkServerType();
        }
        else {
            browseServerButton.setEnabled(false);
            browseFileButton.setEnabled(false);
            txtRmlLogin.setEnabled(false);
            txtRmlPassword.setEnabled(false);
        }
        txtRmlServer.setEnabled(!isAuto);
        txtStartDoc.setEnabled(!isAuto);
        cbServerType.setEnabled(!isAuto);
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        setVisible(false);
        dialog.setVisible(true);
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void caretUpdate(CaretEvent e) {
        if (txtSession.getText().length() > 0) {
            saveButton.setEnabled(true);
        }
        else {
            saveButton.setEnabled(false);
        }
    }
}
