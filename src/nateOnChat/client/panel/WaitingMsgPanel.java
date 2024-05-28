package nateOnChat.client.panel;

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

import lombok.Data;
import nateOnChat.client.NateOnClient;

@Data
public class WaitingMsgPanel extends JPanel {

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

	public WaitingMsgPanel(NateOnClient mContext) {
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

		mainPanel.setBounds(30, 20, 400, 250);
		mainPanel.setLayout(null);
		mainPanel.setBackground(Color.LIGHT_GRAY);
		mainPanel.setBorder(new TitledBorder(new LineBorder(Color.white, 3), "Message"));
		mainPanel.add(scrollPane);
		scrollPane.setBounds(8, 15, 383, 225);
		scrollPane.add(mainMessageBox);
		mainMessageBox.setFocusable(false);
		add(mainPanel);

		sendMessageBtn.setBackground(Color.lightGray);
		sendMessageBtn.setSize(60, 30);
		sendMessageBtn.setLocation(340, 0);
		sendMessageBtn.setText("전송");
		sendMessageBtn.setFont(new Font("HY견고딕", Font.PLAIN, 12));
		sendMessageBtn.setEnabled(false);
		
		writeMessageBox.setSize(340, 30);
		writeMessageBox.setBackground(Color.white);
		
		bottomPanel.setBounds(30, 270, 400, 30);
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
				mContext.sendMessageBtn(message);
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
					mContext.sendMessageBtn(message);
					writeMessageBox.setText(null);
				}
			}
		});
	}

	public void sendRule() {
		if (writeMessageBox.getText().length() < 1) {
			sendMessageBtn.setEnabled(false);
		} else {
			sendMessageBtn.setEnabled(true);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth() - 10, getHeight() - 10, null);
	}

}
