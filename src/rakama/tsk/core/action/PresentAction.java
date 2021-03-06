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
import rakama.tsk.list.Player;
import rakama.tsk.list.SKList;

public class PresentAction implements Action
{
    Console console;
    ListManager manager;

    public PresentAction(Console console, ListManager manager)
    {
        this.console = console;
        this.manager = manager;
    }

    public void execute(String[] names)
    {
        if(names.length == 0)
        {
            console.error("unable to mark present (no names provided).");
            return;
        }

        SKList list = manager.copyList();

        int i = 0;

        for(String name : names)
        {
            // skip empty names
            if(name.length() <= 0)
                continue;

            int rank = list.getIndex(name);

            // mark present if player exists
            if(rank == -1)
                console.error(playerNotFound(name));
            else
            {
                Player p = list.get(rank);
                if(!p.isPresent())
                {
                    p.setPresent(true);
                    i++;
                }
            }
        }

        if(i > 0)
        {
            Entry entry = console.event("Players marked present (" + i + ").");
            manager.setList(list, new EventUndoListener(console, entry));
        }
    }

    private String playerNotFound(String name)
    {
        return "cannot mark '" + name + "' present (name not found).";
    }
}