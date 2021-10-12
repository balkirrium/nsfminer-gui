package nsfminer.gui.swing;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.miginfocom.swing.MigLayout;
import nsfminer.gui.api.Device;
import nsfminer.gui.api.Ping;
import nsfminer.gui.api.StatDetail;
import nsfminer.gui.statics.GlobalActions;
import nsfminer.gui.statics.PropService;
import nsfminer.gui.statics.RPCService;
import nsfminer.gui.statics.StatDetailService;
import nsfminer.gui.statics.SystrayHandler;

public class MainWindow {
	private static final Logger log = LogManager.getLogger(MainWindow.class);

	private JFrame frame;

	private JLabel lblPongValue;
	private JLabel lblHrInstanceValue;
	private JTabbedPane tabbedPane;
	private SystrayHandler systrayHandler;
	private Image imageRun;

	private Set<DeviceTab> deviceTabs = new HashSet<>();

	private StatDetail statDetail;

	private JButton btnStartInstance;

	private Process iostat;

	private JTextArea logArea;

	private JPanel instancePanel;

	private static MainWindow mainWindow;

	/**
	 * Create the application.
	 * 
	 * @throws Throwable
	 */
	private MainWindow() {
		initialize();
		systrayHandler = new SystrayHandler();
	}

	public static MainWindow getMainWindow() {
		if (mainWindow != null)
			return mainWindow;
		MainWindow nmainWindow = new MainWindow();
		mainWindow = nmainWindow;
		return mainWindow;
	}

	public void toggleFrameVisibility() {
		if (!frame.isVisible())
			frame.setVisible(true);
		else
			frame.setVisible(false);
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws Throwable
	 */
	private void initialize() {
		URL imageRunURL = this.getClass().getClassLoader().getResource("run.png");
		imageRun = Toolkit.getDefaultToolkit().getImage(imageRunURL);

		frame = new JFrame();
		frame.setBounds(100, 100, 950, 450);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		instancePanel = new JPanel();
		tabbedPane.addTab("Instance", null, instancePanel, null);
		instancePanel.setLayout(new MigLayout("", "[]", "[]"));

		JLabel lblPongTitle = new JLabel("Connection to service :");
		instancePanel.add(lblPongTitle, "cell 0 0");

		lblPongValue = new JLabel("");
		instancePanel.add(lblPongValue, "cell 1 0");

		lblHrInstanceValue = new JLabel("");
		instancePanel.add(lblHrInstanceValue, "cell 1 2");

		JLabel lblHrInstanceTitle = new JLabel("Instance hashrate :");
		instancePanel.add(lblHrInstanceTitle, "cell 0 2");

		JButton btnPause = new JButton("Pause ALL");
		instancePanel.add(btnPause, "cell 0 8");

		JButton btnResume = new JButton("Resume ALL");
		instancePanel.add(btnResume, "cell 1 8");

		btnPause.addActionListener(GlobalActions.pauseAllAction);
		btnResume.addActionListener(GlobalActions.resumeAllAction);

		frame.setIconImage(imageRun);
		frame.setTitle("nsfminer-gui");
		if (!PropService.getPropService().getBoolean(PropService.GUI_START_MINIMIZE))
			frame.setVisible(true);

		if (!PropService.getPropService().getBoolean(PropService.GUI_CLOSE_MINIMIZE))
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		else {
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					frame.setVisible(false);
				}
			});
		}
		SettingsTab p = new SettingsTab();
		tabbedPane.addTab("Options", p.getPanel());
		tabbedPane.addTab("About", new AboutTab().getPanel());
	}

	public void refreshFields() {
		Ping ping = null;
		try {
			ping = RPCService.getRpcClient().getPing();
		} catch (Throwable e) {
			log.error("Error connecting to nsfminer api", e);
			systrayHandler.setPauseImage();
			lblPongValue.setText("disconnected");
			if (PropService.getPropService().getBoolean(PropService.GUI_AUTOSTART_INSTANCE) && iostat == null) {
				startInstance();
			} else if (btnStartInstance == null && iostat == null) {
				startInstanceButton();
			}
			return;
		}

		lblPongValue.setText(ping.getPong());
		try {
			statDetail = StatDetailService.getStatDetailService().refresh();
		} catch (Throwable e) {
			log.error("Unable to refresh statDetails", e);
			return;
		}
		if ("0x00000000".equals(statDetail.getMining().getHashrateRaw()))
			systrayHandler.setPauseImage();
		else
			systrayHandler.setRunImage();
		lblHrInstanceValue.setText(statDetail.getMining().getHashrate());
		systrayHandler.refreshMenu();
		for (Device dev : statDetail.getDevices()) {
			systrayHandler.popupAddDevice(dev);
			DeviceTab deviceTab = new DeviceTab(tabbedPane, dev);
			boolean newElem = deviceTabs.add(deviceTab);
			if (newElem)
				deviceTab.draw();
			else
				deviceTab.refreshFields(dev);

		}
		List<DeviceTab> toRemove = new ArrayList<>();
		for (DeviceTab dTab : deviceTabs) {
			boolean contains = false;
			for (Device dev : statDetail.getDevices()) {
				DeviceTab deviceTab = new DeviceTab(tabbedPane, dev);
				if (deviceTab.equals(dTab))
					contains = true;
			}
			if (!contains)
				toRemove.remove(dTab);
		}
		for (DeviceTab dTab : toRemove)
			dTab.unDraw();
		systrayHandler.popupAddMainMenu();
//		if (iostat != null && logArea != null) {
//			int len;
//			try {
//				len = iostat.getInputStream().available();
//				if (logArea.getCaretPosition() > 99999)
//					logArea.setText("");
//				if (len > 0)
//					logArea.append(new String(iostat.getInputStream().readNBytes(len)));
//			} catch (IOException e) {
//				log.error("Error parsing nsfminer process logs", e);
//			}
//		}

	}

	private void startInstance() {
		GlobalActions.preResumeAction();

		List<String> cmd = new ArrayList<>();
		cmd.add(PropService.getPropService().getString(PropService.SERVER_EXE));
		cmd.addAll(Arrays.asList(PropService.getPropService().getString(PropService.SERVER_PARAMS).split(" ")));
		try {
			iostat = new ProcessBuilder().command(cmd).inheritIO().start();
		} catch (IOException e) {
			log.error("process server=" + String.join(" ", cmd), e);
			log.error("error creating process for server", e);
			return;
		}
//		logArea = new JTextArea();
//		logArea.setEditable(false);
//		logArea.setText("");
//		JScrollPane scroll = new JScrollPane(logArea);
//		tabbedPane.addTab("Logs", null, scroll, null);

	}

	private void startInstanceButton() {
		btnStartInstance = new JButton("Start instance");
		ActionListener startInstanceActionBtn = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startInstance();
				instancePanel.remove(btnStartInstance);
				btnStartInstance = null;
			}

		};
		btnStartInstance.addActionListener(startInstanceActionBtn);
		instancePanel.add(btnStartInstance, "cell 2 0");
	}

}
