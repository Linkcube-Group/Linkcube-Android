package me.linkcube.app.core.toy;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class ShakeSpeedCache {
	Queue<ShakeSpeedData> data;
	private int SIZE = 10;

	public ShakeSpeedCache(int size) {
		data = new LinkedBlockingDeque<ShakeSpeedData>(size);
		SIZE = size;
	}

	public ShakeSpeedCache() {
		data = new LinkedBlockingDeque<ShakeSpeedData>(SIZE);
	}

	public ShakeSpeedData addElement(float nx, float ny, float nz) {
		ShakeSpeedData tempData = new ShakeSpeedData(nx, ny, nz);
		ShakeSpeedData result = null;
		try {
			if (data.size() == 10) {
				result = getAvg();
				for (int i = 0; i < 5; i++) {

					data.remove();
				}
			}
			data.add(tempData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private ShakeSpeedData getAvg() {
		float x = 0.0f;
		float y = 0.0f;
		float z = 0.0f;
		for (ShakeSpeedData d : data) {
			x += d.getX();
			y += d.getY();
			z += d.getZ();
		}
		int size = data.size();
		x /= size;
		y /= size;
		z /= size;
		return new ShakeSpeedData(x, y, z);
	}
}

class ShakeSpeedData {
	private float x;
	private float y;
	private float z;

	public ShakeSpeedData(float nx, float ny, float nz) {
		x = nx;
		y = ny;
		z = nz;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}
}
