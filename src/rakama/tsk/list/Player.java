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

public class Player
{
    private String name;
    private boolean present;
    private int bids;

    public Player(String name)
    {
        this.name = name;
    }

    public Player(String name, int bids)
    {
        this.name = name;
        this.bids = bids;
    }

    public Player(String name, int bids, boolean present)
    {
        this.name = name;
        this.bids = bids;
        this.present = present;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getBids()
    {
        return bids;
    }

    public void setBids(int bids)
    {
        this.bids = bids;
    }

    public void incrementBids()
    {
        bids++;
    }

    public boolean isPresent()
    {
        return present;
    }

    public void setPresent(boolean a)
    {
        present = a;
    }

    public String toString()
    {
        return name;
    }

    public Player clone()
    {
        return new Player(name, bids, present);
    }
}