package me.linkcube.app.ui.main.multi;

import java.util.List;

import me.linkcube.app.R;
import me.linkcube.app.core.entity.FriendEntity;
import me.linkcube.app.core.user.UserManager;
import me.linkcube.app.util.FormatUtils;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter {

	private Context context;
	private List<FriendEntity> friendEntities;

	public FriendListAdapter(Context _context,
			List<FriendEntity> _friendEntities) {
		this.context = _context;
		this.friendEntities = _friendEntities;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(
				R.layout.friend_listview_cell, null);
		TextView friendName = (TextView) convertView
				.findViewById(R.id.friend_name_tv);
		ImageView friendAvatar = (ImageView) convertView
				.findViewById(R.id.friend_avatar_iv);
		try {
		friendName.setText(friendEntities.get(position).getNickName());
		Drawable friendAvatarDrawable = null;
		if (friendEntities.get(position).getUserAvatar() == null) {
			friendAvatarDrawable = context.getResources().getDrawable(
					UserManager.getInstance().setUserDefaultAvatar(
							friendEntities.get(position).getUserGender()));
		} else {
			friendAvatarDrawable = FormatUtils.Bytes2Drawable(friendEntities
					.get(position).getUserAvatar());
		}
		friendAvatar.setImageDrawable(friendAvatarDrawable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return friendEntities.size();
	}

}
