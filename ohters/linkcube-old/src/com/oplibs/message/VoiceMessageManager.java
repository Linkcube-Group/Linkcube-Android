package com.oplibs.message;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import com.oplibs.support.LogHelper;

import android.os.Environment;

/*
 * 
 * 重要
 * 
 * 声音消息管理
 * 
 * 存储、接受、删除、当前用户、消息列表等
 * 
 */
public class VoiceMessageManager 
{
	private static VoiceMessageManager instance;
	public static String VMFileName = "VM.pcm";
	
	public int MaxStorageUsageMB = 100;
	public int MaxVoiceMessageFileSizeMB = 1;
	public int MaxMessageFromOne = 100;
	public Boolean DeleteFromGlobal = true;
	public Boolean AutoDeleteOldMessage = true;
	public Boolean AutoReceive = true;
	
	private ArrayList<VoiceMessage> messageList;
	private String currentUser;
	private VoiceMessage lastMessage;
	
	public static VoiceMessageManager GetInstance()
	{
		if (instance == null)
		{
			instance = new VoiceMessageManager();
		}
		
		return instance;
	}
	
	private VoiceMessageManager()
	{
		messageList = new ArrayList<VoiceMessage>();
		currentUser = "";
	}
		
	private static void SafeCreateFolder(String folder)
	{
		File f = new File(folder);
		
		if (f.exists())
		{
			return;
		}
		
		f.mkdir();		
	}
	
	public static String GetStoragePath()
	{
		String result = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OnChat";
		SafeCreateFolder(result);
		result += "/VoiceMessage";
		SafeCreateFolder(result);
		
		return result;
	}

	public Boolean LoadContact(String userId)
	{
		if (userId.compareTo(currentUser) == 0)
		{
			return true;
		}
		
		File contactFolder = new File(GetVmFolder(userId));
		
		if (!contactFolder.exists())
		{
			contactFolder.mkdir();
		}
		
		if (!contactFolder.isDirectory())
		{
			LogHelper.LogMessage("The folder name was occupied.");
			return false;
		}
		
		File[] files = contactFolder.listFiles();
		
		messageList.clear();
		currentUser = userId; 
		for (File file : files)
		{
			String fileName = file.getName();
			if (!fileName.endsWith("pcm"))
			{
				continue;
			}
			
			VoiceMessage message = new VoiceMessage(file);
			
			messageList.add(message);			
		}
		
		return true;
	}
	
	public Boolean Send()
	{
		return true;
	}
	
	public VoiceMessage Receive(FileTransferRequest request)
	{
		String fileName = request.getFileName();
		if (fileName.compareTo(VMFileName) != 0)
		{
			return null;
		}
		
		long fileSize = request.getFileSize();
        if (fileSize > MaxVoiceMessageFileSizeMB * 1000000)
        {
        	request.reject();
        	return null;
        }
		
        if (!CanReceiveMessage())
        {
        	request.reject();
        	return null;        	
        }

        IncomingFileTransfer transfer = request.accept();
		
        String requestor = request.getRequestor();
        String receivedFromUser = StringUtils.parseBareAddress(requestor);

        final File targetFile = GenerateVmFile(receivedFromUser, true);
        
        try 
        {
        	//PrintMessage(bareJID, "Starting Receiving...", fileName);
            transfer.recieveFile(targetFile);
        }
        catch (Exception e)
        {
            LogHelper.LogException(e);
        }

        while (true) 
        {
            try 
            {
                Thread.sleep(10);
            }
            catch (Exception e)
            {
                LogHelper.LogException(e);
            }

            FileTransfer.Status status = transfer.getStatus();
            if (status == FileTransfer.Status.error ||
                status == FileTransfer.Status.complete || status == FileTransfer.Status.cancelled ||
                status == FileTransfer.Status.refused) {
                break;
            }
            else if (status == FileTransfer.Status.negotiating_stream) 
            {
            }
            else if (status == FileTransfer.Status.in_progress) 
            {
            }
            
            // Sometimes, the receiving is hung. We have to terminal the transfer.  
            if (targetFile.length() >= fileSize)
            {
                try 
                {
                	Thread.sleep(100);
                }
                catch (Exception e)
                {
                    LogHelper.LogException(e);
                }

                transfer.cancel();
            }
        }

        LogHelper.LogMessage("File recive: " + transfer.getStatus().toString());
        
        if (transfer.getStatus() != FileTransfer.Status.complete && targetFile.length() < fileSize)
        {
        	return null;        
        }

        VoiceMessage message = new VoiceMessage(targetFile);
        lastMessage = message;
        
        return message;
	}
	
	public Boolean Delete()
	{
		return true;
	}
	
	public Boolean Lock()
	{
		return true;
	}

	public Boolean Unlock()
	{
		return true;
	}

	public String GetCurrentUser()
	{
		return currentUser;
	}
	
	public VoiceMessage GetLastMessage()
	{
		return lastMessage;
	}
	private static String GetVmFolder(String contact)
    {
		if (contact == null || contact == "")
		{
			return null;
		}
		
		String result = GetStoragePath() + "/" + contact;
		SafeCreateFolder(result);
    	return result;
    }
	
	private static File GenerateVmFile(String contact, Boolean isReceive)
	{
		if (contact == null || contact == "")
		{
			return null;
		}

		return new File(GetVmFolder(contact) + "/" + GenerateFileName(isReceive));
	}
	
	private static String GenerateFileName(Boolean isReceive)
	{
		// yyyymmdd-hh-mm-ss.pcm		
		String format= isReceive ? "%4d%02d%02d-%02d%02d%02d-r.pcm" : "%4d%02d%02d-%02d%02d%02d-s.pcm"; 

		Calendar c = Calendar.getInstance();
		
		String fileName = String.format(format, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), c.get(Calendar.HOUR), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		return fileName;
	}
	
	private Boolean CanReceiveMessage()
	{
		return true;
	}}
