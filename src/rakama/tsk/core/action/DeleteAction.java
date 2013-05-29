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

import javax.swing.JOptionPane;

import rakama.tsk.console.Action;
import rakama.tsk.console.Console;
import rakama.tsk.console.Entry;
import rakama.tsk.core.table.ListManager;
import rakama.tsk.core.table.event.EventUndoListener;
import rakama.tsk.io.StringUtil;
import rakama.tsk.list.SKList;

public class DeleteAction implements Action
{
    Console console;
    ListManager manager;

    public DeleteAction(Console console, ListManager manager)
    {
        this.console = console;
        this.manager = manager;
    }

    public void execute(String[] names)
    {
        SKList list = manager.copyList();

        // bail on trivial bad input
        if(names.length == 0)
        {
            console.error("unable to delete (no name provided).");
            return;
        }
        else if(names.length == 1 && !list.hasPlayer(names[0]))
        {
            console.error(playerNotFound(StringUtil.applyTitleCase(names[0])));
            return;
        }

        // confirm deletion
        if(!confirm(names))
            return;

        // store old ranks
        int[] old_ranks = new int[names.length];
        for(int i = 0; i < names.length; i++)
            old_ranks[i] = list.getIndex(names[i]);

        for(int i = 0; i < names.length; i++)
        {
            // skip empty names
            if(names[i].length() <= 0)
                continue;

            int rank = list.getIndex(names[i]);
            names[i] = StringUtil.applyTitleCase(names[i]);

            // delete player if player exists
            if(rank == -1)
                console.error(playerNotFound(names[i]));
            else
            {
                list.remove(rank);
                Entry entry = console.event(playerDeleted(names[i], old_ranks[i] + 1));
                manager.setList(list, new EventUndoListener(console, entry));
            }
        }
    }

    private boolean confirm(String[] names)
    {
        int count = 0;
        for(String name : names)
            if(name.length() > 0)
                count++;

        String question;

        if(names.length == 0)
            return true;
        else if(names.length > 1)
            question = "Remove (" + count + ") players from the current list?";
        else
            question = "Remove '" + StringUtil.applyTitleCase(names[0])
                    + "' from the current list?";

        return JOptionPane.showConfirmDialog(null, question, "Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private String playerDeleted(String name, int rank)
    {
        return "'" + Console.italics(name) + "' deleted (rank " + rank + ").";
    }

    private String playerNotFound(String name)
    {
        return "cannot delete '" + name + "' (name not found).";
    }
}