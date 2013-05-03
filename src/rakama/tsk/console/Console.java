/*
 * Copyright (c) 2012, RamsesA <ramsesakama@gmail.com>
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
 * LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
 * OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THIS SOFTWARE.
 */

package rakama.tsk.console;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLWriter;

import rakama.tsk.io.FileLoader;

@SuppressWarnings("serial")
public class Console extends JPanel
{
	//TODO: popup undo/cut/copy/paste/selall
	
	public static boolean echo = true;
	public static boolean timestamp = true;
	
	public static String echo_prefix = "» ";
	public static String event_prefix = "";
	public static String error_prefix = "Error: ";
	public static String verbose_prefix = "";

	public static String echo_suffix = "";
	public static String event_suffix = "";
	public static String error_suffix = "";
	public static String verbose_suffix = "";
	
	URL default_style_sheet = FileLoader.getDefaultFileLoader().getURL("css/stylesheet.css");
	String default_html = "<html><body><table id=container width=100% height=100%>"
		+"</table><p></p></body></html>";
	
	JScrollPane scroll;
	JEditorPane display;
	HTMLDocument document;
	HTMLEditorKit editor;
	JTextField input;
	
	Map<String, Action> actions;
	Map<String, Link> links;
	long linkCounter;
	
	List<Entry> entryHistory;
	int maxHistory = 1000;
	InputHistory history;
	long entryCounter;
	
	public Console()
	{
		init();
	}
	
	private void init()
	{
		actions = new HashMap<String, Action>();
		links = new HashMap<String, Link>();
		entryHistory = new LinkedList<Entry>();
		
		display = new JEditorPane("text/html", default_html);
		display.setEditable(false);
		display.addHyperlinkListener(new LinkListener());
		document = (HTMLDocument)display.getDocument();
		editor = (HTMLEditorKit)display.getEditorKit();
		
		setStyleSheet(default_style_sheet);
		
		scroll = new JScrollPane(display);
		scroll.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		input = new JTextField();
		input.setFont(new Font("SansSerif", Font.PLAIN, 12));
		input.addActionListener(new InputListener());
		history = new InputHistory();
		input.addKeyListener(history);
		
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
		add(input, BorderLayout.SOUTH);
	}
		
	public void setStyleSheet(URL url)
	{
		if(url == null)
			return;
		
		document.getStyleSheet().importStyleSheet(url);
	}
	
	public void setEcho(boolean e)
	{
		echo = e;
	}
	
	public boolean getEcho()
	{
		return echo;
	}
	
	public void registerAction(String command, Action a)
	{
		if(command == null || a == null)
			return;
		
		actions.put(command.toLowerCase(), a);
	}
	
	protected String registerLink(Link l)
	{
		if(l == null)
			return null;
		
		String key = "link" + linkCounter;
		links.put(key, l);
		linkCounter++;
		return key;
	}
	
	protected Element appendHTML(String html)
	{	
		Element element = null;
						
		try
		{
			document.insertBeforeEnd(getHTMLListContainerElement(), html);
//			writeHTML(new File("output/log.txt"));
		}
		catch(BadLocationException e){e.printStackTrace();}
		catch(IOException e){e.printStackTrace();}
		
		// adjust focus to bottom of console
		scrollToBottom();
		
		return element;
	}
	
	protected Element getHTMLBodyElement()
	{
		Element root = document.getRootElements()[0];
		
		for(int i=0; i<root.getElementCount(); i++) 
		{
		    AttributeSet attrib = root.getElement(i).getAttributes();
		    Object tag = attrib.getAttribute(StyleConstants.NameAttribute);
		    if(tag == HTML.Tag.BODY) 
		        return root.getElement(i);
		}
		
		return null;
	}

	protected Element getHTMLListContainerElement()
	{
		Element container = document.getElement("container");
		if(container==null)
			return getHTMLBodyElement();
		else
			return container;		
	}
	
	public Entry appendEntry(String text, String style)
	{
		return appendEntry(text, style, new Link[]{});
	}
	
	public Entry appendEntry(String text, String style, Link ... links)
	{	
		String id = "entry" + entryCounter;
		Entry entry = new Entry(this, id, text, style, links);
		appendHTML(entry.getHTML());
		entryHistory.add(entry);
		if(entryHistory.size() > maxHistory)
			removeEntry(entryHistory.get(0));			
		entryCounter++;
		return entry;
	}
	
	public void removeEntry(Entry entry)
	{
		if(entry == null)
			return;
		Element elem = document.getElement(entry.getID());
		if(elem == null)
			return;
		
		try
		{
			document.setOuterHTML(elem, "&nbsp;");
			entryHistory.remove(entry);
		}
		catch(BadLocationException e){e.printStackTrace();}
		catch(IOException e){e.printStackTrace();}
	}

	public void refreshEntry(Entry entry)
	{
		if(entry == null)
			return;
		Element elem = document.getElement(entry.getID());	
		if(elem == null)
			return;
		
		entry.incrementRevision();
		try
		{
			document.insertAfterEnd(elem, entry.getHTML());
			document.setOuterHTML(elem, "&nbsp;");
		}
		catch(BadLocationException e){e.printStackTrace();}
		catch(IOException e){e.printStackTrace();}
	}
	
	public void scrollToEntry(Entry entry)
	{
		if(entry == null)
			return;
		Element elem = document.getElement(entry.getID());	
		if(elem == null)
			return;
		
		if(elem.getStartOffset() > 0 && elem.getStartOffset() <= document.getLength())
			display.setCaretPosition(elem.getStartOffset());
	}

	public void scrollToBottom()
	{
		display.setCaretPosition(document.getLength());
	}
	
	public void clear()
	{
		display.setText(default_html);
	}

	public boolean invoke(String str)
	{
		String command = "";
		String[] args = {};

		String[] split = str.split("[\\s,]\\s*");
		
		if(split.length > 0)		
		{
			command = split[0];
			args = new String[split.length-1];
			System.arraycopy(split, 1, args, 0, split.length-1);
		}
		
		Action action = actions.get(command.toLowerCase());
		
		if(action == null)
		{
			error("action \"" + command + "\" not recognized.");
			return false;
		}
		else
		{
			action.execute(args);
			return true;
		}
	}
	
	public Entry echo(String text)
	{
		text = echo_prefix + text + echo_suffix;
		return appendEntry(text, "echo");	
	}
	
	public Entry echo(String text, Link ... links)
	{
		text = echo_prefix + text + echo_suffix;
		return appendEntry(text, "echo", links);	
	}

	public Entry event(String text)
	{
		text = event_prefix + text + event_suffix;
		return appendEntry(text, "event");	
	}

	public Entry event(String text, Link ... links)
	{
		text = event_prefix + text + event_suffix;
		return appendEntry(text, "event", links);	
	}
	
	public Entry error(String error)
	{
		error = error_prefix + error + error_suffix;
		return appendEntry(error, "error");	
	}
	
	public Entry error(String error, Link ... links)
	{
		error = error_prefix + error + error_suffix;
		return appendEntry(error, "error", links);	
	}
	
	public Entry verbose(String text)
	{
		text = verbose_prefix + text + verbose_suffix;
		return appendEntry(text, "verbose");	
	}
	
	public Entry verbose(String text, Link ... links)
	{
		text = verbose_prefix + text + verbose_suffix;
		return appendEntry(text, "verbose", links);	
	}

	public void writeHTML(File file) throws BadLocationException, IOException
	{
		FileWriter fw = new FileWriter(file);
		HTMLWriter htmlWriter = new HTMLWriter(fw, document);
		htmlWriter.write();
		fw.flush();
		fw.close();
	}

	public static String italics(String text)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("<i>");
		buffer.append(text);
		buffer.append("</i>");
		return buffer.toString();
	}
	
	public static String bold(String text)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("<b>");
		buffer.append(text);
		buffer.append("</b>");
		return buffer.toString();
	}
	
	public static String color(String text, String color)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("<font color=");
		buffer.append(color);
		buffer.append(">");
		buffer.append(text);
		buffer.append("</font>");
		return buffer.toString();
	}
	
	public static String img(URL url)
	{
		if(url!=null)
			return "<img border=0 src=\"" + url.toString() + "\">";
		else
			return "<img src=\"null\">";
	}
	
	public static String escapeHTML(String html)
	{
		StringBuilder buffer = new StringBuilder();
		char[] characters = html.toCharArray();
		
		for(char c : characters)
			buffer.append(escapeHTML(c));
		
		return buffer.toString();
	}
		
	protected static String escapeHTML(char c)
	{
		switch(c){
		case '<':
			return "&lt;";
		case '>':
			return "&gt;";
		default:
			return Character.toString(c);
		}
	}
	
	class InputListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			JTextField source;
			
			if(e.getSource() instanceof JTextField)
				source = (JTextField)e.getSource();
			else
				return;

			String text = escapeHTML(source.getText().trim());	
			if(echo)
				echo(text);			
			invoke(text);
			
			if(source == input)
			{		
				history.add(source.getText());
				input.setText("");
			}		
		}
	}

	class LinkListener implements HyperlinkListener
	{		
		public void hyperlinkUpdate(HyperlinkEvent e) 
		{
			if(e.getEventType() == EventType.ACTIVATED)
			{
				Link link = links.get(e.getDescription());
				if(link!=null)
					link.clicked(e.getSourceElement());
			}
		}
	}
	
	class InputHistory extends KeyAdapter
	{		
		LinkedList<String> inputHistory;
		ListIterator<String> historyCaret;
		boolean historyCaretReverse;
		int maxHistory = 1000;
		
		public InputHistory()
		{
			inputHistory = new LinkedList<String>();
			historyCaret = inputHistory.listIterator();
			historyCaretReverse = false;
		}
		
		public void add(String str)
		{
			inputHistory.addFirst(str);
			if(inputHistory.size()>maxHistory)
				inputHistory.remove(0);
			historyCaret = inputHistory.listIterator();
			historyCaretReverse = false;
		}
		
		public void keyPressed(KeyEvent e) 
		{
			if(e.getKeyCode()==KeyEvent.VK_UP)
			{ 
				tabUp();
				if(historyCaretReverse)
					tabUp();
				historyCaretReverse = false;
			}
			else if(e.getKeyCode()==KeyEvent.VK_DOWN)
			{
				tabDown();
				if(!historyCaretReverse)
					tabDown();
				historyCaretReverse = true;
			}
		}
		
		private void tabUp()
		{
			if(historyCaret.hasNext())
				input.setText(historyCaret.next());
		}
		
		private void tabDown()
		{
			if(historyCaret.hasPrevious())
				input.setText(historyCaret.previous());
			else
				input.setText("");
		}
	}
}