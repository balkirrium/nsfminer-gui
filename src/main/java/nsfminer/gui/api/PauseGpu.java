package nsfminer.gui.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PauseGpu {
	private Integer index;
	private boolean pause;
}
