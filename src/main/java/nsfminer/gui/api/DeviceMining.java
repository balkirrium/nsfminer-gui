package nsfminer.gui.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMining {
	private String hashrate;
	private String pause_reason;
	private boolean paused;
	private List<String> segment;
	private List<Integer> shares;

	public String getHashrate() {
		if (hashrate == null || hashrate.isEmpty())
			return "";
		return String.format("%.2f Mh", ((float) Integer.parseInt(hashrate.substring(2), 16) / (1000 * 1000)));
	}
}
