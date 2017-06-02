package q2p.quickclickmapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class MapPoint {
	byte[] position;
	
	MapPoint(int relativeX, int relativeY, int relativeZ) {
		position = new byte[]{(byte)relativeX, (byte)relativeY, (byte)relativeZ};
	}
	
	MapPoint(DataInputStream dis) throws IOException {
		position = new byte[]{dis.readByte(), dis.readByte(), dis.readByte()};
	}

	void write(DataOutputStream dos) throws IOException {
		for(byte i = 0; i != 3; i++) dos.writeByte(position[i]);
	}
	
	String byteArrayToFloatString(byte[] array) {
		String ret = "";
		for(byte i = 0; i < 3; i++) {
			ret += " " + (array[i]/2);
			if(array[i]%2 != 0) ret += ".5";
		}
		return ret.trim();
	}

	static float[] threeArgumentsFromLine(String line) {
		float args[] = new float[3];
		String[] s = line.trim().split(" ");
		if(s.length == 3) {
			for(byte i = 0; i < 3; i++) {
				try { args[i] = Float.parseFloat(s[i]); }
				catch(Exception e) { args[i] = 0; }
			}
		} else args = new float[]{0,0,0};
		return args;
	}
}
