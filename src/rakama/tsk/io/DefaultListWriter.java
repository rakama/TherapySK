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

package rakama.tsk.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import rakama.tsk.list.Player;
import rakama.tsk.list.SKList;

public class DefaultListWriter implements ListWriter
{
    public static DateFormat date_format = new SimpleDateFormat("EEEEE, MMMMM d, yyyy hh:mm a z");

    public void writeList(SKList list, File file) throws IOException
    {
        String str = getString(list);
        FileOutputStream stream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-16");
        BufferedWriter out = new BufferedWriter(writer);
        out.write(str);
        out.flush();
        out.close();
    }

    public String getString(SKList list)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("#name\r\n");
        builder.append(list.getTitle());
        builder.append("\r\n");
        builder.append("#date\r\n");
        builder.append(date_format.format(list.getDate()));
        builder.append("\r\n");
        builder.append("#list\r\n");

        int padding = 0;
        for(Player player : list)
            padding = Math.max(padding, player.getName().length());

        for(Player player : list)
        {
            builder.append(getString(player, 0));// padding));
            builder.append("\r\n");
        }

        builder.append("#end\r");
        return builder.toString();
    }

    protected String getString(Player player, int padding)
    {
        padding -= player.getName().length();
        StringBuilder builder = new StringBuilder();
        builder.append(player.getName());
        builder.append(space(padding));
        builder.append(" ");
        builder.append(player.getBids());
        builder.append(",");
        return builder.toString();
    }

    protected String space(int padding)
    {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < padding; i++)
            str.append(" ");
        
        return str.toString();
    }
}