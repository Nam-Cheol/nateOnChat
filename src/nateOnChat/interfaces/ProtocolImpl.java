package nateOnChat.interfaces;

import javax.swing.JTextArea;

public interface ProtocolImpl {
	void chatting(JTextArea messageBox);

	void secretMessage();

	void makeRoom();

	void madeRoom();

	void newRoom();

	void outRoom();

	void enterRoom();

	void newUser();

	void connectedUser();

}
