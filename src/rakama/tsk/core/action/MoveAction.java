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
import rakama.tsk.list.Player;
import rakama.tsk.list.SKList;

public class MoveAction implements Action
{
    Console console;
    ListManager manager;

    Entry entry;

    public MoveAction(Console console, ListManager manager)
    {
        this.console = console;
        this.manager = manager;
    }

    public void execute(String[] args)
    {
        if(args.length == 0)
        {
            console.error("unable to move (no name provided).");
            return;
        }
        else if(args.length == 1)
        {
            console.error("unable to move (expected two arguments).");
            return;
        }
        else if(args.length > 2)
        {
            console.error("unable to move (expected two arguments).");
            return;
        }

        String name = StringUtil.applyTitleCase(args[0]);

        SKList list = manager.copyList();
        int old_rank = list.getIndex(name) + 1;

        // player is not in list
        if(old_rank == 0)
        {
            console.error(playerNotFound(name));
            return;
        }

        int new_rank;

        try
        {
            new_rank = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException e)
        {
            console.error("cannot move'" + name + "' ('" + args[1] + "' is not a valid rank).");
            return;
        }

        if(new_rank < 1)
        {
            console.error("cannot move'" + name + "' (rank must be positive).");
            return;
        }

        if(old_rank == new_rank)
            return;

        if(new_rank > list.size())
        {
            console.error("cannot move'" + name + "' (rank " + new_rank + " is too high).");
            return;
        }

        if(!confirm(name, old_rank, new_rank))
            return;

        Player player = list.get(old_rank - 1);

        if(new_rank < old_rank)
            list.add(new_rank - 1, player.clone());
        else
            list.add(new_rank, player.clone());

        list.remove(player);
        entry = console.event(playerMoved(name, old_rank, list.getIndex(name) + 1));
        manager.setList(list, new EventUndoListener(console, entry));
    }

    private boolean confirm(String name, int old_rank, int new_rank)
    {
        String question = "Are you sure you want to move '" + name + "' from rank " + old_rank
                + " to " + new_rank + "?";

        return JOptionPane.showConfirmDialog(null, question, "Move Player",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private String playerMoved(String name, int old_rank, int new_rank)
    {
        StringBuilder str = new StringBuilder();
        str.append("'");
        str.append(Console.italics(name));
        str.append("' moved");
        str.append("  from rank " + old_rank + " to " + new_rank + ".");
        return str.toString();
    }

    private String playerNotFound(String name)
    {
        return "cannot move '" + name + "' (name not found).";
    }
}