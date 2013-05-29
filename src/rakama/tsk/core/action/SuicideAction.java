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

public class SuicideAction implements Action
{
    Console console;
    ListManager manager;

    Entry entry;

    public SuicideAction(Console console, ListManager manager)
    {
        this.console = console;
        this.manager = manager;
    }

    public void execute(String[] names)
    {
        if(names.length == 0)
        {
            console.error("unable to suicide (no name provided).");
            return;
        }
        else if(names.length > 1)
        {
            console.error("unable to suicide (expected one argument).");
            return;
        }

        String name = StringUtil.applyTitleCase(names[0]);

        SKList list = manager.copyList();
        int old_pos = list.getIndex(name);

        // player is not in list
        if(old_pos == -1)
        {
            console.error(playerNotFound(name));
            return;
        }

        if(!list.get(old_pos).isPresent() && !confirm(name))
            return;

        list.suicide(old_pos);
        entry = console.event(playerSuicided(name, old_pos + 1, list.getIndex(name) + 1));
        manager.setList(list, new EventUndoListener(console, entry));
    }

    private boolean confirm(String names)
    {
        String question = "'" + names + "' is not marked present.\n"
                + "Suicide this player anyway?";

        int response = JOptionPane.showConfirmDialog(null, question, "Suicide Player",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        return response == JOptionPane.YES_OPTION;
    }

    private String playerSuicided(String name, int old_rank, int new_rank)
    {
        StringBuilder str = new StringBuilder();
        str.append("'");
        str.append(Console.italics(name));
        str.append("' suicided");
        str.append(" (moved from rank " + old_rank + " to " + new_rank + ").");
        return str.toString();
    }

    private String playerNotFound(String name)
    {
        return "cannot suicide '" + name + "' (name not found).";
    }
}