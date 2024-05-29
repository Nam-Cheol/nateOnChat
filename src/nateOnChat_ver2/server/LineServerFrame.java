package nateOnChat_ver2.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LineServerFrame extends JFrame implements ActionListener {

	private LineServer mContext;
	
	private JLabel backgroundMap;
	
	private ScrollPane pane;
	private JTextArea serverInfo;
	
	private JTextField portNum;
	
	private JButton startButton;
	private JButton closeButton;
	
	private JLabel text;
	
	String info = null;

	public LineServerFrame(LineServer mContext) {
		this.mContext = mContext;
		initData();
		setInitLayout();
		addEventListener();
	}

	public void initData() {
		
		backgroundMap = new JLabel(new ImageIcon("img/LineServer.gif"));
		serverInfo = new JTextArea();
		portNum = new JTextField(5);
		startButton = new JButton(new ImageIcon("img/ServerStart.png"));
		closeButton = new JButton(new ImageIcon("img/ServerEnd.png"));
		text = new JLabel();
		pane = new ScrollPane();
		
		setTitle("Line Server Management");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(backgroundMap);
		setSize(400, 740);

		serverInfo.setSize(280, 350);
		serverInfo.setFocusable(false);
		serverInfo.setBackground(new Color(255,255,235));
		pane.setBounds(52, 150, 280, 320);
		pane.add(serverInfo);
		
		portNum.setSize(100, 20);
		portNum.setBackground(new Color(255,255,235));
		portNum.setLocation(146, 500);

		startButton.setSize(100, 50);
		startButton.setLocation(75, 550);
		startButton.setEnabled(false);
		startButton.setBorderPainted(false);
		startButton.setContentAreaFilled(false);

		closeButton.setSize(100, 50);
		closeButton.setLocation(210, 550);
		closeButton.setBorderPainted(false);
		closeButton.setContentAreaFilled(false);

		text.setLocation(155, 440);
		text.setSize(150, 100);
		text.setFont(new Font("굴림체", Font.BOLD, 13));
		text.setText("PORT NUMBER");
		
		// 실행 확인을 위한 세팅
//		portNum.setText("50000");

	}

	public void setInitLayout() {
		setLayout(null);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);

		add(pane);
		add(portNum);
		add(startButton);
		add(closeButton);
		add(text);

	}

	public void addEventListener() {
		startButton.addActionListener(this);
		closeButton.addActionListener(this);

		portNum.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				
				char keyChar = e.getKeyChar();
				if (!Character.isDigit(keyChar) && keyChar != KeyEvent.VK_BACK_SPACE) {
					
					e.consume(); // 입력 취소
				}
				
				String text = portNum.getText();
				if (text.length() >= 5) {
					
					e.consume();
					
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String text = portNum.getText();
				if (text.isEmpty() || text.length() <= 3) {
					startButton.setEnabled(false);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				String text = portNum.getText();
				if (!text.isEmpty()) {
					if (text.length() >= 3) {
						startButton.setEnabled(true);
					}
				} else {
					startButton.setEnabled(false);
				}

			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton)e.getSource();
		
		if(b.equals(startButton)) {
			mContext.startServer();
			startButton.setEnabled(false);
			portNum.setEnabled(false);
			
		} else if (b.equals(closeButton)) {
			try {
				
				if(mContext.getSocket() != null) {
					mContext.getSocket().close();
				}
				
				mContext.getServerSocket().close();
				startButton.setEnabled(true);
				portNum.setEnabled(true);
				
			} catch (IOException e2) {
				JOptionPane.showMessageDialog(null, portNum.getText() + "서버가 종료되었습니다.", "알림", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
	}

	public JTextArea getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(JTextArea serverInfo) {
		this.serverInfo = serverInfo;
	}
	
	public JTextField getPortNum() {
		return portNum;
	}

}
