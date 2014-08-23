package me.linkcube.app.sync.chat;

import java.io.File;

import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

public class SendVoiceMessage {

	public SendVoiceMessage() {

	}

	public void sendFile(String user, String filePath) {
		if (! ASmackManager.getInstance().getXMPPConnection().isConnected())
			return;
		// 创建文件传输管理器
		FileTransferManager manager = new FileTransferManager(
				 ASmackManager.getInstance().getXMPPConnection());

		// 创建输出的文件传输
		OutgoingFileTransfer transfer = manager
				.createOutgoingFileTransfer(user);

		// 发送文件
		try {
			transfer.sendFile(new File(filePath), "You won't believe this!");
			
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
}
