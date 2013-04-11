package com.vaadin.ui.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.Responsive;

@Connect(Responsive.class)
public class ResponsiveConnector extends AbstractExtensionConnector implements
		ElementResizeListener {

	/**
	 * The target component which we will monitor for width changes
	 */
	protected AbstractComponentConnector target;

	/**
	 * All the width breakpoints found for this particular instance
	 */
	protected JavaScriptObject widthBreakpoints;

	/**
	 * All the height breakpoints found for this particular instance
	 */
	protected JavaScriptObject heightBreakpoints;

	/**
	 * All width-range breakpoints found from the style sheets on the page.
	 * Common for all instances.
	 */
	protected static JavaScriptObject widthRangeCache;

	/**
	 * All height-range breakpoints found from the style sheets on the page.
	 * Common for all instances.
	 */
	protected static JavaScriptObject heightRangeCache;

	@Override
	protected void extend(ServerConnector target) {
		// Initialize cache if not already done
		if (widthRangeCache == null) {
			searchForBreakPoints();
		}

		this.target = (AbstractComponentConnector) target;

		// Construct the list of selectors we should match against in the
		// range selectors
		String primaryStyle = this.target.getState().primaryStyleName;
		StringBuilder selectors = new StringBuilder();
		selectors.append("." + primaryStyle);

		if (this.target.getState().styles != null
				&& this.target.getState().styles.size() > 0) {
			for (String style : this.target.getState().styles) {
				// TODO decide all the combinations we want to support
				selectors.append(",." + style);
				selectors.append(",." + primaryStyle + "." + style);
				selectors.append(",." + style + "." + primaryStyle);
				selectors.append(",." + primaryStyle + "-" + style);
			}
		}

		// Allow the ID to be used as the selector as well for ranges
		if (this.target.getState().id != null) {
			selectors.append(",#" + this.target.getState().id);
		}

		// Get any breakpoints from the styles defined for this widget
		getBreakPointsFor(selectors.toString());

		// Start listening for size changes
		LayoutManager.get(getConnection()).addElementResizeListener(
				this.target.getWidget().getElement(), this);
	}

	/**
	 * Build a cache of all 'width-range' and 'height-range' attribute selectors
	 * found in the stylesheets.
	 */
	private static native void searchForBreakPoints()
	/*-{
	
	// Initialize variables
	@com.vaadin.ui.client.ResponsiveConnector::widthRangeCache = [];
	@com.vaadin.ui.client.ResponsiveConnector::heightRangeCache = [];
	
	var widthRanges = @com.vaadin.ui.client.ResponsiveConnector::widthRangeCache;
	var heightRanges = @com.vaadin.ui.client.ResponsiveConnector::heightRangeCache;
	
	// Can't do squat if we can't parse stylesheets
	if(!$doc.styleSheets)
	    return null;
	
	var sheets = $doc.styleSheets;
	
	// Loop all stylesheets on the page and process them individually
	for(var i = 0, len = sheets.length; i < len; i++) {
	    var sheet = sheets[i];
	    @com.vaadin.ui.client.ResponsiveConnector::searchStylesheetForBreakPoints(Lcom/google/gwt/core/client/JavaScriptObject;)(sheet);        
	}
	
	// Only for debugging
	// console.log("All breakpoints", widthRanges, heightRanges);
	
	}-*/;

	/**
	 * Process an individual stylesheet object. Any @import statements are
	 * handled recursively. Regular rule declarations are searched for
	 * 'width-range' and 'height-range' attribute selectors.
	 * 
	 * @param sheet
	 */
	private static native void searchStylesheetForBreakPoints(
			final JavaScriptObject sheet)
	/*-{
	
	// Inline variables for easier reading
	var widthRanges = @com.vaadin.ui.client.ResponsiveConnector::widthRangeCache;
	var heightRanges = @com.vaadin.ui.client.ResponsiveConnector::heightRangeCache;
	
	// Get all the rulesets from the stylesheet
	var theRules = new Array();
	if (sheet.cssRules) {
	    theRules = sheet.cssRules
	} else if (sheet.rules) {
	    theRules = sheet.rules
	}
	
	// Loop through the rulesets
	for(var i = 0, len = theRules.length; i < len; i++) {
	    var rule = theRules[i];
	    
	    if(rule.type == 3) {
	        // @import rule, traverse recursively
	        @com.vaadin.ui.client.ResponsiveConnector::searchStylesheetForBreakPoints(Lcom/google/gwt/core/client/JavaScriptObject;)(rule.styleSheet);
	        
	    } else if(rule.type == 1) {
	        // Regular selector rule
	        
	        // Pattern for matching [width-range] selectors
	        var widths = /([\.|#]\S*)\[width-range~?=\"(.*)-(.*)\"\]/i;
	        
	        // Patter for matching [height-range] selectors
	        var heights = /([\.|#]\S*)\[height-range~?=\"(.*)-(.*)\"\]/i;
	        
	        // Array of all of the separate selectors in this ruleset
	        var haystack = rule.selectorText.toLowerCase().split(",");
	        
	        // Loop all the selectors in this ruleset
	        for(var k = 0, len2 = haystack.length; k < len2; k++) {
	            var result;
	            
	            // Check for width-range matches
	            if(result = haystack[k].match(widths)) {
	                // Avoid adding duplicates
	                var duplicate = false;
	                for(var l = 0, len3 = widthRanges.length; l < len3; l++) {
	                    var bp = widthRanges[l];
	                    if(result[1] == bp[0] && result[2] == bp[1] && result[3] == bp[2]) {
	                        duplicate = true;
	                        break;
	                    }
	                }
	                if(!duplicate) {
	                    widthRanges.push([result[1], result[2], result[3]]);
	                }
	            }
	            
	            // Check for height-range matches
	            if(result = haystack[k].match(heights)) {
	                // Avoid adding duplicates
	                var duplicate = false;
	                for(var l = 0, len3 = heightRanges.length; l < len3; l++) {
	                    var bp = heightRanges[l];
	                    if(result[1] == bp[0] && result[2] == bp[1] && result[3] == bp[2]) {
	                        duplicate = true;
	                        break;
	                    }
	                }
	                if(!duplicate) {
	                    heightRanges.push([result[1], result[2], result[3]]);
	                }
	            }
	        }
	    }
	}
	
	}-*/;

	/**
	 * Get all matching ranges from the cache for this particular instance.
	 * 
	 * @param selectors
	 */
	private native void getBreakPointsFor(final String selectors)
	/*-{
	
	var selectors = selectors.split(",");
	
	var widthBreakpoints = this.@com.vaadin.ui.client.ResponsiveConnector::widthBreakpoints = [];
	var heightBreakpoints = this.@com.vaadin.ui.client.ResponsiveConnector::heightBreakpoints = [];
	
	var widthRanges = @com.vaadin.ui.client.ResponsiveConnector::widthRangeCache;
	var heightRanges = @com.vaadin.ui.client.ResponsiveConnector::heightRangeCache;
	
	for(var i = 0, len = widthRanges.length; i < len; i++) {
	    var bp = widthRanges[i];
	    for(var j = 0, len2 = selectors.length; j < len2; j++) {
	        if(bp[0] == selectors[j])
	            widthBreakpoints.push(bp);
	    }
	}
	
	for(var i = 0, len = heightRanges.length; i < len; i++) {
	    var bp = heightRanges[i];
	    for(var j = 0, len2 = selectors.length; j < len2; j++) {
	        if(bp[0] == selectors[j])
	            heightBreakpoints.push(bp);
	    }
	}
	
	// Only for debugging
	// console.log("Breakpoints for", selectors.join(","), widthBreakpoints, heightBreakpoints);
	
	}-*/;

	private String currentWidthRanges;
	private String currentHeightRanges;

	@Override
	public void onElementResize(ElementResizeEvent e) {
		int width = e.getLayoutManager().getOuterWidth(e.getElement());
		int height = e.getLayoutManager().getOuterHeight(e.getElement());

		// Loop through breakpoints and see which one applies to this width
		currentWidthRanges = resolveBreakpoint("width", width, e.getElement());

		if (currentWidthRanges != "") {
			this.target.getWidget().getElement()
					.setAttribute("width-range", currentWidthRanges);
		} else {
			this.target.getWidget().getElement().removeAttribute("width-range");
		}

		// Loop through breakpoints and see which one applies to this height
		currentHeightRanges = resolveBreakpoint("height", height,
				e.getElement());

		if (currentHeightRanges != "") {
			this.target.getWidget().getElement()
					.setAttribute("height-range", currentHeightRanges);
		} else {
			this.target.getWidget().getElement()
					.removeAttribute("height-range");
		}
	}

	private native String resolveBreakpoint(String which, int size,
			Element element)
	/*-{

	// Default to "width" breakpoints
	var breakpoints = this.@com.vaadin.ui.client.ResponsiveConnector::widthBreakpoints;
	
	// Use height breakpoints if we're measuring the height
	if(which == "height")
		breakpoints = this.@com.vaadin.ui.client.ResponsiveConnector::heightBreakpoints;
	
	// Output string that goes into either the "width-range" or "height-range" attribute in the element
	var ranges = "";
	
	// Loop the breakpoints
	for(var i = 0, len = breakpoints.length; i < len; i++) {
	    var bp = breakpoints[i];
	    
	    var min, max;
	    
	    // Do we need to calculate the pixel value?
	    if(bp[1] != "0" && bp[1].indexOf("px") == -1) {
	        min = @com.vaadin.ui.client.ResponsiveConnector::getPixelSize(Ljava/lang/String;Lcom/google/gwt/dom/client/Element;)(bp[1], element);
	        // Calculation failed somehow, ignore this breakpoint
	        // TODO inform the developer somehow?
	        if(min == -1) continue;
	    } else {
	    	// No, we can use the pixel value directly
	        min = parseInt(bp[1]);
	    }
	    
	    // Do we need to calculate the pixel value?
	    if(bp[2] && bp[2].indexOf("px") == -1) {
	        max = @com.vaadin.ui.client.ResponsiveConnector::getPixelSize(Ljava/lang/String;Lcom/google/gwt/dom/client/Element;)(bp[2], element);
	        // Calculation failed somehow, ignore this breakpoint
	        // TODO inform the developer somehow?
	        if(max == -1) continue;
	    } else {
	    	// No, we can use the pixel value directly
	        max = parseInt(bp[2]);
	    }
	    
	    if(max) {
            if(min <= size && size <= max) {
                ranges += " " + bp[1] + "-" + bp[2];
            }
        } else {
            if(min <= size) {
                ranges += " " + bp[1] + "-";
            }
        }
	}
	
	// Trim the output and return it
	return ranges.replace(/^\s+/, "");
	
	}-*/;

	private static native int getPixelSize(String size, Element context)
	/*-{
	
	// Get the value and units from the size
	var items = size.match(/^(\d+)?(\.\d+)?(.{1,3})/);
	var val = (items[1] || 0) + (items[2] || "");
	var unit = items[3].toLowerCase();
	
	// Use a temporay measuring element to get the computed size of the relative units 
	if(unit == "em" || unit == "rem" || unit == "ex" || unit == "ch") {
		var measure = $doc.createElement("div");
		measure.style.width = size;
		context.appendChild(measure);
		var s = measure.offsetWidth;
		context.removeChild(measure)
		return s;
	}
	
	// Handle all other absolute units with basic math
	var ret = -1;
	switch(unit) {
		case "in":
			ret = val * 96;
			break;
		case "cm":
			ret = val * 37.8;
			break;
		case "mm":
			ret = val * 3.78;
			break;
		case "pt":
			ret = (val * 96) / 72;
			break;
		case "pc":
			ret = ((val * 96) / 72) * 12;
			break;
	}

	return ret;
	
	}-*/;
}
