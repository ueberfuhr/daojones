package de.ars.daojones.eclipse.ui.editor.xml;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager {

	protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);

	public void dispose() {
		for(Color color : fColorTable.values()) color.dispose();
	}
	public Color getColor(RGB rgb) {
		Color color = fColorTable.get(rgb);
		if (null == color) {
			fColorTable.put(rgb, color = new Color(Display.getCurrent(), rgb));
		}
		return color;
	}
}
