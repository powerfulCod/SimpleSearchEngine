package GUI;



import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JTextField;
import SearchComponents.SearchEngine;

import java.awt.Container;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Path;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class GuiEngine extends JFrame implements ActionListener {
	//test
	private Path dirPath;
	private JFrame frmSearchEngine;
	private JTextField directoryTextField, userQuery;
	private JPanel mainPanel, directoryPanel, userQueryPanel, indexStatisticsPanel;
	private JTextArea indexStatWindow;
	private JButton btnIndex, btnBack, btnChooseDirectory, btnSearch, btnViewIndexStatistics;
	private JButton btnBackToSearch;
	 /* Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE); 
				//Setting look and feel for window's versions;
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					
					GuiEngine window = new GuiEngine();
					window.frmSearchEngine.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiEngine() {
		initializeComponents();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeComponents() {
		frmSearchEngine = new JFrame("Search Engine");
		frmSearchEngine.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSearchEngine.getContentPane().setLayout(null);
		frmSearchEngine.setBounds(150, 150, 977, 518);
		initializePanels();
		initializeDirectoryComponenets();
		initializeUserQueryComponents();
		initializeIndexStatComponents();
		initializeMainPanelComponenets();

		frmSearchEngine.getContentPane().add(mainPanel, BorderLayout.CENTER);
		frmSearchEngine.setVisible(true);
	}
	
	private void initializePanels(){
		mainPanel = new JPanel();
		directoryPanel = new JPanel();
		directoryTextField = new JTextField();
		userQueryPanel = new JPanel();
		indexStatisticsPanel = new JPanel();
	}
	
	private void initializeMainPanelComponenets(){
		mainPanel.setBounds(0, 0, 960, 479);
		mainPanel.setLayout(new CardLayout());
		
		//Add panels to main panel
		mainPanel.add(directoryPanel, "directory_Panel");
		mainPanel.add(userQueryPanel, "userQuery_Panel");
		mainPanel.add(indexStatisticsPanel, "indexStatistics_Panel");
		mainPanel.validate();
	}
	
	private void initializeDirectoryComponenets(){
		directoryPanel.setBackground(Color.WHITE);
		directoryPanel.setLayout(null);
		
		//Create indexing button
		btnIndex = new JButton("Index");
		btnIndex.addActionListener(this);
		btnIndex.setBounds(500, 242, 128, 26);
		
		//Create directory text field
		directoryTextField.setBounds(284, 211, 404, 20);
		directoryPanel.add(directoryTextField);
		directoryTextField.setColumns(10);
		
		//Create choose directory button
		btnChooseDirectory = new JButton("Choose Directory");
		btnChooseDirectory.setBounds(348, 242, 128, 26);
		btnChooseDirectory.addActionListener(this);
		
		//Create label
		JLabel lblChooseADirectoy = new JLabel("Enter a directoy to index or click the Choose Directory button below");
		lblChooseADirectoy.setBounds(284, 185, 404, 14);
		
		//Add components to directory panel
		directoryPanel.add(lblChooseADirectoy);
		directoryPanel.add(btnChooseDirectory);
		directoryPanel.add(btnIndex);
	}
	
	private void initializeUserQueryComponents(){
		userQueryPanel.setBackground(Color.YELLOW);
		userQueryPanel.setLayout(null);
		
		//Create user query text field
		userQuery = new JTextField();
		userQuery.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() ==  KeyEvent.VK_ENTER){
					viewResults();
				}
			}
		});
		userQuery.setBounds(241, 218, 429, 20);
		userQuery.setColumns(10);
		
		//Create search button
		btnSearch = new JButton("Search");
		btnSearch.setBounds(675, 217, 89, 23);
		btnSearch.addActionListener(this);
		
		//Create index statistics button
		btnViewIndexStatistics = new JButton("View Index Statistics");
		btnViewIndexStatistics.setBounds(390, 249, 148, 23);
		btnViewIndexStatistics.addActionListener(this);
		
		//Create Goclongle label
		JLabel lblGoclongle = new JLabel("Goclongle");
		lblGoclongle.setBounds(402, 186, 106, 26);
		lblGoclongle.setFont(new Font("Bookman Old Style", Font.BOLD, 21));
		
		//Add components to user query panel
		userQueryPanel.add(lblGoclongle);
		userQueryPanel.add(userQuery);
		userQueryPanel.add(btnSearch);
		userQueryPanel.add(btnViewIndexStatistics);
		
		btnBack = new JButton("Back");
		btnBack.addActionListener(this);
		btnBack.setBounds(0, 0, 89, 23);
		userQueryPanel.add(btnBack);
	}
	
	private void initializeIndexStatComponents(){
		indexStatisticsPanel.setBackground(Color.WHITE);
		indexStatisticsPanel.setLayout(null);
		
		//Create back to search panel button
		btnBackToSearch = new JButton("Back to Search");
		btnBackToSearch.setBounds(0, 0, 960, 23);
		btnBackToSearch.addActionListener(this);
		indexStatisticsPanel.add(btnBackToSearch);
		
		//Create text area
		indexStatWindow = new JTextArea();
		indexStatWindow.setLineWrap(true);
		indexStatWindow.setBounds(0, 21, 960, 458);
		indexStatWindow.setFont(new Font("Arial", Font.PLAIN, 12));
		
		//Add components to index statistics panel
		indexStatisticsPanel.add(indexStatWindow);
	}
	
	private void switchPanels(JPanel curPanel, String layoutName){
		CardLayout cl = (CardLayout) (mainPanel.getLayout());
		cl.show(curPanel.getParent(), layoutName);
	}
	
	public Container getMainPanel(){
		return mainPanel;
	}
	
	public Container getDirectoryPanel(){
		return directoryPanel;
	}
	
	public Container getUserQueryPanel(){
		return userQueryPanel;
	}
	
	public Container getIndexStatisticPanel(){
		return indexStatisticsPanel;
	}
	

	public void actionPerformed(ActionEvent e){
		//Handle index button
		if(e.getSource() == btnIndex){
			//Pass directory path to index all text files
			try {
				SearchEngine.tempmain(dirPath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("not working");
			}
			//Maybe have a working progress bar?
			//Change frame to userQuery
			System.out.println("index change");
			switchPanels(userQueryPanel, "userQuery_Panel");
		}
		//Handle back button
		else if(e.getSource() == btnBack){
			switchPanels(directoryPanel, "directory_Panel");
		}
		//Handle choose directory button
		else if(e.getSource() == btnChooseDirectory){
			System.out.println("choose dir");
			dirPath = getDirectory(frmSearchEngine);
			directoryTextField.setText(dirPath.toString());
		}
		//Handle search button
		else if(e.getSource() == btnSearch){
			viewResults();
		}
		//Handle index statistics button
		else if(e.getSource() == btnViewIndexStatistics){
			indexStatWindow.append(directoryTextField.getText() + "\n");
			indexStatWindow.append(SearchEngine.getStatistics());
			switchPanels(indexStatisticsPanel, "indexStatistics_Panel");
		}
		//Handle back to search button
		else if(e.getSource() == btnBackToSearch){
			//go back to search
			switchPanels(userQueryPanel, "userQuery_Panel");
		}
	}
	
	private Path getDirectory(JFrame parent){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION){
			return fc.getSelectedFile().toPath().toAbsolutePath();
		}
		return null;
	}
	
	public void viewResults(){
		String word = userQuery.getText();
		SearchEngine.processQuery(word);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createResultWindow();
            }
        });
	}
	
	private void createResultWindow(){
    	JFrame frame = new JFrame("Results");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JComponent contentPane = new resultWindow();
		contentPane.setOpaque(true);
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(true);
	}
}
