package nsfminer.gui;

import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

import nsfminer.gui.swing.MainWindow;

public class Main {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		(new Main()).run();
	}

	private void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				MainWindow.getMainWindow();
				initRefreshTimer();
			}
		});
	}

	private void initRefreshTimer() {
		Timer timer = new Timer();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				MainWindow.getMainWindow().refreshFields();
			}
		};

		timer.schedule(task, 200, 5000);
	}

}
