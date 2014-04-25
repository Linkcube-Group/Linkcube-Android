package me.linkcube.app.sync.core;

/**
 * ASmack请求的回调接口
 * 
 */
public interface ASmackRequestCallBack {

	public void responseSuccess(Object object);

	public void responseFailure(int reflag);
}
