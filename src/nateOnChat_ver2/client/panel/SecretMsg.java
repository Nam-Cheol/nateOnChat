package nateOnChat_ver2.client.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import nateOnChat_ver2.client.NateOnClient;

public class SecretMsg extends JPanel {

	private NateOnClient mContext;

	// 백그라운드 이미지
	private Image backgroundImage;
	private JPanel backgroundPanel;

	// 내부 패널
	private JPanel mainPanel;
	private JPanel bottomPanel;

	// 스크롤
	private ScrollPane scrollPane;

	// 텍스트 컴포넌트
	private JTextArea mainMessageBox;
	private JTextField writeMessageBox;

	// 메세지 보내기 버튼
	private JButton sendMessageBtn;

	// 메세지를 담는 변수
	String message;

	public SecretMsg(NateOnClient mContext) {
		this.mContext = mContext;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		backgroundImage = new ImageIcon("img/clientNate.png").getImage();
		backgroundPanel = new JPanel();

		mainPanel = new JPanel();
		bottomPanel = new JPanel();

		scrollPane = new ScrollPane();

		mainMessageBox = new JTextArea();
		writeMessageBox = new JTextField(17);
		sendMessageBtn = new JButton();
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null);

		backgroundPanel.setSize(getWidth(), getHeight());
		backgroundPanel.setLayout(null);
		add(backgroundPanel);

		sendMessageBtn.setBackground(Color.lightGray);
		sendMessageBtn.setSize(60, 30);
		sendMessageBtn.setLocation(240, 170);
		sendMessageBtn.setText("전송");
		sendMessageBtn.setFont(new Font("HY견고딕", Font.PLAIN, 12));
		sendMessageBtn.setEnabled(false);
		
		writeMessageBox.setSize(300, 170);
		writeMessageBox.setBackground(Color.white);
		
		bottomPanel.setBounds(10, 30, 300, 200);
		bottomPanel.setLayout(null);
		bottomPanel.setBackground(Color.white);
		bottomPanel.setBorder(new TitledBorder(new LineBorder(Color.black, 1)));
		bottomPanel.add(writeMessageBox);
		bottomPanel.add(sendMessageBtn);
		add(bottomPanel);
	}

	private void addEventListener() {
		sendMessageBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendRule();
				message = writeMessageBox.getText();
				mContext.sendSecretMessageBtn(message);
				mContext.getClientFrame().getRoomPanel().getMessageFrame().setVisible(false);
				writeMessageBox.setText(null);
			}
		});

		writeMessageBox.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				sendRule();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				sendRule();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				sendRule();
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					message = writeMessageBox.getText();
					mContext.sendSecretMessageBtn(message);
					mContext.getClientFrame().getRoomPanel().getMessageFrame().setVisible(false);
					writeMessageBox.setText(null);
				}
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth() - 10, getHeight() - 10, null);
	}

	public void sendRule() {
		if (writeMessageBox.getText().length() < 1) {
			sendMessageBtn.setEnabled(false);
		} else {
			sendMessageBtn.setEnabled(true);
		}
	}

}
