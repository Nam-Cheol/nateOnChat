package nateOnChat_ver2.interfaces;

import javax.swing.JTextArea;

public interface ProtocolImpl {
	void chatting(JTextArea messageBox, String str);

	void secretMessage();

	void makeRoom();

	void madeRoom();

	void newRoom();

	void outRoom();

	void enterRoom();

	void newUser();

	void connectedUser();

}
