package nateOnChat.client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import nateOnChat.client.panel.IndexPanel;
import nateOnChat.client.panel.RoomMessagePanel;
import nateOnChat.client.panel.RoomPanel;
import nateOnChat.client.panel.WaitingMsgPanel;

public class NateOnClientFrame extends JFrame {
	
	private NateOnClient mContext;

	private JPanel background;
	
	// TODO 창을 관리하는 패널
	private JTabbedPane tabPane;
	
	// 로그인 창
	private IndexPanel indexPanel;
	
	// 방목록 창
	private RoomPanel roomPanel;
	
	// 대기실 창
	private WaitingMsgPanel waitingPanel;
	
	// 방 대화 창
	private RoomMessagePanel roomMsgPanel;
	
	public NateOnClientFrame(NateOnClient mContext) {
		this.mContext = mContext;
		initData();
		setInitLayout();
	}
	
	private void initData() {
		indexPanel = new IndexPanel(mContext, this);
		waitingPanel = new WaitingMsgPanel(mContext);
		roomPanel = new RoomPanel(mContext);
		roomMsgPanel = new RoomMessagePanel(mContext);
		tabPane = new JTabbedPane(JTabbedPane.TOP);
		background = new JPanel();
		
	}
	
	private void setInitLayout() {
		
		setTitle("네이트온에 오신 걸 환영합니다.");
		setSize(600, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		background.setBorder(new EmptyBorder(5,5,5,5));
		background.setLayout(null);
		setContentPane(background);
		
		tabPane.setBounds(0, 0, getWidth(), getHeight());
		background.add(tabPane);
		
		tabPane.addTab("로그인", null, indexPanel, null);
		
		tabPane.addTab("방목록", null, roomPanel, null);
		
		tabPane.addTab("대기실", null, waitingPanel, null);
		
		tabPane.addTab("방 대화", null, roomMsgPanel, null);
		
		tabPane.setEnabledAt(1, false);
		tabPane.setEnabledAt(2, false);
		tabPane.setEnabledAt(3, false);
		
		setVisible(true);

	}

	public IndexPanel getIndexPanel() {
		return indexPanel;
	}

	public WaitingMsgPanel getMessagePanel() {
		return waitingPanel;
	}
	
	public RoomPanel getRoomPanel() {
		return roomPanel;
	}

	public RoomMessagePanel getRoomMsgPanel() {
		return roomMsgPanel;
	}

	public JTabbedPane getTabPane() {
		return tabPane;
	}
	
}
