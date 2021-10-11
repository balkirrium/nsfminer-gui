package nsfminer.gui.swing;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.miginfocom.swing.MigLayout;

public class AboutTab {
	private static final Logger log = LogManager.getLogger(AboutTab.class);
	private JPanel frame;

	public JPanel getPanel() {
		return frame;
	}

	/**
	 * Create the application.
	 */
	public AboutTab() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JPanel();
		frame.setBounds(100, 100, 450, 300);
		frame.setLayout(new MigLayout("", "[]", "[]"));

		JLabel lblNewLabel = new JLabel("https://github.com/balkirrium/nsfminer-gui");
		frame.add(lblNewLabel, "cell 0 0");
		lblNewLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(lblNewLabel.getText()));
				} catch (IOException e1) {
					log.error("IO error opening about URL");
				} catch (URISyntaxException e1) {
					log.error("about URL syntax error");
				}

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				lblNewLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				lblNewLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}

}
