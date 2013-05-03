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

package rakama.tsk.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class SKList extends ArrayList<Player> implements List<Player>
{	
	String title;
	Date date;
	
	public SKList(String title)
	{
		this.title = title;
		this.date = new Date();
	}
	
	public SKList(String title, Date date)
	{
		this.title = title;
		this.date = date;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setDate(Date date)
	{
		this.date = date;
	}
	
	public Date getDate()
	{
		return date;
	}
	
	public void insert(String player)
	{
		Player new_player = new Player(player);
		add(new_player);
	}
	
	public void markPresent(int index, boolean p)
	{
		get(index).setPresent(p);
	}
	
	public void suicide(int index)
	{
		// get player to be suicided
		Player suicided_player = get(index);
		suicided_player.incrementBids();
				
		// move active players up the list
		for(int i = index + 1; i < size(); i++)
		{
			Player current_player = get(i);			
			if(current_player.isPresent())
			{
				set(index, current_player);
				index = i;
			}
		}
		
		if(!hasPresentPlayers())
		{
			// place suicided player at bottom
			remove(index);
			add(suicided_player);
		}
		else
		{
			// place suicided player at last active position
			set(index, suicided_player);
		}
	}
	
	public boolean hasPresentPlayers()
	{
		for(Player player : this)
			if(player.isPresent())
				return true;
		return false;
	}
	
	public boolean hasPlayer(String player)
	{		
		// find the matching player
		for(Player p : this)
			if(p.getName().equalsIgnoreCase(player))
				return true;
		
		return false;
	}
	
	public int getIndex(String player)
	{		
		int i=0;
		
		// find the matching player
		for(Player p : this)
		{
			if(p.getName().equalsIgnoreCase(player))
				return i;
			i++;
		}
		
		return -1;
	}
	
	public SKList clone()
	{
		SKList clone = new SKList(title, date);
		for(Player p : this)
			clone.add(p.clone());
		return clone;
	}
}