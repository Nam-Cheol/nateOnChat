package nateOnChat_ver2.client.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import lombok.Data;
import nateOnChat_ver2.client.LineClient;
import nateOnChat_ver2.client.LineClientFrame;

@Data
public class IndexPanel extends JPanel {

	private LineClient mContext;

	private LineClientFrame frame;

	// 백그라운드 이미지
	private Image backgroundImage;
	private JPanel backgroundPanel;

	// 내부 패널
	private JPanel borderPanel;

	// ip 컴포넌트
	private JPanel ipPanel;
	private JLabel ipLabel;
	private JTextField inputIp;

	// port 컴포넌트
	private JPanel portPanel;
	private JLabel portLabel;
	private JTextField inputPort;

	// id 컴포넌트
	private JPanel idPanel;
	private JLabel idLabel;
	private JTextField inputId;

	// 로그인 버튼
	private JButton loginBtn;
	
	public IndexPanel(LineClient mContext, LineClientFrame frame) {
		this.mContext = mContext;
		this.frame = frame;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		backgroundImage = new ImageIcon("img/ClientLogin.png").getImage();
		backgroundPanel = new JPanel();

		borderPanel = new JPanel();

		// IP 컴포넌트
		ipPanel = new JPanel();
		ipLabel = new JLabel("HOST IP");
		inputIp = new JTextField(10);

		// PORT 컴포넌트
		portPanel = new JPanel();
		portLabel = new JLabel("PORT NUMBER");
		inputPort = new JTextField(10);

		// ID 컴포넌트
		idPanel = new JPanel();
		idLabel = new JLabel("ID");
		inputId = new JTextField(10);

		// 로그인 버튼
		loginBtn = new JButton("Login");

		// 실행 확인을 위한 세팅
//		inputIp.setText("localhost");
//		inputPort.setText("50000");
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null);

		backgroundPanel.setSize(getWidth(), getHeight());
		backgroundPanel.setLayout(null);
		add(backgroundPanel);

		borderPanel.setBounds(140, 90, 300, 200);
		borderPanel.setLayout(null);
		borderPanel.setBackground(new Color(255,255,235));
		borderPanel.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY, 3), "Login"));
		add(borderPanel);

		// IP 컴포넌트
		ipPanel.setBounds(10, 10, 120, 100);
		ipPanel.setBackground(new Color(0, 0, 0, 0));
		ipPanel.add(ipLabel);
		ipPanel.add(inputIp);
		borderPanel.add(ipPanel);

		// PORT 컴포넌트
		portPanel.setBounds(10, 70, 120, 100);
		portPanel.setBackground(new Color(0, 0, 0, 0));
		portPanel.add(portLabel);
		portPanel.add(inputPort);
		borderPanel.add(portPanel);

		// IP 컴포넌트
		idPanel.setBounds(10, 130, 120, 100);
		idPanel.setBackground(new Color(0, 0, 0, 0));
		idPanel.add(idLabel);
		idPanel.add(inputId);
		borderPanel.add(idPanel);

		// LoginBtn 컴포넌트
		loginBtn.setBackground(Color.WHITE);
		loginBtn.setBounds(200, 140, 80, 40);
		borderPanel.add(loginBtn);
		loginBtn.setEnabled(false);
	}

	private void addEventListener() {

		loginBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				accessServer();
			}
		});

		loginBtn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (loginBtn.isEnabled()) {
						clickLoginBtn();
					}
				}
			}
		});

		inputPort.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				char keyChar = e.getKeyChar();
				if (!Character.isDigit(keyChar) && keyChar != KeyEvent.VK_BACK_SPACE) {
					e.consume(); // 입력 취소
				}
				String text = inputPort.getText();
				if (text.length() >= 5) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String text = inputPort.getText();
				if (text.isEmpty() || text.length() <= 3) {
					loginBtn.setEnabled(false);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				String text = inputPort.getText();
				if (!text.isEmpty()) {
					if (text.length() >= 3) {
						loginBtn.setEnabled(true);
					}
				} else {
					loginBtn.setEnabled(false);
				}

			}
		});

		inputId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (inputId.getText().length() >= 1) {
					loginBtn.setEnabled(true);
				} else {
					loginBtn.setEnabled(false);
				}

			}
			@Override
			public void keyPressed(KeyEvent e) {
				if(loginBtn.isEnabled() == true) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						accessServer();
					}
				}
			}
		});
		
	}

	private void clickLoginBtn() {
		if ((!inputIp.getText().equals(null)) && (!inputPort.getText().equals(null))
				&& (!inputId.getText().equals(null))) {
			new Thread(() -> {
				mContext.setId(inputId.getText());
				mContext.getClientFrame().setTitle(mContext.getId() + "님, 환영합니다");
				mContext.connectNetwork();
				mContext.connectIO();
				mContext.writer(inputId.getText().trim());
			}).start();

		} else {
			JOptionPane.showMessageDialog(null, "입력한 정보를 확인하세요", "알림", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void accessServer() {
		if (loginBtn.isEnabled()) {
			clickLoginBtn();
			inputIp.setEnabled(false);
			inputPort.setEnabled(false);
			inputId.setEnabled(false);
			loginBtn.setEnabled(false);
			frame.getTabPane().setEnabledAt(1, true);
			frame.getTabPane().setEnabledAt(2, true);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth()-10, getHeight()-10, null);
	}
}
