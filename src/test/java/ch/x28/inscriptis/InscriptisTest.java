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

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

/**
 * @author Sascha Wolski
 * @author Matthias Hewelt
 */
public class InscriptisTest {

	/**
	 * Converts an HTML string to text, optionally including and deduplicating image captions, displaying link targets
	 * and using either the standard or extended indentation strategy.
	 *
	 * @param htmlContent the HTML string to be converted to text.
	 * @return The text representation of the HTML content.
	 */
	private static String getText(String htmlContent) {
		return getText(htmlContent, new ParserConfig());
	}

	/**
	 * Converts an HTML string to text, optionally including and deduplicating image captions, displaying link targets
	 * and using either the standard or extended indentation strategy.
	 *
	 * @param htmlContent the HTML string to be converted to text.
	 * @param config an optional ParserConfig object.
	 * @return The text representation of the HTML content.
	 */
	private static String getText(String htmlContent, ParserConfig config) {

		if (StringUtils.isBlank(htmlContent)) {
			return "";
		}

		htmlContent = htmlContent.trim();

		Document document = W3CDom.convert(Jsoup.parse(htmlContent));
		Inscriptis inscriptis = new Inscriptis(document, config);

		return inscriptis.getText();

	}

	@Test
	public void testBr() {

		//given
		String html = "<html><body><br>"
			+ "first</p></body></html>";

		// when
		// then
		assertThat(getText(html)).isEqualTo("\nfirst");
	}

	@Test
	public void testContent() {

		// given
		// when
		// then
		assertThat(getText("<html><body>first</body></html>")).isEqualTo("first");
	}

	@Test
	public void testDisplayAnchors() {

		// given
		String html = "<html>\n"
			+ "  <body>\n"
			+ "    <a name=\"first\">first</a>\n"
			+ "    <a href=\"second\">second</a>\n"
			+ "  </body>\n"
			+ "</html>";

		// when
		ParserConfig config = new ParserConfig();
		config.setDisplayAnchors(true);

		String text = getText(html, config);

		//then
		assertThat(text).isEqualTo("[first](first) second");
	}

	@Test
	public void testDisplayImages() {

		// given
		String html = "<html>\n"
			+ "  <body>\n"
			+ "    <img src=\"test1\" alt=\"Ein Test Bild\" title=\"Hallo\" />\n"
			+ "    <img src=\"test2\" alt=\"Ein Test Bild\" title=\"Juhu\" />\n"
			+ "    <img src=\"test3\" alt=\"Ein zweites Bild\" title=\"Echo\" />\n"
			+ "  </body>\n"
			+ "</html>";

		// when
		ParserConfig config = new ParserConfig();
		config.setDisplayImages(true);

		String text = getText(html, config);

		//then
		assertThat(text).isEqualTo("[Ein Test Bild] [Ein Test Bild] [Ein zweites Bild]");
	}

	@Test
	public void testDisplayImagesDeduplicated() {

		// given
		String html = "<html>\n"
			+ "  <body>\n"
			+ "    <img src=\"test1\" alt=\"Ein Test Bild\" title=\"Hallo\" />\n"
			+ "    <img src=\"test2\" alt=\"Ein Test Bild\" title=\"Juhu\" />\n"
			+ "    <img src=\"test3\" alt=\"Ein zweites Bild\" title=\"Echo\" />\n"
			+ "  </body>\n"
			+ "</html>";

		// when
		ParserConfig config = new ParserConfig();
		config.setDisplayImages(true);
		config.setDeduplicateCaptions(true);

		String text = getText(html, config);

		//then
		assertThat(text).isEqualTo("[Ein Test Bild] [Ein zweites Bild]");
	}

	@Test
	public void testDisplayLinks() {

		// given
		String html = "<html>\n"
			+ "  <body>\n"
			+ "    <a href=\"first\">first</a>\n"
			+ "    <a href=\"second\">second</a>\n"
			+ "    <a name=\"third\">third</a>\n"
			+ "  </body>\n"
			+ "</html>";

		// when
		ParserConfig config = new ParserConfig();
		config.setDisplayLinks(true);

		String text = getText(html, config);

		//then
		assertThat(text).isEqualTo("[first](first) [second](second) third");
	}

	@Test
	public void testDisplayLinksAndAnchors() {

		// given
		String html = "<html>\n"
			+ "  <body>\n"
			+ "    <a name=\"first\">first</a>\n"
			+ "    <a href=\"second\">second</a>\n"
			+ "    <a href=\"third\">third</a>\n"
			+ "  </body>\n"
			+ "</html>";

		// when
		ParserConfig config = new ParserConfig();
		config.setDisplayLinks(true);
		config.setDisplayAnchors(true);

		String text = getText(html, config);

		//then
		assertThat(text).isEqualTo("[first](first) [second](second) [third](third)");
	}

	@Test
	public void testDivs() {
		// given
		ParserConfig config = new ParserConfig(CssProfile.STRICT);

		// when
		// then
		assertThat(getText("<body>Thomas<div>Anton</div>Maria</body>", config)).isEqualTo("Thomas\nAnton\nMaria");
		assertThat(getText("<body>Thomas<div>Anna <b>läuft</b> weit weg.</div>", config)).isEqualTo("Thomas\nAnna läuft weit weg.");
		assertThat(getText("<body>Thomas <ul><li><div>Anton</div>Maria</ul></body>", config)).isEqualTo("Thomas\n  * Anton\n    Maria");
		assertThat(getText("<body>Thomas <ul><li>  <div>Anton</div>Maria</ul></body>", config)).isEqualTo("Thomas\n  * Anton\n    Maria");
		assertThat(getText("<body>Thomas <ul><li> a  <div>Anton</div>Maria</ul></body>", config)).isEqualTo("Thomas\n  * a\n    Anton\n    Maria");
	}

	@Test
	public void testEmptyAndCorrupt() {

		// given
		// when
		// then
		assertThat(getText("test")).isEqualTo("test");
		assertThat(getText("  ")).isEqualTo("");
		assertThat(getText("")).isEqualTo("");
		assertThat(getText("<<<")).isEqualTo("<<<"); // not equal to python version
	}

	@Test
	public void testForgottenTdCloseTagOneLine() {

		// given
		String html = ("<body>hallo<table><tr><td>1<td>2</tr></table>echo</body>");

		// when
		// then
		assertThat(getText(html)).isEqualTo("hallo\n1  2\necho");
	}

	@Test
	public void testForgottenTdCloseTagTwoLines() {

		// given
		String html = ("<body>hallo<table><tr><td>1<td>2<tr><td>3<td>4</table>echo</body>");

		// when
		// then
		assertThat(getText(html)).isEqualTo("hallo\n1  2\n3  4\necho");
	}

	@Test
	public void testHtmlSnippets() throws IOException, URISyntaxException {

		// given
		Path path = Paths.get(getClass().getClassLoader().getResource("snippets").toURI());

		Set<Path> textFiles;
		try (Stream<Path> stream = Files.walk(path)) {
			textFiles = stream
				.filter(file -> !Files.isDirectory(file))
				.filter(file -> file.getFileName().toString().endsWith(".txt"))
				.collect(Collectors.toSet());
		}

		for (Path textFile : textFiles) {
			String text = new String(Files.readAllBytes(textFile), StandardCharsets.UTF_8);
			String html = new String(Files.readAllBytes(Paths.get(textFile.toString().replace(".txt", ".html"))), StandardCharsets.UTF_8);

			text = StringUtils.stripTrailing(text);
			html = "<html><body>" + html + "</body></html>";

			// when
			ParserConfig config = new ParserConfig(CssProfile.STRICT);
			String result = getText(html, config);

			// then
			assertThat(result)
				.as(textFile.getFileName().toString())
				.isEqualTo(text);
		}
	}

	@Test
	public void testLimitWhitespaceAffixes() {

		// given
		String html = "<html>\n"
			+ "  <body>\n"
			+ "    hallo<span>echo</span>\n"
			+ "    <pre>\n"
			+ "def <span>hallo</span>():\n"
			+ "   print(\"echo\")\n"
			+ "    </pre>\n"
			+ "  </body>\n"
			+ "</html>";

		// when
		String text = getText(html);

		//then
		assertThat(text).isEqualTo("hallo echo\ndef hallo():\n   print(\"echo\")"); // not equal to python version
	}

	@Test
	public void testMarginBefore() {

		// given
		// when
		// then
		assertThat(getText("<html><body><p>first</p></body></html>")).isEqualTo("first");
	}

	@Test
	public void testMarginBeforeWithLinebreak() {

		// given
		String html = "<html><body>first<p>"
			+ "second</p></body></html>";

		// when
		// then
		assertThat(getText(html)).isEqualTo("first\nsecond");
	}

	/**
	 * Ensures that two successive <code>&lt;a&gt;text&lt;/a&gt;</code> contain a space between each other, if there is
	 * a linebreak or space between the tags.
	 */
	@Test
	public void testSuccessiveA() {

		// given
		String htmlNoNewLine = "<html><body><a href=\"first\">first</a><a href=\"second\">second</a></body></html>";
		String htmlWithNewLine = "<html><body><a href=\"first\">first</a>\n<a href=\"second\">second</a></body></html>";

		// when
		// then
		assertThat(getText(htmlNoNewLine)).isEqualTo("firstsecond");
		assertThat(getText(htmlWithNewLine)).isEqualTo("first second");
	}

	@Test
	public void testWhiteSpace() {

		// given
		ParserConfig config = new ParserConfig(CssProfile.STRICT);

		// when
		// then
		assertThat(getText("<body><span style=\"white-space: normal\"><i>1</i>2\n3</span></body>", config)).isEqualTo("12 3");
		assertThat(getText("<body><span style=\"white-space: nowrap\"><i>1</i>2\n3</span></body>", config)).isEqualTo("12 3");
		assertThat(getText("<body><span style=\"white-space: pre\"><i>1</i>2\n3</span></body>", config)).isEqualTo("12\n3");
		assertThat(getText("<body><span style=\"white-space: pre-line\"><i>1</i>2\n3</span></body>", config)).isEqualTo("12\n3");
		assertThat(getText("<body><span style=\"white-space: pre-wrap\"><i>1</i>2\n3</span></body>", config)).isEqualTo("12\n3");
	}

	/**
	 * Ensures that xml declaration are correctly stripped.
	 */
	@Test
	public void testXmlDeclaration() {

		// given
		// when
		// then
		assertThat(getText("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> Hallo?>")).isEqualTo("Hallo?>");
	}

	@Test
	public void test() {
		String htmlWhiteSpace = "<body><span style=\"white-space: normal\"><i>1</i>2\n3</span></body>";
		String htmlNoNewLine = "<html><body><a href=\"first\">first</a><a href=\"second\">second</a></body></html>";
		String htmlWithNewLine = "<html><body><a href=\"first\">first</a>\n<a href=\"second\">second</a></body></html>";
		String html5 = "<h1>Chur</h1><b>Chur</b> is the capital and largest town of the <h2>Swiss canton of theGrisons</h2> and lies in the Grisonian Rhine Valley.";
		String html1 = "a \\\\n \\\\n <h1>b</h1>";
		String html2 = "<ul><li>first</li><li>second</li><ul>";
		String html3 = "<img alt='http://www.etsy.com'>click</img>";
		String html4 = "sfasdf<br>asdfasdf";
		String html6 = "<h1>Chur</h1>\n"
				+ "<b>Chur</b> is the capital and largest town of the Swiss canton"
				+ " of the\n"
				+ "Grisons and lies in the Grisonian Rhine Valley.";

		List<String> htmls = Arrays.asList(html2, html3, html4,
				htmlWhiteSpace, htmlNoNewLine, htmlWithNewLine, html5, html1);

		Map<String, List<String>> rules = new HashMap<>();
		rules.put("ul", Arrays.asList("heading", "h1"));
		rules.put("h2", Arrays.asList("heading", "h2"));
		rules.put("b", Arrays.asList("emphasis"));
		rules.put("div#class=toc", Arrays.asList("table-of-contents"));
		rules.put("#class=FactBox", Arrays.asList("fact-box"));
		rules.put("#cite", Arrays.asList("citation"));
		ParserConfig parserConfig = new ParserConfig(rules);
		parserConfig.setDisplayImages(true);

		for (String page: Arrays.asList(html6)) {
			Document document = W3CDom.convert(Jsoup.parse(page));
			Inscriptis inscriptis = new Inscriptis(document, parserConfig);
			String result = inscriptis.getText();
			String n = inscriptis.getAnnotatedText();
			System.out.println(n);
			assertThat(n).isEqualTo(result);
		}
	}

	@Test
	public void testTable() {
		String html = "<table>\n"
				+ "  <tr>\n"
				+ "    <th>Company</th>\n"
				+ "    <th>Contact</th>\n"
				+ "    <th>Country</th>\n"
				+ "  </tr>\n"
				+ "  <tr>\n"
				+ "    <td>Alfreds Futterkiste</td>\n"
				+ "    <td>Maria Anders</td>\n"
				+ "    <td>Germany</td>\n"
				+ "  </tr>\n"
				+ "  <tr>\n"
				+ "    <td>Centro comercial Moctezuma</td>\n"
				+ "    <td>Francisco Chang</td>\n"
				+ "    <td>Mexico</td>\n"
				+ "  </tr>\n"
				+ "</table>";

		String html2 = "<h1>Chur</h1>\n"
				+ "<b>Chur</b> is the capital and largest town of the Swiss canton"
				+ " of the\n"
				+ "Grisons and lies in the Grisonian Rhine Valley.\n"
				+ "\n"
				+ "<table>\n"
				+ "  <tr>\n"
				+ "    <th>Company</th>\n"
				+ "    <th>Contact</th>\n"
				+ "    <th>Country</th>\n"
				+ "  </tr>\n"
				+ "  <tr>\n"
				+ "    <td>Alfreds Futterkiste</td>\n"
				+ "    <td>Maria Anders</td>\n"
				+ "    <td>Germany</td>\n"
				+ "  </tr>\n"
				+ "  <tr>\n"
				+ "    <td>Centro comercial Moctezuma</td>\n"
				+ "    <td>Francisco Chang</td>\n"
				+ "    <td>Mexico</td>\n"
				+ "  </tr>\n"
				+ "</table>";

		String html3 = "<table border=\"5px\" bordercolor=\"#8707B0\">\n"
				+ "<tr>\n"
				+ "<td>Left side of the main table</td>\n"
				+ "<td>\n"
				+ "<table border=\"5px\" bordercolor=\"#F35557\">\n"
				+ "<h4 align=\"center\">Nested Table</h4>\n"
				+ "<tr>\n"
				+ "<td>nested table C1</td>\n"
				+ "<td>nested table C2</td>\n"
				+ "</tr>\n"
				+ "<tr>\n"
				+ "<td>nested table</td>\n"
				+ "<td>nested table</td>\n"
				+ "</tr>\n"
				+ "</table>\n"
				+ "</td>\n"
				+ "</tr>\n"
				+ "</table>";

		Map<String, List<String>> rules = new HashMap<>();
		rules.put("h1", Arrays.asList("heading", "h1"));
		rules.put("h2", Arrays.asList("heading", "h2"));
		rules.put("b", Arrays.asList("emphasis"));
		rules.put("div#class=toc", Arrays.asList("table-of-contents"));
		rules.put("#class=FactBox", Arrays.asList("fact-box"));
		rules.put("#cite", Arrays.asList("citation"));
		rules.put("table", Arrays.asList("table"));
		ParserConfig parserConfig = new ParserConfig(rules);
		parserConfig.setDisplayImages(true);

		Document document = W3CDom.convert(Jsoup.parse(html3));
		Inscriptis inscriptis = new Inscriptis(document, parserConfig);
		String result = inscriptis.getText();
		String n = inscriptis.getAnnotatedText();
		System.out.println(n);
		assertThat(n).isEqualTo(result);
	}

	@Test
	public void test1() {
		String html = "<div xmlns:html=\\\\\"http://www.w3.org/1999/xhtml\\\\\"\\\\n     "
				+ "class=\\\\\"styles__Content-sc-zxskyr-1 cMCZrL\\\\\"\\\\n     "
				+ "data-test=\\\\\"detailsTab\\\\\">\\\\n   <span "
				+ "class=\\\\\"web-migration-tof__MiscellaneousButtonsSpan-sc"
				+ "-14z8sos-1 iWGaHz h-margin-b-tiny h-margin-t-tight\\\\\"/>\\\\n"
				+ "   <div class=\\\\\"styles__StyledRow-sc-1nuqtm0-0 cuJjmE "
				+ "h-margin-v-default\\\\\"/>\\\\n   <div class=\\\\\"h-bg-white "
				+ "h-margin-a-tight h-padding-h-tiny h-padding-v-tight\\\\\"\\\\n "
				+ "       id=\\\\\"specAndDescript\\\\\">\\\\n      <div "
				+ "class=\\\\\"styles__StyledRow-sc-1nuqtm0-0 cuJjmE\\\\\">\\\\n  "
				+ "       <div class=\\\\\"styles__StyledCol-sc-ct8kx6-0 jOZqCG "
				+ "h-padding-h-tight\\\\\">\\\\n            <h3 "
				+ "class=\\\\\"h-text-bs h-margin-b-tight\\\\\">Specifications</h3"
				+ ">\\\\n            <div>\\\\n               <div>\\\\n          "
				+ "        <b>Number of Pages:</b> 384</div>\\\\n               "
				+ "<hr/>\\\\n            </div>\\\\n            <div>\\\\n        "
				+ "       <div>\\\\n                  <b>Genre:</b> Fiction + "
				+ "Literature Genres</div>\\\\n               <hr/>\\\\n          "
				+ "  </div>\\\\n            <div>\\\\n               <div>\\\\n   "
				+ "               <b>Sub-Genre:</b> Asian American</div>\\\\n     "
				+ "          <hr/>\\\\n            </div>\\\\n            "
				+ "<div>\\\\n               <div>\\\\n                  "
				+ "<b>Format:</b> Hardcover</div>\\\\n               <hr/>\\\\n   "
				+ "         </div>\\\\n            <div>\\\\n               "
				+ "<div>\\\\n                  <b>Publisher:</b> Scribner Book "
				+ "Company</div>\\\\n               <hr/>\\\\n            "
				+ "</div>\\\\n            <div>\\\\n               <div>\\\\n     "
				+ "             <b>Age Range:</b> Adult</div>\\\\n               "
				+ "<hr/>\\\\n            </div>\\\\n            <div>\\\\n        "
				+ "       <div>\\\\n                  <b>Author:</b> Lisa "
				+ "See</div>\\\\n               <hr/>\\\\n            </div>\\\\n "
				+ "           <div>\\\\n               <div>\\\\n                 "
				+ " <b>Language:</b> English</div>\\\\n               <hr/>\\\\n  "
				+ "          </div>\\\\n            <div>\\\\n               "
				+ "<b>Street Date</b>: <!-- -->March 5, 2019<hr/>\\\\n            "
				+ "</div>\\\\n            <div>\\\\n               <b>TCIN</b>: "
				+ "<!-- -->54354532<hr/>\\\\n            </div>\\\\n            "
				+ "<div>\\\\n               <b>UPC</b>: <!-- "
				+ "-->9781501154850<hr/>\\\\n            </div>\\\\n            "
				+ "<div>\\\\n               <b>Item Number (DPCI)</b>: <!-- "
				+ "-->248-70-8791<hr/>\\\\n            </div>\\\\n            "
				+ "<div>\\\\n               <b>Origin</b>: <!-- -->Made in the USA"
				+ " or Imported<hr/>\\\\n            </div>\\\\n         "
				+ "</div>\\\\n         <div "
				+ "class=\\\\\"styles__StyledCol-sc-ct8kx6-0 jOZqCG "
				+ "h-padding-l-default\\\\\">\\\\n            <h3 "
				+ "class=\\\\\"h-text-bs h-margin-b-tight\\\\\">Description</h3"
				+ ">\\\\n            <div "
				+ "class=\\\\\"h-margin-v-default\\\\\">\\\\n               "
				+ "<p/>\\\\n               <br clear=\\\\\"none\\\\\"/>\\\\n      "
				+ "         <p>\\\\n                  <b>  About the Book "
				+ "</b>\\\\n               </p>\\\\\"Mi-ja and Young-sook, two "
				+ "girls living on the Korean island of Jeju, are best friends "
				+ "that come from very different backgrounds. When they are old "
				+ "enough, they begin working in the sea with their village\\'s "
				+ "all-female diving collective, led by Young-sook\\'s mother. As "
				+ "the girls take up their positions as baby divers, they know "
				+ "they are beginning a life of excitement and responsibility but "
				+ "also danger. Despite their love for each other, Mi-ja and "
				+ "Young-sook\\'s differences are impossible to ignore ... Mi-ja "
				+ "is the daughter of a Japanese collaborator, and she will "
				+ "forever be marked by this association. Young-sook was born into"
				+ " a long line of haenyeo and will inherit her mother\\'s "
				+ "position leading the divers in their village. Little do the two"
				+ " friends know that after surviving hundreds of dives and "
				+ "developing the closest of bonds, forces outside their control "
				+ "will push their friendship to the breaking point. This ... "
				+ "novel illuminates a world turned upside down, one where the "
				+ "women are in charge, engaging in dangerous physical work, and "
				+ "the men take care of the children\\\\\"--<p/>\\\\n             "
				+ "  <br clear=\\\\\"none\\\\\"/>\\\\n               <p>\\\\n     "
				+ "             <b>  Book Synopsis </b>\\\\n               "
				+ "</p>\\\\n               <b>A new novel from Lisa See, the "
				+ "<i>New York Times</i> bestselling author of <i>The Tea Girl of "
				+ "Hummingbird Lane</i>, about female friendship and family "
				+ "secrets on a small Korean island.</b>\\\\n               "
				+ "<p/>Mi-ja and Young-sook, two girls living on the Korean island"
				+ " of Jeju, are best friends that come from very different "
				+ "backgrounds. When they are old enough, they begin working in "
				+ "the sea with their village\\'s all-female diving collective, "
				+ "led by Young-sook\\'s mother. As the girls take up their "
				+ "positions as baby divers, they know they are beginning a life "
				+ "of excitement and responsibility but also danger. <p/>Despite "
				+ "their love for each other, Mi-ja and Young-sook\\'s differences"
				+ " are impossible to ignore. <i>The Island of Sea Women </i>is an"
				+ " epoch set over many decades, beginning during a period of "
				+ "Japanese colonialism in the 1930s and 1940s, followed by World "
				+ "War II, the Korean War and its aftermath, through the era of "
				+ "cell phones and wet suits for the women divers. Throughout this"
				+ " time, the residents of Jeju find themselves caught between "
				+ "warring empires. Mi-ja is the daughter of a Japanese "
				+ "collaborator, and she will forever be marked by this "
				+ "association. Young-sook was born into a long line of <i>haenyeo"
				+ " </i>and will inherit her mother\\'s position leading the "
				+ "divers in their village. Little do the two friends know that "
				+ "after surviving hundreds of dives and developing the closest of"
				+ " bonds, forces outside their control will push their friendship"
				+ " to the breaking point. <p/>This beautiful, thoughtful novel "
				+ "illuminates a world turned upside down, one where the women are"
				+ " in charge, engaging in dangerous physical work, and the men "
				+ "take care of the children. A classic Lisa See story--one of "
				+ "women\\'s friendships and the larger forces that shape "
				+ "them--<i>The Island of Sea Women</i> introduces readers to the "
				+ "fierce and unforgettable female divers of Jeju Island and the "
				+ "dramatic history that shaped their lives.<p/>\\\\n             "
				+ "  <br clear=\\\\\"none\\\\\"/>\\\\n               <p>\\\\n     "
				+ "             <b>  Review Quotes </b>\\\\n               "
				+ "</p>\\\\n               <br clear=\\\\\"none\\\\\"/>\\\\\"Vivid"
				+ " ... thoughtful and empathetic ... necessary.\\\\\"<br "
				+ "clear=\\\\\"none\\\\\"/>\\\\n               <i>\\\\n           "
				+ "       <b>--</b>\\\\n                  <b>New York Times Book "
				+ "Review</b>\\\\n               </i>\\\\n               <p/> "
				+ "\\\\\"Lisa See\\'s mesmerizing new historical novel.."
				+ ".celebrates women\\'s strengths--and the strength of their "
				+ "friendships.\\\\\"<br clear=\\\\\"none\\\\\"/>\\\\n            "
				+ "   <i>\\\\n                  <b>--</b>\\\\n                  "
				+ "<b>O, The Oprah Magazine</b>\\\\n               </i>\\\\n      "
				+ "         <p/> \\\\\"Painstakingly researched...deft...a "
				+ "powerful and essential story of humanity.\\\\\" <br "
				+ "clear=\\\\\"none\\\\\"/>\\\\n               <i>\\\\n           "
				+ "       <b>--The Los Angeles Review of Books</b>\\\\n           "
				+ "    </i>\\\\n               <p/> \\\\\"Compelling ... takes "
				+ "readers on a journey spanning generations -- in this case 1938 "
				+ "to 2008 -- as moments of cherished friendship, unspeakable "
				+ "tragedy and, in the end, a plot twist worthy of Raymond "
				+ "Chandler unfold.\\\\\"<br clear=\\\\\"none\\\\\"/>\\\\n        "
				+ "       <i>\\\\n                  <b>--</b>\\\\n               "
				+ "</i>\\\\n               <b>Associated Press</b>\\\\n           "
				+ "    <p/> \\\\\"Lisa See is a <i>New York Times </i>bestselling "
				+ "author, a thorough researcher and a wonderful storyteller. In "
				+ "this novel, she seamlessly weaves history, tradition and "
				+ "culture into a heartfelt story about love and forgiveness. "
				+ "It\\'s an unforgettable read.\\\\\"<br "
				+ "clear=\\\\\"none\\\\\"/>\\\\n               <i>\\\\n           "
				+ "       <b>--Toronto Star</b>\\\\n               </i>\\\\n      "
				+ "         <p/> \\\\\"I fell in love with the writing of "
				+ "bestselling and award-winning author Lisa See more than 10 "
				+ "years ago ... This novel introduces readers to the "
				+ "unforgettable female divers of Jeju Island and the dramatic "
				+ "history that shaped their lives.\\\\\"<br "
				+ "clear=\\\\\"none\\\\\"/>\\\\n               <i>\\\\n           "
				+ "       <b>--</b>\\\\n                  <b>Winston-Salem "
				+ "Journal</b>\\\\n               </i>\\\\n               <p/> "
				+ "\\\\\"The most intriguing parts of the book are those that "
				+ "describe the lives of the <i>haenyeo ... </i>See reveals how "
				+ "perilous the work can be: One diver is almost killed by an "
				+ "octopus, and another drowns because of an abalone. Yet the "
				+ "women love the sense of freedom, competence and strength they "
				+ "find in the water.\\\\\"<br clear=\\\\\"none\\\\\"/>\\\\n      "
				+ "         <i>\\\\n                  <b>--Tampa Bay "
				+ "Times</b>\\\\n               </i>\\\\n               <p/> "
				+ "\\\\\"Fascinating ... Readers will witness the fortitude of "
				+ "these women to transcend tragedy and find forgiveness.\\\\\"<br"
				+ " clear=\\\\\"none\\\\\"/>\\\\n               <i>\\\\n          "
				+ "        <b>--Christian Science Monitor, </b>\\\\n              "
				+ " </i>\\\\n               <b>The Best Fiction Books of 2019 "
				+ "</b>\\\\n               <p/> \\\\\"In this bittersweet novel "
				+ "that spans more than 50 years, Lisa See tells the story of "
				+ "Mi-ja and Young-sook, two best friends who live in a kind of "
				+ "feminist utopia on a Korean island.\\\\\"<br "
				+ "clear=\\\\\"none\\\\\"/>\\\\n               <i>\\\\n           "
				+ "       <b>--Marie Claire</b>\\\\n               </i>\\\\n      "
				+ "         <p/> \\\\\"For centuries on the Korean island of Jeju,"
				+ " Haenyeo women were trained to expand their lungs and go diving"
				+ " on the ocean floor to harvest seafood ... Mi-ja and Young-sook"
				+ " are best friends and Haenyeo divers, set to follow in their "
				+ "mothers\\' footsteps. But as they come of age during a "
				+ "tumultuous period in Korea\\'s history, certain deep-rooted "
				+ "differences may tear them apart.\\\\\"<br "
				+ "clear=\\\\\"none\\\\\"/>\\\\n               <i>\\\\n           "
				+ "       <b>--</b>\\\\n               </i>\\\\n               "
				+ "<b>Refinery29, Best Books of March 2019</b>\\\\n               "
				+ "<p/> \\\\\"This beautiful story follows Mi-ja and Young-sook, "
				+ "friends from very different backgrounds who are members of an "
				+ "all-female diving group in Korea. Really, though, the book is "
				+ "about the endurance of friendship when it\\'s pushed to its "
				+ "limits, and you (+ your BFF, when you lend it to her) will love"
				+ " it.\\\\\"<br clear=\\\\\"none\\\\\"/>\\\\n               "
				+ "<i>\\\\n                  <b>--</b>\\\\n               "
				+ "</i>\\\\n               <i>\\\\n                  "
				+ "<b>Cosmopolitan, </b>\\\\n               </i>\\\\n             "
				+ "  <b>15 Best Books of March 2019</b>\\\\n               <p/> "
				+ "\\\\\"Compelling...[a] story of two best friends who come from "
				+ "very different families, and whose bond will be tested time and"
				+ " time again over the years.\\\\\"<br "
				+ "clear=\\\\\"none\\\\\"/>\\\\n               <b>\\\\n           "
				+ "       <i>--</i>\\\\n               </b>\\\\n               "
				+ "<b>LitHub.com, Most Anticipated Books of 2019</b>\\\\n         "
				+ "      <p/> \\\\\"A stupendous multigenerational family saga, "
				+ "See\\'s latest also provides an enthralling cultural "
				+ "anthropology highlighting the soon-to-be-lost, matriarchal "
				+ "haenyeo phenomenon and an engrossing history of violently "
				+ "tumultuous twentieth-century Korea. A mesmerizing achievement. "
				+ "See\\'s accomplishment, acclaim, and readership continue to "
				+ "rise with each book, and interest in this stellar novel will be"
				+ " well stoked.\\\\\"<br clear=\\\\\"none\\\\\"/>\\\\n           "
				+ "    <b>--<i>Booklist</i>, starred review</b>\\\\n              "
				+ " <p/> \\\\\"See perceptively depicts challenges faced by "
				+ "Koreans over the course of the 20th century, particularly "
				+ "homing in on the ways the haenyeo have struggled to maintain "
				+ "their way of life. Exposing the depths of human cruelty and "
				+ "resilience, See\\'s lush tale is a wonderful ode to a truly "
				+ "singular group of women.\\\\\"<br clear=\\\\\"none\\\\\"/>\\\\n"
				+ "               <b>--<i>Publishers Weekly</i>\\\\n              "
				+ " </b>\\\\n               <p/> \\\\\"On an island off the South "
				+ "Korean coast, an ancient guild of women divers reckons with the"
				+ " depredations of modernity from 1938 to 2008 in See\\'s (<i>The"
				+ " Tea Girl of Hummingbird Lane</i>, 2017, etc.) latest novel...."
				+ " See did extensive research with primary sources to detail not "
				+ "only the haenyeo traditions, but the mass murders on Jeju "
				+ "beginning in 1948, which were covered up for decades by the "
				+ "South Korean government... It is a necessary book.\\\\\" <br "
				+ "clear=\\\\\"none\\\\\"/>\\\\n               <b>--<i>Kirkus "
				+ "Reviews</i>\\\\n               </b>\\\\n               <p/> "
				+ "\\\\\"Lisa See excels at mining the intersection of family, "
				+ "friendship and history, and in her newest novel, she reaches "
				+ "new depths exploring the matrifocal <i>haenyeo </i>society in "
				+ "Korea, caught between tradition and modernization. This novel "
				+ "spans wars and generations, but at its heart is a beautifully "
				+ "rendered story of two women whose individual choices become "
				+ "inextricably tangled.\\\\\"<br clear=\\\\\"none\\\\\"/>\\\\n   "
				+ "            <b>--</b>\\\\n               <b>Jodi Picoult, "
				+ "<i>New York Times </i>bestselling author of <i>A Spark of "
				+ "Light</i> and <i>Small Great Things</i>\\\\n               "
				+ "</b>\\\\n               <p/> \\\\\"I was spellbound the moment "
				+ "I entered the vivid and little-known world of the diving women "
				+ "of Jeju. Set amid sweeping historical events, <i> The Island of"
				+ " Sea Women</i> is the extraordinary story of Young-sook and "
				+ "Mi-ja, of women\\'s daring, heartbreak, strength, and "
				+ "forgiveness. No one writes about female friendship, the dark "
				+ "and the light of it, with more insight and depth than Lisa See"
				+ ".\\\\\"<br clear=\\\\\"none\\\\\"/>\\\\n               <b>--Sue"
				+ " Monk Kidd, author of <i>The Secret Life of Bees </i>and <i>The"
				+ " Invention of Wings </i>\\\\n               </b>\\\\n          "
				+ "     <p/> \\\\\"I loved <i>The Island of Sea Women</i> from the"
				+ " very first page. Lisa See has created an enthralling, "
				+ "compelling portrait of a unique culture and a turbulent time in"
				+ " history, but what\\'s really remarkable about this novel is "
				+ "the characters--two women whose lifelong friendship is tested "
				+ "during impossibly difficult times. Compelling, heart-wrenching,"
				+ " and beautifully written, <i>The Island of Sea Women</i> will "
				+ "plunge you into a world and a story you\\'ve never read before "
				+ "and remind you how powerful women can and must be to survive"
				+ ".\\\\\"<br clear=\\\\\"none\\\\\"/>\\\\n               "
				+ "<b>--Kristin Hannah, author of <i>The Nightingale </i>and "
				+ "<i>The Great Alone</i>\\\\n               </b>\\\\n            "
				+ "   <br clear=\\\\\"none\\\\\"/>\\\\n            </div>\\\\n    "
				+ "     </div>\\\\n      </div>\\\\n      <div "
				+ "class=\\\\\"styles__StyledRow-sc-1nuqtm0-0 cuJjmE "
				+ "h-margin-a-tight h-display-block\\\\\">\\\\n         <div>If "
				+ "the item details above aren’t accurate or complete, we want to "
				+ "know about it.<!-- --> <a shape=\\\\\"rect\\\\\"\\\\n          "
				+ "     class=\\\\\"Link-sc-1khjl8b-0 etINsg\\\\\"\\\\n           "
				+ "    href=\\\\\"#\\\\\"\\\\n               "
				+ "data-test=\\\\\"link\\\\\">Report incorrect product info"
				+ ".</a>\\\\n         </div>\\\\n      </div>\\\\n   "
				+ "</div>\\\\n</div>";


		Document document = W3CDom.convert(Jsoup.parse(html));
		Inscriptis inscriptis = new Inscriptis(document);
		String result = inscriptis.getText();
		assertThat(result).isEqualTo(inscriptis.getAnnotatedText());
	}
}
