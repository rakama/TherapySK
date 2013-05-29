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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.SystemColor;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import rakama.tsk.core.table.ListManager.Attendance;
import rakama.tsk.core.table.ListManager.BidCounter;
import rakama.tsk.io.FileLoader;
import rakama.tsk.list.Player;

class CellRenderer implements TableCellRenderer
{
    static Color bid_color = new Color(128, 255, 128);

    private static FileLoader loader = FileLoader.getDefaultFileLoader();
    static Image check_icon = loader.getImage("icons/check.png");

    ListManager manager;

    public CellRenderer(ListManager manager)
    {
        this.manager = manager;
    }

    public Component getTableCellRendererComponent(JTable table, Object val, boolean sel,
            boolean foc, int r, int c)
    {
        JLabel label;

        if(val == Attendance.PRESENT)
            label = new JLabel(new ImageIcon(check_icon));
        else if(val == Attendance.ABSENT)
            label = new JLabel("-", SwingConstants.CENTER);
        else if(val instanceof BidCounter)
            label = new BidLabel((BidCounter) val);
        else if(val instanceof Player)
            label = new JLabel(val.toString(), SwingConstants.LEFT);
        else
            label = new JLabel(val.toString(), SwingConstants.CENTER);

        label.setFont(table.getFont());

        if(sel)
        {
            label.setOpaque(true);
            label.setBackground(SystemColor.textHighlight);
            label.setForeground(SystemColor.textHighlightText);
        }

        return label;
    }

    @SuppressWarnings("serial")
    class BidLabel extends JLabel
    {
        int val, max;

        public BidLabel(BidCounter bc)
        {
            for(int r = 0; r < manager.getRowCount(); r++)
            {
                BidCounter temp = (BidCounter) manager.getValueAt(r, ListManager.COL_BIDS);
                max = Math.max(max, temp.getBids());
            }

            val = bc.getBids();

            setText(" " + bc.getBids());
        }

        public void paint(Graphics g)
        {
            if(val > 0 && max > 0)
            {
                g.setColor(bid_color);
                int w = getWidth();
                int h = getHeight();
                g.fillRect(0, 0, (w * val) / max, h);
            }

            super.paint(g);
        }
    }
}