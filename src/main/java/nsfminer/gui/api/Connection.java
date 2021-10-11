package nsfminer.gui.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Connection {
	boolean connected;
	Integer switches;
	String uri;

}
