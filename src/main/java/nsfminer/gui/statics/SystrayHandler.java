package nsfminer.gui.statics;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nsfminer.gui.api.Device;

public class SystrayHandler {
	private static final Logger log = LogManager.getLogger(SystrayHandler.class);

	private SystemTray tray;
	private TrayIcon trayIcon;

	private Image imagePause;
	private Image imageRun;
	private PopupMenu popup;

	public SystrayHandler() {
		initSysTray();
	}

	private void initSysTray() {
		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			ClassLoader cldr = this.getClass().getClassLoader();
			URL imagePauseURL = cldr.getResource("pause.png");
			URL imageRunURL = cldr.getResource("run.png");
			imagePause = Toolkit.getDefaultToolkit().getImage(imagePauseURL);
			imageRun = Toolkit.getDefaultToolkit().getImage(imageRunURL);

			popup = new PopupMenu();
			popupAddMainMenu();

			trayIcon = new TrayIcon(imagePause, "nsfminer-gui", popup);
			trayIcon.addActionListener(GlobalActions.toggleVisibilityAction);
			trayIcon.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (MouseEvent.BUTTON2 == e.getButton()) {
						RPCService.getRpcClient().close();
						System.exit(0);
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}
			});
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				log.error("Setting up systray icon : ", e);
			}
		}

	}

	public void refreshMenu() {
		popup = new PopupMenu();
		trayIcon.setPopupMenu(popup);
	}

	public void setPauseImage() {
		trayIcon.setImage(imagePause);
	}

	public void setRunImage() {
		trayIcon.setImage(imageRun);
	}

	public void popupAddMainMenu() {
		popup.addSeparator();
		MenuItem resumeItem = new MenuItem("Resume ALL");
		resumeItem.addActionListener(GlobalActions.resumeAllAction);
		popup.add(resumeItem);

		MenuItem pauseItem = new MenuItem("Pause ALL");
		pauseItem.addActionListener(GlobalActions.pauseAllAction);
		popup.add(pauseItem);

		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(GlobalActions.exitAction);
		popup.add(exitItem);
	}

	public void popupAddDevice(Device dev) {
		popup.addSeparator();
		popup.add(dev.getHardware().getName());
		popup.add(dev.getHardware().getPci() + " : " + (dev.getMining().isPaused() ? "paused" : "running"));
		popup.addSeparator();
		MenuItem resumeItem = new MenuItem("Resume");
		resumeItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					RPCService.getRpcClient().resumeDevice(dev);
				} catch (Throwable e1) {
					log.error("Error resuming device : " + dev.get_index(), e);
				}

			}
		});
		popup.add(resumeItem);
		MenuItem pauseItem = new MenuItem("Pause");
		pauseItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					RPCService.getRpcClient().pauseDevice(dev);
				} catch (Throwable e1) {
					log.error("Error pausing device : " + dev.get_index(), e);
				}

			}
		});
		popup.add(pauseItem);
		popup.addSeparator();

	}

}
