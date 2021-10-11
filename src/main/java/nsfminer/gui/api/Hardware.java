package nsfminer.gui.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hardware {
	private String name;
	private String pci;
	private List<String> sensors;
	private String type;

}
