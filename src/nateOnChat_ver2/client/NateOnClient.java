package nateOnChat_ver2.client;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import lombok.Data;
import nateOnChat_ver2.interfaces.ProtocolImpl;

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
	private String imagePath;

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
	private JButton downloadImgBtn;
	private JButton roomDownloadImgBtn;

	// 사진 저장을 위한 이미지
	private Image image;

	public NateOnClient() {
		clientFrame = new NateOnClientFrame(this);

		userList = clientFrame.getRoomPanel().getUserList();
		userIdList = clientFrame.getRoomPanel().getUserIdVector();

		roomList = clientFrame.getRoomPanel().getRoomList();
		roomNameList = clientFrame.getRoomPanel().getRoomNameVector();

		roomUserList = clientFrame.getRoomMsgPanel().getRoomUserList();
		roomUserIdList = clientFrame.getRoomMsgPanel().getRoomUserIdVector();

		mainMessageBox = clientFrame.getWaitingPanel().getMainMessageBox();
		roomMessageBox = clientFrame.getRoomMsgPanel().getMainMessageBox();

		makeRoomBtn = clientFrame.getRoomPanel().getMakeRoomBtn();
		enterRoomBtn = clientFrame.getRoomPanel().getEnterRoomBtn();
		outRoomBtn = clientFrame.getRoomPanel().getOutRoomBtn();
		downloadImgBtn = clientFrame.getWaitingPanel().getDownloadImgBtn();
		roomDownloadImgBtn = clientFrame.getRoomMsgPanel().getDownloadImgBtn();
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
			chatting(mainMessageBox, null);

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
			chatting(roomMessageBox, null);

		} else if (protocol.equals("RemoveRoom")) {

			if (roomNameList.contains(from)) {
				roomNameList.remove(from);
				roomList.setListData(roomNameList);
			}

		} else if (protocol.equals("FailMakeRoom")) {

			JOptionPane.showMessageDialog(null, "같은 이름의 방이 존재합니다.", "[알림]", JOptionPane.ERROR_MESSAGE);

		} else if (protocol.equals("ImageDownload")) {

			// 내가 보낸 파일이 아닐 때 load 한다.
			if (!from.equals(id)) {

				downloadImgBtn.setEnabled(true);

				// 토크나이저를 /를 사용했기 때문에 가공하는 작업이 여러 번 필요하다.
				String temp = tokenizer.nextToken();
				String downloadImage = temp.replaceAll("홇", "/");
				imagePath = downloadImage.replaceFirst("//https:/", "https://");

				try {
					image = ImageIO.read(new URL(imagePath.trim()));
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
			}

		} else if (protocol.equals("RoomImageDownload")) {
			if (!from.equals(id)) {

				roomDownloadImgBtn.setEnabled(true);

				// 토크나이저를 /를 사용했기 때문에 가공하는 작업이 여러 번 필요하다.
				String temp = tokenizer.nextToken();
				String downloadImage = temp.replaceAll("홇", "/");
				imagePath = downloadImage.replaceFirst("//https:/", "https://");

				try {
					image = ImageIO.read(new URL(imagePath.trim()));
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
			}
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

	public void sendImageUploadBtn(String imgPath) {
		writer("ImageUpload/" + id + "/" + imgPath);
	}
	
	public void sendRoomImageUploadBtn(String imgPath) {
		writer("RoomImageUpload/" + myRoomName + "/" + imgPath);
	}

	public void viewImage(JButton downloadBtn) {
		if (image != null) {

			JFrame frame = new JFrame("Downloaded Image");

			// 사진의 크기만큼 사이즈를 정하자.
			frame.setSize(image.getWidth(null), image.getHeight(null));

			// 프레임이 종료되어도 프로그램을 계속 실행한다.
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			// 익명 내부클래스 작성
			// 이 부분에서만 단독으로 실행될 것이라면
			// 굳이 클래스 파일을 만들 필요는 없다
			JPanel imageView = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
				}
			};

			frame.add(imageView);
			frame.setVisible(true);

			// 프레임을 닫았을 때 이미지가 없어지도록 하자
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent windowEvent) {
					image = null;
				}
			});
		}
		downloadBtn.setEnabled(false);
	}

	@Override
	public void chatting(JTextArea messageBox, String str) {
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
