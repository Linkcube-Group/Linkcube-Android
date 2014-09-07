package me.linkcube.app.sync.user;

import java.io.ByteArrayInputStream;
import java.io.File;

import me.linkcube.app.common.util.FormatUtils;
import me.linkcube.app.sync.core.ASmackRequestCallBack;
import me.linkcube.app.sync.core.ASmackManager;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

/**
 * 修改用户头像
 * 
 * @author Rodriguez-xin
 * 
 */
public class ChangeAvatar {

	private ASmackRequestCallBack changeAvatarCallBack;
	private Bitmap avatarbitmap;

	public ChangeAvatar(Bitmap bitmap, ASmackRequestCallBack iCallBack) {
		changeAvatarCallBack = iCallBack;
		avatarbitmap = bitmap;
		callChangeAvatar();
	}

	public void callChangeAvatar() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what != 0) {
					changeAvatarCallBack.responseFailure(msg.what);
				} else if (msg.what == 0) {
					changeAvatarCallBack.responseSuccess(msg.what);
				}
			}
		};
		Thread thread = new Thread() {
			@Override
			public void run() {
				int reflag = changeAvatar(avatarbitmap);
				Message msg = new Message();
				msg.what = reflag;
				handler.sendMessage(msg);
			}
		};
		thread.start();
	}

	/**
	 * 修改用户头像
	 * 
	 * @param file
	 * @return '0' 更新头像成功 '-1'失败
	 */
	public int changeAvatar(Bitmap bitmap) {
		if (!ASmackManager.getInstance().getXMPPConnection().isConnected())
			return -1;// 没有连接上
		try {
			VCard vcard = new VCard();
			vcard.load(ASmackManager.getInstance().getXMPPConnection());

			byte[] userAvatar = FormatUtils.Bitmap2Bytes(bitmap);
			// String encodedImage = StringUtils.encodeBase64(bytes);
			vcard.setAvatar(userAvatar);// ,encodedImage
			// vcard.setEncodedImage(encodedImage);
			// vcard.setField("PHOTO",
			// "<TYPE>image/jpg</TYPE><BINVAL>"+encodedImage+"</BINVAL>",true);
			// ByteArrayInputStream bais=new
			// ByteArrayInputStream(vcard.getAvatar());
			// FormatUtils.getInstance().InputStream2Bitmap(bais);
			vcard.save(ASmackManager.getInstance().getXMPPConnection());

			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
}
