package nsfminer.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.miginfocom.swing.MigLayout;
import nsfminer.gui.api.Device;
import nsfminer.gui.statics.RPCService;

public class DeviceTab {
	private static final Logger log = LogManager.getLogger(DeviceTab.class);

	JPanel devicePanel;
	JLabel lblPausedValue;
	JLabel lblHrDeviceValue;
	Device dev;
	boolean drawn = false;
	private JTabbedPane tabbedPane;

	@Override
	public int hashCode() {
		return dev.getHardware().getPci().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DeviceTab)
			return ((DeviceTab) obj).dev.getHardware().getPci().equals(this.dev.getHardware().getPci());
		else
			return false;
	}

	public DeviceTab(JTabbedPane tabbedPane, Device dev) {
		this.dev = dev;
		this.tabbedPane = tabbedPane;
	}

	public void unDraw() {
		tabbedPane.remove(devicePanel);

	}

	public void draw() {
		devicePanel = new JPanel();
		tabbedPane.insertTab("Device " + dev.get_index(), null, devicePanel, null, tabbedPane.getTabCount() - 2);
		devicePanel.setLayout(new MigLayout("", "[]", "[]"));

		JLabel lblDevNameTitle = new JLabel("Name :");
		devicePanel.add(lblDevNameTitle, "cell 0 0");

		JLabel lblDevNameValue = new JLabel(dev.getHardware().getName());
		devicePanel.add(lblDevNameValue, "cell 1 0");

		JLabel lblPausedTitle = new JLabel("Paused :");
		devicePanel.add(lblPausedTitle, "cell 0 1");

		lblPausedValue = new JLabel(dev.getMining().isPaused() ? "yes" : "no");
		devicePanel.add(lblPausedValue, "cell 1 1");

		JLabel lblHrDeviceTitle = new JLabel("Hashrate :");
		devicePanel.add(lblHrDeviceTitle, "cell 0 2");

		lblHrDeviceValue = new JLabel(dev.getMining().getHashrate());
		devicePanel.add(lblHrDeviceValue, "cell 1 2");

		JButton btnPause = new JButton("Pause");
		devicePanel.add(btnPause, "cell 1 8");

		JButton btnResume = new JButton("Resume");
		devicePanel.add(btnResume, "cell 2 8");

		btnPause.addActionListener(pauseDevice);
		btnResume.addActionListener(resumeDevice);
		drawn = true;
	}

	public void refreshFields(Device dev) {
		if (!drawn)
			return;
		this.dev = dev;
		lblPausedValue.setText(dev.getMining().isPaused() ? "yes" : "no");
		lblHrDeviceValue.setText(dev.getMining().getHashrate());
	}

	ActionListener pauseDevice = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			try {
				RPCService.getRpcClient().pauseDevice(dev);
			} catch (Throwable e1) {
				log.error("Error pausing device " + dev.get_index(), e);
			}
		}

	};
	ActionListener resumeDevice = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			try {
				RPCService.getRpcClient().resumeDevice(dev);
			} catch (Throwable e1) {
				log.error("Error resuming device " + dev.get_index(), e);
			}
		}

	};

}
