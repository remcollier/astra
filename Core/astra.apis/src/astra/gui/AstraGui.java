package astra.gui;

import java.util.List;

public interface AstraGui {
	public boolean receive(String type, List<?> args);
	public void launch(AstraEventListener listener);
}
