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

package rakama.tsk.core.action;

import rakama.tsk.console.Action;
import rakama.tsk.console.Console;
import rakama.tsk.console.link.DeleteEntryLink;


public class HelpAction implements Action
{
	public String helptext = "";
	
	Console console;
	
	public HelpAction(Console console)
	{
		this.console = console;
		initENG();
	}
	
	private void initENG()
	{
		StringBuilder text = new StringBuilder();

		text.append("<table><tr><td align=right>");	

		text.append("<u>Description</u> &nbsp;&nbsp; <br>");
		text.append("List all players by rank :&nbsp; <br>");	
		text.append("List specific players by rank :&nbsp; <br>");	
		text.append("Suicide a player :&nbsp; <br>");
		text.append("Mark player(s) present :&nbsp; <br>");		
		text.append("Mark player(s) absent :&nbsp; <br>");		
		text.append("Re-names a player on the list :&nbsp; <br>");
		text.append("Insert player(s) into the list :&nbsp; <br>");
		text.append("Delete player(s) from the list :&nbsp; <br>");
		text.append("Undo or re-do the last action :&nbsp; <br>");	
		text.append("Clear the console :&nbsp; <br>");	
		
		text.append("</td><td align=left>");
		
		text.append("<u>Command</u><br>");
		text.append("<b>list</b><br>");
		text.append("<b>rank</b> <i>multiple names</i> <br>");
		text.append("<b>suicide</b> <i>name</i><br>");
		text.append("<b>present</b> <i>multiple names</i> <br>");
		text.append("<b>absent</b> <i>multiple names</i> <br>");
		text.append("<b>rename</b> <i>oldname newname</i> <br>");
		text.append("<b>insert</b> <i>multiple names</i> <br>");
		text.append("<b>delete</b> <i>multiple names</i> <br>");
		text.append("<b>undo</b> or <b>redo</b><br>");
		text.append("<b>clear</b><br>");	


		text.append("</td></tr></table>");
		
		helptext = text.toString();
	}

	public void execute(String[] names) 
	{	
		DeleteEntryLink delete = new DeleteEntryLink();
		delete.setEntry(console.verbose(helptext, delete));
	}
}