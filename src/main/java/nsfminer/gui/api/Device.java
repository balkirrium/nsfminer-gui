package nsfminer.gui.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
	private Integer _index;
	private String _mode;
	private Hardware hardware;
	private DeviceMining mining;

}
