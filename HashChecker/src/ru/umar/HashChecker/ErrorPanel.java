package ru.umar.HashChecker;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

public class ErrorPanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int errors = 0;
	private Color background = new Color(250, 250, 250);//light
	
	SpringLayout spring;
	JLabel info;
	JPanel panel;
	JTextArea text;
	JScrollPane scroll;
	
	public ErrorPanel() {
		super();
		panel = (JPanel)this;
		spring = new SpringLayout();
		setLayout(spring);
		setBackground(background);
		
		info = new JLabel("Пропущено файлов:");
		info.setFont(info.getFont().deriveFont(0, 16));
		info.setPreferredSize(new Dimension(400, 30));
		add(info);
		spring.putConstraint(SpringLayout.NORTH, info, 10, SpringLayout.NORTH, panel);
		spring.putConstraint(SpringLayout.WEST, info, 10, SpringLayout.WEST, panel);
		
		text = new JTextArea();
		text.setFont(text.getFont().deriveFont(0, 16));
		scroll = new JScrollPane(text);
		add(scroll);
		spring.putConstraint(SpringLayout.NORTH, scroll, 10, SpringLayout.SOUTH, info);
		spring.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, panel);
		spring.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, panel);
		spring.putConstraint(SpringLayout.SOUTH, scroll, -10, SpringLayout.SOUTH, panel);
		
		setVisible(false);
	}
	
	public void showPanel() {setVisible(true);}
	public void hidePanel() {setVisible(false);}
	
	public void resetValues() {
		errors = 0;
		text.setText("");
	}
	
	public int getErrors() {
		return this.errors;
	}
	
	public void addError(File fl, Exception ex) {
		errors++;
		info.setText("Пропущено файлов: "+errors);
		text.append(fl.getAbsolutePath()+" | "+ex.getMessage()+"\n");
	}
}
