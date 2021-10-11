package nsfminer.gui.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Host {
	private String name;
	private Integer runtime;
	private String version;

}
