package nsfminer.gui.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceMining {
	private Integer difficulty;
	private Integer epoch;
	private Integer epoch_changes;
	private String hashrate;
	private List<String> shares;

	public String getHashrate() {
		if (hashrate == null || hashrate.isEmpty())
			return "";
		return String.format("%.2f Mh", ((float) Integer.parseInt(hashrate.substring(2), 16) / (1000 * 1000)));
	}

	public String getHashrateRaw() {
		return hashrate;
	}
}
