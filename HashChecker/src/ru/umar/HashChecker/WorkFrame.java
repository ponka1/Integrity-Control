package ru.umar.HashChecker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class WorkFrame extends JFrame{
	private static String header_name = "HashChecker 1.0 - Рабочее окно";
	private static String header_part = "HashChecker 1.0 - ";
	public File main_dir=null;
	public byte[] encrypt_key=null;
	
	private static final long serialVersionUID = 1L;
	private int width=800;
	private int heigh=600;
	
	private JPanel panel;
	private JPanel header;
	private JPanel body;
	
	private ConfigurationPanel configPanel;
	private ReportPanel reportPanel;
	private CheckPanel checkPanel;
	
	public static JButton checkButton;
	public static JButton reportButton;
	public static JButton configButton;
	
	private Color background1 = new Color(230, 225, 225);//dark
	private Color textFields = new Color(189, 213, 255);
	//private Color selectColor = new Color(204, 123, 110);
	private Color background = new Color(250, 250, 250);//light
	
	public WorkFrame(File main_dir, byte[]encrypt_key) {
		super(header_name);
		this.main_dir = main_dir;
		this.encrypt_key = encrypt_key;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(width, heigh);
		setMinimumSize(new Dimension(width, heigh));
		setResizable(true);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
	    Dimension dim = toolkit.getScreenSize();
	    setBounds(dim.width/2-getSize().width/2, dim.height/2-getSize().height/2, getSize().width, getSize().height);
	    
	    panel = createPanel();
	    header = createHeader();
	    body = createBody();
	    panel.add(header);
	    panel.add(body);
	    
	    add(panel);
	    configPanel = new ConfigurationPanel();
	    reportPanel  = new ReportPanel();
	    checkPanel = new CheckPanel();
	    
	    showConfiguationPanel();
	    
	    setVisible(true);
	}
	
	private JPanel createPanel() {
		panel = new JPanel();
		panel.setBackground(background);
		panel.setLayout(new SpringLayout());
		return panel;
	}
	private JPanel createBody() {
		body = new JPanel();
		body.setBackground(Color.MAGENTA);
		body.setLayout(new SpringLayout());
		SpringLayout springPanel = (SpringLayout) panel.getLayout();
		springPanel.putConstraint(SpringLayout.NORTH, body, 0, SpringLayout.SOUTH, header);
		springPanel.putConstraint(SpringLayout.SOUTH, body, 0, SpringLayout.SOUTH, panel);
		springPanel.putConstraint(SpringLayout.EAST, body, 0, SpringLayout.EAST, panel);
		springPanel.putConstraint(SpringLayout.WEST, body, 0, SpringLayout.WEST, panel);
		return body;
	}
	private JPanel createHeader() {
		header = new JPanel();
		header.setPreferredSize(new Dimension(width, 40));
		header.setBackground(background1);
		header.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
		SpringLayout springPanel = (SpringLayout) panel.getLayout();
		springPanel.putConstraint(SpringLayout.NORTH, header, 0, SpringLayout.NORTH, panel);
		springPanel.putConstraint(SpringLayout.EAST, header, 0, SpringLayout.EAST, panel);
		springPanel.putConstraint(SpringLayout.WEST, header, 0, SpringLayout.WEST, panel);
		
		SpringLayout spring = new SpringLayout();
		header.setLayout(spring);
		
		checkButton = new JButton("Проверка");
		checkButton.setPreferredSize(new Dimension(120, 40));
		checkButton.setMinimumSize(new Dimension(120, 40));
		spring.putConstraint(SpringLayout.NORTH, checkButton, 0, SpringLayout.NORTH, header);
		spring.putConstraint(SpringLayout.SOUTH, checkButton, 0, SpringLayout.SOUTH, header);
		spring.putConstraint(SpringLayout.WEST, checkButton, 0, SpringLayout.WEST, panel);
		decorHeaderButton(checkButton);
		header.add(checkButton);
		checkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCheckPanel();
			}
		});
		
		reportButton = new JButton("Фиксация");
		reportButton.setPreferredSize(new Dimension(120, 40));
		reportButton.setMinimumSize(new Dimension(120, 40));
		spring.putConstraint(SpringLayout.NORTH, reportButton, 0, SpringLayout.NORTH, header);
		spring.putConstraint(SpringLayout.SOUTH, reportButton, 0, SpringLayout.SOUTH, header);
		spring.putConstraint(SpringLayout.WEST, reportButton, 0, SpringLayout.EAST, checkButton);
		decorHeaderButton(reportButton);
		header.add(reportButton);
		reportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReportPanel();
			}
		});
		
		configButton = new JButton("Конфигурация");
		configButton.setPreferredSize(new Dimension(120, 40));
		configButton.setMinimumSize(new Dimension(120, 40));
		spring.putConstraint(SpringLayout.NORTH, configButton, 0, SpringLayout.NORTH, header);
		spring.putConstraint(SpringLayout.SOUTH, configButton, 0, SpringLayout.SOUTH, header);
		spring.putConstraint(SpringLayout.WEST, configButton, 0, SpringLayout.EAST, reportButton);
		decorHeaderButton(configButton);
		header.add(configButton);
		configButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showConfiguationPanel();
			}
		});
		
		return header;
	}
	private void decorHeaderButton(JButton btn) {
		Color backgroundButton = background1;
		//Color backgroundFocus = textFields;
		
		btn.setBackground(backgroundButton);
		btn.setFocusable(false);
		btn.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
		btn.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(MouseEvent arg0) {
				//btn.setBackground(backgroundButton);
				btn.setFont(btn.getFont().deriveFont(1, btn.getFont().getSize()-1));
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				//btn.setBackground(backgroundFocus);
				btn.setFont(btn.getFont().deriveFont(1, btn.getFont().getSize()+1));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				//btn.setFont(btn.getFont().deriveFont(1, btn.getFont().getSize()+1));
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				//btn.setFont(btn.getFont().deriveFont(1, btn.getFont().getSize()-1));
			}
			
		});	
	}
//================== INTERACTIVE ==================\\
	private void showConfiguationPanel() {
		reportPanel.removeFromPanel(body);
		checkPanel.removeFromPanel(body);
		configPanel.addToPanel(body);
		
		reportButton.setBackground(background1);
		checkButton.setBackground(background1);
		configButton.setBackground(textFields);
		
		WorkFrame.this.setTitle(header_part+"Настройка конфигурации");
		
		body.revalidate();
		body.repaint();
	}
	private void showReportPanel() {
		configPanel.removeFromPanel(body);
		checkPanel.removeFromPanel(body);
		reportPanel.addToPanel(body);
		
		configButton.setBackground(background1);
		checkButton.setBackground(background1);
		reportButton.setBackground(textFields);
		
		WorkFrame.this.setTitle(header_part+"Фиксация состояния");
		
		body.revalidate();
		body.repaint();
	}
	private void showCheckPanel() {
		reportPanel.removeFromPanel(body);
		configPanel.removeFromPanel(body);
		checkPanel.addToPanel(body);
		checkPanel.updateHeader();
		
		reportButton.setBackground(background1);
		configButton.setBackground(background1);
		checkButton.setBackground(textFields);
		
		WorkFrame.this.setTitle(header_part+"Проверка целостности");
		
		body.revalidate();
		body.repaint();
	}
	

}
