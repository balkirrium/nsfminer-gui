package nsfminer.gui.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mslinks.ShellLink;
import net.miginfocom.swing.MigLayout;
import nsfminer.gui.statics.PropService;

public class SettingsTab {
	private static final Logger log = LogManager.getLogger(SettingsTab.class);

	private JPanel panel;

	private JLabel lblRestartReq;
	private JTextField textFieldPreResumeExe;
	private JTextField textFieldPreResumeParams;
	private JTextField textFieldPrePauseExe;
	private JTextField textFieldPrePauseParams;
	private JTextField textFieldClientHost;
	private JTextField textFieldClientPort;
	private JTextField textFieldInstanceExe;
	private JTextField textFieldInstanceParams;
	private JCheckBox chckbxStartMinimized;
	private JCheckBox chckbxResumeBlocking;
	private JCheckBox chckbxPauseBlocking;
	private JCheckBox chckbxMinimizeOnClose;
	private JCheckBox chckbxStartInstOnStartup;

	private JButton btnSave;

	private static final String PROP_PROPERTYFILE = "MAPPING_PROPERTY";

	public JPanel getPanel() {
		return panel;
	}

	public SettingsTab() {
		try {
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void refresh() {
		try {
			for (Field f : this.getClass().getDeclaredFields()) {
				if (f.getType().equals(JCheckBox.class) || f.getType().equals(JTextField.class)) {
					JComponent o;

					o = (JComponent) f.get(this);

					String propertyKey = (String) o.getClientProperty(PROP_PROPERTYFILE);
					if (propertyKey != null) {
						if (f.getType().equals(JCheckBox.class)) {
							JCheckBox p = (JCheckBox) o;
							p.setSelected(PropService.getPropService().getBoolean(propertyKey));
						} else if (f.getType().equals(JTextField.class)) {
							JTextField p = (JTextField) o;
							p.setText(PropService.getPropService().getString(propertyKey));
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			log.error("error resreshing settings", e);
		} catch (IllegalAccessException e) {
			log.error("error resreshing settings", e);
		}
		btnSave.setEnabled(activateSaveButton());

	}

	private boolean activateSaveButton() {
		try {

			for (Field f : this.getClass().getDeclaredFields()) {
				if (f.getType().equals(JCheckBox.class) || f.getType().equals(JTextField.class)) {
					JComponent o = (JComponent) f.get(this);
					String propertyKey = (String) o.getClientProperty(PROP_PROPERTYFILE);
					if (propertyKey != null) {
						if (f.getType().equals(JCheckBox.class)) {
							JCheckBox p = (JCheckBox) o;
							if (!p.isSelected() == PropService.getPropService().getBoolean(propertyKey))
								return true;
						} else if (f.getType().equals(JTextField.class)) {
							JTextField p = (JTextField) o;
							if (!p.getText().equals(PropService.getPropService().getString(propertyKey)))
								return true;
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			log.error("error checking settings", e);
		} catch (IllegalAccessException e) {
			log.error("error checking settings", e);
		}
		return false;

	}

	private void save() {
		try {
			for (Field f : this.getClass().getDeclaredFields()) {
				if (f.getType().equals(JCheckBox.class) || f.getType().equals(JTextField.class)) {
					JComponent o = (JComponent) f.get(this);
					String propertyKey = (String) o.getClientProperty(PROP_PROPERTYFILE);
					if (propertyKey != null) {
						if (f.getType().equals(JCheckBox.class)) {
							JCheckBox p = (JCheckBox) o;
							PropService.getPropService().setBoolean(propertyKey, p.isSelected());
						} else if (f.getType().equals(JTextField.class)) {
							JTextField p = (JTextField) o;
							PropService.getPropService().setString(propertyKey, p.getText());

						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			log.error("error saving settings", e);
		} catch (IllegalAccessException e) {
			log.error("error saving settings", e);
		}
		PropService.getPropService().save();
		lblRestartReq.setVisible(true);
		refresh();
	}

	private ActionListener modifiedAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			btnSave.setEnabled(activateSaveButton());
		}
	};

	private String serviceName = "nsfminer-gui";
	private final String STARTUP_PATH = "/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup";

	private boolean checkAutoStartPresent(File file) {
		return file.exists();
	}

	private ItemListener startWithWindowsAction = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			File file = new File(System.getProperty("user.home") + STARTUP_PATH + "/" + serviceName + ".lnk");
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (!checkAutoStartPresent(file)) {
					try {
						ShellLink shellLink = new ShellLink();
						shellLink.setTarget(System.getProperty("sun.boot.library.path") + "\\javaw.exe");
						shellLink.setCMDArgs("-Dgui.properties.file=" + System.getProperty("user.dir")
								+ "\\gui.properties -jar " + System.getProperty("sun.java.command"));
						shellLink.setWorkingDir(System.getProperty("user.dir"));
						shellLink.saveTo(file.getAbsolutePath());
					} catch (IOException e1) {
						log.error("IO error saving windows startup lnk", e);
					}
				}
			} else {
				if (checkAutoStartPresent(file)) {
					file.delete();
				}
			}
		}
	};

	private void initialize() throws UnknownHostException, IOException {
		DeferredDocumentChangedListener modifiedFieldListener = new DeferredDocumentChangedListener();
		modifiedFieldListener.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				btnSave.setEnabled(activateSaveButton());
			}
		});

		panel = new JPanel();
		panel.setBounds(100, 100, 450, 400);
		panel.setLayout(new MigLayout("", "[][grow]", "[]"));

		JLabel lblStartMinimized = new JLabel("Start minimized");
		panel.add(lblStartMinimized, "cell 0 0");

		chckbxStartMinimized = new JCheckBox("");
		panel.add(chckbxStartMinimized, "cell 1 0");
		chckbxStartMinimized.addActionListener(modifiedAction);
		chckbxStartMinimized.putClientProperty(PROP_PROPERTYFILE, PropService.GUI_START_MINIMIZE);

		JLabel lblMinimizeOnClose = new JLabel("Minimize on close");
		panel.add(lblMinimizeOnClose, "cell 0 1");

		chckbxMinimizeOnClose = new JCheckBox("");
		panel.add(chckbxMinimizeOnClose, "cell 1 1");
		chckbxMinimizeOnClose.addActionListener(modifiedAction);
		chckbxMinimizeOnClose.putClientProperty(PROP_PROPERTYFILE, PropService.GUI_CLOSE_MINIMIZE);

		JLabel lblStartInstanceOnStartup = new JLabel("Start instance on startup");
		panel.add(lblStartInstanceOnStartup, "cell 0 2");

		chckbxStartInstOnStartup = new JCheckBox("");
		panel.add(chckbxStartInstOnStartup, "cell 1 2");
		chckbxStartInstOnStartup.addActionListener(modifiedAction);
		chckbxStartInstOnStartup.putClientProperty(PROP_PROPERTYFILE, PropService.GUI_AUTOSTART_INSTANCE);

		if (SystemUtils.IS_OS_WINDOWS) {
			JLabel lblStartWithWindows = new JLabel("Start with Windows");
			panel.add(lblStartWithWindows, "cell 0 3");

			JCheckBox chckbxStartWithWindows = new JCheckBox("");
			File file = new File(System.getProperty("user.home") + STARTUP_PATH + "/" + serviceName + ".lnk");
			chckbxStartWithWindows.setSelected(checkAutoStartPresent(file));
			panel.add(chckbxStartWithWindows, "cell 1 3");
			chckbxStartWithWindows.addItemListener(startWithWindowsAction);
		}
		JLabel lblClientHost = new JLabel("Client host");
		panel.add(lblClientHost, "cell 0 4");

		textFieldClientHost = new JTextField();
		panel.add(textFieldClientHost, "cell 1 4,growx");
		textFieldClientHost.setColumns(10);
		textFieldClientHost.getDocument().addDocumentListener(modifiedFieldListener);
		textFieldClientHost.putClientProperty(PROP_PROPERTYFILE, PropService.CLIENT_HOST);

		JLabel lblClientPort = new JLabel("Client port");
		panel.add(lblClientPort, "cell 0 5");

		textFieldClientPort = new JTextField();
		panel.add(textFieldClientPort, "cell 1 5,growx");
		textFieldClientPort.setColumns(10);
		textFieldClientPort.getDocument().addDocumentListener(modifiedFieldListener);
		textFieldClientPort.putClientProperty(PROP_PROPERTYFILE, PropService.CLIENT_PORT);

		JLabel lblInstanceExe = new JLabel("Instance exe");
		panel.add(lblInstanceExe, "cell 0 6");

		textFieldInstanceExe = new JTextField();
		panel.add(textFieldInstanceExe, "cell 1 6,growx");
		textFieldInstanceExe.setColumns(10);
		textFieldInstanceExe.getDocument().addDocumentListener(modifiedFieldListener);
		textFieldInstanceExe.putClientProperty(PROP_PROPERTYFILE, PropService.SERVER_EXE);

		JLabel lblInstanceParams = new JLabel("Instance params");
		panel.add(lblInstanceParams, "cell 0 7");

		textFieldInstanceParams = new JTextField();
		panel.add(textFieldInstanceParams, "cell 1 7,growx");
		textFieldInstanceParams.setColumns(10);
		textFieldInstanceParams.getDocument().addDocumentListener(modifiedFieldListener);
		textFieldInstanceParams.putClientProperty(PROP_PROPERTYFILE, PropService.SERVER_PARAMS);

		btnSave = new JButton("Save");
		ActionListener saveAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		};
		btnSave.addActionListener(saveAction);

		JLabel lblPreResumeExe = new JLabel("Pre resume script exe");
		panel.add(lblPreResumeExe, "cell 0 8");

		textFieldPreResumeExe = new JTextField();
		panel.add(textFieldPreResumeExe, "cell 1 8,growx");
		textFieldPreResumeExe.setColumns(10);
		textFieldPreResumeExe.getDocument().addDocumentListener(modifiedFieldListener);
		textFieldPreResumeExe.putClientProperty(PROP_PROPERTYFILE, PropService.PRESCRIPT_RESUME_EXE);

		JLabel lblPreResumeParams = new JLabel("Pre resume script params");
		panel.add(lblPreResumeParams, "cell 0 9");

		textFieldPreResumeParams = new JTextField();
		panel.add(textFieldPreResumeParams, "cell 1 9,growx");
		textFieldPreResumeParams.setColumns(10);
		textFieldPreResumeParams.getDocument().addDocumentListener(modifiedFieldListener);
		textFieldPreResumeParams.putClientProperty(PROP_PROPERTYFILE, PropService.PRESCRIPT_RESUME_PARAMS);

		JLabel lblPreResumeBlocking = new JLabel("Pre resume script blocking");
		panel.add(lblPreResumeBlocking, "cell 0 10");

		chckbxResumeBlocking = new JCheckBox("");
		panel.add(chckbxResumeBlocking, "cell 1 10");
		chckbxResumeBlocking.addActionListener(modifiedAction);
		chckbxResumeBlocking.putClientProperty(PROP_PROPERTYFILE, PropService.PRESCRIPT_RESUME_BLOCKING);

		JLabel lblPrePauseExe = new JLabel("Pre pause script exe");
		panel.add(lblPrePauseExe, "cell 0 11");

		textFieldPrePauseExe = new JTextField();
		panel.add(textFieldPrePauseExe, "cell 1 11,growx");
		textFieldPrePauseExe.setColumns(10);
		textFieldPrePauseExe.getDocument().addDocumentListener(modifiedFieldListener);
		textFieldPrePauseExe.putClientProperty(PROP_PROPERTYFILE, PropService.PRESCRIPT_PAUSE_EXE);

		JLabel lblPrePauseParams = new JLabel("Pre pause script params");
		panel.add(lblPrePauseParams, "cell 0 12");

		textFieldPrePauseParams = new JTextField();
		panel.add(textFieldPrePauseParams, "cell 1 12,growx");
		textFieldPrePauseParams.setColumns(10);
		textFieldPrePauseParams.getDocument().addDocumentListener(modifiedFieldListener);
		textFieldPrePauseParams.putClientProperty(PROP_PROPERTYFILE, PropService.PRESCRIPT_PAUSE_PARAMS);

		JLabel lblPrePauseBlocking = new JLabel("Pre pause script blocking");
		panel.add(lblPrePauseBlocking, "cell 0 13");

		chckbxPauseBlocking = new JCheckBox("");
		panel.add(chckbxPauseBlocking, "cell 1 13");
		chckbxPauseBlocking.addActionListener(modifiedAction);
		chckbxPauseBlocking.putClientProperty(PROP_PROPERTYFILE, PropService.PRESCRIPT_PAUSE_BLOCKING);

		panel.add(btnSave, "flowx,cell 0 15");

		lblRestartReq = new JLabel("Restart required");
		lblRestartReq.setForeground(Color.RED);
		lblRestartReq.setVisible(false);
		panel.add(lblRestartReq, "cell 1 15");

		refresh();

	}

	public class DeferredDocumentChangedListener implements DocumentListener {

		private Timer timer;
		private List<ChangeListener> listeners;

		public DeferredDocumentChangedListener() {

			listeners = new ArrayList<>(25);
			timer = new Timer(250, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					fireStateChanged();
				}
			});
			timer.setRepeats(false);
		}

		public void addChangeListener(ChangeListener listener) {
			listeners.add(listener);
		}

		public void removeChangeListener(ChangeListener listener) {
			listeners.remove(listener);
		}

		protected void fireStateChanged() {
			if (!listeners.isEmpty()) {
				ChangeEvent evt = new ChangeEvent(this);
				for (ChangeListener listener : listeners) {
					listener.stateChanged(evt);
				}
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			timer.restart();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			timer.restart();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			timer.restart();
		}

	}
}
