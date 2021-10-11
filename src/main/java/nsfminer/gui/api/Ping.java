package nsfminer.gui.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ping {
	private String pong;

	public String getPong() {
		return "pong".equals(pong) ? "ok" : "failed";
	}
}
