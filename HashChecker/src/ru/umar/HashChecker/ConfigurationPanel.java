package ru.umar.HashChecker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent; 
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class ConfigurationPanel extends JPanel{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Color background = new Color(250, 250, 250);//light
	private Color background1 = new Color(230, 225, 225);//dark
	private Color textFields = new Color(189, 213, 255);
	
	
	JPanel panel;
	
	JPanel top;
	JPanel middle;
	
	JTextArea directoriesText;
	HashChooserPanel hashChooserPanel;
	JComboBox<String> name;
	JTextField newName;
	
	public ConfigurationPanel() {
		super();
		panel = (JPanel)this;
		setLayout(new SpringLayout());
		setBackground(background);
		
		createTop();
		createMiddle();
	}
	
	private void createMiddle() {
		middle = new JPanel();
		SpringLayout spring = new SpringLayout();
		middle.setLayout(spring);
		middle.setBackground(background);
		
		SpringLayout panelLayout = (SpringLayout) panel.getLayout();
		panelLayout.putConstraint(SpringLayout.NORTH, middle, 0, SpringLayout.SOUTH, top);
		panelLayout.putConstraint(SpringLayout.WEST, middle, 0, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, middle, 0, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, middle, 0, SpringLayout.SOUTH, panel);
		
		panel.add(middle);
		
	//== ELEMENTS ==\\
		JLabel hash = new JLabel("Хеш:");
		hashChooserPanel = new HashChooserPanel();
		JLabel catLab = new JLabel("Каталоги:");
		directoriesText = new JTextArea();
		JScrollPane scrollDirectories = new JScrollPane(directoriesText);
		
		JButton addLine = new JButton("+");
		JButton removeLine = new JButton ("-");
		JButton clearLines = new JButton("X");
	//===============\\
		
		hash.setFont(hash.getFont().deriveFont(0, 16));
		spring.putConstraint(SpringLayout.NORTH, hash, 20, SpringLayout.NORTH, middle);
		spring.putConstraint(SpringLayout.EAST, hash, 0, SpringLayout.EAST, catLab);
		middle.add(hash);
		
		
		hashChooserPanel.setPreferredSize(new Dimension(300, 30));
		hashChooserPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middle.add(hashChooserPanel);
		spring.putConstraint(SpringLayout.NORTH, hashChooserPanel, -6, SpringLayout.NORTH, hash);
		spring.putConstraint(SpringLayout.WEST, hashChooserPanel, 20, SpringLayout.EAST, hash);
		hashChooserPanel.addButton("MD5", new JButton("MD5"));
		hashChooserPanel.addButton("SHA-256", new JButton("SHA-256"));
		hashChooserPanel.addButton("SHA-512", new JButton("SHA-512"));
		hashChooserPanel.addButton("ГОСТ Р 34.11-94", new JButton("ГОСТ Р 34.11-94"));
		hashChooserPanel.addButton("ГОСТ Р 34.11-2012", new JButton("ГОСТ Р 34.11-2012"));
		hashChooserPanel.setSelected("MD5");
		
		catLab.setFont(hash.getFont().deriveFont(0, 16));
		spring.putConstraint(SpringLayout.NORTH, catLab, 30, SpringLayout.SOUTH, hash);
		spring.putConstraint(SpringLayout.WEST, catLab, 40, SpringLayout.WEST, middle);
		middle.add(catLab);
		
		directoriesText.setFont(directoriesText.getFont().deriveFont(0, 16));
		
		middle.add(scrollDirectories);
		spring.putConstraint(SpringLayout.NORTH, scrollDirectories, 0, SpringLayout.NORTH, catLab);
		spring.putConstraint(SpringLayout.SOUTH, scrollDirectories, -100, SpringLayout.SOUTH, middle);
		spring.putConstraint(SpringLayout.EAST, scrollDirectories, -100, SpringLayout.EAST, middle);
		spring.putConstraint(SpringLayout.WEST, scrollDirectories, 0, SpringLayout.WEST, hashChooserPanel);
		
		decorLineButton(addLine);
		addLine.setFont(addLine.getFont().deriveFont(1, 20));
		addLine.setToolTipText("Открыть проводник");
		decorLineButton(clearLines);
		clearLines.setToolTipText("Очистить список");
		decorLineButton(removeLine);
		removeLine.setFont(removeLine.getFont().deriveFont(1, 20));
		removeLine.setToolTipText("Удалить выделенныую строку");
		
		
		middle.add(addLine);
		middle.add(removeLine);
		middle.add(clearLines);
		
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, clearLines, 20, SpringLayout.SOUTH, scrollDirectories);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, clearLines, -16, SpringLayout.EAST, scrollDirectories);
		
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, removeLine, 20, SpringLayout.SOUTH, scrollDirectories);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, removeLine, -56, SpringLayout.EAST, scrollDirectories);
		
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, addLine, 20, SpringLayout.SOUTH, scrollDirectories);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, addLine, -96, SpringLayout.EAST, scrollDirectories);
		
	//======== ACTION LISTENERS
		addLine.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String path = "C:"+File.separator;
				JFileChooser fileOpen = new JFileChooser(new File(path));
				fileOpen.setMultiSelectionEnabled(true);
				fileOpen.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				MainHashChecker.setUpdateUI(fileOpen);
				int res = fileOpen.showDialog(null, "Выбрать файлы");
				if (res==JFileChooser.APPROVE_OPTION) {
					for (File file: fileOpen.getSelectedFiles()) {
						directoriesText.append(file.getAbsolutePath()+"\n");
					}
				}
			}
		});
		clearLines.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				directoriesText.setText("");
			}
		});
		removeLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int current = directoriesText.getCaretPosition();
				try {
					int count = current;
					int finish = -1;
					while (true) {
						String str = directoriesText.getText(count, 1);
						if (str.toCharArray()[0]==10) {
							finish = count;
							break;
						}
						count++;
					}
					
					int count1 = current;
					int start = -1;
					while (true) {
						if (count1==0) {
							start = 0;
							break;
						}
						String str = directoriesText.getText(count1, 1);
						if (str.toCharArray()[0]==10) {
							start = count1;
							break;
						}
						count1--;
					}
					
					String allContent = directoriesText.getText();
					
					String first = allContent.substring(0, start);
					String end = allContent.substring(finish, allContent.length());
					
					String res = first+end;
					
					directoriesText.setText(res);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
	}
	private void decorLineButton(JButton btn) {
		btn.setPreferredSize(new Dimension(30, 30));
		btn.setMinimumSize(new Dimension(30, 30));
		btn.setBackground(textFields);
		btn.setFocusable(false);
		btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		btn.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				int change = 4;
				Dimension old = btn.getPreferredSize();
				Dimension newD = new Dimension(old.width-change, old.height-change);
				btn.setPreferredSize(newD);
				btn.setBackground(textFields);
				btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				int change = 4;
				Dimension old = btn.getPreferredSize();
				Dimension newD = new Dimension(old.width+change, old.height+change);
				btn.setPreferredSize(newD);
				btn.setBackground(background1);
				btn.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
			}
		});
		
	}
	private void createTop() {
		top = new JPanel();
		SpringLayout spring = new SpringLayout();
		top.setLayout(spring);
		top.setBackground(background);
		top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(213, 210, 210)));
		//top.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		SpringLayout panelLayout = (SpringLayout) panel.getLayout();
		panelLayout.putConstraint(SpringLayout.NORTH, top, 0, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, top, 0, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, top, 0, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, top, 90, SpringLayout.NORTH, panel);
		
		
		JLabel label = new JLabel("Конфигурация");
		name = new JComboBox<String>();
		newName = new JTextField();
		JLabel newLabel = new JLabel("Создать новую:");
		JButton download = new JButton("Загрузить");
		JButton save = new JButton("Сохранить");
		JButton delete = new JButton("Удалить");
		//JLabel line = new JLabel();
		
		label.setFont(label.getFont().deriveFont(0, 16));
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, label, -20, SpringLayout.VERTICAL_CENTER, top);
		spring.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, top);
		top.add(label);
		
		name.setFont(name.getFont().deriveFont(0, 14));
		name.setPreferredSize(new Dimension(200, label.getPreferredSize().height+10));
		updateName();
		name.setEditable(true);
		name.setFocusable(false);
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, name, 0, SpringLayout.VERTICAL_CENTER, label);
		spring.putConstraint(SpringLayout.WEST, name, 20, SpringLayout.EAST, label);
		top.add(name);
		
		newName.setPreferredSize(name.getPreferredSize());
		newName.setFont(name.getFont());
		top.add(newName);
		spring.putConstraint(SpringLayout.NORTH, newName, 5, SpringLayout.SOUTH, name);
		spring.putConstraint(SpringLayout.WEST, newName, 20, SpringLayout.EAST, label);
		
		newLabel.setFont(label.getFont().deriveFont(0, label.getFont().getSize()-2));
		top.add(newLabel);
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, newLabel, 0, SpringLayout.VERTICAL_CENTER, newName);
		spring.putConstraint(SpringLayout.EAST, newLabel, -20, SpringLayout.WEST, newName);
		
		download.setPreferredSize(new Dimension(120, name.getPreferredSize().height));
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, download, 0, SpringLayout.VERTICAL_CENTER, label);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, download, 80, SpringLayout.EAST, name);
		top.add(download);
		decorTopButtons(download);
		
		save.setPreferredSize(new Dimension(120, name.getPreferredSize().height));
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, save, 0, SpringLayout.VERTICAL_CENTER, label);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, save, 220, SpringLayout.EAST, name);
		top.add(save);
		decorTopButtons(save);
		
		delete.setPreferredSize(new Dimension(120, name.getPreferredSize().height));
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, delete, 0, SpringLayout.VERTICAL_CENTER, label);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, delete, 360, SpringLayout.EAST, name);
		top.add(delete);
		decorTopButtons(delete);
		
		panel.add(top);
		
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					fixDirectories();
					Configuration config = createConfiguration();
					config.createFile();
					updateAll();
				} catch (BadLocationException e1) {
					//e1.printStackTrace();
				}
			}
		});
		
		download.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nameC = (String) name.getSelectedItem();
				if (MainHashChecker.encrypt_key!=null) {
					try {
						nameC = Configuration.encryptLine(nameC, MainHashChecker.encrypt_key);
					} catch (Exception e1) {e1.printStackTrace();}
				}
				File targ = new File(MainHashChecker.workspace_directory.getAbsolutePath()+File.separator+MainHashChecker.configDirectory
						+File.separator+nameC+".cnfg");
				if (targ.exists()) {
					if (Configuration.validConfiguration(targ, MainHashChecker.encrypt_key)) {
						Configuration config = Configuration.downloadConfiguration(targ, MainHashChecker.encrypt_key);
						hashChooserPanel.setSelected(config.getHash());
						directoriesText.setText("");
						for (String str: config.getDirectories()) {
							directoriesText.append(str+"\n");
						}
					}
				}
			}
		});
		
		name.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String nameC = (String) name.getSelectedItem();
				File targ = new File(MainHashChecker.workspace_directory.getAbsolutePath()+File.separator+MainHashChecker.configDirectory
						+File.separator+nameC+".cnfg");
				if (targ.exists()) {
					if (Configuration.validConfiguration(targ, MainHashChecker.encrypt_key)) {
						Configuration config = Configuration.downloadConfiguration(targ, MainHashChecker.encrypt_key);
						hashChooserPanel.setSelected(config.getHash());
						directoriesText.setText("");
						for (String str: config.getDirectories()) {
							directoriesText.append(str+"\n");
						}
					}
				}
			}
		});
		newName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				//updateName();
				if (name.getSelectedItem()!=null) {
					name.addItem(null);
					name.setSelectedItem(null);
				}
				if (e.getKeyCode()==KeyEvent.VK_ENTER) {
					//String text = newName.getText()+e.getKeyChar();
					String text = newName.getText();
					updateAll();
					name.addItem(text);
					name.setSelectedItem(text);
					newName.setText("");
				}
			}
		});
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nameC = (String) name.getSelectedItem();
				File targ = new File(MainHashChecker.workspace_directory.getAbsolutePath()+File.separator+MainHashChecker.configDirectory
						+File.separator+nameC+".cnfg");
				if (targ.exists()) {
					targ.delete();
					updateName();
				}
			}
		});
	}
	private void updateAll() {
		updateName();
		directoriesText.setText("");
		hashChooserPanel.setSelected("MD5");
		newName.setText("");
	}
	private void updateName() {
		name.removeAllItems();
		File dir = new File(MainHashChecker.workspace_directory.getAbsolutePath()+File.separator+MainHashChecker.configDirectory);
		if (dir.exists()) {
			for (File file: dir.listFiles()) {
				if (Configuration.validConfiguration(file, MainHashChecker.encrypt_key)) {
					if (MainHashChecker.encrypt_key!=null) {
						try {
							name.addItem(Configuration.decryptLine((file.getName().substring(0, file.getName().length()-5)), MainHashChecker.encrypt_key));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else {
						name.addItem(file.getName().substring(0, file.getName().length()-5));
					}
				}
			}
		}
	}
	
	private void decorTopButtons(JButton btn) {
		btn.setBackground(textFields);
		btn.setFocusable(false);
		btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int change = 4;
				Dimension old = btn.getPreferredSize();
				Dimension newD = new Dimension(old.width-change, old.height-change);
				btn.setPreferredSize(newD);
				btn.setBackground(textFields);
				btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			@Override
			public void mousePressed(MouseEvent e) {
				int change = 4;
				Dimension old = btn.getPreferredSize();
				Dimension newD = new Dimension(old.width+change, old.height+change);
				btn.setPreferredSize(newD);
				btn.setBackground(background1);
				btn.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				btn.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, background1));
			}
		});
	}
	
	public void addToPanel(JPanel target) {
		target.add(this);
		SpringLayout spring = (SpringLayout)target.getLayout();
		spring.putConstraint(SpringLayout.NORTH, this, 0, SpringLayout.NORTH, target);
		spring.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, target);
		spring.putConstraint(SpringLayout.WEST, this, 0, SpringLayout.WEST, target);
		spring.putConstraint(SpringLayout.EAST, this, 0, SpringLayout.EAST, target);
		
	}
	public void removeFromPanel(JPanel target) {
		try {
			target.remove(this);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//===================== CHECK CONFIGURATION =======================\\
	private void fixDirectories() throws BadLocationException {
		ArrayList<String>valid = new ArrayList<String>();
		for (int i = 0; i < directoriesText.getLineCount(); i++) {
			Document doc = directoriesText.getDocument();
			Element root = doc.getDefaultRootElement();
			Element element = root.getElement(i);
			int start = element.getStartOffset();
			int end = element.getEndOffset();
			String str = doc.getText(start, end-start);
			String path = str.substring(0, str.length()-1);
			File fl = new File(path);
			if (fl.exists()) {
				valid.add(str);
			}
		}
		directoriesText.setText("");
		for (String str: valid) {
			directoriesText.append(str);
		}
	}
	private ArrayList<String> getDirectories() throws BadLocationException{
		ArrayList<String>arr = new ArrayList<String>();
		for (int i = 0; i < directoriesText.getLineCount(); i++) {
			Document doc = directoriesText.getDocument();
			Element root = doc.getDefaultRootElement();
			Element element = root.getElement(i);
			int start = element.getStartOffset();
			int end = element.getEndOffset();
			String str = doc.getText(start, end-start);
			String path = str.substring(0, str.length()-1);
			File fl = new File(path);
			if (fl.exists()) {
				arr.add(str);
			}
		}
		if (arr.size()==0) {
			return null;
		}
		return arr;
	}
	private Configuration createConfiguration() throws BadLocationException {
		String nameC = (String) name.getSelectedItem();
		String hashC = hashChooserPanel.getSelected();
		ArrayList<String>arr = getDirectories();
		Configuration config = new Configuration(nameC, arr, hashC);
		return config;
	}
}
