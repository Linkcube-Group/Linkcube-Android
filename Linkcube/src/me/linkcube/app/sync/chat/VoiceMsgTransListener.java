package me.linkcube.app.sync.chat;

import java.io.File;

import me.linkcube.app.core.Timber;

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
			Timber.d("FileName:" + request.getFileName());
		} catch (XMPPException e) {
			e.printStackTrace();
		}

	}

}
