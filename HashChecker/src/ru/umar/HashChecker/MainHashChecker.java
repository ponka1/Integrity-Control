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
	    UIManager.put("FileChooser.openButtonText", "Открыть");
	    UIManager.put("FileChooser.cancelButtonText", "Отмена");
	    UIManager.put("FileChooser.lookInLabelText", "Смотреть в");
	    UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
	    UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файла");
	 
	    UIManager.put("FileChooser.saveButtonText", "Сохранить");
	    UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
	    UIManager.put("FileChooser.openButtonText", "Открыть");
	    UIManager.put("FileChooser.openButtonToolTipText", "Открыть");
	    UIManager.put("FileChooser.cancelButtonText", "Отмена");
	    UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");
	 
	    UIManager.put("FileChooser.lookInLabelText", "Папка");
	    UIManager.put("FileChooser.saveInLabelText", "Папка");
	    UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
	    UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов");
	 
	    UIManager.put("FileChooser.upFolderToolTipText", "На один уровень вверх");
	    UIManager.put("FileChooser.newFolderToolTipText", "Создание новой папки");
	    UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
	    UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
	    UIManager.put("FileChooser.fileNameHeaderText", "Имя");
	    UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
	    UIManager.put("FileChooser.fileTypeHeaderText", "Тип");
	    UIManager.put("FileChooser.fileDateHeaderText", "Изменен");
	    UIManager.put("FileChooser.fileAttrHeaderText", "Атрибуты");
	 
	    UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
	    choose.updateUI();
	}
}
