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
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import lombok.Data;
import nateOnChat_ver2.client.LineClient;

@Data
public class RoomMessagePanel extends JPanel {

	private LineClient mContext;

	// 백그라운드 이미지
	private Image backgroundImage;
	private JPanel backgroundPanel;

	// 내부 패널
	private JPanel mainPanel;
	private JPanel bottomPanel;
	private JPanel userPanel;
	private JPanel imgPanel;

	// 스크롤
	private ScrollPane scrollPane;

	// 텍스트 컴포넌트
	private JTextArea mainMessageBox;
	private JTextField writeMessageBox;

	// 메세지 보내기 버튼
	private JButton sendMessageBtn;

	// 파일 다운로드, 업로드 버튼
	private JButton downloadImgBtn;
	private JButton uploadImgBtn;

	// 메세지를 담는 변수
	String message;

	// 방에 참여한 유저
	private JList<String> roomUserList;
	private Vector<String> roomUserIdVector = new Vector<>();

	public RoomMessagePanel(LineClient mContext) {
		this.mContext = mContext;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		backgroundImage = new ImageIcon("img/ClientRoom.png").getImage();
		backgroundPanel = new JPanel();

		mainPanel = new JPanel();
		bottomPanel = new JPanel();
		userPanel = new JPanel();
		imgPanel = new JPanel();

		roomUserList = new JList<>();

		scrollPane = new ScrollPane();

		mainMessageBox = new JTextArea();
		writeMessageBox = new JTextField(17);
		sendMessageBtn = new JButton(new ImageIcon("img/SendBtn.png"));
		
		downloadImgBtn = new JButton(new ImageIcon("img/download.png"));
		uploadImgBtn = new JButton(new ImageIcon("img/upload.png"));
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null);

		backgroundPanel.setSize(getWidth(), getHeight());
		backgroundPanel.setLayout(null);
		add(backgroundPanel);

		mainPanel.setBounds(30, 20, 400, 250);
		mainPanel.setLayout(null);
		mainPanel.setBackground(new Color(255,255,235));
		mainPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 3), "Message"));
		mainPanel.add(scrollPane);
		scrollPane.setBounds(8, 15, 383, 225);
		scrollPane.add(mainMessageBox);
		mainMessageBox.setFocusable(false);
		mainMessageBox.setBackground(new Color(255,255,235));
		add(mainPanel);

		sendMessageBtn.setBackground(Color.lightGray);
		sendMessageBtn.setSize(60, 30);
		sendMessageBtn.setLocation(340, 0);
		sendMessageBtn.setBorderPainted(false);
		sendMessageBtn.setEnabled(false);

		writeMessageBox.setSize(340, 30);
		writeMessageBox.setBackground(new Color(255,255,235));

		bottomPanel.setBounds(30, 270, 400, 30);
		bottomPanel.setLayout(null);
		bottomPanel.setBackground(new Color(255,255,235));
		bottomPanel.add(writeMessageBox);
		bottomPanel.add(sendMessageBtn);
		add(bottomPanel);

		userPanel.setBounds(450, 100, 110, 180);
		userPanel.setBackground(new Color(255,255,235));
		userPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 3), "User"));
		roomUserList.setListData(roomUserIdVector);
		roomUserList.setBackground(new Color(255,255,235));
		userPanel.add(roomUserList);
		add(userPanel);
		
		downloadImgBtn.setBackground(Color.LIGHT_GRAY);
		downloadImgBtn.setSize(42, 32);
		downloadImgBtn.setLocation(0, 0);
		downloadImgBtn.setOpaque(false);
		downloadImgBtn.setBorderPainted(false);
		downloadImgBtn.setEnabled(false);
		
		uploadImgBtn.setBackground(Color.LIGHT_GRAY);
		uploadImgBtn.setSize(42, 32);
		uploadImgBtn.setLocation(0, 33);
		uploadImgBtn.setOpaque(false);
		uploadImgBtn.setBorderPainted(false);
		
		imgPanel.setBounds(430, 30, 42, 66);
		imgPanel.setLayout(null);
		imgPanel.setOpaque(false);
		imgPanel.add(downloadImgBtn);
		imgPanel.add(uploadImgBtn);
		add(imgPanel);
	}

	private void addEventListener() {
		sendMessageBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sendRule();
				sendMessage();
			}
		});
		
		uploadImgBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String imgPath = JOptionPane.showInputDialog("보낼 이미지 링크를 입력하세요.");
				mContext.sendRoomImageUploadBtn(imgPath);
			}
		});
		
		downloadImgBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(downloadImgBtn.isEnabled()) {
					mContext.viewImage(downloadImgBtn, mContext.getRoomImage());
				}
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
					sendMessage();
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

	private void sendMessage() {
		if (!writeMessageBox.getText().equals(null)) {
			String msg = writeMessageBox.getText();
			mContext.sendRoomChatBtn(msg);
			writeMessageBox.setText(null);
			writeMessageBox.requestFocus();
		}
	}

}
