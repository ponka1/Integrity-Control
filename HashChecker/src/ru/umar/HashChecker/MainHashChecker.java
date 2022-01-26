package ru.umar.HashChecker;

import java.io.File;
import java.security.Security;

import javax.swing.JFileChooser;
import javax.swing.UIManager;

import ru.fsb.gost.GOSTProvider;

public class MainHashChecker {
	
	public static File workspace_directory = null;
	public static String configDirectory = "Configurations";
	public static String reportDirectory = "Snapshots";
	//public static String resultDirectory = "Results";
	public static String tempDirecorty = "Temporal";
	public static String tempFile = "temp.temp";
	public static byte[] encrypt_key = null;
	
	public static LaunchFrame launchFrame=null;
	public static WorkFrame workFrame=null;
	
	public static void main(String args[]) {
		if (Security.getProvider("GOST") == null) {
            Security.addProvider(new GOSTProvider());
        }
		launchFrame = new LaunchFrame();
	}
	
	public static void showWorkFrame() {
		launchFrame.hideFrame();
		workFrame = new WorkFrame(workspace_directory, encrypt_key);
	}
	
	public static void setUpdateUI(JFileChooser choose) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	    UIManager.put("FileChooser.openButtonText", "�������");
	    UIManager.put("FileChooser.cancelButtonText", "������");
	    UIManager.put("FileChooser.lookInLabelText", "�������� �");
	    UIManager.put("FileChooser.fileNameLabelText", "��� �����");
	    UIManager.put("FileChooser.filesOfTypeLabelText", "��� �����");
	 
	    UIManager.put("FileChooser.saveButtonText", "���������");
	    UIManager.put("FileChooser.saveButtonToolTipText", "���������");
	    UIManager.put("FileChooser.openButtonText", "�������");
	    UIManager.put("FileChooser.openButtonToolTipText", "�������");
	    UIManager.put("FileChooser.cancelButtonText", "������");
	    UIManager.put("FileChooser.cancelButtonToolTipText", "������");
	 
	    UIManager.put("FileChooser.lookInLabelText", "�����");
	    UIManager.put("FileChooser.saveInLabelText", "�����");
	    UIManager.put("FileChooser.fileNameLabelText", "��� �����");
	    UIManager.put("FileChooser.filesOfTypeLabelText", "��� ������");
	 
	    UIManager.put("FileChooser.upFolderToolTipText", "�� ���� ������� �����");
	    UIManager.put("FileChooser.newFolderToolTipText", "�������� ����� �����");
	    UIManager.put("FileChooser.listViewButtonToolTipText", "������");
	    UIManager.put("FileChooser.detailsViewButtonToolTipText", "�������");
	    UIManager.put("FileChooser.fileNameHeaderText", "���");
	    UIManager.put("FileChooser.fileSizeHeaderText", "������");
	    UIManager.put("FileChooser.fileTypeHeaderText", "���");
	    UIManager.put("FileChooser.fileDateHeaderText", "�������");
	    UIManager.put("FileChooser.fileAttrHeaderText", "��������");
	 
	    UIManager.put("FileChooser.acceptAllFileFilterText", "��� �����");
	    choose.updateUI();
	}
}
