package ru.umar.HashChecker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ReportPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Color background = new Color(250, 250, 250);//light
	private Color background1 = new Color(230, 225, 225);//dark
	private Color textFields = new Color(189, 213, 255);
	private Color gray = new Color(213, 210, 210);
	private Color disableButtons = new Color(161, 185, 227);
	
	private JPanel panel;
	
	private JPanel top;
	private JPanel middle;
	private JPanel right;
	
//--top
	private JButton start;
	public JComboBox<String> name;
//--middle
	private JLabel configName;
	private JLabel numFiles;
	private JLabel sumSize;
	private JLabel hashType;
	private JLabel snapStatus;
	private JProgressBar progressBar;
	private JLabel progressLabel;
	private JButton pause;
	private JButton continueB;
	private JButton interrupt;
//--right
	private JTable table;
	private JScrollPane scroll;
	private JLabel headLabel;
	private JButton delete;
	
	public enum State{
		Empty, Working, Pause, Pausing, Resuming, Finished
	}
	
	private State state;
	private double time = 0;
	private ReportCreaterThread reportCreaterThread;
	
	public ErrorPanel errorPanel;
	
	public ReportPanel() {
		super();
		
		panel = (JPanel)this;
		setLayout(new SpringLayout());
		setBackground(background);
		
		createTop();
		createMiddle();
		createRight();
		createErrorPanel();
	}
	
	private void createErrorPanel() {
		errorPanel = new ErrorPanel();
		SpringLayout panelLayout = (SpringLayout) middle.getLayout();
		middle.add(errorPanel);
		errorPanel.setPreferredSize(new Dimension(100, 100));
		panelLayout.putConstraint(SpringLayout.NORTH, errorPanel, 0, SpringLayout.SOUTH, snapStatus);
		panelLayout.putConstraint(SpringLayout.WEST, errorPanel, 0, SpringLayout.WEST, middle);
		panelLayout.putConstraint(SpringLayout.EAST, errorPanel, 0, SpringLayout.EAST, middle);
		panelLayout.putConstraint(SpringLayout.SOUTH, errorPanel, 0, SpringLayout.SOUTH, middle);
	}
	private void createTop() {
		top = new JPanel();
		SpringLayout spring = new SpringLayout();
		top.setLayout(spring);
		top.setBackground(background);
		top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, gray));
		
		SpringLayout panelLayout = (SpringLayout) panel.getLayout();
		panelLayout.putConstraint(SpringLayout.NORTH, top, 0, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, top, 0, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, top, 0, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, top, 90, SpringLayout.NORTH, panel);
		
		JLabel label = new JLabel("Конфигурация");
		name = new JComboBox<String>();
		start = new JButton("Запустить фиксацию");
		
		label.setFont(label.getFont().deriveFont(0, 16));
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, label, 0, SpringLayout.VERTICAL_CENTER, top);
		spring.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, top);
		top.add(label);
		
		name.setFont(name.getFont().deriveFont(0, 14));
		name.setPreferredSize(new Dimension(200, label.getPreferredSize().height+10));
		updateName();
		name.setFocusable(false);
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, name, 0, SpringLayout.VERTICAL_CENTER, label);
		spring.putConstraint(SpringLayout.WEST, name, 20, SpringLayout.EAST, label);
		top.add(name);
		name.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (name.getSelectedItem()!=null) {
					recreateTableSnapshots((String)name.getSelectedItem(), MainHashChecker.encrypt_key);
				}
			}
		});
		
		
		decorTopButtons(start);
		start.setPreferredSize(new Dimension(160, name.getPreferredSize().height));
		top.add(start);
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, start, 0, SpringLayout.VERTICAL_CENTER, name);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, start, 20+start.getPreferredSize().width/2, SpringLayout.EAST, name);
		
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (state==State.Empty||state==State.Finished) {
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
							errorPanel.resetValues();
							errorPanel.hidePanel();
							reportCreaterThread = new ReportCreaterThread(config, ReportPanel.this);
						}
					}
				}
			}
		});
		panel.add(top);
	}
	
	private boolean entered = false;
	private void decorTopButtons(JButton btn) {
		btn.setBackground(textFields);
		btn.setFocusable(false);
		btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		btn.addMouseListener(new MouseAdapter() {				
			@Override
			public void mouseExited(MouseEvent arg0) {
				if (entered) {
					entered = false;
					int change = 4;
					Dimension old = btn.getPreferredSize();
					Dimension newD = new Dimension(old.width-change, old.height-change);
					btn.setPreferredSize(newD);
					btn.setBackground(textFields);
					btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					if (state!=State.Empty&&state!=State.Finished) {
						btn.setBackground(disableButtons);
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (state==State.Empty||state==State.Finished) {
					entered = true;
					int change = 4;
					Dimension old = btn.getPreferredSize();
					Dimension newD = new Dimension(old.width+change, old.height+change);
					btn.setPreferredSize(newD);
					btn.setBackground(background1);
					btn.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
				}
			}
		});
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
	
	
	private boolean enteredPause = false;
	private boolean enteredContinue = false;
	private boolean enteredFinish = false;
	private void createMiddle() {
		
		state = State.Empty;
		
		middle = new JPanel();
		SpringLayout spring = new SpringLayout();
		middle.setLayout(spring);
		middle.setBackground(background);
		middle.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, gray));
		
		SpringLayout panelLayout = (SpringLayout) panel.getLayout();
		panelLayout.putConstraint(SpringLayout.NORTH, middle, 0, SpringLayout.SOUTH, top);
		panelLayout.putConstraint(SpringLayout.WEST, middle, 0, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, middle, 420, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, middle, 0, SpringLayout.SOUTH, panel);
		
		panel.add(middle);
		
		//== ELEMENTS ==\\
		JLabel header = new JLabel("Процесс фиксации");
		configName = new JLabel("Конфигурация:");
		pause = new JButton("Пауза");
		continueB = new JButton("Возобновить");
		interrupt = new JButton("Завершить");
		numFiles = new JLabel("Всего файлов:");
		sumSize = new JLabel("Суммарный размер:");
		hashType = new JLabel();
		snapStatus = new JLabel();
		
		progressBar = new JProgressBar();
		progressLabel = new JLabel("Выполнено 0 %");
		
		//==============\\
		
		header.setFont(header.getFont().deriveFont(1, 16));
		middle.add(header);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, header, 0, SpringLayout.HORIZONTAL_CENTER, middle);
		spring.putConstraint(SpringLayout.NORTH, header, 10, SpringLayout.NORTH, middle);
		
		configName.setPreferredSize(new Dimension(400, 30));
		configName.setFont(configName.getFont().deriveFont(0, 14));
		//configName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middle.add(configName);
		spring.putConstraint(SpringLayout.NORTH, configName, 10, SpringLayout.SOUTH, header);
		spring.putConstraint(SpringLayout.WEST, configName, 10, SpringLayout.WEST, middle);
		
		hashType.setPreferredSize(new Dimension(400, 30));
		hashType.setFont(hashType.getFont().deriveFont(0, 14));
		//hashType.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middle.add(hashType);
		spring.putConstraint(SpringLayout.NORTH, hashType, 0, SpringLayout.SOUTH, configName);
		spring.putConstraint(SpringLayout.WEST, hashType, 10, SpringLayout.WEST, middle);
		
		numFiles.setPreferredSize(new Dimension(400, 30));
		numFiles.setFont(numFiles.getFont().deriveFont(0, 14));
		//numFiles.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middle.add(numFiles);
		spring.putConstraint(SpringLayout.NORTH, numFiles, 0, SpringLayout.SOUTH, hashType);
		spring.putConstraint(SpringLayout.WEST, numFiles, 10, SpringLayout.WEST, middle);
		
		sumSize.setPreferredSize(new Dimension(400, 30));
		sumSize.setFont(sumSize.getFont().deriveFont(0, 14));
		//sumSize.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middle.add(sumSize);
		spring.putConstraint(SpringLayout.NORTH, sumSize, 0, SpringLayout.SOUTH, numFiles);
		spring.putConstraint(SpringLayout.WEST, sumSize, 10, SpringLayout.WEST, middle);
		
		progressLabel.setFont(progressLabel.getFont().deriveFont(0, 14));
		progressLabel.setPreferredSize(new Dimension(400, 30));
		progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
		//progressLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middle.add(progressLabel);
		spring.putConstraint(SpringLayout.NORTH, progressLabel, 0, SpringLayout.SOUTH, sumSize);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, progressLabel, 0, SpringLayout.HORIZONTAL_CENTER, progressBar);
		
		progressBar.setPreferredSize(new Dimension(400, 15));
		progressBar.setMinimum(0);
		progressBar.setMaximum(1000);
		progressBar.setValue(0);
		middle.add(progressBar);
		spring.putConstraint(SpringLayout.NORTH, progressBar, 0, SpringLayout.SOUTH, progressLabel);
		spring.putConstraint(SpringLayout.WEST, progressBar, 0, SpringLayout.WEST, numFiles);
		
		decorLineButton(pause);
		decorLineButton(continueB);
		decorLineButton(interrupt);
		
		middle.add(pause);
		middle.add(continueB);
		middle.add(interrupt);
		
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, pause, 20+15+progressBar.getPreferredSize().height/2, SpringLayout.VERTICAL_CENTER, progressBar);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, pause, 10+pause.getPreferredSize().width/2, SpringLayout.WEST, middle);
		
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, continueB, 0, SpringLayout.VERTICAL_CENTER, pause);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, continueB, 20+pause.getPreferredSize().width/2+continueB.getPreferredSize().width/2, SpringLayout.HORIZONTAL_CENTER, pause);
		
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, interrupt, 0, SpringLayout.VERTICAL_CENTER, pause);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, interrupt, 20+continueB.getPreferredSize().width/2+interrupt.getPreferredSize().width/2, SpringLayout.HORIZONTAL_CENTER, continueB);
	
		snapStatus.setPreferredSize(new Dimension(400, 30));
		snapStatus.setFont(snapStatus.getFont().deriveFont(0, 14));
		//snapStatus.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		middle.add(snapStatus);
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, snapStatus, 10+15+pause.getPreferredSize().height/2, SpringLayout.VERTICAL_CENTER, pause);
		spring.putConstraint(SpringLayout.WEST, snapStatus, 10, SpringLayout.WEST, middle);
		
//===============================================================================\\
		pause.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				if (enteredPause) {
					enteredPause = false;
					int change = 4;
					Dimension old = pause.getPreferredSize();
					Dimension newD = new Dimension(old.width-change, old.height-change);
					pause.setPreferredSize(newD);
					pause.setBackground(textFields);
					pause.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					if (state!=State.Working&&state!=State.Resuming) {
						pause.setBackground(disableButtons);
					}
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (state==State.Working||state==State.Resuming) {
					enteredPause = true;
					int change = 4;
					Dimension old = pause.getPreferredSize();
					Dimension newD = new Dimension(old.width+change, old.height+change);
					pause.setPreferredSize(newD);
					pause.setBackground(background1);
					pause.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
				}
			}
		});
		continueB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				if (enteredContinue) {
					enteredContinue = false;
					int change = 4;
					Dimension old = continueB.getPreferredSize();
					Dimension newD = new Dimension(old.width-change, old.height-change);
					continueB.setPreferredSize(newD);
					continueB.setBackground(textFields);
					continueB.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					if (state!=State.Pause&&state!=State.Pausing) {
						continueB.setBackground(disableButtons);
					}
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (state==State.Pause||state==State.Pausing) {
					enteredContinue = true;
					int change = 4;
					Dimension old = continueB.getPreferredSize();
					Dimension newD = new Dimension(old.width+change, old.height+change);
					continueB.setPreferredSize(newD);
					continueB.setBackground(background1);
					continueB.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
				}
			}
		});
		interrupt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				if (enteredFinish) {
					enteredFinish = false;
					int change = 4;
					Dimension old = interrupt.getPreferredSize();
					Dimension newD = new Dimension(old.width-change, old.height-change);
					interrupt.setPreferredSize(newD);
					interrupt.setBackground(textFields);
					interrupt.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					if (state==State.Empty) {
						interrupt.setBackground(disableButtons);
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (state!=State.Empty) {
					enteredFinish = true;
					int change = 4;
					Dimension old = interrupt.getPreferredSize();
					Dimension newD = new Dimension(old.width+change, old.height+change);
					interrupt.setPreferredSize(newD);
					interrupt.setBackground(background1);
					interrupt.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
				}
			}
		});
		
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (state==State.Working||state==State.Resuming) {
					if (reportCreaterThread!=null) {
						setState(State.Pausing, null);
						reportCreaterThread.pause();
					}
				}
			}
		});
		continueB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (state==State.Pause||state==State.Pausing) {
					if (reportCreaterThread!=null) {
						setState(State.Resuming, null);
						reportCreaterThread.resume();
					}
				}
			}
		});
		interrupt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (state!=State.Empty) {
					if (reportCreaterThread!=null) {
						reportCreaterThread.interrupt();
					}else {
						setState(State.Empty, null);
					}
				}
				errorPanel.resetValues();
				errorPanel.hidePanel();
			}
		});
		
		
		setState(State.Empty, null);
	}
//========== MIDDLE SETTERS ============\\
	public void clearLabels() {
		configName.setText("Конфигурация:");
		numFiles.setText("Всего файлов:");
		sumSize.setText("Суммарный размер:");
		hashType.setText("Алгоритм хеширования:");;
		snapStatus.setText("Статус:");
		setProgress(0);
		time = 0;
		reportCreaterThread = null;
	}
	public void setSumSize(String str) {
		sumSize.setText(str);
	}
	public void setConfigName(String str) {
		configName.setText(str);
	}
	public void setProgress(int progress) {
		progressBar.setValue(progress);
		progressLabel.setText("Выполнено "+progress/10+" %");
	}
	public void setHash(String str) {
		hashType.setText(str);
	}
	public void setStatus(String str) {
		snapStatus.setText(str);
	}
	public void setState(ReportPanel.State state, Object ob) {
		this.state = state;
		switch (state) {
		case Empty:{
			clearLabels();
			interrupt.setBackground(disableButtons);
			continueB.setBackground(disableButtons);
			pause.setBackground(disableButtons);
			start.setBackground(textFields);
			//name.setBackground(nameColor);
			break;
		}
		case Pause:{
			double timeperiod = (Double)ob;
			time+=timeperiod;
			setStatus("Статус: приостановлено");
			pause.setBackground(disableButtons);
			interrupt.setBackground(textFields);
			continueB.setBackground(textFields);
			//name.setBackground(disableButtons);
			break;
		}
		case Pausing:{
			setStatus("Статус: останавливается");
			pause.setBackground(disableButtons);
			interrupt.setBackground(textFields);
			continueB.setBackground(textFields);
			//name.setBackground(disableButtons);
			break;
		}
		case Working:{
			setStatus("Статус: выполняется");
			pause.setBackground(textFields);
			interrupt.setBackground(textFields);
			continueB.setBackground(disableButtons);
			//name.setBackground(disableButtons);
			break;
		}
		case Resuming:{
			setStatus("Статус: возобновляется");
			pause.setBackground(textFields);
			interrupt.setBackground(textFields);
			continueB.setBackground(disableButtons);
			//name.setBackground(disableButtons);
			break;
		}
		case Finished:{
			double timeperiod = (Double)ob;
			time+=timeperiod;
			setStatus("Статус: завершено ("+String.format("%.2f", time)+" с).");
			time = 0;
			if (((String)name.getSelectedItem()).equals(reportCreaterThread.config.getName())) {
				recreateTableSnapshots((String)name.getSelectedItem(), MainHashChecker.encrypt_key);
			}
			reportCreaterThread = null;
			pause.setBackground(disableButtons);
			interrupt.setBackground(textFields);
			continueB.setBackground(disableButtons);
			//name.setBackground(disableButtons);
		}
		default:
			break;
		}
	}
	public void setNumFilesToLabel(String str) {
		numFiles.setText(str);
	}
	private void decorLineButton(JButton btn) {
		btn.setPreferredSize(new Dimension(120, 30));
		btn.setMinimumSize(new Dimension(120, 30));
		btn.setFont(btn.getFont().deriveFont(0, 14));
		btn.setBackground(textFields);
		btn.setFocusable(false);
		btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
//===================== RIGHT PANEL ==============================\\
	private boolean enteredDeleteRight = false;
	private void createRight() {
		right = new JPanel();
		SpringLayout spring = new SpringLayout();
		right.setLayout(spring);
		right.setBackground(background);
		right.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, gray));
		
		SpringLayout panelLayout = (SpringLayout) panel.getLayout();
		panelLayout.putConstraint(SpringLayout.NORTH, right, 0, SpringLayout.SOUTH, top);
		panelLayout.putConstraint(SpringLayout.WEST, right, 0, SpringLayout.EAST, middle);
		panelLayout.putConstraint(SpringLayout.EAST, right, 0, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, right, 0, SpringLayout.SOUTH, panel);
		
		panel.add(right);
		
	//==== ELEMENTS ====
		headLabel = new JLabel("Список сделанных снимков");
		delete = new JButton("Удалить выделенные");
	//==================
		right.add(headLabel);
		headLabel.setFont(headLabel.getFont().deriveFont(1, 16));
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, headLabel, 0, SpringLayout.HORIZONTAL_CENTER, right);
		spring.putConstraint(SpringLayout.NORTH, headLabel, 10, SpringLayout.NORTH, right);
		
		recreateTableSnapshots((String) name.getSelectedItem(), MainHashChecker.encrypt_key);
		
		right.add(delete);
		delete.setFont(delete.getFont().deriveFont(0, 14));
		delete.setPreferredSize(new Dimension(200, 30));
		spring.putConstraint(SpringLayout.VERTICAL_CENTER, delete, -25, SpringLayout.SOUTH, right);
		spring.putConstraint(SpringLayout.HORIZONTAL_CENTER, delete, 10+delete.getPreferredSize().width/2, SpringLayout.WEST, right);
		delete.setBackground(textFields);
		delete.setFocusable(false);
		delete.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int row: table.getSelectedRows()) {
					String str = (String) table.getModel().getValueAt(row, 0);
					if (MainHashChecker.encrypt_key!=null) {
						try {
							str = Configuration.encryptLine(str, MainHashChecker.encrypt_key);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					File file = new File(MainHashChecker.workspace_directory+File.separator+MainHashChecker.reportDirectory+File.separator+str);
					try {
						file.delete();
					}catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				recreateTableSnapshots((String) name.getSelectedItem(), MainHashChecker.encrypt_key);
			}
		});
		delete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				if (enteredDeleteRight) {
					enteredDeleteRight = false;
					int change = 4;
					Dimension old = delete.getPreferredSize();
					Dimension newD = new Dimension(old.width-change, old.height-change);
					delete.setPreferredSize(newD);
					delete.setBackground(textFields);
					delete.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					if (table.getSelectedRows().length==0) {
						delete.setBackground(disableButtons);
					}
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (table.getSelectedRows().length!=0) {
					enteredDeleteRight = true;
					int change = 4;
					Dimension old = delete.getPreferredSize();
					Dimension newD = new Dimension(old.width+change, old.height+change);
					delete.setPreferredSize(newD);
					delete.setBackground(background1);
					delete.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, textFields));
				}
			}
		});
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				table.clearSelection();
			}
		});
	}
	public void recreateTableSnapshots(String configName, byte[] encrypt_key) {
		try {
			right.remove(scroll);
		}catch (Exception e) {}
		ArrayList<String>snaps = downloadSnapshots(configName, encrypt_key);
		String array[][] = new String[snaps.size()][1];
		String names[] = new String[1];
		names[0] = "name";
		for (int i = 0; i<snaps.size(); i++) {
			array[i][0] = snaps.get(i);
		}
		
		table = new JTable(array, names);
		table.setTableHeader(null);
		DefaultTableModel tableModel = new DefaultTableModel(array, names) {
			private static final long serialVersionUID = 1L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		table.setModel(tableModel);
		table.setRowHeight(20);
		table.setFocusable(false);
		table.setFont(table.getFont().deriveFont(0 , 14));
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
		
		scroll = new JScrollPane(table);
		right.add(scroll);
		SpringLayout spring = (SpringLayout) right.getLayout();
		spring.putConstraint(SpringLayout.NORTH, scroll, 10, SpringLayout.SOUTH, headLabel);
		spring.putConstraint(SpringLayout.SOUTH, scroll, -50, SpringLayout.SOUTH, right);
		spring.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, right);
		spring.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, right);
		right.revalidate();
		right.repaint();

	}
	public ArrayList<String> downloadSnapshots(String configName, byte[] encrypt_key){
		File dir = new File(MainHashChecker.workspace_directory.getAbsolutePath()+File.separator+MainHashChecker.reportDirectory);
		ArrayList<String> list = new ArrayList<String>();
		if (configName!=null) {
			for (File fl: dir.listFiles()) {
				if (Configuration.validSnapshot(fl, configName, encrypt_key)) {
					String name = fl.getName();
					if (MainHashChecker.encrypt_key!=null) {
						try {
							name = Configuration.decryptLine(name, encrypt_key);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					list.add(name);
				}
			}
		}
		return list;
	}
	
//==================================================================
	public void updateHeader() {
		String confS = (String) name.getSelectedItem();
		
		updateName();
		
		name.setSelectedItem(confS);
	}
	public void addToPanel(JPanel target) {
		Object ob1 = name.getSelectedItem();
		updateName();
		name.setSelectedItem(ob1);
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
}
