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

package rakama.tsk.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import rakama.tsk.console.Console;
import rakama.tsk.console.action.ClearAction;
import rakama.tsk.console.action.MemInfoAction;
import rakama.tsk.console.link.DeleteEntryLink;
import rakama.tsk.core.action.AboutAction;
import rakama.tsk.core.action.AbsentAction;
import rakama.tsk.core.action.DeleteAction;
import rakama.tsk.core.action.HelpAction;
import rakama.tsk.core.action.InsertAction;
import rakama.tsk.core.action.ListAction;
import rakama.tsk.core.action.MoveAction;
import rakama.tsk.core.action.PresentAction;
import rakama.tsk.core.action.RankAction;
import rakama.tsk.core.action.RedoAction;
import rakama.tsk.core.action.RenameAction;
import rakama.tsk.core.action.RenameListAction;
import rakama.tsk.core.action.SuicideAction;
import rakama.tsk.core.action.UndoAction;
import rakama.tsk.core.table.ListManager;
import rakama.tsk.core.table.ListTable;
import rakama.tsk.io.DefaultListReader;
import rakama.tsk.io.DefaultListWriter;
import rakama.tsk.io.FileLoader;
import rakama.tsk.io.ListReader;
import rakama.tsk.io.ListWriter;
import rakama.tsk.list.SKList;

@SuppressWarnings("serial")
public class Editor extends JPanel
{
	JFrame parent;
	Console console;
	ListTable table;
	JSplitPane split;
	JMenuBar menu;
	
	File current_file;
	
	boolean dirty;
	
	public Editor(JFrame parent)
	{
		this.parent = parent;
		init();
	}
	
	private void init()
	{
		ListManager manager = new ListManager();
		SKList testList = new SKList("Untitled");
		manager.setList(testList);		
		console = new Console();
		table = new ListTable(manager, console);
		
		refreshTitle();
		
		split = new JSplitPane();
		split.setLeftComponent(table);
		split.setRightComponent(console);
		split.setDividerSize(3);
		
		FileLoader loader = FileLoader.getDefaultFileLoader();
		DeleteEntryLink.close_html = Console.img(loader.getURL("icons/close.png"));
		Console.error_prefix = "<table cellpadding=0><tr><td valign=top>";
		Console.error_prefix += Console.img(loader.getURL("icons/warning.png"));
		Console.error_prefix += "</td><td>&nbsp;Error: ";
		Console.error_suffix = "</td></tr></table>";
		
		// register console commands
		console.registerAction("suicide", new SuicideAction(console, manager));
		console.registerAction("move", new MoveAction(console, manager));
		console.registerAction("present", new PresentAction(console, manager));
		console.registerAction("absent", new AbsentAction(console, manager));
		console.registerAction("insert", new InsertAction(console, manager));
		console.registerAction("delete", new DeleteAction(console, manager));
		console.registerAction("rename", new RenameAction(console, manager));
		console.registerAction("rlist", new RenameListAction(console, manager));
		console.registerAction("rank", new RankAction(console, manager));
		console.registerAction("list", new ListAction(console, manager));
		console.registerAction("undo", new UndoAction(console, manager));
		console.registerAction("redo", new RedoAction(console, manager));
		console.registerAction("clear", new ClearAction(console));
		console.registerAction("help", new HelpAction(console));
		console.registerAction("about", new AboutAction(console));
		console.registerAction("meminfo", new MemInfoAction(console));
		
		console.setPreferredSize(new Dimension(300, 600));
		
		setLayout(new BorderLayout());
		add(split, BorderLayout.CENTER);
	}

	public Console getConsole()
	{
		return console;
	}
	
	public ListTable getTable()
	{
		return table;
	}

	public ListManager getListManager()
	{
		return table.getListManager();
	}
	
	public File getCurrentFile()
	{
		return current_file;
	}
	
	public JFrame getParentFrame()
	{
		return parent;
	}
	
	public JMenuBar getMenu()
	{
		JMenuBar menu = new JMenuBar();

		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu help = new JMenu("Help");

		JMenuItem newf = new JMenuItem("New");
		JMenuItem open = new JMenuItem("Open File...");	
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));	
		JMenuItem saveas = new JMenuItem("Save As...");
		JMenuItem prop = new JMenuItem("Properties");
		JMenuItem exit = new JMenuItem("Exit");

		file.add(newf);
		file.add(open);
		file.addSeparator();
		file.add(save);
		file.add(saveas);
		file.addSeparator();
		file.add(prop);
		file.addSeparator();
		file.add(exit);

		JMenuItem undo = new JMenuItem("Undo");
		undo.setAccelerator(KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK, true));		
		JMenuItem redo = new JMenuItem("Redo");
		redo.setAccelerator(KeyStroke.getKeyStroke('Y', InputEvent.CTRL_DOWN_MASK, true));
		JMenuItem all = new JMenuItem("Select All");
		all.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK));	
		JMenuItem ins = new JMenuItem("Insert New Player...");
		
		undo.setEnabled(false);
		redo.setEnabled(false);
		
		edit.add(undo);
		edit.add(redo);
		edit.addSeparator();
		edit.add(all);
		edit.add(ins);
		
		JMenuItem helpf = new JMenuItem("Help");
		JMenuItem about = new JMenuItem("About");

		help.add(helpf);
		help.addSeparator();
		help.add(about);		
		
		menu.add(file);
		menu.add(edit);
		menu.add(help);
		
		MenuListener ml = new MenuListener();	
		ml.registerAction(newf, new Runnable(){public void run(){
			newfile();}});	
		ml.registerAction(save, new Runnable(){public void run(){
			save();}});	
		ml.registerAction(saveas, new Runnable(){public void run(){
			saveas();}});	
		ml.registerAction(open, new Runnable(){public void run(){
			open();}});	
		ml.registerAction(prop, new Runnable(){public void run(){
			showProperties();}});	
		ml.registerAction(exit, new Runnable(){public void run(){
			if(closeFile()) System.exit(0);}});

		ml.registerAction(undo, new Runnable(){public void run(){
			console.invoke("undo");}});	
		ml.registerAction(redo, new Runnable(){public void run(){
			console.invoke("redo");}});	
		ml.registerAction(all, new Runnable(){public void run(){
			table.getTable().selectAll();}});
		ml.registerAction(ins, new Runnable(){public void run(){
			table.showInsertPopup();}});	

		ml.registerAction(helpf, new Runnable(){public void run(){
			console.invoke("help");}});
		ml.registerAction(about, new Runnable(){public void run(){
			console.invoke("about");}});	
			
		table.getListManager().addTableModelListener(new TableModelAdapter(undo){
			public void run(){item.setEnabled(table.getListManager().canUndo());}});
		table.getListManager().addTableModelListener(new TableModelAdapter(redo){
			public void run(){item.setEnabled(table.getListManager().canRedo());}});
		table.getListManager().addTableModelListener(new TableModelAdapter(null){
			public void run(){listUpdated();}});
		
		return menu;
	}
	
	public void newfile()
	{
		final String title = "Create New List";
		final String message = "Enter list name:";
		String name = JOptionPane.showInputDialog(null, message, title,
				JOptionPane.PLAIN_MESSAGE);
		
		if(name==null)
			return;
		
		if(!closeFile())
			return;
		
		current_file = null;
		getListManager().setList(new SKList(name));
		getListManager().clearHistory();
		dirty = false;
		console.verbose("Created new list (" + Console.italics(name) + ")");
		
		refreshTitle();
	}
	
	public boolean closeFile()
	{
		if(current_file == null && dirty)
		{
			int response = JOptionPane.showConfirmDialog(this, 
					"Save changes to '" + getListManager().getListTitle() 
					+ "' before closing?", "Save File", JOptionPane.YES_NO_CANCEL_OPTION);
			
			if(response == JOptionPane.CANCEL_OPTION)
				return false;
			
			if(response == JOptionPane.YES_OPTION)
				return saveas();
		}
		else if(current_file != null && dirty)
		{
			int response = JOptionPane.showConfirmDialog(this, 
					"Save changes to '" + current_file.getName() + "' before closing?", 
					"Save File", JOptionPane.YES_NO_CANCEL_OPTION);
			
			if(response == JOptionPane.CANCEL_OPTION)
				return false;
			
			if(response == JOptionPane.YES_OPTION)
				return save();
		}
		
		return true;
	}
	
	public boolean save()
	{
		if(current_file == null)
		{			
			return saveas();
		}
		
		ListWriter writer = new DefaultListWriter();
		
		try
		{
			SKList list = table.getListManager().copyList();
			list.setDate(new Date());
			writer.writeList(list, current_file);
			dirty = false;
			console.verbose("Saved '" + current_file.getName() + "' (" 
					+ Console.italics(list.getTitle()) + ")");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		
		refreshTitle();
		return true;
	}

	public boolean saveas()
	{
		FileDialog dialog = new FileDialog(parent,"Save");
		dialog.setLocationRelativeTo(parent);
		dialog.setMode(FileDialog.SAVE);
		dialog.setVisible(true);
		
		if(dialog.getFile() == null)
			return false;
		
		current_file = new File(dialog.getDirectory() + dialog.getFile());
		save();

		refreshTitle();
		return true;
	}

	public void open()
	{
		FileDialog dialog = new FileDialog(parent,"Open");
		dialog.setLocationRelativeTo(parent);
		dialog.setMode(FileDialog.LOAD);
		dialog.setVisible(true);

		if(dialog.getFile() == null)
			return;
		
		if(!closeFile())
			return;
		
		current_file = new File(dialog.getDirectory() + dialog.getFile());
		
		ListReader reader = new DefaultListReader(console);
		try
		{
			SKList list = reader.readList(current_file);
			if(list==null)
				return;
			
			getListManager().setList(list);
			getListManager().clearHistory();
			dirty = false;
			console.verbose("Loaded '" + current_file.getName() + "' (" 
					+ Console.italics(list.getTitle()) + ")");
		} 
		catch(MalformedURLException e){e.printStackTrace();} 
		catch(IOException e){e.printStackTrace();}

		refreshTitle();
	}
	
	public void showProperties()
	{
		Properties.getDefaultPropertiesDialog(this).setVisible(true);
	}
	
	public void listUpdated()
	{
		dirty = true;
		refreshTitle();
	}
	
	public void refreshTitle()
	{
		String listname = table.getListManager().getListTitle();
		if(current_file!=null && current_file.isFile())
			listname += " (" + current_file.getAbsolutePath() + ")";
		parent.setTitle("Therapy SK - " + listname);
	}
	
	class MenuListener implements ActionListener
	{
		Map<Object, Runnable> actions;
		
		public MenuListener()
		{
			actions = new HashMap<Object, Runnable>();
		}

		public void registerAction(JMenuItem s, Runnable r)
		{
			s.addActionListener(this);
			actions.put(s, r);
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			Runnable action = actions.get(e.getSource());
			if(action!=null)
				action.run();
		}		
	}

	class TableModelAdapter implements TableModelListener
	{
		JMenuItem item;
		
		public TableModelAdapter(JMenuItem item)
		{
			this.item = item;
		}
		
		public void tableChanged(TableModelEvent e){run();}	
		public void run(){}
	}
}