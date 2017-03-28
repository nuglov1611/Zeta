package properties;

import boot.Boot;
import core.browser.AboutDialog;
import loader.ZetaProperties;
import org.apache.log4j.Logger;
import views.MessageFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/*
 * The application's main frame.
 */
public class SettingsDialog extends JDialog implements ActionListener, FocusListener, WindowListener {

    private static final Logger log = Logger.getLogger(SettingsDialog.class);

    private JButton bDelSession;

    private JButton bEditSession;

    private JButton bCloneSession;

    private JButton bNewSession;

    private JCheckBox cbLoginAfterExit;

    private JCheckBox cbLoginAuto;

    private JCheckBox cbNtlmAuth;

    private JCheckBox cbProxyAuthorization;

    private JCheckBox cbUseProxy;

    private JCheckBox cbUseCache;

    private JPanel displayTabPanel;

    private JMenuItem exitMenuItem;

    private JComboBox cbProxyType;

    private JLabel lblDbname;

    private JLabel lblLogin;

    private JLabel lblPort;

    private JLabel lblProxyLogin;

    private JLabel lblProxyPassword;

    private JLabel lblProxyPort;

    private JLabel lblProxyServer;

    private JLabel lblProxyType;

    private JLabel lblReportZoom;

    private JLabel lblRmlServer;

    private JLabel lblServer;

    private JLabel lblCachePath;

    private JLabel lblAutoLoginSession;

    private JMenuItem loadMenuItem;

    private JPanel mainPanel;

    private JMenuBar menuBar;

    private JPanel previewPanel;

    private JPanel proxyTabPanel;

    private JPanel proxyPanel;

    private JPanel cachePanel;

    private JList sessionList;

    private JPanel sessionTabPanel;

    private JTabbedPane settingsPane;

    private JSlider slReportZoom;

    private JScrollPane spSessionList;

    private JTextField txtDbName;

    private JTextField txtLogin;

    private JTextField txtPort;

    private JTextField txtProxyLogin;

    private JPasswordField txtProxyPassword;

    private JTextField txtProxyPort;

    private JTextField txtProxyServer;

    private JTextField txtCachePath;

    private JTextField txtRmlServer;

    private JTextField txtServer;

    private Boot bootFrame;

    private SessionDialog sessionDialog;

    private Session selectedSession;

    private static SettingsDialog instance;

    private JMenu helpMenu;

    private JMenu settingsMenu;

    private JMenuItem aboutMenuItem;

    private JFileChooser settingsChooser;

    private ResourceBundle bundle;

    private JPanel sessionPanel;

    private JMenuItem saveAsItem;

    private JMenuItem saveAndExitMenuItem;

    private JComboBox cbSessions;

    private SettingsDialog() {
        initComponents();
        setSize(400, 500);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static SettingsDialog getInstance() {
        if (instance == null) {
            instance = new SettingsDialog();
        }
        return instance;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {

        bundle = ResourceBundle.getBundle("properties/Bundle"); // NOI18N

        mainPanel = new JPanel();
        settingsPane = new JTabbedPane();
        sessionTabPanel = new JPanel();
        previewPanel = new JPanel();
        lblServer = new JLabel(bundle.getString("SettingsDialog.lblServer.text"));
        lblPort = new JLabel(bundle.getString("SettingsDialog.lblPort.text"));
        txtLogin = new JTextField();
        txtDbName = new JTextField();
        lblDbname = new JLabel(bundle.getString("SettingsDialog.lblDbname.text"));
        txtServer = new JTextField();
        lblRmlServer = new JLabel(bundle.getString("SettingsDialog.lblRmlServer.text"));
        txtRmlServer = new JTextField();
        lblLogin = new JLabel(bundle.getString("SettingsDialog.lblLogin.text"));
        txtPort = new JTextField();
        sessionPanel = new JPanel();
        spSessionList = new JScrollPane();
        sessionList = new JList();
        bNewSession = new JButton(bundle.getString("SettingsDialog.bNewSession.text"));
        bEditSession = new JButton(bundle.getString("SettingsDialog.bEditSession.text"));
        bCloneSession = new JButton(bundle.getString("SettingsDialog.bCloneSession.text"));
        bDelSession = new JButton(bundle.getString("SettingsDialog.bDelSession.text"));

        displayTabPanel = new JPanel();
        cbLoginAfterExit = new JCheckBox(bundle.getString("SettingsDialog.cbLoginAfterExit.text"));
        cbLoginAuto = new JCheckBox(bundle.getString("SettingsDialog.cbLoginAuto.text"));
        slReportZoom = new JSlider();
        lblReportZoom = new JLabel(bundle.getString("SettingsDialog.lblReportZoom.text"));

        proxyTabPanel = new JPanel();
        proxyPanel = new JPanel();
        cachePanel = new JPanel();
        cbUseProxy = new JCheckBox(bundle.getString("SettingsDialog.cbUseProxy.text"));
        lblProxyType = new JLabel(bundle.getString("SettingsDialog.lblProxyType.text"));
        txtProxyServer = new JTextField();
        cbProxyType = new JComboBox();
        lblProxyServer = new JLabel(bundle.getString("SettingsDialog.lblProxyServer.text"));
        lblProxyPort = new JLabel(bundle.getString("SettingsDialog.lblProxyPort.text"));
        txtProxyPort = new JTextField();
        cbProxyAuthorization = new JCheckBox(bundle.getString("SettingsDialog.cbProxyAuthorization.text"));
        lblProxyLogin = new JLabel(bundle.getString("SettingsDialog.lblProxyLogin.text"));
        txtProxyLogin = new JTextField();
        lblProxyPassword = new JLabel(bundle.getString("SettingsDialog.lblProxyPassword.text"));
        cbUseCache = new JCheckBox(bundle.getString("SettingsDialog.cbUseCache.text"));
        lblCachePath = new JLabel(bundle.getString("SettingsDialog.lblCachePath.text"));
        txtCachePath = new JTextField();
        cbNtlmAuth = new JCheckBox(bundle.getString("SettingsDialog.cbNtlmAuth.text"));
        txtProxyPassword = new JPasswordField();
        cbSessions = new JComboBox();
        lblAutoLoginSession = new JLabel(bundle.getString("SettingsDialog.lblAutoLoginSession.text"));
        setTitle(bundle.getString("SettingsDialog.title"));
        setLocationByPlatform(true);
        setResizable(false);
        setIconImage(getToolkit().getImage(getClass().getClassLoader().getResource(ZetaProperties.IMAGE_ICON)));

        previewPanel.setBorder(BorderFactory.createTitledBorder(bundle
                .getString("SettingsDialog.previewPanel.border.title")));
        txtLogin.setEditable(false);
        txtDbName.setEditable(false);
        txtServer.setEditable(false);
        txtRmlServer.setEditable(false);
        txtPort.setEditable(false);

        addWindowListener(this);

        initPreviewPanelLayout();

        sessionPanel.setBorder(BorderFactory.createTitledBorder(bundle
                .getString("SettingsDialog.savedSessionPanel.border.title")));

        sessionList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                sessionListValueChanged(evt);
            }
        });
        spSessionList.setViewportView(sessionList);

        bNewSession.addActionListener(this);
        bCloneSession.addActionListener(this);
        bEditSession.addActionListener(this);
        bDelSession.addActionListener(this);
        bEditSession.setEnabled(false);
        bCloneSession.setEnabled(false);
        bDelSession.setEnabled(false);

        initSessionPanelLayout();

        initSettingsTabLayout();

        settingsPane.addTab(bundle.getString("SettingsDialog.sessionTab.TabConstraints.tabTitle"), sessionTabPanel);

        slReportZoom.setMajorTickSpacing(25);
        slReportZoom.setMaximum(200);
        slReportZoom.setMinimum(25);
        slReportZoom.setMinorTickSpacing(25);
        slReportZoom.setPaintLabels(true);
        slReportZoom.setPaintTicks(true);
        slReportZoom.setSnapToTicks(true);
        slReportZoom.setValue(125);

        proxyPanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("SettingsDialog.proxyPanel.border.title"))); // NOI18N
        cachePanel.setBorder(BorderFactory.createTitledBorder(bundle.getString("SettingsDialog.cachePanel.border.title"))); // NOI18N

        cbProxyType.setModel(new DefaultComboBoxModel(new String[]{
                PropertyConstants.PROXY_HTTP,
                PropertyConstants.PROXY_HTTPS,
                PropertyConstants.PROXY_SOCKS4,
                PropertyConstants.PROXY_SOCKS4A,
                PropertyConstants.PROXY_SOCKS5,}));
        cbSessions.addActionListener(this);
        cbUseProxy.addActionListener(this);
        txtProxyServer.setName(PropertyConstants.PROXY_SERVER);
        txtProxyServer.addFocusListener(this);
        txtProxyPort.setName(PropertyConstants.PROXY_PORT);
        txtProxyPort.addFocusListener(this);
        txtProxyLogin.setName(PropertyConstants.PROXY_LOGIN);
        txtProxyLogin.addFocusListener(this);
        txtProxyPassword.setName(PropertyConstants.PROXY_PASSWORD);
        txtProxyPassword.addFocusListener(this);
        cbProxyAuthorization.addActionListener(this);
        cbLoginAuto.addActionListener(this);
        cbNtlmAuth.addActionListener(this);
        cbUseCache.addActionListener(this);
        txtCachePath.setName(PropertyConstants.CACHE_PATH);
        txtCachePath.addFocusListener(this);

        initProxyPanelLayout();

        initCachePanelLayout();

        initDisplayTabLayout();

        initProxyTabLayout();

        settingsPane.addTab(bundle.getString("SettingsDialog.displayTab.TabConstraints.tabTitle"), displayTabPanel);

        settingsPane.addTab(bundle.getString("SettingsDialog.proxyTab.TabConstraints.tabTitle"), proxyTabPanel);

        GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).
                addComponent(settingsPane, GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE));
        mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).
                addComponent(settingsPane, GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE));

        getContentPane().add(mainPanel, BorderLayout.CENTER);

        initMenuBar();

        settingsChooser = new JFileChooser();
        settingsChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        settingsChooser.setFileFilter(new FileNameExtensionFilter(".xml zeta file", "xml"));
    }

    private void initDisplayTabLayout() {
        GroupLayout displayTabLayout = new GroupLayout(displayTabPanel);
        displayTabPanel.setLayout(displayTabLayout);
        displayTabLayout.setHorizontalGroup(
                displayTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(slReportZoom, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbLoginAuto)
                        .addGroup(displayTabLayout.createSequentialGroup()
                                .addComponent(lblAutoLoginSession)
                                .addComponent(cbSessions))
                        .addComponent(cbLoginAfterExit)
                        .addGap(31, 31, 31)
                        .addComponent(lblReportZoom)
        );
        displayTabLayout.setVerticalGroup(
                displayTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(displayTabLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(cbLoginAuto, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()
                                .addGroup(displayTabLayout.createParallelGroup()
                                        .addComponent(lblAutoLoginSession, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cbSessions, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                )
                                .addComponent(cbLoginAfterExit)
                                .addGap(19, 19, 19)
                                .addComponent(lblReportZoom)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(slReportZoom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
    }

    private void initProxyPanelLayout() {
        GroupLayout proxyPanelLayout = new GroupLayout(proxyPanel);
        proxyPanel.setLayout(proxyPanelLayout);
        proxyPanelLayout.setHorizontalGroup(
                proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(proxyPanelLayout.createSequentialGroup()
                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(GroupLayout.Alignment.LEADING, proxyPanelLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addGroup(proxyPanelLayout.createSequentialGroup()
                                                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                        .addComponent(lblProxyType)
                                                                        .addComponent(lblProxyPort))
                                                                .addGap(20, 20, 20))
                                                        .addGroup(proxyPanelLayout.createSequentialGroup()
                                                                .addComponent(lblProxyServer)
                                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)))
                                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(cbProxyType, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtProxyServer)
                                                        .addComponent(txtProxyPort, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)))
                                        .addComponent(cbUseProxy, GroupLayout.Alignment.LEADING))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(proxyPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(proxyPanelLayout.createSequentialGroup()
                                                .addComponent(cbProxyAuthorization, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(cbNtlmAuth))
                                        .addGroup(proxyPanelLayout.createSequentialGroup()
                                                .addComponent(lblProxyLogin)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtProxyLogin, GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
                                        .addGroup(proxyPanelLayout.createSequentialGroup()
                                                .addComponent(lblProxyPassword)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtProxyPassword, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        proxyPanelLayout.setVerticalGroup(
                proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(proxyPanelLayout.createSequentialGroup()
                                .addComponent(cbUseProxy)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cbProxyType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblProxyType))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblProxyServer)
                                        .addComponent(txtProxyServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblProxyPort)
                                        .addComponent(txtProxyPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cbProxyAuthorization)
                                        .addComponent(cbNtlmAuth))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblProxyLogin)
                                        .addComponent(txtProxyLogin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(proxyPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblProxyPassword)
                                        .addComponent(txtProxyPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(27, Short.MAX_VALUE))
        );
    }

    private void initCachePanelLayout() {
        GroupLayout cachePanelLayout = new GroupLayout(cachePanel);
        cachePanel.setLayout(cachePanelLayout);
        cachePanelLayout.setHorizontalGroup(
                cachePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(cbUseCache)
                        .addGroup(cachePanelLayout.createSequentialGroup()
                                .addComponent(lblCachePath)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCachePath, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)));

        cachePanelLayout.setVerticalGroup(
                cachePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(cachePanelLayout.createSequentialGroup()
                                .addComponent(cbUseCache)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(cachePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblCachePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtCachePath))
                        ));
    }

    private void initSessionPanelLayout() {
        GroupLayout sessionPanelLayout = new GroupLayout(sessionPanel);
        sessionPanel.setLayout(sessionPanelLayout);
        sessionPanelLayout
                .setHorizontalGroup(sessionPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(spSessionList, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sessionPanelLayout
                                .createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(bNewSession, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                .addComponent(bEditSession, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                .addComponent(bCloneSession, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                .addComponent(bDelSession, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)));
        sessionPanelLayout
                .setVerticalGroup(sessionPanelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(sessionPanelLayout
                                .createSequentialGroup()
                                .addComponent(bNewSession)
                                .addGap(18, 18, 18)
                                .addComponent(bEditSession)
                                .addGap(18, 18, 18)
                                .addComponent(bCloneSession)
                                .addGap(18, 18, 18)
                                .addComponent(bDelSession))
                        .addComponent(spSessionList, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE));
    }

    private void initPreviewPanelLayout() {
        GroupLayout previewPanelLayout = new GroupLayout(previewPanel);
        previewPanel.setLayout(previewPanelLayout);
        previewPanelLayout
                .setHorizontalGroup(previewPanelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(previewPanelLayout
                                .createSequentialGroup()
                                .addContainerGap()
                                .addGroup(previewPanelLayout
                                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(lblRmlServer)
                                        .addGroup(previewPanelLayout
                                                .createSequentialGroup()
                                                .addGroup(previewPanelLayout
                                                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtDbName, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                                                        .addComponent(txtServer, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                                                        .addComponent(lblServer)
                                                        .addComponent(lblDbname))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(previewPanelLayout
                                                        .createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(txtPort, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                                        .addComponent(txtLogin, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                                        .addComponent(lblLogin, GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblPort, GroupLayout.Alignment.LEADING)))
                                        .addComponent(txtRmlServer, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))
                                .addContainerGap()));
        previewPanelLayout
                .setVerticalGroup(previewPanelLayout
                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(previewPanelLayout
                                .createSequentialGroup()
                                .addContainerGap()
                                .addGroup(previewPanelLayout
                                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblDbname)
                                        .addComponent(lblLogin))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(previewPanelLayout
                                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtDbName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtLogin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(previewPanelLayout
                                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblServer)
                                        .addComponent(lblPort))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(previewPanelLayout
                                        .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblRmlServer)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRmlServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }

    private void initProxyTabLayout() {
        GroupLayout proxyTabLayout = new GroupLayout(proxyTabPanel);
        proxyTabPanel.setLayout(proxyTabLayout);
        proxyTabLayout.setHorizontalGroup(proxyTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING).
                addComponent(proxyPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).
                addComponent(cachePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        proxyTabLayout.setVerticalGroup(proxyTabLayout.createSequentialGroup().
                addComponent(proxyPanel, 0, 0, Short.MAX_VALUE).
                addComponent(cachePanel, 0, 0, Short.MAX_VALUE));
    }

    private void initSettingsTabLayout() {
        GroupLayout settingsTabLayout = new GroupLayout(sessionTabPanel);
        sessionTabPanel.setLayout(settingsTabLayout);
        settingsTabLayout
                .setHorizontalGroup(settingsTabLayout
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(settingsTabLayout
                                .createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(sessionPanel)
                                .addComponent(previewPanel))
                        .addContainerGap());
        settingsTabLayout.setVerticalGroup(settingsTabLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(settingsTabLayout.createSequentialGroup().addContainerGap()
                .addComponent(sessionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(previewPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap()));
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();
        settingsMenu = new JMenu(bundle.getString("SettingsDialog.settingsMenu.text"));
        loadMenuItem = new JMenuItem(bundle.getString("SettingsDialog.loadMenuItem.text"));
        saveAsItem = new JMenuItem(bundle.getString("SettingsDialog.saveAsItem.text"));
        saveAndExitMenuItem = new JMenuItem(bundle.getString("SettingsDialog.saveAndExitMenuItem.text"));
        exitMenuItem = new JMenuItem(bundle.getString("SettingsDialog.exitMenuItem.text"));
        helpMenu = new JMenu(bundle.getString("SettingsDialog.helpMenu.text"));
        aboutMenuItem = new JMenuItem(bundle.getString("SettingsDialog.aboutMenuItem.text"));

        loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        loadMenuItem.addActionListener(this);
        settingsMenu.add(loadMenuItem);
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        saveAsItem.addActionListener(this);
        settingsMenu.add(saveAsItem);
        saveAndExitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveAndExitMenuItem.addActionListener(this);
        settingsMenu.add(saveAndExitMenuItem);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        exitMenuItem.addActionListener(this);
        settingsMenu.add(exitMenuItem);
        menuBar.add(settingsMenu);
        aboutMenuItem.addActionListener(this);
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void clearPreview() {
        txtServer.setText("");
        txtPort.setText("");
        txtDbName.setText("");
        txtLogin.setText("");
        txtRmlServer.setText("");
        bEditSession.setEnabled(false);
        bCloneSession.setEnabled(false);
        bDelSession.setEnabled(false);
    }

    private void initPreview(Session selectedSession) {
        if (selectedSession != null) {
            txtServer.setText(selectedSession
                    .getProperty(PropertyConstants.DB_SERVER));
            txtPort.setText(selectedSession
                    .getProperty(PropertyConstants.DB_PORT));
            txtDbName.setText(selectedSession
                    .getProperty(PropertyConstants.DB_NAME));
            txtLogin.setText(selectedSession
                    .getProperty(PropertyConstants.DB_USERNAME));
            txtRmlServer.setText(selectedSession
                    .getProperty(PropertyConstants.RML_SERVER));
            bEditSession.setEnabled(true);
            bCloneSession.setEnabled(true);
            bDelSession.setEnabled(true);
        } else {
            clearPreview();
        }
    }

    private void showSessionDialog(boolean isNew) {
        if (sessionDialog == null) {
            sessionDialog = new SessionDialog(bootFrame, this, true);
            sessionDialog.setLocationRelativeTo(null);
        }
        if (isNew) {
            sessionDialog.resetProperties();
        } else {
            sessionDialog.initProperties(selectedSession);
        }
        setVisible(false);
        sessionDialog.setVisible(true);
    }

    private void sessionListValueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting() && evt.getLastIndex() >= 0) {
            selectedSession = (Session) sessionList.getSelectedValue();
            initPreview(selectedSession);
        }
    }

    private void loadSettings() {
        try {
            String loginAuto = PropertyManager.getIntance().getProperty(PropertyConstants.LOGIN_AUTO);
            cbLoginAuto.setSelected(PropertyManager.getBooleanProperty(loginAuto));
            String loginAfterExit = PropertyManager.getIntance().getProperty(PropertyConstants.LOGIN_AFTER_EXIT);
            cbLoginAfterExit.setSelected(PropertyManager.getBooleanProperty(loginAfterExit));
            String reportZoom = PropertyManager.getIntance().getProperty(PropertyConstants.REPORT_ZOOM);
            slReportZoom.setValue(PropertyManager.getIntegerProperty(reportZoom));
            String useProxy = PropertyManager.getIntance().getProperty(PropertyConstants.USE_PROXY);
            cbUseProxy.setSelected(PropertyManager.getBooleanProperty(useProxy));
            String proxyType = PropertyManager.getIntance().getProperty(PropertyConstants.PROXY_TYPE);
            cbProxyType.setSelectedItem(getProxyType(proxyType));
            String proxyServer = PropertyManager.getIntance().getProperty(PropertyConstants.PROXY_SERVER);
            txtProxyServer.setText(proxyServer);
            String proxyPort = PropertyManager.getIntance().getProperty(PropertyConstants.PROXY_PORT);
            txtProxyPort.setText(proxyPort);
            String proxyAuth = PropertyManager.getIntance().getProperty(PropertyConstants.PROXY_AUTH);
            cbProxyAuthorization.setSelected(PropertyManager.getBooleanProperty(proxyAuth));
            String proxyLogin = PropertyManager.getIntance().getProperty(PropertyConstants.PROXY_LOGIN);
            txtProxyLogin.setText(proxyLogin);
            String proxyPassword = PropertyManager.getIntance().getProperty(PropertyConstants.PROXY_PASSWORD);
            txtProxyPassword.setText(proxyPassword);
            String proxyNtlm = PropertyManager.getIntance().getProperty(PropertyConstants.PROXY_NTLM);
            cbNtlmAuth.setSelected(PropertyManager.getBooleanProperty(proxyNtlm));
            setProxyEnabled(cbUseProxy.isSelected(), cbProxyAuthorization.isSelected());
            String useCache = PropertyManager.getIntance().getProperty(PropertyConstants.USE_CACHE);
            cbUseCache.setSelected(PropertyManager.getBooleanProperty(useCache));
            txtCachePath.setEnabled(cbUseCache.isSelected());
            String cachePath = PropertyManager.getIntance().getProperty(PropertyConstants.CACHE_PATH);
            txtCachePath.setText(cachePath);
            settingsPane.setSelectedIndex(0);
            updateSessions();
            String sessionId = PropertyManager.getIntance().getProperty(PropertyConstants.DEFAULT_LOGIN_SESSION);
            cbSessions.setSelectedItem(SessionManager.getIntance().getSessionById(sessionId));
            cbSessions.setEnabled(cbLoginAuto.isSelected());
        } catch (Exception e) {
            log.error("Shit happens");
        }
    }

    private String getProxyType(String proxyType) {
        if ("".equals(proxyType)) {
            return PropertyConstants.PROXY_HTTP;
        }
        return proxyType;
    }

    public void updateSessions() {
        Object[] sessions = SessionManager.getIntance().getSessions().toArray();
        Arrays.sort(sessions);
        sessionList.setListData(sessions);
        cbSessions.setModel(new DefaultComboBoxModel(sessions));
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == bNewSession) {
            showSessionDialog(true);
        } else if (source == bEditSession) {
            showSessionDialog(false);
        } else if (source == bCloneSession) {
            int selectedIndex = sessionList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Object selectedSession = sessionList.getModel().getElementAt(selectedIndex);
                if (selectedSession != null && selectedSession instanceof Session) {
                    String sessionName =
                            MessageFactory.getInstance().showInputMessage(this,
                                    "¬ведите им€ новой сессии",
                                    ((Session) selectedSession).getProperty(PropertyConstants.NAME) + " clone");
                    if (!sessionName.equals(MessageFactory.CANCEL_INPUT)) {
                        Session cloneSession = ((Session) selectedSession).clone(sessionName);
                        SessionManager.getIntance().save(cloneSession);
                        updateSessions();
                    }
                }
            }
        } else if (source == bDelSession) {
            int selectedIndex = sessionList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Object selectedSession = sessionList.getModel().getElementAt(selectedIndex);
                if (selectedSession != null && selectedSession instanceof Session) {
                    PropertyManager.getIntance().removeProperty((Session) selectedSession);
                    updateSessions();
                }
            }
        } else if (source == loadMenuItem) {
            settingsChooser.setDialogTitle(bundle.getString("SettingsDialog.loadMenuItem.loadTitle"));
            String approveText = bundle.getString("SettingsDialog.loadMenuItem.loadApprove");
            //получаем директорию откуда было запущено приложение
            URL startDirUrl = SettingsDialog.class.getProtectionDomain().getCodeSource().getLocation();
            File startDir = new File(startDirUrl.getPath());
            settingsChooser.setCurrentDirectory(startDir);
            int selectResult = settingsChooser.showDialog(this, approveText);
            if (selectResult == JFileChooser.APPROVE_OPTION) {
                File settingsFile = settingsChooser.getSelectedFile();
                PropertyManager.getIntance().loadProperties(settingsFile.getAbsolutePath());
                loadSettings();
            }
        } else if (source == saveAsItem) {
            settingsChooser.setDialogTitle(bundle.getString("SettingsDialog.saveAsItem.saveTitle"));
            String approveText = bundle.getString("SettingsDialog.saveAsItem.saveApprove");
            //получаем директорию откуда было запущено приложение
            URL startDirUrl = SettingsDialog.class.getProtectionDomain().getCodeSource().getLocation();
            File startDir = new File(startDirUrl.getPath());
            settingsChooser.setCurrentDirectory(startDir);
            int selectResult = settingsChooser.showDialog(this, approveText);
            if (selectResult == JFileChooser.APPROVE_OPTION) {
                File settingsFile = settingsChooser.getSelectedFile();
                PropertyManager.getIntance().saveToNewFile(settingsFile);
            }
        } else if (source == saveAndExitMenuItem) {
            String loginAuto = PropertyManager.getXmlBooleanProperty(cbLoginAuto.isSelected());
            PropertyManager.getIntance().saveProperty(PropertyConstants.LOGIN_AUTO, loginAuto);
            String loginAfterExit = PropertyManager.getXmlBooleanProperty(cbLoginAfterExit.isSelected());
            PropertyManager.getIntance().saveProperty(PropertyConstants.LOGIN_AFTER_EXIT, loginAfterExit);
            String reportZoom = String.valueOf(slReportZoom.getValue());
            PropertyManager.getIntance().saveProperty(PropertyConstants.REPORT_ZOOM, reportZoom);
            if (cbLoginAuto.isSelected()) {
                Session loginSession = (Session) cbSessions.getSelectedItem();
                if (loginSession != null) {
                    PropertyManager.getIntance().saveProperty(PropertyConstants.DEFAULT_LOGIN_SESSION, loginSession.getId());
                }
            }
            setVisible(false);
            bootFrame.updateSessions(true);
        } else if (source == exitMenuItem) {
            setVisible(false);
            bootFrame.updateSessions(true);
        } else if (source == aboutMenuItem) {
            final AboutDialog aboutDialog = new AboutDialog("ќ программе", bootFrame);
            if (SwingUtilities.isEventDispatchThread())
                aboutDialog.setVisible(true);
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        aboutDialog.setVisible(true);
                    }
                });
            }
        } else if (source == cbUseProxy) {
            String useProxy = PropertyManager.getXmlBooleanProperty(cbUseProxy.isSelected());
            PropertyManager.getIntance().saveProperty(PropertyConstants.USE_PROXY, useProxy);
            setProxyEnabled(cbUseProxy.isSelected(), cbProxyAuthorization.isSelected());
        } else if (source == cbProxyAuthorization) {
            String proxyAuth = PropertyManager.getXmlBooleanProperty(cbProxyAuthorization.isSelected());
            PropertyManager.getIntance().saveProperty(PropertyConstants.PROXY_AUTH, proxyAuth);
            setProxyEnabled(cbUseProxy.isSelected(), cbProxyAuthorization.isSelected());
        } else if (source == cbUseCache) {
            String useCache = PropertyManager.getXmlBooleanProperty(cbUseCache.isSelected());
            PropertyManager.getIntance().saveProperty(PropertyConstants.USE_CACHE, useCache);
            txtCachePath.setEnabled(cbUseCache.isSelected());
        } else if (source == cbNtlmAuth) {
            String ntlmAuth = PropertyManager.getXmlBooleanProperty(cbNtlmAuth.isSelected());
            PropertyManager.getIntance().saveProperty(PropertyConstants.PROXY_NTLM, ntlmAuth);
        } else if (source == cbLoginAuto) {
            cbSessions.setEnabled(cbLoginAuto.isSelected());
        }
    }

    private void setProxyEnabled(boolean useProxy, boolean useAuth) {
        txtProxyServer.setEnabled(useProxy);
        txtProxyPort.setEnabled(useProxy);
        cbProxyAuthorization.setEnabled(useProxy);
        txtProxyLogin.setEnabled(useAuth);
        txtProxyPassword.setEnabled(useAuth);
        cbNtlmAuth.setEnabled(useAuth);
    }

    public void show(final Boot mainFrame) {
        this.bootFrame = mainFrame;
        setLocationRelativeTo(null);
        loadSettings();
        setVisible(true);
    }

    public void focusGained(FocusEvent e) {
    }

    public void focusLost(FocusEvent e) {
        JTextComponent component = (JTextComponent) e.getSource();
        String propValue = component.getText();
        PropertyManager.getIntance().saveProperty(component.getName(), propValue);
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        bootFrame.updateSessions(true);
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
}
