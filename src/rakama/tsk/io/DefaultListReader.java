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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rakama.tsk.console.Console;
import rakama.tsk.list.Player;
import rakama.tsk.list.SKList;

public class DefaultListReader implements ListReader
{
	Console console;
	
	public DefaultListReader()
	{
		
	}
	
	public DefaultListReader(Console console)
	{
		this.console = console;
	}
	
	public SKList readList(File file) throws IOException
	{
		try
		{
			SKList list = readList(file, "UTF-16LE");
			
			if(list != null)
				return list;
			
			list = readList(file, "UTF-16BE");
			
			if(list != null)
				return list;
	
			list = readList(file, null);
	
			if(list != null)
				return list;
			
			error("file '" + file.getName() + "' could not be parsed.");
			return null;
		}
		catch(IOException e)
		{
			error("file '" + file.getName() + "' could not be parsed.");
			throw e;
		}
	}

	private SKList readList(File file, String encoding) throws IOException
	{
		URL url = file.toURI().toURL();
		InputStreamReader reader;
		if(encoding != null)
			reader = new InputStreamReader(url.openStream(), encoding);
		else
			reader = new InputStreamReader(url.openStream());
		
		BufferedReader in = new BufferedReader(reader);
		
		StringBuilder builder = new StringBuilder();
		String line = in.readLine();
		while(line != null)
		{
			builder.append(line);
			line = in.readLine();
		}
		
		in.close();
		
		return parseList(builder.toString());
	}
	
	public SKList parseList(String str)
	{
		DateFormat date_format = DefaultListWriter.date_format;
		
		String name = getElement("name", str);
		if(name == null)
			return null;
		
		if(!StringUtil.isAlphaNumeric(name, true))
		{
			error("list name '" + name + "' has invalid characters.");
			name = "Untitled";
		}
		
		String timestamp = getElement("date", str);
		if(timestamp == null)
			return null;
		
		Date date = null;
		
		try
		{
			date = date_format.parse(timestamp);
		}
		catch(ParseException e)
		{
			error("date could not be parsed (" + timestamp + ")");
			date = new Date();
		}
		
		SKList list = new SKList(name, date);
		
		String players = getElement("list", str);
		if(players == null)
			return null;
		
		for(Player player : parsePlayers(players))
		{
			if(!list.hasPlayer(player.getName()))
				list.add(player);
			else
				error("duplicate name '" + player.getName() 
						+ "' could not be added to the list.");
		}
		
		return list;
	}
	
	protected List<Player> parsePlayers(String input)
	{
		Matcher matcher = Pattern.compile("([^,]*),").matcher(input);

		List<Player> players = new LinkedList<Player>();
		
		while(matcher.find())
		{
			if(matcher.groupCount()>0)
			{
				Player player = parsePlayer(matcher.group(1).trim());
				if(player!=null)
					players.add(player);
			}
		}
		
		return players;
	}
	
	protected Player parsePlayer(String input)
	{
		Matcher matcher = Pattern.compile("([^\\s]*)\\s*([^\\s]*)?").matcher(input);
		
		if(!matcher.find() || matcher.groupCount()==0)
			return null;

		String name = StringUtil.applyTitleCase(matcher.group(1));
		if(!StringUtil.isAlphaNumeric(name))
		{
			error("invalid name '" + name + "' could not be added to the list.");
			return null;
		}
		
		Player player = new Player(name);
		
		try
		{
			if(matcher.groupCount()>1)
				player.setBids(Math.max(0, Integer.parseInt(matcher.group(2))));
		}
		catch(NumberFormatException e)
		{
			error("bids for player '" + name + "' could not be parsed.");
		}
		
		return player;
	}
	
	protected String getElement(String tag, String input)
	{
		Matcher matcher = Pattern.compile("#" + tag + "([^#]*)#").matcher(input);
		if(matcher.find() && matcher.groupCount() > 0)
			return matcher.group(1).trim();
		else
			return getElementNoEnd(tag, input);
	}

	protected String getElementNoEnd(String tag, String input)
	{
		Matcher matcher = Pattern.compile("#" + tag + "([^#]*)").matcher(input);
		if(matcher.find() && matcher.groupCount() > 0)
			return matcher.group(1).trim();
		else
			return null;
	}
	
	protected void error(String err)
	{
		if(console!=null)
			console.error(err);
		else
			System.err.println(err);
	}
}