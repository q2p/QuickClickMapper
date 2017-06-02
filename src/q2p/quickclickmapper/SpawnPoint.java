package q2p.quickclickmapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class SpawnPoint extends MapPoint{
	private byte rotation; /* -135 -90 -45 0 45 90 135 180 equals 0 1 2 3 4 5 6 7 */
	boolean isBlue; 
	
	SpawnPoint(int relativeX, int relativeY, int relativeZ, int rotation, boolean isBlue) {
		super(relativeX, relativeY, relativeZ);
		this.rotation = (byte)(rotation/45+3);
		if(this.rotation < 0 || this.rotation > 7) this.rotation = 0;
		this.isBlue = isBlue;
	}
	
	SpawnPoint(DataInputStream dis) throws IOException {
		super(dis);
		rotation = dis.readByte();
		isBlue = dis.readBoolean();
	}
	
	int getRotation() {
		return (rotation-3)*45;
	}

	void write(DataOutputStream dos) throws IOException {
		super.write(dos);
		dos.writeByte(rotation);
		dos.writeBoolean(isBlue);
	}
}