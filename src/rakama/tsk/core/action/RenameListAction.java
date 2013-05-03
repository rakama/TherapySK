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
import rakama.tsk.console.Entry;
import rakama.tsk.core.table.ListManager;
import rakama.tsk.core.table.event.EventUndoListener;
import rakama.tsk.io.StringUtil;
import rakama.tsk.list.SKList;

public class RenameListAction implements Action
{
	Console console;
	ListManager manager;
	
	public RenameListAction(Console console, ListManager manager)
	{
		this.console = console;
		this.manager = manager;
	}

	public void execute(String[] args) 
	{	
		if(args.length == 0)
		{
			console.error("unable to rename (name cannot be empty).");
			return;		
		}
		
		SKList list = manager.copyList();
		String old_name = list.getTitle();
		StringBuilder builder = new StringBuilder();
		builder.append(args[0]);
		for(int i=1; i<args.length; i++)
			builder.append(" " + args[i]);
		String new_name = builder.toString();

		if(new_name.length() > 64)
		{
			console.error("unable to rename ('" + new_name + "' is too long).");
			return;		
		}
		
		if(new_name.equals(old_name))
			return;
		
		// check if name is non-null and alphanumeric
		if(!isValidName(new_name))
		{
			console.error(invalidName(new_name));
			return;
		}
		
		list.setTitle(new_name);		
		Entry entry = console.event(listRenamed(old_name, new_name));
		manager.setList(list, new EventUndoListener(console, entry));
	}

	private boolean isValidName(String name)
	{
		if(name.length()==0)
			return false;
		
		if(StringUtil.isAlphaNumeric(name, true))
			return true;
		
		return false;
	}
	
	private String listRenamed(String old_name, String new_name)
	{
		StringBuilder str = new StringBuilder();
		str.append("List '");
		str.append(Console.italics(old_name));
		str.append("' renamed to '" + Console.italics(new_name));
		str.append("'.");
		return str.toString();
	}

	private String invalidName(String name)
	{
		return "unable to rename ('" + name + "' has invalid characters).";
	}
}