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

package rakama.tsk.core.table.event;

import rakama.tsk.console.Console;
import rakama.tsk.console.Entry;
import rakama.tsk.core.table.UndoListener;
import rakama.tsk.core.table.ListManager.ListEvent;

public class EventUndoListener implements UndoListener
{
	Console console;
	Entry entry;
	
	public EventUndoListener(Console console, Entry entry)
	{
		this.console = console;
		this.entry = entry;
	}
	
	public void listStatusChanged(ListEvent e) 
	{
		if(e == ListEvent.UNDO)
		{
			entry.setStyle("undo");
			console.scrollToEntry(entry);
		}
		else if(e == ListEvent.REDO)
		{
			entry.setStyle("event");
			console.scrollToEntry(entry);
		}
		else if(e == ListEvent.DISCARDED)
			entry.setStyle("discarded");
	}
}