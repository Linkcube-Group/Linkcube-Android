package me.linkcube.app.core.entity;

import android.graphics.Bitmap;

/**
 * 相关列表中的app文件类
 * 
 * @author xinyang
 * 
 */
public class AppFileEntity {

	public int id;

	public String name;

	public String downloadUrl;

	public Bitmap appIcon;
	// app的大小
	public int size;
	// 已下载大小
	public int downloadSize;
	// 下载状态:正常,正在下载，暂停，等待，已下载
	public int downloadState;

	public String downloadApkName;
}
