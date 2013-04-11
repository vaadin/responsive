package com.vaadin.ui;

import com.vaadin.server.AbstractExtension;

/**
 * An extension providing responsive layout capabilities to any Vaadin
 * component.
 * 
 * Defining a fixed width/height (or leaving the width/height undefined/auto)
 * for the target component will only trigger one of the specified ranges. Using
 * a relative width/height will allow all ranges to be triggered when the
 * width/height changes.
 * 
 * All configuration of the visual breakpoints for the component are done with
 * CSS currently. Ranges specified in pixels are truncated to full pixels only,
 * fractional pixels are not supported. Other supported CSS units (in, cm, mm,
 * em, rem, pt, pc, ex, ch) are converted to pixels, and fractional values are
 * supported.
 * 
 * Percentages are not supported (because they make little sense to use as
 * thresholds, since the threshold will then depend on the context where it is
 * defined).
 * 
 * <i>Dynamic style injections or any other style updates after the initial page
 * load are not supported at the moment.</i>
 * 
 * 
 * 
 * Example:
 * 
 * <b>Java</b>
 * 
 * <pre>
 * CssLayout layout = new CssLayout();
 * layout.setSizeFull();
 * new Responsive(layout);
 * </pre>
 * 
 * <b>CSS</b>
 * 
 * <pre>
 * .v-csslayout[width-range~="0px-300px"] {
 *    // Styles for the layout when its width is between 0 and 300 pixels
 * }
 * .v-csslayout[width-range~="301px-500px"] {
 *    // Styles for the layout when its width is between 301 and 500 pixels
 * }
 * .v-csslayout[width-range~="501px-"] {
 *    // Styles for the layout when its width is over 500 pixels
 * }
 * 
 * .v-csslayout[height-range~="0px-300px"] {
 *    // Styles for the layout when its height is between 0 and 300 pixels
 * }
 * .v-csslayout[height-range~="301px-500px"] {
 *    // Styles for the layout when its height is between 301 and 500 pixels
 * }
 * .v-csslayout[height-range~="501px-"] {
 *    // Styles for the layout when its height is over 500 pixels
 * }
 * </pre>
 * <p>
 * <b>Note:</b> <i>The defined ranges are applied on a global context, so even
 * if you would write your CSS to target only a given context, the ranges would
 * be applied to all other instances with the same style name.</i>
 * </p>
 * <p>
 * E.g. this would affect all CssLayout instances in the application, even
 * though the CSS implies it would only affect CssLayout instances inside a
 * parent with a style name "foobar":
 * </p>
 * 
 * <pre>
 * .foobar .v-csslayout[width-range~="0px-100px"] {
 *    // These properties will affect all responsive CssLayout instances
 * }
 * </pre>
 * 
 * <p>
 * To scope the ranges, use an additional style name for the target component,
 * and add that to your CSS selector:
 * </p>
 * 
 * <pre>
 *  .v-csslayout.mystyle[width-range="0px-100px"] {
 *    // These properties will only affect responsive CssLayout instances with an additional style name of 'mystyle'
 * }
 * </pre>
 * 
 * @author jouni@vaadin.com
 * 
 */
public class Responsive extends AbstractExtension {

	private static final long serialVersionUID = 1799744299105835420L;

	/**
	 * Enable responsive width and height range styling for the target component
	 * or UI instance.
	 * 
	 * @param target
	 *            The component which should be able to respond to width and/or
	 *            height changes.
	 */
	public Responsive(AbstractComponent target) {
		super.extend(target);
	}

}
