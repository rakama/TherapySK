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
import rakama.tsk.core.table.ListManager;
import rakama.tsk.io.StringUtil;
import rakama.tsk.list.SKList;

public class ListAction implements Action
{
	Console console;
	ListManager manager;
	
	public ListAction(Console console, ListManager manager)
	{
		this.console = console;
		this.manager = manager;
	}

	public void execute(String[] names) 
	{
		SKList list = manager.copyList();		
		
		StringBuilder output = new StringBuilder();
		
		for(int i=0; i<list.size(); i++)
		{
			String name = list.get(i).getName();
			String rank = Console.bold(i + 1 + " ");
			output.append(rank + StringUtil.applyTitleCase(name) + "<br>");
		}
		
		if(!list.isEmpty())
		{
			DeleteEntryLink delete = new DeleteEntryLink();
			String text = "<u># Name</u><br>" + output.toString();
			delete.setEntry(console.verbose(text, delete));
		}
	}
}