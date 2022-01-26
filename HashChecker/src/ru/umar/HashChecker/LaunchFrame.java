package ru.umar.HashChecker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

public class LaunchFrame extends JFrame{
	private static String header = "HashChecker 1.0";
	private static String workspace_name = "HashCheckerWorkspace";
	
	private static final long serialVersionUID = 1L;
	private int width=600;
	private int heigh=300;
	
	JPanel panel;
	
	public boolean visiblePassword = false;
	
	private Color background1 = new Color(230, 225, 225);
	private Color textFields = new Color(189, 213, 255);
	private Color selectColor = new Color(204, 123, 110);
	private Color background = new Color(250, 250, 250);
	
	public LaunchFrame() {
		super(header);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width, heigh);
		setMinimumSize(new Dimension(width, heigh));
		setResizable(true);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
	    Dimension dim = toolkit.getScreenSize();
	    setBounds(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2, getSize().width, getSize().height);
	    
	    
	    panel = new JPanel();
	    SpringLayout spring = new SpringLayout();
	    panel.setLayout(spring);
	    panel.setBackground(Color.BLUE);
	    
	    try {
			fillPanel(panel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    add(panel);
	    
	    revalidate();
	    repaint();
	    setVisible(true);
	}
	
	private void fillPanel(JPanel panel) throws IOException {
		SpringLayout spring = (SpringLayout) panel.getLayout();
		
		JPanel info = new JPanel();
		info.setBackground(background1);
		info.setLayout(new SpringLayout());
		
		panel.add(info);
		spring.putConstraint(SpringLayout.NORTH, info, 0, SpringLayout.NORTH, panel);
		spring.putConstraint(SpringLayout.WEST, info, 0, SpringLayout.WEST, panel);
		spring.putConstraint(SpringLayout.EAST, info, 0, SpringLayout.EAST, panel);
		spring.putConstraint(SpringLayout.SOUTH, info, 80, SpringLayout.NORTH, panel);
		
		JLabel infoLab1 = new JLabel("<html> Укажите рабочую директорию программы <br> и введите ключ сессии. </html>");
		infoLab1.setPreferredSize(new Dimension(500, 60));
		infoLab1.setFont(infoLab1.getFont().deriveFont(1, 20));
		//infoLab1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		SpringLayout infoSpring = (SpringLayout)info.getLayout();
		info.add(infoLab1);
		infoSpring.putConstraint(SpringLayout.NORTH, infoLab1, 10, SpringLayout.NORTH, info);
		infoSpring.putConstraint(SpringLayout.WEST, infoLab1, 10, SpringLayout.WEST, info);

//================== INPUT PANEL ====================\\
		JPanel inputPanel = new JPanel();
		SpringLayout inputSpring = new SpringLayout();
		inputPanel.setLayout(inputSpring);
		inputPanel.setBackground(background);
		panel.add(inputPanel);
		spring.putConstraint(SpringLayout.NORTH, inputPanel, 0, SpringLayout.SOUTH, info);
		spring.putConstraint(SpringLayout.WEST, inputPanel, 0, SpringLayout.WEST, info);
		spring.putConstraint(SpringLayout.EAST, inputPanel, 0, SpringLayout.EAST, info);
		spring.putConstraint(SpringLayout.SOUTH, inputPanel, 0, SpringLayout.SOUTH, panel);
		
//---------- Workspace	
		JLabel workspaceLabel1 = new JLabel("Рабочая");
		workspaceLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		workspaceLabel1.setPreferredSize(new Dimension(100, 30));
		workspaceLabel1.setFont(workspaceLabel1.getFont().deriveFont(1, 15));
		//workspaceLabel1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		inputPanel.add(workspaceLabel1);
		inputSpring.putConstraint(SpringLayout.NORTH, workspaceLabel1, 20, SpringLayout.NORTH, inputPanel);
		inputSpring.putConstraint(SpringLayout.WEST, workspaceLabel1, 10, SpringLayout.WEST, inputPanel);
		
		JLabel workspaceLabel2 = new JLabel("директория:");
		workspaceLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		workspaceLabel2.setPreferredSize(new Dimension(100, 30));
		workspaceLabel2.setFont(workspaceLabel2.getFont().deriveFont(1, 15));
		//workspaceLabel2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		inputPanel.add(workspaceLabel2);
		inputSpring.putConstraint(SpringLayout.NORTH, workspaceLabel2, -15, SpringLayout.SOUTH, workspaceLabel1);
		inputSpring.putConstraint(SpringLayout.WEST, workspaceLabel2, 0, SpringLayout.WEST, workspaceLabel1);
		
		//Workspace TEXT
		
		JTextField workspaceText = new JTextField();
		Dimension wd = new Dimension(100, 30);
		workspaceText.setPreferredSize(wd);
		workspaceText.setMinimumSize(wd);
		workspaceText.setFont(workspaceText.getFont().deriveFont(0, 14));
		File currentDir = new File(System.getProperty("user.dir"));
		workspaceText.setText(currentDir.getAbsolutePath()+File.separator+workspace_name);
		
		inputPanel.add(workspaceText);
		inputSpring.putConstraint(SpringLayout.NORTH, workspaceText, 7, SpringLayout.NORTH, workspaceLabel1);
		inputSpring.putConstraint(SpringLayout.WEST, workspaceText, 20, SpringLayout.EAST, workspaceLabel1);
		inputSpring.putConstraint(SpringLayout.EAST, workspaceText, -80, SpringLayout.EAST, inputPanel);
		
		workspaceText.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				workspaceText.setBackground(Color.WHITE);
			}
		});
		
		//Workspace BUTTON
		
		Image img = ImageIO.read(LaunchFrame.class.getResource("/test2.png"));
		JButton workspaceChoose = new JButton(new ImageIcon(img));
		workspaceChoose.setBorder(null);
		workspaceChoose.setBackground(textFields);
		workspaceChoose.setFocusable(false);
		workspaceChoose.setPreferredSize(new Dimension(30, 30));
		workspaceChoose.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		inputPanel.add(workspaceChoose);
		inputSpring.putConstraint(SpringLayout.VERTICAL_CENTER, workspaceChoose, 0, SpringLayout.VERTICAL_CENTER, workspaceText);
		inputSpring.putConstraint(SpringLayout.HORIZONTAL_CENTER, workspaceChoose, 25, SpringLayout.EAST, workspaceText);
		
		workspaceChoose.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				int change = 6;
				Dimension old = workspaceChoose.getPreferredSize();
				Dimension newD = new Dimension(old.width-change, old.height-change);
				workspaceChoose.setPreferredSize(newD);
				workspaceChoose.setBackground(textFields);
				workspaceChoose.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				int change = 6;
				Dimension old = workspaceChoose.getPreferredSize();
				Dimension newD = new Dimension(old.width+change, old.height+change);
				workspaceChoose.setPreferredSize(newD);
				workspaceChoose.setBackground(background1);
				workspaceChoose.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
			}
		});
		ActionListener chooseFile = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String path = "C:"+File.separator;
				//System.out.println(path);
				JFileChooser fileOpen = new JFileChooser(new File(path));
				fileOpen.setMultiSelectionEnabled(true);
				fileOpen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				MainHashChecker.setUpdateUI(fileOpen);
				int res = fileOpen.showDialog(null, "Выбрать директорию");
				if (res==JFileChooser.APPROVE_OPTION) {
					File dir = fileOpen.getSelectedFile();
					workspaceText.setText(dir.getAbsolutePath());
				}
			}
		};
		workspaceChoose.addActionListener(chooseFile);
		
// ---------------- Key
		
		JLabel keyLabel1 = new JLabel("Ключ сессии:");
		keyLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		keyLabel1.setPreferredSize(new Dimension(100, 30));
		keyLabel1.setFont(keyLabel1.getFont().deriveFont(1, 15));
		//keyLabel1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		inputPanel.add(keyLabel1);
		inputSpring.putConstraint(SpringLayout.NORTH, keyLabel1, 20, SpringLayout.SOUTH, workspaceLabel2);
		inputSpring.putConstraint(SpringLayout.WEST, keyLabel1, 10, SpringLayout.WEST, inputPanel);
		
		JLabel keyLabel2 = new JLabel("ключ:");
		keyLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		keyLabel2.setPreferredSize(new Dimension(100, 30));
		keyLabel2.setFont(keyLabel2.getFont().deriveFont(1, 15));
		//keyLabel2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		//inputPanel.add(keyLabel2);
		inputSpring.putConstraint(SpringLayout.NORTH, keyLabel2, -15, SpringLayout.SOUTH, keyLabel1);
		inputSpring.putConstraint(SpringLayout.WEST, keyLabel2, 0, SpringLayout.WEST, keyLabel1);
		
		//KeyField
		
		JPasswordField pass = new JPasswordField();
		pass.setPreferredSize(new Dimension(200, 30));
		pass.setFont(pass.getFont().deriveFont(0, 14));
		
		inputPanel.add(pass);
		inputSpring.putConstraint(SpringLayout.VERTICAL_CENTER, pass, 0, SpringLayout.VERTICAL_CENTER, keyLabel1);
		//inputSpring.putConstraint(SpringLayout.NORTH, pass, 7, SpringLayout.NORTH, keyLabel1);
		inputSpring.putConstraint(SpringLayout.WEST, pass, 20, SpringLayout.EAST, keyLabel2);
		inputSpring.putConstraint(SpringLayout.EAST, pass, 0, SpringLayout.EAST, workspaceText);
		
		//EYE BUTTON
		
		
		Image img1 = ImageIO.read(LaunchFrame.class.getResource("/eye.png"));
		Image img2 = ImageIO.read(LaunchFrame.class.getResource("/eye2.png"));
		//Color colorMouse = new Color(arg0, arg1, arg2)
		
		JButton showPass = new JButton(new ImageIcon(img1));
		showPass.setBorder(null);
		showPass.setBackground(textFields);
		showPass.setFocusable(false);
		showPass.setPreferredSize(new Dimension(30, 30));
		showPass.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		inputPanel.add(showPass);
		inputSpring.putConstraint(SpringLayout.VERTICAL_CENTER, showPass, 0, SpringLayout.VERTICAL_CENTER, pass);
		inputSpring.putConstraint(SpringLayout.HORIZONTAL_CENTER, showPass, 25, SpringLayout.EAST, pass);
		
		char c = pass.getEchoChar();
		showPass.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (visiblePassword) {
					pass.setEchoChar(c);
					showPass.setIcon(new ImageIcon(img1));
				}else {
					pass.setEchoChar((char)0);
					showPass.setIcon(new ImageIcon(img2));
				}
				visiblePassword = !visiblePassword;
			}
		});
		showPass.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				int change = 6;
				Dimension old = showPass.getPreferredSize();
				Dimension newD = new Dimension(old.width-change, old.height-change);
				showPass.setPreferredSize(newD);
				showPass.setBackground(textFields);
				showPass.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				int change = 6;
				Dimension old = showPass.getPreferredSize();
				Dimension newD = new Dimension(old.width+change, old.height+change);
				showPass.setPreferredSize(newD);
				showPass.setBackground(background1);
				showPass.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
			}
		});
		
//============= Launch Cancel buttons
		JButton cancel = new JButton("Отмена");
		cancel.setPreferredSize(new Dimension(80, 30));
		cancel.setMinimumSize(cancel.getPreferredSize());
		cancel.setBackground(textFields);
		cancel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		cancel.setFocusable(false);
		
		inputPanel.add(cancel);
		inputSpring.putConstraint(SpringLayout.VERTICAL_CENTER, cancel, 30+20, SpringLayout.VERTICAL_CENTER, pass);
		inputSpring.putConstraint(SpringLayout.HORIZONTAL_CENTER, cancel, -25, SpringLayout.HORIZONTAL_CENTER, showPass);
		cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				int change = 4;
				Dimension old = cancel.getPreferredSize();
				Dimension newD = new Dimension(old.width-change, old.height-change);
				cancel.setPreferredSize(newD);
				cancel.setBackground(textFields);
				cancel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				int change = 4;
				Dimension old = cancel.getPreferredSize();
				Dimension newD = new Dimension(old.width+change, old.height+change);
				cancel.setPreferredSize(newD);
				cancel.setBackground(background1);
				cancel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
			}
		});
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		//------
		JButton launch = new JButton("Запуск");
		launch.setPreferredSize(new Dimension(80, 30));
		launch.setMinimumSize(launch.getPreferredSize());
		launch.setBackground(textFields);
		launch.setFocusable(false);
		launch.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		launch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				int change = 4;
				Dimension old = launch.getPreferredSize();
				Dimension newD = new Dimension(old.width-change, old.height-change);
				launch.setPreferredSize(newD);
				launch.setBackground(textFields);
				launch.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				int change = 4;
				Dimension old = launch.getPreferredSize();
				Dimension newD = new Dimension(old.width+change, old.height+change);
				launch.setPreferredSize(newD);
				launch.setBackground(background1);
				launch.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
			}
		});
		
		launch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = workspaceText.getText();
				File file = new File(str);
				try {
					if (file.mkdirs()||file.exists()) {
						if (file.isDirectory()) {
							if (pass.getPassword().length!=0) {
								String password="";
								for (char c: pass.getPassword()) {
									password+=c;
								}
								while(password.length()!=16) {
									if (password.length()>16) {
										password = password.substring(0, 16);
									}else {
										password+=password;
									}
								}
								
								MainHashChecker.encrypt_key = password.getBytes();
							}
							MainHashChecker.workspace_directory = file;
							MainHashChecker.showWorkFrame();
							return;
						}
					}
					workspaceText.setBackground(selectColor);
				}catch (Exception ex) {
					workspaceText.setBackground(Color.RED);
				}
			}
		});
		
		inputPanel.add(launch);
		inputSpring.putConstraint(SpringLayout.VERTICAL_CENTER, launch, 0, SpringLayout.VERTICAL_CENTER, cancel);
		inputSpring.putConstraint(SpringLayout.HORIZONTAL_CENTER, launch, -20-80, SpringLayout.HORIZONTAL_CENTER, cancel);
	}
	public void hideFrame() {
		setVisible(false);
	}
}
