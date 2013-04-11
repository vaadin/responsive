package com.vaadin.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Responsive;
import com.vaadin.ui.UI;

@Theme("demo")
@Title("Responsive Add-on Demo")
@SuppressWarnings("serial")
public class ResponsiveDemoUI extends UI {

	@Override
	protected void init(VaadinRequest request) {

		HorizontalSplitPanel split = new HorizontalSplitPanel();
		setContent(split);
		split.setSplitPosition(50, Unit.PERCENTAGE);
		split.setMinSplitPosition(10, Unit.PERCENTAGE);

		CssLayout grid = new CssLayout();
		grid.setWidth("100%");
		grid.addStyleName("grid");
		split.addComponent(grid);

		for (int i = 1; i < 10; i++) {
			Label l = new Label("" + i);
			l.setSizeUndefined();
			grid.addComponent(l);
		}

		Label description = new Label(
				"<h3>This application demonstrates the Responsive add-on for Vaadin.</h3>"
						+ "<p>Drag the splitter to see how the boxes on the left side adapt to different "
						+ "widths (they maintain a width of 100-200px at all times).</p><p>This label "
						+ "will adapt its font size and line height for different widths.</p>"
						+ "<p><a href=\"http://vaadin.com/addon/responsive\">Download the Responsive add-on</a></p>",
				ContentMode.HTML);
		description.addStyleName("description");
		split.addComponent(description);

		// Add the responsive capabilities to the components
		new Responsive(grid);
		new Responsive(description);
	}

}
