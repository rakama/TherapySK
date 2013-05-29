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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import rakama.tsk.console.Console;
import rakama.tsk.core.table.ListManager.Attendance;
import rakama.tsk.io.FileLoader;
import rakama.tsk.list.Player;

@SuppressWarnings("serial")
public class ListTable extends JPanel
{
    static Font default_font = new Font("SanSerif", Font.PLAIN, 14);

    ListManager manager;
    Console console;

    JTable table;
    JScrollPane scroll;
    JPopupMenu popup;

    public ListTable(ListManager manager, Console console)
    {
        this.manager = manager;
        this.console = console;
        init();
    }

    public JPopupMenu getPopupMenu()
    {
        return popup;
    }

    public JTable getTable()
    {
        return table;
    }

    public ListManager getListManager()
    {
        return manager;
    }

    public Console getConsole()
    {
        return console;
    }

    private void init()
    {
        CellRenderer renderer = new CellRenderer(manager);

        table = new JTable(manager);
        table.getColumnModel().getColumn(ListManager.COL_RANK).setCellRenderer(renderer);
        table.getColumnModel().getColumn(ListManager.COL_RANK).setPreferredWidth(24);
        table.getColumnModel().getColumn(ListManager.COL_NAME).setCellRenderer(renderer);
        table.getColumnModel().getColumn(ListManager.COL_NAME).setPreferredWidth(128);
        table.getColumnModel().getColumn(ListManager.COL_PRESENT).setCellRenderer(renderer);
        table.getColumnModel().getColumn(ListManager.COL_PRESENT).setPreferredWidth(48);
        table.getColumnModel().getColumn(ListManager.COL_BIDS).setCellRenderer(renderer);
        table.getColumnModel().getColumn(ListManager.COL_BIDS).setPreferredWidth(72);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(default_font);

        table.setDragEnabled(true);
        table.setDropMode(DropMode.INSERT_ROWS);
        table.setTransferHandler(new PlayerTransferHandler());
        table.addKeyListener(new KeyCommandListener());

        scroll = new JScrollPane(table);
        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        setPreferredSize(new Dimension(300, 600));

        initPopupMenu();
    }

    private void initPopupMenu()
    {
        popup = new JPopupMenu();

        FileLoader loader = FileLoader.getDefaultFileLoader();
        URL check_icon = loader.getURL("icons/check.png");
        URL x_icon = loader.getURL("icons/x.png");

        JMenuItem prs = new JMenuItem("Mark Present", new ImageIcon(check_icon));
        JMenuItem abs = new JMenuItem("Mark Absent");
        JMenuItem sui = new JMenuItem("Suicide Player");
        JMenuItem ren = new JMenuItem("Rename Player");
        JMenuItem del = new JMenuItem("Delete Player", new ImageIcon(x_icon));
        JMenuItem all = new JMenuItem("Select All");
        JMenuItem ins = new JMenuItem("Insert New Player...");

        popup.add(prs);
        popup.add(abs);
        popup.add(sui);
        popup.addSeparator();
        popup.add(ren);
        popup.add(del);
        popup.add(all);
        popup.addSeparator();
        popup.add(ins);

        MenuListener ml = new MenuListener();
        ml.registerAction(prs, new Runnable() {
            public void run()
            {
                invokeWithSelected("present");
            }
        });
        ml.registerAction(abs, new Runnable() {
            public void run()
            {
                invokeWithSelected("absent");
            }
        });
        ml.registerAction(sui, new Runnable() {
            public void run()
            {
                invokeWithSelected("suicide");
            }
        });
        ml.registerAction(del, new Runnable() {
            public void run()
            {
                invokeWithSelected("delete");
            }
        });
        ml.registerAction(all, new Runnable() {
            public void run()
            {
                table.selectAll();
            }
        });
        ml.registerAction(ins, new Runnable() {
            public void run()
            {
                showInsertPopup();
            }
        });
        ml.registerAction(ren, new Runnable() {
            public void run()
            {
                showRenamePopup();
            }
        });

        ListSelectionModel sel = table.getSelectionModel();
        sel.addListSelectionListener(new SelectionAdapter(prs) {
            public void run()
            {
                item.setEnabled(checkAttendance(Attendance.ABSENT, table.getSelectedRows()));
            }
        });
        sel.addListSelectionListener(new SelectionAdapter(abs) {
            public void run()
            {
                item.setEnabled(checkAttendance(Attendance.PRESENT, table.getSelectedRows()));
            }
        });
        sel.addListSelectionListener(new SelectionAdapter(sui) {
            public void run()
            {
                item.setEnabled(table.getSelectedRows().length == 1);
            }
        });
        sel.addListSelectionListener(new SelectionAdapter(ren) {
            public void run()
            {
                item.setEnabled(table.getSelectedRows().length == 1);
            }
        });
        sel.addListSelectionListener(new SelectionAdapter(del) {
            public void run()
            {
                item.setEnabled(table.getSelectedRows().length > 0);
                if(table.getSelectedRows().length == 1)
                    item.setText("Delete Player");
                else
                    item.setText("Delete Players");
            }
        });

        prs.setEnabled(false);
        abs.setEnabled(false);
        sui.setEnabled(false);
        ren.setEnabled(false);
        del.setEnabled(false);

        table.add(popup);
        scroll.add(popup);
        scroll.addMouseListener(new PopupListener());
        table.addMouseListener(new PopupListener());
    }

    private void invokeWithSelected(String command)
    {
        String names = getNames();
        if(names.length() > 0)
            console.invoke(command + " " + names);
    }

    private String getNames()
    {
        StringBuilder builder = new StringBuilder();
        for(int i : table.getSelectedRows())
            builder.append(manager.getValueAt(i, ListManager.COL_NAME) + " ");
        return builder.toString();
    }

    public void showInsertPopup()
    {
        final String title = "Insert New Player";
        final String message = "Enter new player name(s):";
        String args = JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
        if(args != null)
            console.invoke("insert " + args);
    }

    public void showRenamePopup()
    {
        final String title = "Rename Player";
        final String message = "Enter a new name:";

        int row = table.getSelectedRow();
        Player current_player = (Player) manager.getValueAt(row, ListManager.COL_NAME);
        String current_name = current_player.getName();

        String args = (String) JOptionPane.showInputDialog(null, message, title,
                JOptionPane.PLAIN_MESSAGE, null, null, current_name);
        if(args != null)
            console.invoke("rename " + current_name + " " + args);
    }

    private boolean checkAttendance(Attendance status, int... rows)
    {
        if(rows.length == 0)
            return false;

        for(int r : rows)
            if(manager.getValueAt(r, ListManager.COL_PRESENT) == status)
                return true;
        return false;
    }

    class PlayerTransferHandler extends TransferHandler
    {
        public boolean canImport(TransferSupport ts)
        {
            int row = table.rowAtPoint(ts.getDropLocation().getDropPoint());
            if(row > -1)
                return true;
            else
                return false;
        }

        public boolean importData(TransferSupport ts)
        {
            JTable.DropLocation loc = (JTable.DropLocation) ts.getDropLocation();
            int row = loc.getRow();
            Transferable transfer = ts.getTransferable();

            try
            {
                Reader reader = DataFlavor.stringFlavor.getReaderForText(transfer);
                BufferedReader bfr = new BufferedReader((StringReader) reader);
                movePlayer(bfr.readLine(), row);
                return true;
            }
            catch(IOException e)
            {
                e.printStackTrace();
                return false;
            }
            catch(UnsupportedFlavorException e)
            {
                e.printStackTrace();
                return false;
            }
            catch(ClassCastException e)
            {
                e.printStackTrace();
                return false;
            }
        }

        private void movePlayer(String name, int row)
        {
            int new_rank = row + 1;
            int cur_rank = manager.copyList().getIndex(name) + 1;

            if(new_rank > cur_rank)
                new_rank--;

            console.invoke("move " + name + " " + new_rank);
        }

        public int getSourceActions(JComponent c)
        {
            if(table.getSelectedRowCount() == 1)
                return MOVE;
            else
                return 0;
        }

        public Transferable createTransferable(JComponent c)
        {
            if(c != table)
                return null;
            int row = table.getSelectedRow();
            String name = manager.getValueAt(row, ListManager.COL_NAME).toString();
            return new StringSelection(name);
        }
    }

    class MenuListener implements ActionListener
    {
        Map<Object, Runnable> actions;

        public MenuListener()
        {
            actions = new HashMap<Object, Runnable>();
        }

        public void registerAction(JMenuItem s, Runnable r)
        {
            s.addActionListener(this);
            actions.put(s, r);
        }

        public void actionPerformed(ActionEvent e)
        {
            Runnable action = actions.get(e.getSource());
            if(action != null)
                action.run();
        }
    }

    class PopupListener extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            selectElement(e);
            triggerPopup(e);
        }

        public void mouseReleased(MouseEvent e)
        {
            selectElement(e);
            triggerPopup(e);
        }

        private void selectElement(MouseEvent e)
        {
            if(e.getButton() == MouseEvent.BUTTON1 && e.getSource() != table)
                table.clearSelection();
        }

        private void triggerPopup(MouseEvent e)
        {
            if(e.isPopupTrigger())
                popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    class KeyCommandListener extends KeyAdapter
    {
        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode() == KeyEvent.VK_DELETE)
                invokeWithSelected("delete");

        }
    }

    class SelectionAdapter implements ListSelectionListener
    {
        JMenuItem item;

        public SelectionAdapter(JMenuItem item)
        {
            this.item = item;
        }

        public void valueChanged(ListSelectionEvent e)
        {
            run();
        }

        public void run()
        {
        }
    }
}