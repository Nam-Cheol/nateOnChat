package nateOnChat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import lombok.Data;
import nateOnChat.interfaces.ProtocolImpl;

@Data
public class NateOnClient implements ProtocolImpl {

	// 프레임 창
	private NateOnClientFrame clientFrame;

	// 소켓 장치
	private Socket socket;

	// 입출력 장치
	private BufferedReader reader;
	private BufferedWriter writer;

	// 유저 정보
	private String ip;
	private int port;
	private String id;
	private String myRoomName;

	// TODO 토크나이저 사용 변수
	private String protocol;
	private String from;
	private String message;

	// TODO 클라이언트로 가져오기 위한 변수
	
	// 접속자 명단(userList), 방 명단(roomList), 방에 참여한 유저(roomUserList)를 업데이트 하기 위한 문자열 벡터
	private JList<String> userList;
	private Vector<String> userIdList = new Vector<>();

	private JList<String> roomList;
	private Vector<String> roomNameList = new Vector<>();

	private JList<String> roomUserList;
	private Vector<String> roomUserIdList = new Vector<>();

	// 메세지 업데이트를 위한 박스
	private JTextArea mainMessageBox;
	private JTextArea roomMessageBox;
	
	// 버튼을 활성화, 비활성화 하기 위한 변수
	private JButton makeRoomBtn;
	private JButton enterRoomBtn;
	private JButton outRoomBtn;

	public NateOnClient() {
		clientFrame = new NateOnClientFrame(this);

		userList = clientFrame.getRoomPanel().getUserList();
		userIdList = clientFrame.getRoomPanel().getUserIdVector();

		roomList = clientFrame.getRoomPanel().getRoomList();
		roomNameList = clientFrame.getRoomPanel().getRoomNameVector();

		roomUserList = clientFrame.getRoomMsgPanel().getRoomUserList();
		roomUserIdList = clientFrame.getRoomMsgPanel().getRoomUserIdVector();

		mainMessageBox = clientFrame.getMessagePanel().getMainMessageBox();
		roomMessageBox = clientFrame.getRoomMsgPanel().getMainMessageBox();

		makeRoomBtn = clientFrame.getRoomPanel().getMakeRoomBtn();
		enterRoomBtn = clientFrame.getRoomPanel().getEnterRoomBtn();
		outRoomBtn = clientFrame.getRoomPanel().getOutRoomBtn();
	}

	// 서버와 연결
	public void connectNetwork() {
		try {
			ip = clientFrame.getIndexPanel().getInputIp().getText();
			port = Integer.parseInt(clientFrame.getIndexPanel().getInputPort().getText().trim());
			id = clientFrame.getIndexPanel().getInputId().getText().trim();

			socket = new Socket(ip, port);
			JOptionPane.showMessageDialog(null, "서버 접속 완료", "알림", JOptionPane.INFORMATION_MESSAGE);

		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "접속 에러 !", "알림", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			clientFrame.getIndexPanel().getInputIp().setEnabled(true);
			clientFrame.getIndexPanel().getInputPort().setEnabled(true);
			clientFrame.getIndexPanel().getInputId().setEnabled(true);
			JOptionPane.showMessageDialog(null, "접속 에러 !", "알림", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public void connectIO() {
		try {
			// 입출력 장치
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new PrintWriter(socket.getOutputStream()));

			// 입력 스레드
			readThread();
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "클라이언트 입출력 장치 에러 !", "알림", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "클라이언트 입출력 장치 에러 !", "알림", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 서버에서 데이터를 여기에서 받음.
	private void readThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {

						// 읽을 준비를 하고 있다
						String msg = reader.readLine();
						checkProtocol(msg);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "클라이언트 입력 장치 에러 !", "알림", JOptionPane.ERROR_MESSAGE);
						break;
					}
				}
			}
		}).start();
	}

	// 여기에서 서버로 데이터를 보냄.
	public void writer(String str) {
		try {
			writer.write(str + "\n");
			writer.flush();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "클라이언트 출력 장치 에러 !", "알림", JOptionPane.ERROR_MESSAGE);
		}

	}

	// 프로토콜 인식 메소드
	private void checkProtocol(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str, "/");

		protocol = tokenizer.nextToken();
		from = tokenizer.nextToken();

		if (protocol.equals("NewUser")) {
			
			newUser();
			
		} else if (protocol.equals("ConnectedUser")) {
			
			connectedUser();

		} else if (protocol.equals("UserOut")) {
			
			userIdList.remove(from);
			userList.setListData(userIdList);
			
		} else if (protocol.equals("PublicMsg")) {
			
			message = tokenizer.nextToken();
			chatting(mainMessageBox);
			
		} else if (protocol.equals("SecretMsg")) {
			
			message = tokenizer.nextToken();
			secretMessage();

		} else if (protocol.equals("MakeRoom")) {
			
			makeRoom();
			clientFrame.getTabPane().setEnabledAt(3, true);

		} else if (protocol.equals("NewRoom")) {
			
			newRoom();

		} else if (protocol.equals("MadeRoom")) {
			
			madeRoom();

		} else if (protocol.equals("OutRoom")) {
			
			outRoom();

		} else if (protocol.equals("EnterRoom")) {
			
			enterRoom();
			clientFrame.getTabPane().setEnabledAt(3, true);

		} else if (protocol.equals("RoomList")) {
			
			if (!roomUserIdList.contains(from)) {
				roomUserIdList.add(from);
				roomUserList.setListData(roomUserIdList);
			}
			
		} else if (protocol.equals("RemoveList")) {
			
			if (roomUserIdList.contains(from)) {
				roomUserIdList.remove(from);
				roomUserList.setListData(roomUserIdList);
			}
			
		} else if (protocol.equals("RoomChat")) {
			
			message = tokenizer.nextToken();
			chatting(roomMessageBox);
			
		} else if (protocol.equals("RemoveRoom")) {
			
			if (roomNameList.contains(from)) {
				roomNameList.remove(from);
				roomList.setListData(roomNameList);
			}
			
		} else if (protocol.equals("FailMakeRoom")) {
			
			JOptionPane.showMessageDialog(null, "같은 이름의 방이 존재합니다.", "[알림]", JOptionPane.ERROR_MESSAGE);
			
		}

	}

	public void sendMessageBtn(String messageText) {
		writer("PublicMsg/" + id + "/" + messageText);
	}

	public void sendSecretMessageBtn(String msg) {
		String user = (String) clientFrame.getRoomPanel().getUserList().getSelectedValue();
		writer("SecretMsg/" + user + "/" + msg);
	}

	public void sendMakeRoomBtn(String roomName) {
		writer("MakeRoom/" + roomName);
	}

	public void sendOutRoomBtn(String roomName) {
		writer("OutRoom/" + roomName);
	}

	public void sendEnterRoomBtn(String roomName) {
		writer("EnterRoom/" + roomName);
	}

	public void sendRoomChatBtn(String message) {
		writer("RoomChat/" + myRoomName + "/" + message);
	}

	@Override
	public void chatting(JTextArea messageBox) {
		if (from.equals(id)) {
			messageBox.setText(messageBox.getText() + "나 ▷ " + message + "\n");
		} else {
			messageBox.setText(messageBox.getText() + from + " ▶ " + message + "\n");
		}
	}

	@Override
	public void secretMessage() {
		JOptionPane.showMessageDialog(null, from + "님의 메세지\n\"" + message + "\"", "[비밀메세지]", JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void makeRoom() {
		myRoomName = from;
		roomUserIdList.add(id);
		roomUserList.setListData(roomUserIdList);
		makeRoomBtn.setEnabled(false);
		enterRoomBtn.setEnabled(false);
		outRoomBtn.setEnabled(true);
	}

	@Override
	public void madeRoom() {
		roomNameList.add(from);
		roomList.setListData(roomNameList);
		if (!(roomNameList.size() == 0)) {
			roomList.setListData(roomNameList);
		}
	}

	@Override
	public void newRoom() {
		roomNameList.add(from);
		roomList.setListData(roomNameList);
	}

	@Override
	public void outRoom() {
		myRoomName = null;
		makeRoomBtn.setEnabled(true);
		enterRoomBtn.setEnabled(true);
		outRoomBtn.setEnabled(false);
		roomUserIdList.clear();
		roomMessageBox.setText(null);
		clientFrame.getTabPane().setEnabledAt(3, false);
	}

	@Override
	public void enterRoom() {
		myRoomName = from;
		makeRoomBtn.setEnabled(false);
		enterRoomBtn.setEnabled(false);
		outRoomBtn.setEnabled(true);
	}

	@Override
	public void newUser() {
		if (!from.equals(this.id)) {
			userIdList.add(from);
			userList.setListData(userIdList);
		}
	}

	@Override
	public void connectedUser() {
		userIdList.add(from);
		userList.setListData(userIdList);
	}

	public static void main(String[] args) {
		new NateOnClient();
	}

}
