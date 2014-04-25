package me.linkcube.app.sync.chat;

import java.io.File;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

public class VoiceMsgTransListener implements FileTransferListener {

	@Override
	public void fileTransferRequest(FileTransferRequest request) {
		IncomingFileTransfer accept = request.accept();
		File file = new File("" + request.getFileName());
		try {
			accept.recieveFile(file);
			System.out.println("FileName:" + request.getFileName());
		} catch (XMPPException e) {
			e.printStackTrace();
		}

	}

}
