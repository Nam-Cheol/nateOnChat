package nateOnChat_ver2.client.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import lombok.Data;
import nateOnChat_ver2.client.LineClient;

@Data
public class RoomPanel extends JPanel implements ActionListener{

	private LineClient mContext;

	// 백그라운드 이미지
	private Image backgroundImage;

	// 내부 패널
	private JPanel userListPanel;
	private JPanel roomListPanel;
	private JPanel roomBtnPanel;
	private SecretMsg secretMsgPanel;

	private JList<String> userList;
	private JList<String> roomList;

	// 생성 버튼
	private JButton makeRoomBtn;
	private JButton outRoomBtn;
	private JButton enterRoomBtn;
	private JButton secretMsgBtn;
	
	//개인 쪽지
	private JFrame messageFrame;

	private Vector<String> userIdVector = new Vector<>();
	private Vector<String> roomNameVector = new Vector<>();

	public RoomPanel(LineClient mContext) {
		this.mContext = mContext;
		initData();
		setInitLayout();
		addEventListener();
	}

	private void initData() {
		backgroundImage = new ImageIcon("img/ClientRoom.png").getImage();

		userListPanel = new JPanel();
		roomListPanel = new JPanel();
		roomBtnPanel = new JPanel();

		userList = new JList<>();
		roomList = new JList<>();

		makeRoomBtn = new JButton("방 생성");
		enterRoomBtn = new JButton("방 입장");
		outRoomBtn = new JButton("방 나가기");
		secretMsgBtn = new JButton("쪽  지");
	}

	private void setInitLayout() {
		setSize(getWidth(), getHeight());
		setLayout(null);

		userListPanel.setBounds(20, 30, 120, 250);
		userListPanel.setBackground(new Color(255,255,235));
		userListPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 3), "user"));
		userList.setListData(userIdVector);
		userList.setBackground(new Color(255,255,235));
		userListPanel.add(userList);
		add(userListPanel);

		roomListPanel.setBounds(145, 30, 300, 250);
		roomListPanel.setBackground(new Color(255,255,235));
		roomListPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 3), "room"));
		roomList.setListData(roomNameVector);
		roomList.setBackground(new Color(255,255,235));
		roomListPanel.add(roomList);
		add(roomListPanel);

		roomBtnPanel.setBounds(450, 160, 120, 120);
		roomBtnPanel.setBackground(new Color(255,255,235));
		roomBtnPanel.setBorder(new TitledBorder(new LineBorder(Color.lightGray, 3)));
		roomBtnPanel.setLayout(null);

		makeRoomBtn.setBackground(new Color(255,255,205));
		makeRoomBtn.setBounds(10, 10, 100, 20);
		makeRoomBtn.setBorder(new LineBorder(Color.lightGray, 2));
		makeRoomBtn.setFont(new Font("HY견고딕", Font.PLAIN, 12));

		enterRoomBtn.setBackground(new Color(255,255,205));
		enterRoomBtn.setBounds(10, 37, 100, 20);
		enterRoomBtn.setBorder(new LineBorder(Color.lightGray, 2));
		enterRoomBtn.setFont(new Font("HY견고딕", Font.PLAIN, 12));
		
		outRoomBtn.setBackground(new Color(255,255,205));
		outRoomBtn.setBounds(10, 64, 100, 20);
		outRoomBtn.setBorder(new LineBorder(Color.lightGray, 2));
		outRoomBtn.setFont(new Font("HY견고딕", Font.PLAIN, 12));
		outRoomBtn.setEnabled(false);

		secretMsgBtn.setBounds(10, 91, 100, 20);
		secretMsgBtn.setBackground(new Color(255,255,205));
		secretMsgBtn.setBorder(new LineBorder(Color.lightGray, 2));
		secretMsgBtn.setFont(new Font("HY견고딕", Font.PLAIN, 12));

		roomBtnPanel.add(makeRoomBtn);
		roomBtnPanel.add(outRoomBtn);
		roomBtnPanel.add(enterRoomBtn);
		roomBtnPanel.add(secretMsgBtn);
		add(roomBtnPanel);

	}

	private void addEventListener() {
		makeRoomBtn.addActionListener(this);
		outRoomBtn.addActionListener(this);
		enterRoomBtn.addActionListener(this);
		secretMsgBtn.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == makeRoomBtn) {
			String roomName = JOptionPane.showInputDialog("[ 방 이름 설정 ]");
			if (!roomName.equals(null)) {
				mContext.sendMakeRoomBtn(roomName);
			}
		} else if(e.getSource() == outRoomBtn) {
			String roomName = roomList.getSelectedValue();
			mContext.sendOutRoomBtn(roomName);
			roomList.setSelectedValue(null, false);
		} else if(e.getSource() == enterRoomBtn) {
			String roomName = roomList.getSelectedValue();
			mContext.sendEnterRoomBtn(roomName);
			roomList.setSelectedValue(null, false);
		} else if(e.getSource() == secretMsgBtn) {
			messageFrame = new JFrame("개인 쪽지");

            // MessagePanel 객체 생성 시 NateOnClient 객체 전달
            secretMsgPanel = new SecretMsg(mContext);

            // 프레임에 MessagePanel 추가
            messageFrame.getContentPane().add(secretMsgPanel);

            // 프레임 설정
            messageFrame.setSize(400,300);
            messageFrame.setVisible(true);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, getWidth() - 10, getHeight() - 10, null);
	}

}
