package nateOnChat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import lombok.Data;
import nateOnChat.interfaces.ProtocolImpl;

@Data
public class NateOnServer {

	// TODO 이름 중복 시 생성 안돼야함.
	// TODO 같은 방 생성 이후 방 생성 안됨

	private NateOnServerFrame frame;

	private ServerSocket serverSocket;
	private Socket socket;

	private String id;

	private JTextArea serverInfo;

	// 접속된 유저 벡터
	private Vector<ConnectedUser> connectedUsers = new Vector<>();

	// 만들어진 방 벡터
	private Vector<MyRoom> madeRooms = new Vector<>();

	// 방 만들기 같은 방 이름 체크
	private boolean roomCheck;

	// TODO 토크나이저 사용 변수
	private String protocol;
	private String from;
	private String message;

	public NateOnServer() {
		frame = new NateOnServerFrame(this);
		roomCheck = true;
		serverInfo = frame.getServerInfo();
	}

	public void startServer() {
		try {
			int portNum = Integer.parseInt(frame.getPortNum().getText().trim());
			serverSocket = new ServerSocket(portNum);
			serverInfoWriter(portNum + "포트 서버 시작");
			connectClient();
		} catch (IOException e) {
		}
	}

	public void serverInfoWriter(String str) {
		frame.getServerInfo().setText(frame.getServerInfo().getText() + "\n" + str);
	}

	private void connectClient() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {

						// 소켓 장치
						socket = serverSocket.accept();

						// 연결을 대기 하다가 유저가 들어오면 유저 생성, 소켓으로 유저 구분 가능.
						ConnectedUser user = new ConnectedUser(socket);
						user.start();
						
					} catch (IOException e) {
						// 서버 중지
						serverInfoWriter(frame.getPortNum().getText() + "번호 서버가 종료되었습니다.");
						break;

					}
				}
			}
		}).start();
	}

	private class ConnectedUser extends Thread implements ProtocolImpl {

		private Socket socket;

		private BufferedReader reader;
		private BufferedWriter writer;

		// 유저 정보
		private String id;
		private String myRoomName;

		// 사용자 전체에게 보내기 위한 Vector

		public ConnectedUser(Socket socket) {
			this.socket = socket;
			connectIO();
		}

		private void connectIO() {
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new BufferedWriter(new PrintWriter(socket.getOutputStream(), true));

				sendInfomation();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "서버 입출력 장치 에러!", "알림", JOptionPane.ERROR_MESSAGE);
				serverInfoWriter("[에러] 서버 입출력 장치 에러 ! !\n");
			}
		}

		private void sendInfomation() {
			try {
				id = reader.readLine();
				serverInfoWriter("[알림] " + id + "님이 접속하였습니다.");

				// 접속된 유저들에게 유저 명단 업데이트를 위한 출력
				newUser();

				// 방금 연결된 유저측에서 유저 명단 업데이트를 위한 출력
				connectedUser();

				// 방금 연결된 유저측에서 룸 명단 업데이트를 위한 출력
				madeRoom();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "접속 에러 !", "알림", JOptionPane.ERROR_MESSAGE);
				serverInfoWriter("[에러] 접속 에러 ! !\n");
			}
		}

		// 사용자가 보내는 메시지를 읽어서 다 뿌림
		@Override
		public void run() {
			try {
				while (true) {
					String str = reader.readLine();
					checkProtocol(str);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, id + "님 접속 종료", "알림", JOptionPane.ERROR_MESSAGE);
				serverInfoWriter("[알림] " + id + "님이 종료하였습니다.");
			}
			connectedUsers.remove(this);
			broadCast("UserOut/" + id);

		}

		// 사용자 모두에게 메세지를 보내는 메소드
		private void broadCast(String msg) {
			for (int i = 0; i < connectedUsers.size(); i++) {
				ConnectedUser user = connectedUsers.elementAt(i);
				user.writer(msg);
			}
		}

		// 프로토콜 인식 메소드
		private void checkProtocol(String str) {
			StringTokenizer tokenizer = new StringTokenizer(str, "/");

			protocol = tokenizer.nextToken();
			from = tokenizer.nextToken();

			if (protocol.equals("PublicMsg")) {
				message = tokenizer.nextToken();
				for (int i = 0; i < connectedUsers.size(); i++) {
					ConnectedUser user = connectedUsers.elementAt(i);
					user.writer(protocol + "/" + from + "/" + message);
				}

			} else if (protocol.equals("SecretMsg")) {
				message = tokenizer.nextToken();
				secretMessage();
			} else if (protocol.equals("MakeRoom")) {
				makeRoom();
			} else if (protocol.equals("OutRoom")) {
				outRoom();
			} else if (protocol.equals("EnterRoom")) {
				enterRoom();
			} else if (protocol.equals("RoomChat")) {
				message = tokenizer.nextToken();
				chatting(null);
			}

		}

		private void writer(String str) {
			try {
				writer.write(str + "\n");
				writer.flush();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "서버 출력 에러 !", "알림", JOptionPane.ERROR_MESSAGE);
			}
		}

		// 프로토콜별 가공하는 메소드
		@Override
		public void chatting(JTextArea messageBox) {
			serverInfoWriter("[메세지] " + from + "_" + message);

			for (int i = 0; i < madeRooms.size(); i++) {
				MyRoom myRoom = madeRooms.elementAt(i);

				if (myRoom.roomName.equals(from)) {
					myRoom.roomBroadCast("RoomChat/" + id + "/" + message);
				}
			}
		}

		@Override
		public void secretMessage() {
			for (int i = 0; i < connectedUsers.size(); i++) {
				ConnectedUser user = connectedUsers.elementAt(i);

				if (user.id.equals(from)) {
					user.writer("SecretMsg/" + id + "/" + message);
				}
			}
		}

		@Override
		public void makeRoom() {
			for (int i = 0; i < madeRooms.size(); i++) {
				MyRoom room = madeRooms.elementAt(i);

				if (room.roomName.equals(from)) {
					writer("FailMakeRoom/" + from);
					serverInfoWriter("[방 생성 실패]" + id + "_" + from);
					roomCheck = false;
				} else {
					roomCheck = true;
				}
			}

			if (roomCheck) {
				myRoomName = from;
				MyRoom myRoom = new MyRoom(from, this);
				madeRooms.add(myRoom);
				serverInfoWriter("[방 생성]" + id + "_" + from);

				newRoom();
				writer("MakeRoom/" + from);
			}
		}

		@Override
		public void madeRoom() {
			for (int i = 0; i < madeRooms.size(); i++) {
				MyRoom myRoom = madeRooms.elementAt(i);
				writer("MadeRoom/" + myRoom.roomName);
			}
		}

		@Override
		public void newRoom() {
			broadCast("NewRoom/" + from);
		}

		@Override
		public void outRoom() {
			for (int i = 0; i < madeRooms.size(); i++) {
				MyRoom myRoom = madeRooms.elementAt(i);

				if (myRoom.roomName.equals(from)) {
					myRoomName = null;
					serverInfoWriter("[방 퇴장]" + id + "_" + from);
					myRoom.removeRoom(this);
					writer("OutRoom/" + from);
				}
			}
		}

		@Override
		public void enterRoom() {
			for (int i = 0; i < madeRooms.size(); i++) {
				MyRoom myRoom = madeRooms.elementAt(i);

				if (myRoom.roomName.equals(from)) {
					// from 은 방 이름
					myRoomName = from;
					myRoom.addUser(this);
					myRoom.upDataRoomUserList("RoomList/");
					serverInfoWriter("[입장]" + from + " 방_" + id);
					writer("EnterRoom/" + from);
				}
			}
		}

		@Override
		public void newUser() {
			connectedUsers.add(this);
			broadCast("NewUser/" + id);
		}

		@Override
		public void connectedUser() {
			for (int i = 0; i < connectedUsers.size(); i++) {
				ConnectedUser user = connectedUsers.elementAt(i);
				writer("ConnectedUser/" + user.id);
			}

		}

	}

	private class MyRoom {

		private String roomName;

		// myRoom에 들어온 사람들의 정보가 담김.
		private Vector<ConnectedUser> myRoom = new Vector<>();
		private Vector<String> userName = new Vector<>();

		public MyRoom(String roomName, ConnectedUser connectedUser) {
			this.roomName = roomName;
			this.myRoom.add(connectedUser);
			connectedUser.myRoomName = roomName;
		}

		// 방에 있는 사람들에게 출력
		private void roomBroadCast(String msg) {
			for (int i = 0; i < myRoom.size(); i++) {
				ConnectedUser user = myRoom.elementAt(i);

				user.writer(msg);
			}
		}

		// 방에 있는 사람들에게 명단 업데이트를 해줌
		private void upDataRoomUserList(String roomList) {
			for (int i = 0; i < myRoom.size(); i++) {
				ConnectedUser user = myRoom.elementAt(i);
				for (int j = 0; j < myRoom.size(); j++) {
					ConnectedUser name = myRoom.elementAt(j);
					user.writer(roomList + name.id);
				}
			}
		}

		private void addUser(ConnectedUser connectedUser) {
			myRoom.add(connectedUser);
		}

		private void removeRoom(ConnectedUser user) {

			for (int i = 0; i < myRoom.size(); i++) {
				ConnectedUser outUser = myRoom.elementAt(i);
				for (int j = 0; j < myRoom.size(); j++) {
					ConnectedUser outUserName = myRoom.elementAt(j);
					outUser.writer("RemoveList/" + user.id);
				}
			}

			myRoom.remove(user);

			boolean empty = myRoom.isEmpty();

			if (empty) {
				for (int i = 0; i < madeRooms.size(); i++) {
					MyRoom myRoom = madeRooms.elementAt(i);

					if (myRoom.roomName.equals(roomName)) {
						madeRooms.remove(this);
						serverInfoWriter("[방 삭제]" + user.id + "_" + from);
						roomBroadCast("OutRoom/" + from);
						for (int j = 0; j < connectedUsers.size(); j++) {
							connectedUsers.get(j).writer("RemoveRoom/" + from);
						}
						break;
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		new NateOnServer();
	}
}
