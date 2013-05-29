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

package rakama.tsk.core;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import rakama.tsk.TherapySK;
import rakama.tsk.io.DefaultListWriter;
import rakama.tsk.list.SKList;

@SuppressWarnings("serial")
public class Properties extends JPanel
{
    static JDialog default_dialog;
    static NumberFormat format = new DecimalFormat(".##");

    Editor editor;
    JDialog dialog;
    JTextField title;
    JButton ok;

    public Properties(Editor editor, JDialog dialog)
    {
        this.editor = editor;
        this.dialog = dialog;
        SKList list = editor.getListManager().copyList();
        File file = editor.getCurrentFile();

        JLabel title_label = new JLabel("Title:");
        JLabel players_label = new JLabel("Names:");
        JLabel location_label = new JLabel("Location:");
        JLabel size_label = new JLabel("Size:");
        JLabel lastmodified_label = new JLabel("Modified:   ");

        DateFormat format = DefaultListWriter.date_format;

        title = new JTextField(list.getTitle());
        title.setColumns(64);
        JTextField players = new Borderless(String.valueOf(list.size()));

        title.addKeyListener(new EnterListener());

        title.setEditable(true);
        players.setEditable(false);

        JTextField location, lastmodified, size;

        if(file != null)
        {
            location = new JTextField(file.getAbsolutePath());
            String timestamp = format.format(new Date(file.lastModified()));
            lastmodified = new Borderless(timestamp);
            size = new Borderless(filesize(file.length()));
            location.setEditable(false);
            lastmodified.setEditable(false);
            size.setEditable(false);
        }
        else
        {
            location = new JTextField(" ");
            lastmodified = new Borderless(" ");
            size = new Borderless(" ");
            location.setEnabled(false);
            lastmodified.setEnabled(false);
            size.setEnabled(false);
        }

        setLayout(new GridBagLayout());
        GridBagConstraints large_constraints = new GridBagConstraints();
        large_constraints.insets = new Insets(6, 8, 8, 8);
        large_constraints.fill = GridBagConstraints.BOTH;
        large_constraints.weightx = 1;
        large_constraints.weighty = 1;

        // Center layout

        JPanel center = new JPanel();
        center.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 1;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0f;
        center.add(title_label, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1f;
        center.add(title, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0f;
        center.add(players_label, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1f;
        center.add(players, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 0f;
        center.add(new JPanel(), constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 0f;
        center.add(location_label, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1f;
        center.add(location, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.weightx = 0f;
        center.add(lastmodified_label, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1f;
        center.add(lastmodified, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.weightx = 0f;
        center.add(size_label, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1f;
        center.add(size, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        constraints.weightx = 0f;
        center.add(new JPanel(), constraints);

        large_constraints.gridy = 0;
        add(center, large_constraints);
        center.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow));

        // OK Cancel

        ok = new JButton("OK");
        ok.setPreferredSize(new Dimension(85, 26));
        ok.setFont(ok.getFont().deriveFont(12f));
        ok.addActionListener(new ButtonListener());

        JButton cancel = new JButton("Cancel");
        cancel.setPreferredSize(new Dimension(85, 26));
        cancel.setFont(ok.getFont().deriveFont(12f));
        cancel.addActionListener(new ButtonListener());

        // South layout

        JPanel south = new JPanel();
        south.setLayout(new GridBagLayout());
        constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.LAST_LINE_END;
        constraints.weightx = 1;
        constraints.weighty = 1;

        constraints.gridx = 0;
        south.add(new JLabel(), constraints);
        constraints.weightx = 0;
        constraints.gridx = 1;
        south.add(ok, constraints);
        constraints.gridx = 2;
        south.add(cancel, constraints);

        large_constraints.gridy = 1;
        add(south, large_constraints);
    }

    private String filesize(long b)
    {
        double kb = b * 0.001;
        double mb = b * 0.000001;
        if(kb < 1)
            return format.format(b) + " bytes";
        else if(mb < 1)
            return format.format(kb) + " kilobytes";
        else
            return format.format(mb) + " megabytes";
    }

    public static JDialog getDefaultPropertiesDialog(Editor source)
    {
        if(default_dialog == null)
        {
            default_dialog = new JDialog(source.getParentFrame(), "File Properties", true);
            default_dialog.setPreferredSize(new Dimension(350, 200));
            default_dialog.setResizable(false);
        }

        Properties prop = new Properties(source, default_dialog);
        default_dialog.getContentPane().removeAll();
        default_dialog.getContentPane().add(prop);
        default_dialog.setIconImages(TherapySK.icon_images);
        default_dialog.pack();
        default_dialog.setLocationRelativeTo(null);

        return default_dialog;
    }

    class ButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getActionCommand().equals("OK"))
            {
                String new_title = title.getText();
                editor.getConsole().invoke("rlist " + new_title);
            }

            dialog.setVisible(false);
            dialog.getContentPane().removeAll();
        }
    }

    class EnterListener extends KeyAdapter
    {
        public void keyPressed(KeyEvent e)
        {
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
            {
                String new_title = title.getText();
                editor.getConsole().invoke("rlist " + new_title);
                dialog.setVisible(false);
                dialog.getContentPane().removeAll();
            }
        }
    }

    class Borderless extends JTextField
    {
        public Borderless(String str)
        {
            super(str);
        }

        public void setBorder(Border border)
        {
            super.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }
    }
}