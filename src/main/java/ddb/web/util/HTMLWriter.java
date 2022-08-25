package ddb.web.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class HTMLWriter extends StringWriter implements HTMLConstants {
   public static final String classVersion = "5.5";
   protected static final String[] headingTags = new String[]{null, "h1", "h2", "h3", "h4", "h5", "h6"};
   protected HTMLAttributes attributes = new HTMLAttributes();
   protected String indent = "  ";
   protected int indentLevel = 0;
   protected boolean newLine = true;
   protected boolean xhtml = false;

   public HTMLWriter() {
      this.clear();
   }

   protected final void add(String tag, HTMLAttributes attributes) {
      this.begin(tag, attributes);
      this.end(tag);
   }

   protected final void add(String tag, HTMLAttributes attributes, String content) {
      this.begin(tag, attributes);
      if (content != null && content.length() > 0) {
         this.write(content);
      }

      this.end(tag);
   }

   public final void addAbbreviation(String text) {
      this.beginAbbreviation();
      this.write(text);
      this.endAbbreviation();
   }

   public final void addAcronym(String text) {
      this.beginAcronym();
      this.write(text);
      this.endAcronym();
   }

   public final void addAddress(String text) {
      this.writeln();
      this.beginAddress();
      this.write(text);
      this.endAddress();
      this.writeln();
   }

   public final void addAnchor(String name) {
      this.attributes.clear();
      this.attributes.put("name", name);
      this.addAnchor(this.attributes);
   }

   public final void addAnchor(HTMLAttributes attributes) {
      this.add("a", attributes);
   }

   public final void addBase(String url) {
      this.attributes.clear();
      this.attributes.put("href", url);
      this.addBase(this.attributes);
   }

   public final void addBase(HTMLAttributes attributes) {
      this.writeln();
      this.begin("base", attributes);
      if (this.xhtml) {
         this.end("base");
      }

      this.writeln();
   }

   public final void addBigger(String text) {
      this.beginBigger();
      this.write(text);
      this.endBigger();
   }

   public final void addBlockQuote(String text) {
      this.writeln();
      this.beginBlockQuote();
      this.write(text);
      this.endBlockQuote();
      this.writeln();
   }

   public final void addBold(String text) {
      this.beginBold();
      this.write(text);
      this.endBold();
   }

   public final void addBreak() {
      this.addBreak((HTMLAttributes)null);
   }

   public final void addBreak(HTMLAttributes attributes) {
      this.begin("br", attributes);
      if (this.xhtml) {
         this.end("br");
      }

      this.writeln();
   }

   public final void addButtonInput(String name, String value, String onclick) {
      this.attributes.clear();
      this.attributes.put("type", "button");
      this.attributes.put("name", name);
      this.attributes.put("value", value);
      if (onclick != null && onclick.length() > 0) {
         this.attributes.put("onclick", onclick);
      }

      this.addInput(this.attributes);
      this.writeln();
   }

   public final void addCheckBoxInput(String name, String value, String label, boolean checked) {
      this.attributes.clear();
      this.attributes.put("type", "checkbox");
      this.attributes.put("name", name);
      this.attributes.put("value", value);
      if (checked) {
         this.attributes.put("checked");
      }

      this.addInput(this.attributes);
      if (label != null) {
         this.write(label);
      }

      this.writeln();
   }

   public final void addCitation(String text) {
      this.beginCitation();
      this.write(text);
      this.endCitation();
   }

   public final void addCode(String text) {
      this.beginCode();
      this.write(text);
      this.endCode();
   }

   public final void addComment(String text) {
      this.beginComment();
      this.write(text);
      this.endComment();
   }

   public final void addDefinition(String text) {
      this.beginDefinition();
      this.write(text);
      this.endDefinition();
   }

   public final void addDefinition(String term, String definition) {
      this.addDefinitionTerm(term);
      this.addDefinitionDefinition(definition);
   }

   public final void addDefinitionDefinition(String text) {
      this.beginDefinitionDefinition();
      this.write(text);
      this.endDefinitionDefinition();
   }

   public final void addDefinitionTerm(String text) {
      this.beginDefinitionTerm();
      this.write(text);
      this.endDefinitionTerm();
   }

   public final void addDeleted(String text) {
      this.beginDeleted();
      this.write(text);
      this.endDeleted();
   }

   public final void addEmphasized(String text) {
      this.beginEmphasized();
      this.write(text);
      this.endEmphasized();
   }

   public final void addFileInput(String name, int size) {
      this.attributes.clear();
      this.attributes.put("type", "file");
      this.attributes.put("name", name);
      this.attributes.put("size", size);
      this.addInput(this.attributes);
      this.writeln();
   }

   public final void addFrame(HTMLAttributes attributes) {
      this.writeln();
      this.begin("frame", attributes);
      if (this.xhtml) {
         this.end("frame");
      }

      this.writeln();
   }

   public final void addFrame(String src, String name) {
      this.attributes.clear();
      this.attributes.put("src", src);
      if (name != null && name.length() > 0) {
         this.attributes.put("name", name);
      }

      this.addFrame(this.attributes);
   }

   public final void addHeading(int size, String text) {
      this.beginHeading(size, (HTMLAttributes)null);
      this.write(text);
      this.endHeading(size);
   }

   public final void addHiddenInput(String name, String value) {
      this.attributes.clear();
      this.attributes.put("type", "hidden");
      this.attributes.put("name", name);
      this.attributes.put("value", value);
      this.addInput(this.attributes);
      this.writeln();
   }

   public final void addHorizontalRule() {
      this.addHorizontalRule((HTMLAttributes)null);
   }

   public final void addHorizontalRule(String width) {
      this.attributes.clear();
      this.attributes.put("width", width);
      this.addHorizontalRule(this.attributes);
   }

   public final void addHorizontalRule(HTMLAttributes attributes) {
      this.writeln();
      this.begin("hr", attributes);
      if (this.xhtml) {
         this.end("hr");
      }

      this.writeln();
   }

   public final void addHyperlink(String url, String text) {
      this.attributes.clear();
      this.attributes.put("href", url);
      this.add("a", this.attributes, text);
   }

   public final void addImage(String src, String alt, int border, String align, int width, int height) {
      this.attributes.clear();
      this.attributes.put("src", src);
      this.attributes.put("alt", alt);
      this.attributes.put("border", border);
      if (align != null && align.length() > 0) {
         this.attributes.put("align", align);
      }

      if (width > 0) {
         this.attributes.put("width", width);
      }

      if (height > 0) {
         this.attributes.put("height", height);
      }

      this.addImage(this.attributes);
   }

   public final void addImage(HTMLAttributes attributes) {
      this.begin("img", attributes);
      if (this.xhtml) {
         this.end("img");
      }

   }

   public final void addImageInput(String name, String src, String alt, int border, String align) {
      this.attributes.clear();
      this.attributes.put("type", "image");
      this.attributes.put("name", name);
      this.attributes.put("src", src);
      this.attributes.put("alt", alt);
      this.attributes.put("border", border);
      if (align != null && align.length() > 0) {
         this.attributes.put("align", align);
      }

      this.addInput(this.attributes);
      this.writeln();
   }

   public final void addInlineFrame(String src, int width, int height, String align) {
      this.beginInlineFrame(src, width, height, align);
      this.writeln("Your browser does not support inline frames.");
      this.write("To view this ");
      this.addHyperlink(src, "document");
      this.writeln(" correctly,");
      this.writeln("you'll need a copy of the latest Internet Explorer or Netscape.");
      this.endInlineFrame();
   }

   public final void addInput(String type, String name, String value, String size) {
      this.attributes.clear();
      this.attributes.put("type", type);
      if (name != null && name.length() > 0) {
         this.attributes.put("name", name);
      }

      if (value != null && value.length() > 0) {
         this.attributes.put("value", value);
      }

      if (size != null && size.length() > 0) {
         this.attributes.put("size", size);
      }

      this.addInput(this.attributes);
   }

   public final void addInput(HTMLAttributes attributes) {
      this.writeln();
      this.begin("input", attributes);
      if (this.xhtml) {
         this.end("input");
      }

   }

   public final void addInserted(String text) {
      this.beginInserted();
      this.write(text);
      this.endInserted();
   }

   public final void addItalic(String text) {
      this.beginItalic();
      this.write(text);
      this.endItalic();
   }

   public final void addKeyboard(String text) {
      this.beginKeyboard();
      this.write(text);
      this.endKeyboard();
   }

   public final void addLabel(String id, String label) {
      this.attributes.clear();
      this.attributes.put("for", id);
      this.add("label", this.attributes, label);
   }

   public final void addLegend(String label, String align) {
      this.attributes.clear();
      if (align != null && align.length() > 0) {
         this.attributes.put("align", align);
      }

      this.writeln();
      this.add("legend", this.attributes, label);
      this.writeln();
   }

   public final void addLink(HTMLAttributes attributes) {
      this.writeln();
      this.begin("link", attributes);
      if (this.xhtml) {
         this.end("link");
      }

      this.writeln();
   }

   public final void addListItem(String text) {
      this.beginListItem();
      this.write(text);
      if (this.xhtml) {
         this.end("li");
      }

      this.writeln();
   }

   public final void addMeta(HTMLAttributes attributes) {
      this.writeln();
      this.begin("meta", attributes);
      if (this.xhtml) {
         this.end("meta");
      }

      this.writeln();
   }

   public final void addNoFrames(String text) {
      this.writeln();
      this.add("noframes", (HTMLAttributes)null, text);
      this.writeln();
   }

   public final void addNoScript(String text) {
      this.writeln();
      this.add("noscript", (HTMLAttributes)null, text);
      this.writeln();
   }

   public final void addOption(String text, String value, boolean selected) {
      this.attributes.clear();
      if (value != null && value.length() > 0) {
         this.attributes.put("value", value);
      }

      if (selected) {
         this.attributes.put("selected");
      }

      this.writeln();
      this.begin("option", this.attributes);
      this.write(text);
      if (this.xhtml) {
         this.end("option");
      }

   }

   public final void addPasswordInput(String name, int size, int maxlength, String value) {
      this.attributes.clear();
      this.attributes.put("type", "password");
      this.attributes.put("name", name);
      this.attributes.put("size", size);
      this.attributes.put("maxlength", maxlength);
      if (value != null && value.length() > 0) {
         this.attributes.put("value", value);
      }

      this.addInput(this.attributes);
      this.writeln();
   }

   public final void addPreformatted(String text) {
      this.beginPreformatted();
      this.write(text);
      this.endPreformatted();
   }

   public final void addQuote(String text) {
      this.beginQuote();
      this.write(text);
      this.endQuote();
   }

   public final void addRadioInput(String name, String value, String label, boolean checked) {
      this.attributes.clear();
      this.attributes.put("type", "radio");
      this.attributes.put("name", name);
      this.attributes.put("value", value);
      if (checked) {
         this.attributes.put("checked");
      }

      this.addInput(this.attributes);
      if (label != null) {
         this.write(label);
      }

      this.writeln();
   }

   public final void addResetInput(String value) {
      this.attributes.clear();
      this.attributes.put("type", "reset");
      if (value != null && value.length() > 0) {
         this.attributes.put("value", value);
      }

      this.addInput(this.attributes);
      this.writeln();
   }

   public final void addSample(String text) {
      this.beginSample();
      this.write(text);
      this.endSample();
   }

   public final void addScript(String language, String src) {
      this.attributes.clear();
      if (language != null && language.length() > 0) {
         this.attributes.put("language", language);
      }

      this.attributes.put("src", src);
      this.writeln();
      this.add("script", this.attributes);
      this.writeln();
   }

   public final void addSmaller(String text) {
      this.beginSmaller();
      this.write(text);
      this.endSmaller();
   }

   public final void addStrong(String text) {
      this.beginStrong();
      this.write(text);
      this.endStrong();
   }

   public final void addSubmitInput(String name, String value) {
      this.attributes.clear();
      this.attributes.put("type", "submit");
      if (name != null && name.length() > 0) {
         this.attributes.put("name", name);
      }

      if (value != null && value.length() > 0) {
         this.attributes.put("value", value);
      }

      this.addInput(this.attributes);
      this.writeln();
   }

   public final void addSubscript(String text) {
      this.beginSubscript();
      this.write(text);
      this.endSubscript();
   }

   public final void addSuperscript(String text) {
      this.beginSuperscript();
      this.write(text);
      this.endSuperscript();
   }

   public final void addTableCaption(String text) {
      this.writeln();
      this.add("caption", (HTMLAttributes)null, text);
      this.writeln();
   }

   public final void addTableColumn(String align, int span) {
      this.attributes.clear();
      if (align != null && align.length() > 0) {
         this.attributes.put("align", align);
      }

      if (span > 0) {
         this.attributes.put("span", span);
      }

      this.addTableColumn(this.attributes);
   }

   public final void addTableColumn(HTMLAttributes attributes) {
      this.writeln();
      this.begin("col", attributes);
      if (this.xhtml) {
         this.end("col");
      }

      this.writeln();
   }

   public final void addTableData(String text) {
      this.writeln();
      this.add("td", (HTMLAttributes)null, text);
      this.writeln();
   }

   public final void addTableHeader(String text) {
      this.writeln();
      this.add("th", (HTMLAttributes)null, text);
      this.writeln();
   }

   public final void addTeletype(String text) {
      this.beginTeletype();
      this.write(text);
      this.endTeletype();
   }

   public final void addTextArea(String name, int cols, int rows, String wrap, String text) {
      this.attributes.clear();
      this.attributes.put("name", name);
      this.attributes.put("cols", cols);
      this.attributes.put("rows", rows);
      if (wrap != null && wrap.length() > 0) {
         this.attributes.put("wrap", wrap);
      }

      this.beginTextArea(this.attributes);
      if (text != null && text.length() > 0) {
         this.write(text);
      }

      this.endTextArea();
   }

   public final void addTextInput(String name, int size, int maxlength, String value) {
      this.attributes.clear();
      this.attributes.put("type", "text");
      this.attributes.put("name", name);
      this.attributes.put("size", size);
      this.attributes.put("maxlength", maxlength);
      if (value != null && value.length() > 0) {
         this.attributes.put("value", value);
      }

      this.addInput(this.attributes);
      this.writeln();
   }

   public final void addTitle(String title) {
      this.writeln();
      this.add("title", (HTMLAttributes)null, title);
      this.writeln();
   }

   public final void addVariable(String text) {
      this.beginVariable();
      this.write(text);
      this.endVariable();
   }

   protected final void begin(String tag, HTMLAttributes attributes) {
      this.write("<");
      this.write(tag);
      if (attributes != null && attributes.size() != 0) {
         try {
            attributes.write(this);
         } catch (IOException var4) {
            throw new InternalError("Unexpected IOException in HTMLWriter.begin(String,HTMLAttributes)");
         }
      }

      this.write(">");
   }

   public final void beginAbbreviation() {
      this.beginAbbreviation((HTMLAttributes)null);
   }

   public final void beginAbbreviation(HTMLAttributes attributes) {
      this.begin("abbr", attributes);
   }

   public final void beginAcronym() {
      this.beginAcronym((HTMLAttributes)null);
   }

   public final void beginAcronym(HTMLAttributes attributes) {
      this.begin("acronym", attributes);
   }

   public final void beginAddress() {
      this.beginAddress((HTMLAttributes)null);
   }

   public final void beginAddress(HTMLAttributes attributes) {
      this.writeln();
      this.begin("address", attributes);
   }

   public final void beginAnchor(HTMLAttributes attributes) {
      this.begin("a", attributes);
   }

   public final void beginBidirectionalOverride(String dir) {
      this.attributes.clear();
      this.attributes.put("dir", dir);
      this.beginBidirectionalOverride(this.attributes);
   }

   public final void beginBidirectionalOverride(HTMLAttributes attributes) {
      this.begin("bdo", attributes);
   }

   public final void beginBigger() {
      this.beginBigger((HTMLAttributes)null);
   }

   public final void beginBigger(HTMLAttributes attributes) {
      this.begin("big", attributes);
   }

   public final void beginBlockQuote() {
      this.beginBlockQuote((HTMLAttributes)null);
   }

   public final void beginBlockQuote(HTMLAttributes attributes) {
      this.writeln();
      this.begin("blockquote", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginBody() {
      this.beginBody((HTMLAttributes)null);
   }

   public final void beginBody(HTMLAttributes attributes) {
      this.writeln();
      this.begin("body", attributes);
      this.writeln();
   }

   public final void beginBold() {
      this.beginBold((HTMLAttributes)null);
   }

   public final void beginBold(HTMLAttributes attributes) {
      this.begin("b", attributes);
   }

   public final void beginButton(String type) {
      this.attributes.clear();
      this.attributes.put("type", type);
      this.begin("button", this.attributes);
   }

   public final void beginButton(HTMLAttributes attributes) {
      this.begin("button", attributes);
   }

   public final void beginCenter() {
      this.beginCenter((HTMLAttributes)null);
   }

   public final void beginCenter(HTMLAttributes attributes) {
      this.writeln();
      this.begin("center", attributes);
      this.writeln();
   }

   public final void beginCitation() {
      this.beginCitation((HTMLAttributes)null);
   }

   public final void beginCitation(HTMLAttributes attributes) {
      this.begin("cite", attributes);
   }

   public final void beginCode() {
      this.beginCode((HTMLAttributes)null);
   }

   public final void beginCode(HTMLAttributes attributes) {
      this.begin("code", attributes);
   }

   public final void beginComment() {
      this.writeln();
      this.write("<!-- ");
   }

   public final void beginDefinition() {
      this.beginDefinition((HTMLAttributes)null);
   }

   public final void beginDefinition(HTMLAttributes attributes) {
      this.begin("dfn", attributes);
   }

   public final void beginDefinitionDefinition() {
      this.beginDefinitionDefinition((HTMLAttributes)null);
   }

   public final void beginDefinitionDefinition(HTMLAttributes attributes) {
      this.writeln();
      this.begin("dd", attributes);
   }

   public final void beginDefinitionList() {
      this.beginDefinitionList((HTMLAttributes)null);
   }

   public final void beginDefinitionList(HTMLAttributes attributes) {
      this.writeln();
      this.begin("dl", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginDefinitionTerm() {
      this.beginDefinitionTerm((HTMLAttributes)null);
   }

   public final void beginDefinitionTerm(HTMLAttributes attributes) {
      this.writeln();
      this.begin("dt", attributes);
   }

   public final void beginDeleted() {
      this.beginDeleted((HTMLAttributes)null);
   }

   public final void beginDeleted(HTMLAttributes attributes) {
      this.begin("del", attributes);
   }

   public final void beginDivision(HTMLAttributes attributes) {
      this.writeln();
      this.begin("div", attributes);
      this.writeln();
   }

   public final void beginEmphasized() {
      this.beginEmphasized((HTMLAttributes)null);
   }

   public final void beginEmphasized(HTMLAttributes attributes) {
      this.begin("em", attributes);
   }

   public final void beginFieldSet() {
      this.beginFieldSet((HTMLAttributes)null);
   }

   public final void beginFieldSet(String label, String align) {
      this.beginFieldSet((HTMLAttributes)null);
      this.addLegend(label, align);
   }

   public final void beginFieldSet(HTMLAttributes attributes) {
      this.writeln();
      this.begin("fieldset", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginFont(HTMLAttributes attributes) {
      this.begin("font", attributes);
   }

   public final void beginForm(String action, String method) {
      this.writeln();
      this.attributes.clear();
      this.attributes.put("action", action);
      this.attributes.put("method", method);
      this.begin("form", this.attributes);
      this.writeln();
   }

   public final void beginForm(HTMLAttributes attributes) {
      this.writeln();
      this.begin("form", attributes);
      this.writeln();
   }

   public final void beginFrameSet() {
      this.beginFrameSet((HTMLAttributes)null);
   }

   public final void beginFrameSet(HTMLAttributes attributes) {
      this.writeln();
      this.begin("frameset", attributes);
      this.writeln();
   }

   public final void beginHeader() {
      this.beginHeader((HTMLAttributes)null);
   }

   public final void beginHeader(HTMLAttributes attributes) {
      this.writeln();
      this.begin("head", attributes);
      this.writeln();
   }

   public final void beginHeading(int size) {
      this.beginHeading(size, (HTMLAttributes)null);
   }

   public final void beginHeading(int size, HTMLAttributes attributes) {
      if (size >= 1 && size <= 6) {
         this.begin(headingTags[size], attributes);
      } else {
         throw new IllegalArgumentException("Invalid heading size: " + size);
      }
   }

   public final void beginHTML() {
      this.beginHTML((HTMLAttributes)null);
   }

   public final void beginHTML(HTMLAttributes attributes) {
      this.begin("html", attributes);
      this.writeln();
   }

   public final void beginHyperlink(String url) {
      this.attributes.clear();
      this.attributes.put("href", url);
      this.begin("a", this.attributes);
   }

   public final void beginInlineFrame(String src, int width, int height, String align) {
      this.attributes.clear();
      if (src != null && src.length() > 0) {
         this.attributes.put("src", src);
      }

      if (width > 0) {
         this.attributes.put("width", width);
      }

      if (height > 0) {
         this.attributes.put("height", height);
      }

      if (align != null && align.length() > 0) {
         this.attributes.put("align", align);
      }

      this.beginInlineFrame(this.attributes);
   }

   public final void beginInlineFrame(HTMLAttributes attributes) {
      this.writeln();
      this.begin("iframe", attributes);
      this.writeln();
   }

   public final void beginInserted() {
      this.beginInserted((HTMLAttributes)null);
   }

   public final void beginInserted(HTMLAttributes attributes) {
      this.begin("ins", attributes);
   }

   public final void beginItalic() {
      this.beginItalic((HTMLAttributes)null);
   }

   public final void beginItalic(HTMLAttributes attributes) {
      this.begin("i", attributes);
   }

   public final void beginKeyboard() {
      this.beginKeyboard((HTMLAttributes)null);
   }

   public final void beginKeyboard(HTMLAttributes attributes) {
      this.begin("kbd", attributes);
   }

   public final void beginLabel() {
      this.beginLabel((HTMLAttributes)null);
   }

   public final void beginLabel(HTMLAttributes attributes) {
      this.begin("label", attributes);
   }

   public final void beginLegend(HTMLAttributes attributes) {
      this.writeln();
      this.begin("legend", attributes);
   }

   public final void beginListItem() {
      this.beginListItem((HTMLAttributes)null);
   }

   public final void beginListItem(HTMLAttributes attributes) {
      this.writeln();
      this.begin("li", attributes);
   }

   public final void beginNoFrames() {
      this.beginNoFrames((HTMLAttributes)null);
   }

   public final void beginNoFrames(HTMLAttributes attributes) {
      this.begin("noframes", attributes);
   }

   public final void beginNoScript() {
      this.beginNoScript((HTMLAttributes)null);
   }

   public final void beginNoScript(HTMLAttributes attributes) {
      this.begin("noscript", attributes);
   }

   public final void beginObject(HTMLAttributes attributes) {
      this.writeln();
      this.begin("object", attributes);
      this.writeln();
   }

   public final void beginOption(HTMLAttributes attributes) {
      this.writeln();
      this.begin("option", attributes);
   }

   public final void beginOptionGroup(String label) {
      this.attributes.clear();
      this.attributes.put("label", label);
      this.beginOptionGroup(this.attributes);
   }

   public final void beginOptionGroup(HTMLAttributes attributes) {
      this.writeln();
      this.begin("optgroup", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginOrderedList() {
      this.beginOrderedList((HTMLAttributes)null);
   }

   public final void beginOrderedList(HTMLAttributes attributes) {
      this.writeln();
      this.begin("ol", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginParagraph() {
      this.beginParagraph((HTMLAttributes)null);
   }

   public final void beginParagraph(String align) {
      this.attributes.clear();
      this.attributes.put("align", align);
      this.beginParagraph(this.attributes);
   }

   public final void beginParagraph(HTMLAttributes attributes) {
      this.writeln();
      this.begin("p", attributes);
   }

   public final void beginPreformatted() {
      this.beginPreformatted((HTMLAttributes)null);
   }

   public final void beginPreformatted(HTMLAttributes attributes) {
      this.writeln();
      this.begin("pre", attributes);
      this.writeln();
   }

   public final void beginQuote() {
      this.beginQuote((HTMLAttributes)null);
   }

   public final void beginQuote(HTMLAttributes attributes) {
      this.begin("q", attributes);
   }

   public final void beginSample() {
      this.beginSample((HTMLAttributes)null);
   }

   public final void beginSample(HTMLAttributes attributes) {
      this.begin("samp", attributes);
   }

   public final void beginScript(String language) {
      this.attributes.clear();
      if (language != null && language.length() > 0) {
         this.attributes.put("language", language);
      }

      this.beginScript(this.attributes);
   }

   public final void beginScript(HTMLAttributes attributes) {
      this.writeln();
      this.begin("script", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginSelect(String name, int size, boolean multiple) {
      this.attributes.clear();
      this.attributes.put("name", name);
      if (size > 0) {
         this.attributes.put("size", size);
      }

      if (multiple) {
         this.attributes.put("multiple");
      }

      this.beginSelect(this.attributes);
   }

   public final void beginSelect(HTMLAttributes attributes) {
      this.writeln();
      this.begin("select", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginSmaller() {
      this.beginSmaller((HTMLAttributes)null);
   }

   public final void beginSmaller(HTMLAttributes attributes) {
      this.begin("small", attributes);
   }

   public final void beginSpan(String style) {
      this.attributes.clear();
      this.attributes.put("style", style);
      this.beginSpan(this.attributes);
   }

   public final void beginSpan(HTMLAttributes attributes) {
      this.begin("span", attributes);
   }

   public final void beginStrong() {
      this.beginStrong((HTMLAttributes)null);
   }

   public final void beginStrong(HTMLAttributes attributes) {
      this.begin("strong", attributes);
   }

   public final void beginStyle(String type, String media) {
      this.attributes.clear();
      if (type != null && type.length() > 0) {
         this.attributes.put("type", type);
      }

      if (media != null && media.length() > 0) {
         this.attributes.put("media", media);
      }

      this.beginStyle(this.attributes);
   }

   public final void beginStyle(HTMLAttributes attributes) {
      this.writeln();
      this.begin("style", attributes);
      this.indent(1);
      this.beginComment();
      this.writeln();
   }

   public final void beginSubscript() {
      this.beginSubscript((HTMLAttributes)null);
   }

   public final void beginSubscript(HTMLAttributes attributes) {
      this.begin("sub", attributes);
   }

   public final void beginSuperscript() {
      this.beginSuperscript((HTMLAttributes)null);
   }

   public final void beginSuperscript(HTMLAttributes attributes) {
      this.begin("sup", attributes);
   }

   public void beginTable() {
      this.beginTable((HTMLAttributes)null);
   }

   public void beginTable(int border, String width, int cellSpacing, int cellPadding, String bgcolor) {
      this.attributes.clear();
      this.attributes.put("border", border);
      this.attributes.put("width", width);
      this.attributes.put("cellspacing", cellSpacing);
      this.attributes.put("cellpadding", cellPadding);
      if (bgcolor != null) {
         this.attributes.put("bgcolor", bgcolor);
      }

      this.beginTable(this.attributes);
   }

   public void beginTable(HTMLAttributes attributes) {
      this.writeln();
      this.begin("table", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginTableBodySection() {
      this.beginTableBodySection((HTMLAttributes)null);
   }

   public final void beginTableBodySection(HTMLAttributes attributes) {
      this.writeln();
      this.begin("tbody", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginTableCaption() {
      this.beginTableCaption((HTMLAttributes)null);
   }

   public final void beginTableCaption(HTMLAttributes attributes) {
      this.writeln();
      this.begin("caption", attributes);
   }

   public final void beginTableColumnGroup() {
      this.beginTableColumnGroup((HTMLAttributes)null);
   }

   public final void beginTableColumnGroup(int span, String width) {
      this.attributes.clear();
      if (span > 0) {
         this.attributes.put("span", span);
      }

      if (width != null && width.length() > 0) {
         this.attributes.put("width", width);
      }

      this.beginTableColumnGroup(this.attributes);
   }

   public final void beginTableColumnGroup(HTMLAttributes attributes) {
      this.writeln();
      this.begin("colgroup", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginTableData() {
      this.beginTableData((HTMLAttributes)null);
   }

   public final void beginTableData(int colspan, String align, String valign, String width, String bgcolor) {
      this.attributes.clear();
      if (colspan > 1) {
         this.attributes.put("colspan", colspan);
      }

      if (align != null && align.length() > 0) {
         this.attributes.put("align", align);
      }

      if (valign != null && valign.length() > 0) {
         this.attributes.put("valign", valign);
      }

      if (width != null && width.length() > 0) {
         this.attributes.put("width", width);
      }

      if (bgcolor != null && bgcolor.length() > 0) {
         this.attributes.put("bgcolor", bgcolor);
      }

      this.beginTableData(this.attributes);
   }

   public final void beginTableData(HTMLAttributes attributes) {
      this.writeln();
      this.begin("td", attributes);
   }

   public final void beginTableFooterSection() {
      this.beginTableFooterSection((HTMLAttributes)null);
   }

   public final void beginTableFooterSection(HTMLAttributes attributes) {
      this.writeln();
      this.begin("tfoot", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginTableHeader() {
      this.beginTableHeader((HTMLAttributes)null);
   }

   public final void beginTableHeader(int colspan, String align, String valign, String width, String bgcolor) {
      this.attributes.clear();
      if (colspan > 1) {
         this.attributes.put("colspan", colspan);
      }

      if (align != null && align.length() > 0) {
         this.attributes.put("align", align);
      }

      if (valign != null && valign.length() > 0) {
         this.attributes.put("valign", valign);
      }

      if (width != null && width.length() > 0) {
         this.attributes.put("width", width);
      }

      if (bgcolor != null && bgcolor.length() > 0) {
         this.attributes.put("bgcolor", bgcolor);
      }

      this.beginTableHeader(this.attributes);
   }

   public final void beginTableHeader(HTMLAttributes attributes) {
      this.writeln();
      this.begin("th", attributes);
   }

   public final void beginTableHeaderSection() {
      this.beginTableHeaderSection((HTMLAttributes)null);
   }

   public final void beginTableHeaderSection(HTMLAttributes attributes) {
      this.writeln();
      this.begin("thead", attributes);
      this.indent(1);
      this.writeln();
   }

   public void beginTableRow() {
      this.beginTableRow((HTMLAttributes)null);
   }

   public void beginTableRow(HTMLAttributes attributes) {
      this.writeln();
      this.begin("tr", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginTeletype() {
      this.beginTeletype((HTMLAttributes)null);
   }

   public final void beginTeletype(HTMLAttributes attributes) {
      this.begin("tt", attributes);
   }

   public final void beginTextArea(HTMLAttributes attributes) {
      this.writeln();
      this.begin("textarea", attributes);
   }

   public final void beginUnorderedList() {
      this.beginUnorderedList((HTMLAttributes)null);
   }

   public final void beginUnorderedList(HTMLAttributes attributes) {
      this.writeln();
      this.begin("ul", attributes);
      this.indent(1);
      this.writeln();
   }

   public final void beginVariable() {
      this.beginVariable((HTMLAttributes)null);
   }

   public final void beginVariable(HTMLAttributes attributes) {
      this.begin("var", attributes);
   }

   public final void clear() {
      this.getBuffer().setLength(0);
      this.indentLevel = 0;
      this.newLine = true;
   }

   public final void copyTo(Writer out) throws IOException {
      out.write(this.toString());
      out.flush();
   }

   public final void copyTo(String filename) throws IOException {
      this.copyTo(new File(filename));
   }

   public final void copyTo(File file) throws IOException {
      FileOutputStream fos = null;

      try {
         fos = new FileOutputStream(file);
         this.copyTo((Writer)(new PrintWriter(fos)));
      } finally {
         if (fos != null) {
            fos.close();
         }

      }

   }

   protected final void end(String tag) {
      this.write("</");
      this.write(tag);
      this.write(">");
   }

   public final void endAbbreviation() {
      this.end("abbr");
   }

   public final void endAcronym() {
      this.end("acronym");
   }

   public final void endAddress() {
      this.end("address");
   }

   public final void endAnchor() {
      this.end("a");
   }

   public final void endBidirectionalOverride() {
      this.end("bdo");
   }

   public final void endBigger() {
      this.end("big");
   }

   public final void endBlockQuote() {
      this.writeln();
      this.indent(-1);
      this.end("blockquote");
      this.writeln();
   }

   public final void endBody() {
      this.writeln();
      this.end("body");
      this.writeln();
   }

   public final void endBold() {
      this.end("b");
   }

   public final void endButton() {
      this.end("button");
   }

   public final void endCenter() {
      this.writeln();
      this.end("center");
      this.writeln();
   }

   public final void endCitation() {
      this.end("cite");
   }

   public final void endCode() {
      this.end("code");
   }

   public final void endComment() {
      this.write(" -->");
      this.writeln();
   }

   public final void endDefinition() {
      this.end("dfn");
   }

   public final void endDefinitionDefinition() {
      this.end("dd");
   }

   public final void endDefinitionList() {
      this.writeln();
      this.indent(-1);
      this.end("dl");
      this.writeln();
   }

   public final void endDefinitionTerm() {
      this.end("dt");
   }

   public final void endDeleted() {
      this.end("del");
   }

   public final void endDivision() {
      this.writeln();
      this.end("div");
      this.writeln();
   }

   public final void endEmphasized() {
      this.end("em");
   }

   public final void endFieldSet() {
      this.indent(-1);
      this.writeln();
      this.end("fieldset");
      this.writeln();
   }

   public final void endFont() {
      this.end("font");
   }

   public final void endForm() {
      this.writeln();
      this.end("form");
      this.writeln();
   }

   public final void endFrameSet() {
      this.writeln();
      this.end("frameset");
      this.writeln();
   }

   public final void endHeader() {
      this.writeln();
      this.end("head");
      this.writeln();
   }

   public final void endHeading(int size) {
      if (size >= 1 && size <= 6) {
         this.end(headingTags[size]);
      } else {
         throw new IllegalArgumentException("Invalid heading size: " + size);
      }
   }

   public final void endHTML() {
      this.writeln();
      this.end("html");
      this.writeln();
   }

   public final void endHyperlink() {
      this.end("a");
   }

   public final void endInlineFrame() {
      this.writeln();
      this.end("iframe");
      this.writeln();
   }

   public final void endInserted() {
      this.end("ins");
   }

   public final void endItalic() {
      this.end("i");
   }

   public final void endKeyboard() {
      this.end("kbd");
   }

   public final void endLabel() {
      this.end("label");
   }

   public void endLegend() {
      this.end("legend");
      this.writeln();
   }

   public void endListItem() {
      this.end("li");
      this.writeln();
   }

   public final void endNoFrames() {
      this.end("noframes");
      this.writeln();
   }

   public final void endNoScript() {
      this.end("noscript");
      this.writeln();
   }

   public final void endObject() {
      this.writeln();
      this.end("object");
      this.writeln();
   }

   public final void endOption() {
      this.end("option");
      this.writeln();
   }

   public final void endOptionGroup() {
      this.writeln();
      this.indent(-1);
      this.end("optgroup");
      this.writeln();
   }

   public final void endOrderedList() {
      this.writeln();
      this.indent(-1);
      this.end("ol");
      this.writeln();
   }

   public final void endParagraph() {
      this.end("p");
      this.writeln();
   }

   public final void endPreformatted() {
      this.writeln();
      this.end("pre");
      this.writeln();
   }

   public final void endQuote() {
      this.end("q");
   }

   public final void endSample() {
      this.end("samp");
   }

   public final void endScript() {
      this.writeln();
      this.indent(-1);
      this.end("script");
      this.writeln();
   }

   public final void endSelect() {
      this.writeln();
      this.indent(-1);
      this.end("select");
      this.writeln();
   }

   public final void endSmaller() {
      this.end("small");
   }

   public final void endSpan() {
      this.end("span");
   }

   public final void endStrong() {
      this.end("strong");
   }

   public final void endStyle() {
      this.writeln();
      this.endComment();
      this.indent(-1);
      this.end("style");
      this.writeln();
   }

   public final void endSubscript() {
      this.end("sub");
   }

   public final void endSuperscript() {
      this.end("sup");
   }

   public void endTable() {
      this.writeln();
      this.indent(-1);
      this.end("table");
      this.writeln();
   }

   public final void endTableBodySection() {
      this.writeln();
      this.indent(-1);
      this.end("tbody");
      this.writeln();
   }

   public final void endTableCaption() {
      this.end("caption");
      this.writeln();
   }

   public final void endTableColumnGroup() {
      this.writeln();
      this.indent(-1);
      this.end("colgroup");
      this.writeln();
   }

   public void endTableData() {
      this.end("td");
      this.writeln();
   }

   public final void endTableFooterSection() {
      this.writeln();
      this.indent(-1);
      this.end("tfoot");
      this.writeln();
   }

   public void endTableHeader() {
      this.end("th");
      this.writeln();
   }

   public final void endTableHeaderSection() {
      this.writeln();
      this.indent(-1);
      this.end("thead");
      this.writeln();
   }

   public void endTableRow() {
      this.writeln();
      this.indent(-1);
      this.end("tr");
      this.writeln();
   }

   public final void endTeletype() {
      this.end("tt");
   }

   public final void endTextArea() {
      this.end("textarea");
      this.writeln();
   }

   public final void endUnorderedList() {
      this.writeln();
      this.indent(-1);
      this.end("ul");
      this.writeln();
   }

   public final void endVariable() {
      this.end("var");
   }

   public String getIndent() {
      return this.indent;
   }

   public final void indent(int delta) throws IllegalArgumentException {
      this.indentLevel += delta;
      if (this.indentLevel < 0) {
         throw new IllegalArgumentException("Indent level cannot be negative!");
      }
   }

   public boolean isXHTML() {
      return this.xhtml;
   }

   public void setIndent(String indent) {
      this.indent = indent;
   }

   public void setXHTML(boolean xhtml) {
      this.xhtml = xhtml;
   }

   public final void write(String s) {
      if (this.newLine) {
         this.writeIndent();
      }

      super.write(s);
      this.newLine = false;
   }

   public final void writeEscaped(String s) {
      this.write(HTMLUtils.escapeText(s));
   }

   public final void write(char value) {
      this.write(String.valueOf(value));
   }

   public final void writeIndent() {
      for(int i = 0; i < this.indentLevel; ++i) {
         super.write(this.indent);
      }

   }

   public final void writeln() {
      if (!this.newLine) {
         this.write(NEWLINE);
         this.newLine = true;
      }

   }

   public final void writeln(String s) {
      this.write(s);
      this.writeln();
   }
}
