/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.x28.inscriptis;

import ch.x28.inscriptis.HtmlProperties.Display;
import ch.x28.inscriptis.HtmlProperties.WhiteSpace;
import ch.x28.inscriptis.models.Canvas;

import java.util.Set;

/**
 * The HtmlElement class stores the CSS properties.
 *
 * @author Sascha Wolski
 * @author Matthias Hewelt
 */
public class HtmlElement {

	/**
	 * Name of the given HtmlElement
	 */
	private String tag = "/";
	/**
	 * Specifies a prefix that to insert before the tag's content.
	 */
	private String prefix = "";
	/**
	 * A suffix to append after the tag's content.
	 */
	private String suffix = "";
	/**
	 * {@link Display} strategy used for the content.
	 */
	private Display display = null;
	/**
	 * Vertical margin before the tag's content.
	 */
	private int marginBefore = 0;
	/**
	 * Vertical margin after the tag's content.
	 */
	private int marginAfter = 0;
	/**
	 * Vertical margin after the previous tag's content.
	 */
	private int previousMarginAfter = 0;
	/**
	 * Horizontal padding before the tag's content.
	 */
	private int padding = 0;
	/**
	 * {@link WhiteSpace} handling strategy.
	 */
	private WhiteSpace whitespace = null;
	/**
	 * Limit printing of whitespace affixes to elements with `normal` whitepsace handling.
	 */
	private boolean limitWhitespaceAffixes = false;

	/**
	 *
	 */
	private String listBullet = "";

	private Set<String> annotation;

	private Canvas canvas;

	private HtmlProperties.HorizontalAlignment align  = HtmlProperties.HorizontalAlignment.LEFT;

	private HtmlProperties.VerticalAlignment valign  = HtmlProperties.VerticalAlignment.TOP;

	public HtmlElement() {
	}

	public HtmlElement(String tag) {
		this.tag = tag;
	}

	public HtmlElement(String tag, Display display) {
		this.tag = tag;
		this.display = display;
	}

	public HtmlElement(String tag, Display display, int padding) {

		this.tag = tag;
		this.display = display;
		this.padding = padding;
	}

	public HtmlElement(String tag, Display display, int marginBefore, int marginAfter) {
		this.tag = tag;
		this.display = display;
		this.marginBefore = marginBefore;
		this.marginAfter = marginAfter;
	}

	public HtmlElement(String tag, Display display, int marginBefore, int marginAfter, int padding) {
		this.tag = tag;
		this.display = display;
		this.marginBefore = marginBefore;
		this.marginAfter = marginAfter;
		this.padding = padding;
	}

	public HtmlElement(String tag, Display display, String prefix, String suffix, boolean limitWhitespaceAffixes) {

		this.tag = tag;
		this.prefix = prefix;
		this.suffix = suffix;
		this.display = display;
		this.limitWhitespaceAffixes = limitWhitespaceAffixes;
	}

	public HtmlElement(String tag, Display display, WhiteSpace whitespace) {
		this.tag = tag;
		this.display = display;
		this.whitespace = whitespace;
	}

	public HtmlElement(
		String tag,
		Display display,
		WhiteSpace whitespace,
		String prefix,
		String suffix,
		int marginBefore,
		int marginAfter,
		int padding,
		boolean limitWhitespaceAffixes,
		Canvas canvas,
		int previousMarginAfter,
	        Set<String> annotation) {

		this.tag = tag;
		this.prefix = prefix;
		this.suffix = suffix;
		this.display = display;
		this.marginBefore = marginBefore;
		this.marginAfter = marginAfter;
		this.padding = padding;
		this.whitespace = whitespace;
		this.limitWhitespaceAffixes = limitWhitespaceAffixes;
		this.canvas = canvas;
		this.previousMarginAfter = previousMarginAfter;
		this.annotation = annotation;
	}

	public HtmlElement(String tag, String prefix, String suffix) {

		this.tag = tag;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	/**
	 * @return a clone of the current HtmlElement
	 */
	@Override
	public HtmlElement clone() {

		return new HtmlElement(
				tag,
				display,
				whitespace,
				prefix,
				suffix,
				marginBefore,
				marginAfter,
				padding,
				limitWhitespaceAffixes,
				canvas,
				previousMarginAfter,
				annotation);
	}

	public Display getDisplay() {
		return display;
	}

	public int getMarginAfter() {
		return marginAfter;
	}

	public int getPreviousMarginAfter() {
		return previousMarginAfter;
	}

	public int getMarginBefore() {
		return marginBefore;
	}

	public int getPadding() {
		return padding;
	}

	public String getPrefix() {
		return prefix;
	}

	public HtmlProperties.HorizontalAlignment getAlign() {
		return align;
	}

	public void setAlign(HtmlProperties.HorizontalAlignment align) {
		this.align = align;
	}

	public HtmlProperties.VerticalAlignment getValign() {
		return valign;
	}

	public void setValign(HtmlProperties.VerticalAlignment valign) {
		this.valign = valign;
	}

	public String getListBullet() {
		return listBullet;
	}

	public void setListBullet(String listBullet) {
		this.listBullet = listBullet;
	}

	public Set<String> getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Set<String> annotation) {
		this.annotation = annotation;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public HtmlElement setCanvas(Canvas canvas) {
		this.canvas = canvas;
		return this;
	}

	public void setLimitWhitespaceAffixes(boolean isLimitWhitespaceAffixes) {
		this.limitWhitespaceAffixes = isLimitWhitespaceAffixes;
	}

	/**
	 * @param htmlElement the new HtmlElement to be applied to the current context.
	 * @return the refined element with the context applied.
	 */
	public HtmlElement getRefinedHtmlElement(HtmlElement htmlElement) {

		Display display = this.display == Display.NONE
			? Display.NONE
			: htmlElement.getDisplay();

		WhiteSpace whiteSpace = null;
		if (htmlElement.getWhitespace() != null) {
			whiteSpace = htmlElement.getWhitespace();
		} else if (this.getWhitespace() != null) {
			whiteSpace = this.whitespace;
		}

		// do not display whitespace only affixes in Whitespace.pre areas
		// if `limit_whitespace_affixes` is set.
		String prefix = htmlElement.getPrefix();
		String suffix = htmlElement.getSuffix();

		if (htmlElement.isLimitWhitespaceAffixes() && whiteSpace == WhiteSpace.PRE) {
			if (StringUtils.isBlank(prefix)) {
				prefix = "";
			}

			if (StringUtils.isBlank(suffix)) {
				suffix = "";
			}
		}

		int previousMarginAfter = 0;
		if (htmlElement.display == Display.BLOCK && display == Display.BLOCK) {
			previousMarginAfter = marginAfter;
		}

		return new HtmlElement(
				htmlElement.getTag(),
				display,
				whiteSpace,
				prefix,
				suffix,
				htmlElement.getMarginBefore(),
				htmlElement.getMarginAfter(),
				htmlElement.getPadding(),
				htmlElement.isLimitWhitespaceAffixes(),
				canvas,
				previousMarginAfter,
				htmlElement.getAnnotation());

	}

	public String getSuffix() {
		return suffix;
	}

	public String getTag() {
		return tag;
	}

	public WhiteSpace getWhitespace() {
		return whitespace;
	}

	public boolean isLimitWhitespaceAffixes() {
		return limitWhitespaceAffixes;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	public void setMarginAfter(int marginAfter) {
		this.marginAfter = marginAfter;
	}

	public void setMarginBefore(int marginBefore) {
		this.marginBefore = marginBefore;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public HtmlElement setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public void setWhitespace(WhiteSpace whitespace) {
		this.whitespace = whitespace;
	}

	public void setPreviousMarginAfter(int m) {
		this.previousMarginAfter = m;
	}

	@Override
	public String toString() {
		return "HtmlElement[tag=" + tag +
			", display=" + display +
			", whitespace=" + whitespace +
			", prefix=" + prefix +
			", suffix=" + suffix +
			", previousMarginAfter=" + previousMarginAfter +
			", marginBefore=" + marginBefore +
			", marginAfter=" + marginAfter +
			", padding=" + padding +
			", Canvas=" + canvas +
			", Annotation=" + annotation +
			", limitWhitespaceAffixes=" + limitWhitespaceAffixes + "]";
	}

	public void write(String text) {
		if (text == null || display == Display.NONE) {
			return;
		}
		canvas.write(this, prefix + text + suffix, null);
	}

	public void writeVerbatimText(String text) {
		if (text == null || text.isEmpty()) {
			return;
		}
		if (display == Display.BLOCK) {
			canvas.openBlock(this);
		}

		canvas.write(this, text, WhiteSpace.PRE);

		if (display == Display.BLOCK) {
			canvas.closeBlock(this);
		}
	}
}
