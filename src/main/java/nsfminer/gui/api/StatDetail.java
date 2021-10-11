package nsfminer.gui.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StatDetail {
	private Connection connection;
	private List<Device> devices;
	private Host host;
	private InstanceMining mining;
	private Monitor monitors;

}
