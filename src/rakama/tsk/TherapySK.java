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

package rakama.tsk;

import java.awt.Image;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import rakama.tsk.console.Entry;
import rakama.tsk.console.link.DeleteEntryLink;
import rakama.tsk.core.Editor;
import rakama.tsk.io.FileLoader;

@SuppressWarnings("serial")
public class TherapySK extends JFrame
{	
	public static List<Image> icon_images = fetchIconImages();
	
	Editor panel;
	
	public static void main(String[] args)
	{		
		TherapySK tsk = new TherapySK();
		tsk.setSize(800, 600);
		tsk.setLocationRelativeTo(null);
		tsk.setVisible(true);
	}
	
	public TherapySK()
	{
		super("Therapy SK");
		load();
	}
	
	private void load()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			System.err.println(e);
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		setIconImages(icon_images);
		
		panel = new Editor(this);		
		getContentPane().add(panel);
		setJMenuBar(panel.getMenu());
		
		String welcome_message = "Welcome to Therapy SK";
		String help_message = "type <b>help</b> for a list of console commands.";
		Entry welcome = panel.getConsole().verbose(welcome_message + " - " + help_message);
		welcome.addLink(new DeleteEntryLink(welcome));
		
//		panel.getConsole().echo(TherapySK.class.getResource("TherapySK.class").toString());
		
		pack();
	}

	protected void processWindowEvent(WindowEvent e) 
	{
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
        	if(panel.closeFile())
        		System.exit(0);
        }
        else
            super.processWindowEvent(e);
	}
	
	private static List<Image> fetchIconImages()
	{
		List<Image> icons = new ArrayList<Image>();
		FileLoader loader = FileLoader.getDefaultFileLoader();
		icons.add(loader.getImage("icons/sk_white_huge.png"));
		icons.add(loader.getImage("icons/sk_white_large.png"));
		icons.add(loader.getImage("icons/sk_white_med.png"));
		icons.add(loader.getImage("icons/sk_white_small.png"));	
		return icons;
	}
}