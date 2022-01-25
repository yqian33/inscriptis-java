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

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents a line to render.
 *
 * @author Sascha Wolski
 * @author Matthias Hewelt
 */
class Line {

	private Long index;
	private int marginBefore = 0;
	private int marginAfter = 0;
	private String prefix = "";
	private String suffix = "";
	private String content = "";
	private String listBullet = "";
	private int padding = 0;
	private boolean collapsableWhitespace = true;

	private Prefix prefixObj;

	public Line() {
	}

	public Line(Long index, Prefix prefix) {
		this.index = index;
		prefixObj = prefix;
	}

	public void addContent(String content) {
		this.content += content;
	}

	public String getContent() {
		return content;
	}

	public String getListBullet() {
		return listBullet;
	}

	public int getMarginAfter() {
		return marginAfter;
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

	public String getSuffix() {
		return suffix;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isCollapsableWhitespace() {
		return collapsableWhitespace;
	}

	public void setCollapsableWhitespace(boolean collapsableWhitespace) {
		this.collapsableWhitespace = collapsableWhitespace;
	}

	public Prefix getPrefixObj() {
		return prefixObj;
	}

	public void setPrefixObj(Prefix prefixObj) {
		this.prefixObj = prefixObj;
	}

	public String getContentText() {
		if (!collapsableWhitespace) {
			return content;
		}
		if (content.endsWith(" ")) {
			content = content.substring(0, content.length()-1);
			index -= 1;
		}
		return content;
	}

	/**
	 * @return the text representation of the current line.
	 */
	public String getText() {

		List<String> text = new ArrayList<>();

		if (!content.contains("\0")) {
			// standard text without any `WhiteSpace#PRE` formatted text.
			text.addAll(Arrays.asList(content.trim().split("\\s+")));
		} else {
			// content containing `WhiteSpace#PRE` formatted text
			content = content.replace("\0\0", "");

			String basePadding = StringUtils.repeat(" ", padding);

			int i = 0;
			for (String data : content.split("\0")) {
				if (i++ % 2 == 0) {
					// handle standard content
					// python extend filters empty elements
					List<String> d = Stream.of(data.trim().split("\\s+"))
						.filter(str -> !str.isEmpty())
						.collect(Collectors.toList());

					text.addAll(d);
				} else {
					// handle `WhiteSpace#PRE` formatted content.
					text.add(data.replaceAll("\n", "\n" + basePadding));
				}
			}
		}

		StringBuilder result = new StringBuilder()
			.append(StringUtils.repeat("\n", marginBefore))
			.append(StringUtils.repeat(" ", Math.max(0, padding - listBullet.length())))
			.append(listBullet)
			.append(prefix)
			.append(String.join(" ", text))
			.append(suffix)
			.append(StringUtils.repeat("\n", marginAfter));

		return result.toString();
	}

	/**
	 * Set the String that will be used as a bullet symbol in a list.
	 *
	 * @param listBullet the bullet to be used in a list.
	 */
	public void setListBullet(String listBullet) {
		this.listBullet = listBullet;
	}

	/**
	 * Set the amount of empty lines that will be added after the lines content.
	 *
	 * @param marginAfter the number of empty lines
	 */
	public void setMarginAfter(int marginAfter) {
		this.marginAfter = marginAfter;
	}

	/**
	 * Set the amount of empty lines that will be added before the lines content.
	 *
	 * @param marginBefore the number of empty lines.
	 */
	public void setMarginBefore(int marginBefore) {
		this.marginBefore = marginBefore;
	}

	/**
	 * Set the amount of horizontal padding (spaces) that will be used to intend the lines content. If a list bullet is
	 * used, the actual padding will be reduced by the amount of characters of this list bullet. This means the list
	 * bullet is handled as part of the padding.
	 *
	 * @param padding the amount of spaces to be added.
	 */
	public void setPadding(int padding) {
		this.padding = padding;
	}

	/**
	 * Set the String value that will be added in front of the lines content.
	 *
	 * @param prefix the string value to be added.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Set the String value that will be added behind the lines content.
	 *
	 * @param suffix the string value to be added.
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public Line newLine() {
		this.prefixObj.setConsumed(false);
		return new Line(this.index + 1, this.prefixObj);
	}

	public void merge(String text, HtmlProperties.WhiteSpace whiteSpace) {
		if ( whiteSpace == HtmlProperties.WhiteSpace.PRE) {
			mergePreText(text);
		} else {
			mergeNormalText(text);
		}
	}

	public void mergeNormalText(String text) {
		List<String> normalizedText = new ArrayList<>();
		for (char ch : text.toCharArray()) {
			if (!Character.isWhitespace(ch)) {
				normalizedText.add(String.valueOf(ch));
				collapsableWhitespace = false;
			}else if (!collapsableWhitespace) {
				normalizedText.add(" ");
				collapsableWhitespace = true;
			}
		}

		if (normalizedText.size() > 0) {
			String resText = String.join("", normalizedText);
			if (content == null || content.isEmpty()) {
				// System.out.println("line obj:" + this.prefixObj);
				// System.out.println("prefix consumed:" + prefixObj.consumed);
				resText = prefixObj.first() + resText;
			}
			// TODO:  unescape text
			resText = StringEscapeUtils.unescapeCsv(resText);
			resText = StringEscapeUtils.unescapeHtml(resText);
			content += resText;
			index += resText.length();
		}
	}

	public void mergePreText(String text) {
		String ntext = text.replace("\n", "\n"+prefixObj.rest());
		String resText = prefixObj.first() + ntext;
		// TODO:  unescape text
		resText = StringEscapeUtils.unescapeCsv(resText);
		resText = StringEscapeUtils.unescapeHtml(resText);
		content += resText;
		index += resText.length();
		collapsableWhitespace = false;
	}
}
