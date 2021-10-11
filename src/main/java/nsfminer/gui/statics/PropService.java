package nsfminer.gui.statics;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropService {
	private static final Logger log = LogManager.getLogger(PropService.class);

	private static final String GUI_PROPERTIES = "gui.properties.file";
	public static final String CLIENT_HOST = "client.host";
	public static final String CLIENT_PORT = "client.port";
	public static final String SERVER_PARAMS = "instance.params";
	public static final String SERVER_EXE = "instance.exe";
	public static final String GUI_START_MINIMIZE = "gui.startminimized";
	public static final String GUI_CLOSE_MINIMIZE = "gui.closeminimize";
	public static final String GUI_AUTOSTART_INSTANCE = "gui.autostartinstance";
	public static final String PRESCRIPT_PAUSE_EXE = "prescript.pause.exe";
	public static final String PRESCRIPT_PAUSE_PARAMS = "prescript.pause.params";
	public static final String PRESCRIPT_RESUME_EXE = "prescript.resume.exe";
	public static final String PRESCRIPT_RESUME_PARAMS = "prescript.resume.params";
	public static final String PRESCRIPT_RESUME_BLOCKING = "prescript.resume.blocking";
	public static final String PRESCRIPT_PAUSE_BLOCKING = "prescript.pause.blocking";

	private Properties p;
	private static PropService propService;

	public static PropService getPropService() {
		if (propService != null)
			return propService;
		PropService propSingleton2 = new PropService();
		propSingleton2.p = new Properties();
		try {
			propSingleton2.p.load(new FileReader(System.getProperty(GUI_PROPERTIES, "gui.properties")));
		} catch (IOException e) {
			log.error("IO error loading properties", e);
		}
		propService = propSingleton2;
		return propSingleton2;
	}

	public void save() {
		try (Writer os = new FileWriter(System.getProperty(GUI_PROPERTIES, "gui.properties"))) {
			Properties tmp = new Properties() {
				private static final long serialVersionUID = 3680668037637997461L;

				@Override
				public synchronized Set<Map.Entry<Object, Object>> entrySet() {
					return Collections.synchronizedSet(
							super.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().toString()))
									.collect(Collectors.toCollection(LinkedHashSet::new)));
				}
			};
			tmp.putAll(p);
			tmp.store(os, CLIENT_HOST);
			os.close();
			p.load(new FileReader(System.getProperty(GUI_PROPERTIES, "gui.properties")));
		} catch (IOException e) {
			log.error("Error saving settings", e);
		}
	}

	public String getString(String name) {
		return p.getProperty(name);
	}

	public boolean getBoolean(String name) {
		return Boolean.parseBoolean(p.getProperty(name));
	}

	public void setString(String name, String value) {
		p.setProperty(name, value);
	}

	public void setBoolean(String name, boolean value) {
		p.setProperty(name, value ? "true" : "false");
	}
}
