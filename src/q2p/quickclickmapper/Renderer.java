package q2p.quickclickmapper;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class Renderer extends MapRenderer {
	boolean rendered = false;
	public void render(MapView view, MapCanvas canvas, Player player) {
		if(!rendered) return;
		Random random = new Random();
		canvas.drawImage(0, 0, Mapper.image[random.nextInt(Mapper.image.length)]);
		rendered = true;
	}
}
