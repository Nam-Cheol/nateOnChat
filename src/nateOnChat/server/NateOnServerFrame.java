package nateOnChat.server;

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

public class NateOnServerFrame extends JFrame implements ActionListener {

	private NateOnServer mContext;
	
	private JLabel backgroundMap;
	
	private ScrollPane pane;
	private JTextArea serverInfo;
	
	private JTextField portNum;
	
	private JButton startButton;
	private JButton closeButton;
	
	private JLabel text;
	
	String info = null;

	public NateOnServerFrame(NateOnServer mContext) {
		this.mContext = mContext;
		initData();
		setInitLayout();
		addEventListener();
	}

	public void initData() {
		
		backgroundMap = new JLabel(new ImageIcon("img/nate.png"));
		serverInfo = new JTextArea();
		portNum = new JTextField(5);
		startButton = new JButton("서버 실행");
		closeButton = new JButton("서버 종료");
		text = new JLabel();
		pane = new ScrollPane();
		
		setTitle("NateOn Server Management");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(backgroundMap);
		setSize(400, 740);

		serverInfo.setSize(280, 350);
		serverInfo.setFocusable(false);
		pane.setBounds(50, 100, 280, 350);
		pane.add(serverInfo);
		
		portNum.setSize(100, 20);
		portNum.setLocation(180, 540);

		startButton.setSize(100, 50);
		startButton.setLocation(80, 600);
		startButton.setEnabled(false);

		closeButton.setLocation(200, 600);
		closeButton.setSize(100, 50);

		text.setLocation(80, 500);
		text.setSize(150, 100);
		text.setFont(new Font("궁서체", Font.BOLD, 13));
		text.setText("PORT NUMBER : ");
		
		// 실행 확인을 위한 세팅
		portNum.setText("50000");

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
