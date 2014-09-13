package com.oplibs.syncore;

import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;

import com.oplibs.message.VoiceMessage;
import com.oplibs.message.VoiceMessageManager;
import com.oplibs.support.Intents;
import com.oplibs.support.LogHelper;


/*
 * 文件
 * 
 * 
 */

public class SyncFileTransferListener implements FileTransferListener
{

	@Override
	public void fileTransferRequest(FileTransferRequest request)
	{
		// PrintMessage("Start", "Accpting...", "na");
		
        String fileName = request.getFileName();
		if (fileName.compareTo(VoiceMessageManager.VMFileName) == 0)
		{
			SyncMgr user = SyncMgr.GetInstance();
			user.BroadcaseIntent(Intents.VoiceMessageStartReceive);
			
			VoiceMessage voiceMessage = VoiceMessageManager.GetInstance().Receive(request);
			
			if (voiceMessage != null)
			{
				String message = "Receiving voice message from " + voiceMessage.from;		    
				LogHelper.LogMessage(message);
							
				user.BroadcaseIntent(Intents.VoiceMessageReceived);
			}
			
			return;
		}
		
		/* Normal file
		IncomingFileTransfer transfer = request.accept();
		
        if (request.getFileSize() > 100000000)
        {
        	transfer.cancel();
        }

        String requestor = request.getRequestor();
        String bareJID = StringUtils.parseBareAddress(requestor);

      
        String path = GetStoragePath(bareJID);        
        
        final File downloadedFile = new File(path + "/" + fileName);
        
        try 
        {
        	//PrintMessage(bareJID, "Starting Receiving...", fileName);
            transfer.recieveFile(downloadedFile);
        }
        catch (XMPPException e) {
            Log.e(TAG, e.getMessage());
        }
        catch (Exception ex)
		{
			PrintMessage(bareJID, ex.getMessage(), fileName);
		}


        String message = "Receiving file '" + fileName + "' from " + bareJID;
					
		PrintMessage(bareJID, message, fileName);
		
        while (true) 
        {
            try 
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e) {
            	Log.e(TAG, e.getMessage());
            }
            catch (Exception ex)
    		{
    			PrintMessage(bareJID, ex.getMessage(), fileName);
    		}

            FileTransfer.Status status = transfer.getStatus();
            if (status == FileTransfer.Status.error ||
                status == FileTransfer.Status.complete || status == FileTransfer.Status.cancelled ||
                status == FileTransfer.Status.refused) 
            {
                break;
            }
            else if (status == FileTransfer.Status.negotiating_stream) 
            {
            }
            else if (status == FileTransfer.Status.in_progress) 
            {
            }
        }

        message = "Received file '" + fileName + "' from " + bareJID;
		
        PrintMessage(bareJID, message, fileName);
		
        Log.e(TAG, "File recive: " + transfer.getStatus().toString());
        */
	}

}
