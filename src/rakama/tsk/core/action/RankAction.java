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

import java.util.SortedMap;
import java.util.TreeMap;

import rakama.tsk.console.Action;
import rakama.tsk.console.Console;
import rakama.tsk.console.link.DeleteEntryLink;
import rakama.tsk.core.table.ListManager;
import rakama.tsk.io.StringUtil;
import rakama.tsk.list.SKList;

public class RankAction implements Action
{
	Console console;
	ListManager manager;
	
	public RankAction(Console console, ListManager manager)
	{
		this.console = console;
		this.manager = manager;
	}

	public void execute(String[] names) 
	{
		if(names.length == 0)
		{
			console.error("unable to rank (no names provided).");
			return;		
		}
		
		SKList list = manager.copyList();		
		
		StringBuilder output = new StringBuilder();
		SortedMap<Integer, String> map = new TreeMap<Integer, String>();
		
		for(String name : names)
		{
			// skip empty names
			if(name.length()<=0)
				continue;

			int rank = list.getIndex(name);
			if(rank > -1)
				map.put(list.getIndex(name), name.toLowerCase());
			else
				console.error(playerNotFound(name));	
		}
		
		for(Integer i : map.keySet())
		{
			String name = map.get(i);
			String rank = Console.bold(i.intValue() + 1 + " ");
			output.append(rank + StringUtil.applyTitleCase(name) + "<br>");
		}
		
		if(!map.isEmpty())
		{
			DeleteEntryLink delete = new DeleteEntryLink();
			String text = "<u># Name</u><br>" + output.toString();
			delete.setEntry(console.verbose(text, delete));
		}
	}
	
	private String playerNotFound(String name)
	{
		return "cannot rank '" + StringUtil.applyTitleCase(name) + "' (name not found).";
	}
}