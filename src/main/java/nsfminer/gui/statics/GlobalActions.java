package nsfminer.gui.statics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nsfminer.gui.swing.MainWindow;

public class GlobalActions {
	private static final Logger log = LogManager.getLogger(GlobalActions.class);

	public static ActionListener exitAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			RPCService.getRpcClient().close();

			System.exit(0);
		}
	};

	public static ActionListener toggleVisibilityAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			MainWindow.getMainWindow().toggleFrameVisibility();
		}
	};

	public static ActionListener pauseAllAction = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			reqPauseAll();

		}

	};

	private static void reqPauseAll() {
		prePauseAction();
		StatDetailService.getStatDetailService().getLastStatDetail().getDevices().forEach(dev -> {
			if (!dev.getMining().isPaused())
				try {
					RPCService.getRpcClient().pauseDevice(dev);
				} catch (Throwable e) {
					log.error("Error pausing device : " + dev.get_index(), e);
				}
		});

	}

	public static ActionListener resumeAllAction = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			reqResumeAll();

		}

	};

	private static void reqResumeAll() {
		GlobalActions.preResumeAction();
		StatDetailService.getStatDetailService().getLastStatDetail().getDevices().forEach(dev -> {
			if (dev.getMining().isPaused())
				try {
					RPCService.getRpcClient().resumeDevice(dev);
				} catch (Throwable e) {
					log.error("Error resuming device : " + dev.get_index(), e);
				}
		});

	}

	public static void preResumeAction() {
		List<String> cmd = new ArrayList<>();
		cmd.add(PropService.getPropService().getString(PropService.PRESCRIPT_RESUME_EXE));
		cmd.addAll(
				Arrays.asList(PropService.getPropService().getString(PropService.PRESCRIPT_RESUME_PARAMS).split(" ")));
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd.toArray(new String[0]));
		} catch (IOException e) {
			log.error("process preResumeActionCMD=" + String.join(" ", cmd), e);
			log.error("error creating process for preResumeAction", e);
			return;

		}
		if (PropService.getPropService().getBoolean(PropService.PRESCRIPT_RESUME_BLOCKING)) {
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				log.error("process preResumeActionCMD=" + String.join(" ", cmd), e);
				log.error("process interrupted", e);
			}
		}
	}

	public static void prePauseAction() {
		List<String> cmd = new ArrayList<>();
		cmd.add(PropService.getPropService().getString(PropService.PRESCRIPT_PAUSE_EXE));
		cmd.addAll(Arrays.asList(PropService.getPropService().getString(PropService.PRESCRIPT_PAUSE_PARAMS).split(" ")));
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd.toArray(new String[0]));
		} catch (IOException e) {
			log.error("process prePauseActionCMD=" + String.join(" ", cmd), e);
			log.error("error creating process for prePauseAction", e);
			return;
		}
		if (PropService.getPropService().getBoolean(PropService.PRESCRIPT_PAUSE_BLOCKING)) {
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				log.error("process prePauseActionCMD=" + String.join(" ", cmd), e);
				log.error("process interrupted", e);
			}
		}
	}

}
