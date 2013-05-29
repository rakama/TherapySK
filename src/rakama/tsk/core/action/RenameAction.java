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

public class RenameAction implements Action
{
    Console console;
    ListManager manager;

    public RenameAction(Console console, ListManager manager)
    {
        this.console = console;
        this.manager = manager;
    }

    public void execute(String[] args)
    {
        if(args.length == 0)
        {
            console.error("unable to rename (name must be provided).");
            return;
        }
        else if(args.length == 1)
        {
            console.error("unable to rename (a new name must be provided).");
            return;
        }
        else if(args.length > 2)
        {
            console.error("unable to rename (expected two arguments).");
            return;
        }

        String old_name = StringUtil.applyTitleCase(args[0]);
        String new_name = StringUtil.applyTitleCase(args[1]);

        if(old_name.equals(new_name))
            return;

        SKList list = manager.copyList();
        int rank = list.getIndex(old_name);

        // player is not in list
        if(rank == -1)
        {
            console.error(playerNotFound(old_name));
            return;
        }

        // check if name is non-null and alphanumeric
        if(!isValidName(new_name))
        {
            console.error(invalidName(old_name, new_name));
            return;
        }

        // player already exists
        if(list.hasPlayer(new_name))
        {
            console.error(playerFound(old_name, new_name));
            return;
        }

        list.get(rank).setName(new_name);
        Entry entry = console.event(playerRenamed(old_name, new_name));
        manager.setList(list, new EventUndoListener(console, entry));
    }

    private boolean isValidName(String name)
    {
        if(name.length() == 0)
            return false;

        if(StringUtil.isAlphaNumeric(name))
            return true;

        return false;
    }

    private String playerRenamed(String old_name, String new_name)
    {
        StringBuilder str = new StringBuilder();
        str.append("Player '");
        str.append(Console.italics(old_name));
        str.append("' renamed to '" + Console.italics(new_name));
        str.append("'.");
        return str.toString();
    }

    private String playerNotFound(String old_name)
    {
        return "cannot rename '" + old_name + "' (name not found).";
    }

    private String invalidName(String old_name, String name)
    {
        return "unable to rename '" + old_name + "' ('" + name + "' has invalid characters).";
    }

    private String playerFound(String old_name, String name)
    {
        return "unable to rename '" + old_name + "' ('" + name + "' already exists).";
    }
}