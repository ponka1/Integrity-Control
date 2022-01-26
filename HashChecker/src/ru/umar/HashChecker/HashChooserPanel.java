package ru.umar.HashChecker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class HashChooserPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, JButton>buttons;
	private String selected = null;
	
	private Color selectedColor = new Color(189, 213, 255);
	private Color noselectedColor = Color.WHITE;
	
	public HashChooserPanel() {
		super();
		buttons = new HashMap<String, JButton>();
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	}
	
	public void addButton(String name, JButton btn) {
		buttons.put(name, btn);
		btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, HashChooserPanel.this.getPreferredSize().height-2));
		btn.setBorder(null);
		btn.setFocusable(false);
		btn.setBackground(noselectedColor);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selected = name;
				btn.setBackground(selectedColor);
				for (String key: buttons.keySet()) {
					if (!name.equals(key)) {
						buttons.get(key).setBackground(noselectedColor);
					}
				}
				HashChooserPanel.this.repaint();
			}
		});
		
		int width = 2;
		for (String key: buttons.keySet()) {
			if (!name.equals(key)) {
				JButton targ = buttons.get(key);
				targ.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
			}
			width+=buttons.get(key).getPreferredSize().width;
		}
		HashChooserPanel.this.setPreferredSize(new Dimension(width, HashChooserPanel.this.getPreferredSize().height));
		HashChooserPanel.this.revalidate();
		HashChooserPanel.this.repaint();
		HashChooserPanel.this.add(btn);
	}
	public void setSelected(String name) {
		if (buttons.containsKey(name)) {
			selected = name;
			JButton btn = buttons.get(name);
			btn.setBackground(selectedColor);
			for (String key: buttons.keySet()) {
				if (!name.equals(key)) {
					buttons.get(key).setBackground(noselectedColor);
				}
			}
		}
	}
	public String getSelected() {
		return selected;
	}
}
