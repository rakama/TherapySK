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

package rakama.tsk.console;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Entry
{
    public static DateFormat date_format = new SimpleDateFormat("hh:mm");

    Date date;
    String text;
    String style;
    List<LinkKeyPair> links;
    String[] keys;

    Console console;
    String name;
    long rev;

    protected Entry(Console c, String name, String text, String style, Link[] links)
    {
        this.console = c;
        this.name = name;
        this.date = new Date();
        this.text = text;
        this.style = style;
        this.links = new ArrayList<LinkKeyPair>();
        for(Link l : links)
            this.links.add(new LinkKeyPair(l, console.registerLink(l)));
    }

    protected String getID()
    {
        return name + "-" + rev;
    }

    protected void incrementRevision()
    {
        rev++;
    }

    public Console getConsole()
    {
        return console;
    }

    public void setDate(Date date)
    {
        this.date = date;
        console.refreshEntry(this);
    }

    public Date getDate()
    {
        return date;
    }

    public void setText(String text)
    {
        this.text = text;
        console.refreshEntry(this);
    }

    public String getText()
    {
        return text;
    }

    public void setStyle(String style)
    {
        this.style = style;
        console.refreshEntry(this);
    }

    public String getStyle()
    {
        return style;
    }

    public void addLink(Link link)
    {
        links.add(new LinkKeyPair(link, console.registerLink(link)));
        console.refreshEntry(this);
    }

    public void removeLink(Link link)
    {
        Iterator<LinkKeyPair> iter = links.iterator();
        while(iter.hasNext())
            if(iter.next().link.equals(link))
                iter.remove();
        console.refreshEntry(this);
    }

    protected List<LinkKeyPair> getLinks()
    {
        return links;
    }

    public String getHTML()
    {
        String time = date_format.format(date);

        StringBuffer buffer = new StringBuffer();
        buffer.append("<tr id=");
        buffer.append(name + "-" + rev);
        buffer.append(" width=100% align=left class=");
        buffer.append(style);
        buffer.append(">");

        if(Console.timestamp)
        {
            buffer.append("<td class=timestamp valign=top>");
            buffer.append(time);
            buffer.append("</td>");
        }

        buffer.append("<td width=100%>");
        buffer.append(text);
        buffer.append("</td>");

        if(links.size() > 0)
        {
            buffer.append("<td align=right valign=top>");
            for(LinkKeyPair pair : links)
            {
                buffer.append("<a href=");
                buffer.append(pair.key);
                buffer.append(">");
                buffer.append(pair.link.getLinkHTML());
                buffer.append("</a>");
            }
            buffer.append("</td>");
        }

        buffer.append("</font></tr>");
        return buffer.toString();
    }

    class LinkKeyPair
    {
        Link link;
        String key;

        public LinkKeyPair(Link link, String key)
        {
            this.link = link;
            this.key = key;
        }
    }
}