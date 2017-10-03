package tutorial;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.fazecast.jSerialComm.SerialPort;

import models.TimedRun;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JTextArea;
import javax.swing.JSeparator;

public class jframe extends JFrame {

	private JPanel contentPane;
	private Integer time = 0 ;
	private TimedRun runner;
	private static Integer runnerID = 0;
	private JLabel lbltimer;
	private static ArrayList<TimedRun> topScore = new ArrayList<TimedRun>();
	
	private static String fileName = "HelicopterDrops.csv";
	private static JTextArea txtrItem;



	//Timer
	private boolean readyForNextRunner = true;
	private boolean stoppedTime = true;
	private Timer t;

	//Menu
	private JMenu mnConnections;
	private JMenuItem mntmRefreshlist;


	//Arduino 
	private SerialPort chosenPort;

	private ArrayList<TimedRun> runs = new ArrayList<TimedRun>();
	private JButton btnNextRunner;
	private Date StartTime;
	private Date EndTime;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		ReadRunnersFromFile(fileName);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					jframe frame = new jframe();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public jframe() {
		InitalizeComponents();
		InitalizeEvents();
		RefreshConnectionList();
		UpdateTopScore();
	}

	private static void ReadRunnersFromFile(String file) {
		try {
			String line = null;
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null) {
				String[] args = line.split(",");
				int id = Integer.parseInt(args[0]);
				
				if(runnerID < id)
					runnerID = id;
				topScore.add(new TimedRun(id ,Long.parseLong(args[1])));
			}   
			
			
		} catch (FileNotFoundException e) {
			File newFile = new File(file);
			try {
				newFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private static void UpdateTopScore(){
		
		Collections.sort(topScore, Collections.reverseOrder());
		
		String scores = "";
		int range = Math.min(topScore.size(), 5);
		for(Integer i = 0 ; i < range; ++i)
		{
			scores += (i+1) + ":" + "  " + topScore.get(i) + "\n";
		}
		txtrItem.setText(scores);
		
	}
	
	
	private void WriteToFile(String fileName, TimedRun tr)
	{
		try {
			FileWriter fout = new FileWriter(fileName, true);
			fout.write(tr.toFile());
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void InitalizeEvents() {
		mntmRefreshlist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RefreshConnectionList();
			}
		});
		btnNextRunner.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readyForNextRunner = true;
				lbltimer.setText("00:000");
			}
		});

	}

	private void InitalizeComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 600);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnConnections = new JMenu("Connections");
		menuBar.add(mnConnections);



		mntmRefreshlist = new JMenuItem("RefreshList");

		mnConnections.add(mntmRefreshlist);
		contentPane = new JPanel();
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		lbltimer = new JLabel("00:000");
		lbltimer.setFont(new Font("OCR A Std", Font.PLAIN, 150));
		lbltimer.setForeground(Color.RED);

		btnNextRunner = new JButton("Next Runner");

		btnNextRunner.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(54)
							.addComponent(lbltimer, GroupLayout.PREFERRED_SIZE, 669, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(299)
							.addComponent(btnNextRunner, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 422, GroupLayout.PREFERRED_SIZE)
					.addGap(18))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(101)
							.addComponent(lbltimer)
							.addGap(40)
							.addComponent(btnNextRunner, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(32)
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)))
					.addContainerGap())
		);
		
		JLabel lblTopScore = new JLabel("Top Score");
		lblTopScore.setFont(new Font("OCR A Std", Font.PLAIN, 40));
		lblTopScore.setForeground(Color.RED);
		
		txtrItem = new JTextArea();

		txtrItem.setBackground(Color.BLACK);
		txtrItem.setFont(new Font("OCR A Std", Font.PLAIN, 30));
		txtrItem.setForeground(Color.RED);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.RED);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(81)
					.addComponent(lblTopScore)
					.addContainerGap(71, Short.MAX_VALUE))
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap(69, Short.MAX_VALUE)
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(txtrItem, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
						.addComponent(separator, GroupLayout.PREFERRED_SIZE, 291, GroupLayout.PREFERRED_SIZE))
					.addGap(62))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(23)
					.addComponent(lblTopScore)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(txtrItem, GroupLayout.PREFERRED_SIZE, 333, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(67, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);

	}

	//Helpers
	protected void RefreshConnectionList() {
		mnConnections.removeAll();


		SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++){

			final String portName = portNames[i].getSystemPortName();
			final JRadioButtonMenuItem rdbtnmntmNewRadioItem = new JRadioButtonMenuItem(portName);

			mnConnections.add(rdbtnmntmNewRadioItem);

			CreateArduinoConnection(rdbtnmntmNewRadioItem, portName);
		}

		mnConnections.add(mntmRefreshlist);
	}

	protected void CreateArduinoConnection(final JRadioButtonMenuItem rdbtnmntmNewRadioItem, final String portName) {
		rdbtnmntmNewRadioItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chosenPort = SerialPort.getCommPort(portName);
				chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

				if(chosenPort.openPort()) {
					rdbtnmntmNewRadioItem.setSelected(true);
					System.out.println("Success connect");
					StartThreadedConnection();
				}
				else{
					chosenPort.closePort();
					System.out.println("Bad connect");
				}
			}
		});

	}

	protected void StartThreadedConnection() {
		Thread thread = new Thread(){

			@Override public void run() {
				Scanner scanner = new Scanner(chosenPort.getInputStream());
				while(scanner.hasNextLine()) {
					try {
						String line = scanner.nextLine();
						int number = Integer.parseInt(line);
						if (number == 1 && readyForNextRunner)
						{
							stoppedTime = false;
							readyForNextRunner = false;
							runner = new TimedRun(++runnerID);
							StartTime = new Date();
							t = new Timer(1, new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {
									runner.milliseconds++;
									lbltimer.setText(runner.toDisplay());
								}
							});
							t.start();
						}
						else if(number == 2)
						{
							EndTime = new Date();
							if(!stoppedTime){
								stoppedTime = true;
								runner.milliseconds = (EndTime.getTime() - StartTime.getTime());
								topScore.add(runner);
								WriteToFile(fileName, runner);
								UpdateTopScore();
								lbltimer.setText(runner.toDisplay());
								System.out.println("Total System Time:" +  runner.milliseconds);
							}
							t.stop();
						}
						else
						{
							System.out.println("Arduino delay:" + number);
						}
					} catch(Exception e) {}
				}
				scanner.close();
			}
		};
		thread.start();
	}
}
