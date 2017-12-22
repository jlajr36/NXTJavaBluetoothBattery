package pkg;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NxtBattVoltage {

	private JFrame frame;
	private JTextField txtVoltage;
	private JButton btnReadBatt;
	private JComboBox comboPorts;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NxtBattVoltage window = new NxtBattVoltage();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public NxtBattVoltage() {
		initialize();
	}
	
	public void getBattVoltage() {
		byte[] cmd = {0x2,0x0,0x0,0xB};
		String port = comboPorts.getSelectedItem().toString();
		SerialPort comm = new SerialPort(port);
		try {
			comm.openPort();
			comm.writeBytes(cmd);
			Thread.sleep(500);
			byte[] data = comm.readBytes(7);
			if (data.length == 7) {
				float voltage = (data[5] & 0xff) + (data[6] & 0xff) * 265;
				txtVoltage.setText(String.valueOf(voltage) + " counts");
			} else {
				txtVoltage.setText("No Byte");
			}
			comm.closePort();
		} catch (SerialPortException e) {
			txtVoltage.setText("Error");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 290, 152);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("NXT Battery Voltage");
		frame.getContentPane().setLayout(null);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		btnReadBatt = new JButton("Read Voltage");
		btnReadBatt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread getBatThread = new Thread() {
					public void run() {
						getBattVoltage();
					}
				};
				getBatThread.start();
			}
		});
		btnReadBatt.setBounds(24, 62, 111, 23);
		frame.getContentPane().add(btnReadBatt);
		
		txtVoltage = new JTextField();
		txtVoltage.setBounds(156, 63, 94, 20);
		frame.getContentPane().add(txtVoltage);
		txtVoltage.setColumns(10);
		
		comboPorts = new JComboBox(SerialPortList.getPortNames());
		comboPorts.setBounds(24, 23, 111, 20);
		frame.getContentPane().add(comboPorts);
	}
}
