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

package rakama.tsk.core.table;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import rakama.tsk.list.Player;
import rakama.tsk.list.SKList;

public class ListManager implements TableModel
{
	public enum Attendance{PRESENT, ABSENT}
	public enum ListEvent{UNDO, REDO, DISCARDED, EXPIRED}

	public static final int COL_RANK = 0;
	public static final int COL_NAME = 1;
	public static final int COL_PRESENT = 2;
	public static final int COL_BIDS = 3;

	public static int DEFAULT_HISTORY_LENGTH = 1000;

	List<TableModelListener> tableListeners;
	Map<SKList, List<UndoListener>> undoListeners;

	LinkedList<SKList> history;
	LinkedList<SKList> future;
	int maxHistoryLength;
	
	public ListManager()
	{
		tableListeners = new ArrayList<TableModelListener>();
		undoListeners = new HashMap<SKList, List<UndoListener>>();
		history = new LinkedList<SKList>();
		future = new LinkedList<SKList>();
		maxHistoryLength = DEFAULT_HISTORY_LENGTH;
	}
	
	/**
	 * Replaces the list held by this ListManager with a new list,
	 * notifying any TableModelListeners of the change. This method
	 * makes a copy of the list using the {@link SKList#clone()}
	 * method, so any changes made to the list after calling this method
	 * will not be reflected by the ListManager.
	 *
	 * @param list	a new list
	 * @see #revertList(SKList)
	 * @see #copyList()
	 */
	public void setList(SKList list)
	{
		SKList copy = list.clone();
		
		history.add(copy);
		checkExpiration();
		discardFuture();
		notifyTableModelListeners();
	}
	
	public void setList(SKList list, UndoListener listener)
	{
		SKList copy = list.clone();
		addUndoListener(copy, listener);
		
		history.add(copy);
		checkExpiration();
		discardFuture();
		notifyTableModelListeners();
	}
	
	protected void checkExpiration()
	{
		while(history.size() > maxHistoryLength)
		{
			SKList expired = history.removeFirst();
			notifyUndoListeners(expired, ListEvent.EXPIRED);
			removeUndoListeners(expired);	
		}
	}
	
	public void clearHistory()
	{
		while(history.size() > 1)
		{
			SKList expired = history.removeFirst();
			notifyUndoListeners(expired, ListEvent.EXPIRED);
			removeUndoListeners(expired);	
		}
		discardFuture();
	}
	
	public void discardFuture()
	{
		while(!future.isEmpty())
		{
			SKList discarded = future.removeFirst();
			notifyUndoListeners(discarded, ListEvent.DISCARDED);
			removeUndoListeners(discarded);	
		}
	}
		
	public String getListTitle()
	{
		return history.getLast().getTitle();
	}
	
	public Date getListDate()
	{
		return (Date)history.getLast().getDate().clone();
	}
	
	public int getListSize()
	{
		return history.getLast().size();
	}
	
	/**
	 * Returns a copy of the list held by this ListManager. Changes made
	 * to the copy will not alter the original list.
	 * 
	 * To alter the current list, the method {@link #setList(SKList)}
	 * should be used.
	 * 
	 * @return a copy of the list
	 * @see #setList(SKList)
	 */
	public SKList copyList()
	{
		return history.getLast().clone();
	}

	public boolean canUndo()
	{
		return history.size() > 1;
	}

	public boolean canRedo()
	{
		return future.size() > 0;
	}
		
	public SKList undo()
	{
		if(history.size() <= 1)
			return null;
		
		SKList current = history.removeLast();
		future.add(current);

		notifyUndoListeners(current, ListEvent.UNDO);
		notifyTableModelListeners();
		return history.getLast().clone();
	}
	
	public SKList redo()
	{
		if(future.size() <= 0)
			return null;
		
		SKList current = future.removeLast();
		history.add(current);

		notifyUndoListeners(current, ListEvent.REDO);
		notifyTableModelListeners();
		return history.getLast().clone();
	}
	
	/**
	 * Notifies any TableModelListeners added to this ListManager
	 * that the list has been modified.
	 */
	protected void refresh()
	{
		notifyTableModelListeners();
	}

	public void addTableModelListener(TableModelListener l) 
	{
		tableListeners.add(l);
	}
	
	public void removeTableModelListener(TableModelListener l) 
	{		
		tableListeners.remove(l);
	}

	protected void addUndoListener(SKList list, UndoListener listener) 
	{
		if(undoListeners.containsKey(list))
			undoListeners.get(list).add(listener);
		else
		{
			List<UndoListener> entry = new ArrayList<UndoListener>();
			entry.add(listener);
			undoListeners.put(list, entry);
		}
	}

	protected void removeUndoListener(SKList list, UndoListener listener) 
	{
		List<UndoListener> entry = undoListeners.get(list);
		if(entry != null)
		{
			entry.remove(listener);
			if(entry.isEmpty())
				undoListeners.remove(list);
		}
	}

	protected void removeUndoListeners(SKList list) 
	{
		undoListeners.remove(list.clone());
	}
	
	protected void notifyUndoListeners(SKList list, ListEvent e)
	{
		List<UndoListener> entry = undoListeners.get(list);
		if(entry != null)
			for(UndoListener l : entry)
				l.listStatusChanged(e);
	}
	
	protected void notifyTableModelListeners()
	{
		TableModelEvent event = new TableModelEvent(this);
		for(TableModelListener l : tableListeners)
			l.tableChanged(event);
	}

	public int getColumnCount() 
	{
		return 4;
	}

	public int getRowCount() 
	{
		return history.getLast().size();
	}

	public String getColumnName(int c) 
	{
		switch(c)
		{
		case COL_RANK:
			return "#";
		case COL_NAME:
			return "Name";
		case COL_PRESENT:
			return "Present";
		case COL_BIDS:
			return "Bids";
		default:
			return "";
		}
	}
	
	public Class<?> getColumnClass(int c) 
	{
		switch(c)
		{
		case COL_RANK:
			return Integer.class;
		case COL_NAME:
			return Player.class;
		case COL_PRESENT:
			return Attendance.class;
		case COL_BIDS:
			return BidCounter.class;
		default:
			return Object.class;
		}
	}

	public void setValueAt(Object o, int r, int c)
	{
		
	}	

	public Object getValueAt(int r, int c) 
	{
		Player player = history.getLast().get(r);

		switch(c)
		{
		case COL_RANK:
			return r + 1;
		case COL_NAME:
			return player.clone();
		case COL_PRESENT:
			return player.isPresent() ? Attendance.PRESENT : Attendance.ABSENT;
		case COL_BIDS:
			return new BidCounter(player.getBids());
		default:
			return null;
		}
	}

	public boolean isCellEditable(int r, int c) 
	{
		return false;
	}
	
	public final class BidCounter
	{
		int bids;
		
		public BidCounter(int b)
		{
			bids = b;
		}
		
		public int getBids()
		{
			return bids;
		}
		
		public String toString()
		{
			return String.valueOf(bids);
		}
	}
}