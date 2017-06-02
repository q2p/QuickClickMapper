package q2p.quickclickmapper;

import java.awt.Image;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.SmoothBrick;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

public class Mapper extends JavaPlugin implements Listener {
	@SuppressWarnings("deprecation")
	private static final ItemStack[] BUILD_KIT_ITEMS = new ItemStack[] {
		new ItemStack(Material.GLOWSTONE),
		new ItemStack(Material.GOLD_BLOCK),
		new ItemStack(Material.STONE, 1, (short)1, (byte)2),
		new Wool(DyeColor.ORANGE).toItemStack(1),
		new SmoothBrick().toItemStack(1),
		new Step(Material.SMOOTH_BRICK).toItemStack(1),
		new Stairs(Material.SMOOTH_STAIRS).toItemStack(1),
		new ItemStack(Material.SIGN)
	};
	private static final String validLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Image[] image = new Image[2]; 
	/*
	TODO:
	инструкция по использованию
	убрать лишние функции
	рефактор кода
	*/
	public void onEnable() {
		try { 
			image[0] = ImageIO.read(new File("E:/@MyFolder/MEGA/p/nws/nw1/IdmeOFwDujQ.jpg"));
			image[1] = ImageIO.read(new File("E:/@MyFolder/MEGA/p/nws/nw1/xwRoEniK_a4.jpg"));
		}
		catch (IOException e) { return; }	
		for(int i = 0; i < 2; i++) {
			float aspect = (float)image[i].getWidth(null)/(float)image[i].getHeight(null);
			int width = 128;
			int height = 128;
			if(aspect >= 1) height = (int) Math.max(width,(float)(width/aspect));
			else width = (int) Math.max(height,(float)(height*aspect));
			image[i] = image[i].getScaledInstance(width, height, Image.SCALE_SMOOTH);
		}
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(command.getName().equals("createbox")) {
				if(args.length != 3) {
					sender.sendMessage("Usage: /createbox <x size> <y size> <z size>");
					return true;
				}
				int[] size = new int[3];
				for(byte i = 0; i < 3; i++) {
					try {
						size[i] = Integer.parseInt(args[i]);
					} catch (NumberFormatException e) {
						sender.sendMessage("Usage: /createbox <x size> <y size> <z size>");
						return true;
					}
					if(size[i] > 128) {
						sender.sendMessage("Max size 128 blocks");
						return true;
					} else if(size[i] < 1) {
						sender.sendMessage("Minimal size 1 blocks");
						return true;
					}
				}
				Location location = player.getLocation();
				location.setY(location.getY() - 1);
				location.setX(location.getX() + 1);
				location.setZ(location.getZ() + 1);
				Block beg = location.getBlock();
				int bx = beg.getX();
				int by = beg.getY();
				int bz = beg.getZ();
				World w = player.getWorld();
				for(int y = 0; y < size[1]+2; y++) {
					for(int x = 0; x < size[0]+2; x++) {
						w.getBlockAt(bx+x, by+y, bz).setType(Material.BEDROCK);
						w.getBlockAt(bx+x, by+y, bz+size[2]+1).setType(Material.BEDROCK);
					}
					for(int z = 0; z < size[2]+2; z++) {
						w.getBlockAt(bx, by+y, bz+z).setType(Material.BEDROCK);
						w.getBlockAt(bx+size[0]+1, by+y, bz+z).setType(Material.BEDROCK);
					}
				}
				for(int x = 0; x < size[0]; x++) {
					for(int z = 0; z < size[2]; z++) {
						w.getBlockAt(bx+x+1, by, bz+z+1).setType(Material.BEDROCK);
						w.getBlockAt(bx+x+1, by+size[1]+1, bz+z+1).setType(Material.GLASS);
					}
				}
				for(int x = 0; x < size[0]; x++) {
					for(int y = 0; y < size[1]; y++) {
						for(int z = 0; z < size[2]; z++) {
							w.getBlockAt(bx+x+1, by+y+1, bz+z+1).setType(Material.AIR);
						}
					}
				}
				w.getBlockAt(bx-1, by, bz-1).setType(Material.BEDROCK);
				player.teleport(new Location(w, bx-0.5, by+1, bz-0.5), TeleportCause.PLUGIN);
				return true;
			}
			if(command.getName().equals("removebox")) {
				if(args.length != 0) {
					sender.sendMessage("Usage: /removebox");
					return true;
				}
				if(!((Entity)player).isOnGround()) {
					sender.sendMessage("You must stand on marked bedrock block");
					return true;
				}
				Location location = player.getLocation();
				location.setY(location.getY()-0.8);
				if(location.getBlock().getType() != Material.BEDROCK) {
					sender.sendMessage("You must stand on marked bedrock block");
					return true;
				}
				location.setX(location.getX() + 1);
				location.setZ(location.getZ() + 1);
				Block beg = location.getBlock();
				int bx = beg.getX();
				int by = beg.getY();
				int bz = beg.getZ();
				World w = player.getWorld();
				int[] size = new int[]{0,0,0};
				while(true) {
					Block b = w.getBlockAt(bx+size[0], by, bz);
					if(b.getType() != Material.BEDROCK) break;
					size[0]++;
				}
				while(true) {
					Block b = w.getBlockAt(bx, by+size[1], bz);
					if(b.getType() != Material.BEDROCK) break;
					size[1]++;
				}
				while(true) {
					Block b = w.getBlockAt(bx, by, bz+size[2]);
					if(b.getType() != Material.BEDROCK) break;
					size[2]++;
				}
				for(byte i = 0; i < 3; i++) size[i]-=2;
				for(int x = 0; x < size[0]+2; x++) {
					for(int y = 0; y < size[1]+2; y++) {
						for(int z = 0; z < size[2]+2; z++) {
							w.getBlockAt(bx+x, by+y, bz+z).setType(Material.AIR);
						}
					}
				}
				w.getBlockAt(bx-1, by, bz-1).setType(Material.AIR);
				return true;
			}
			if(command.getName().equals("parse")) {
				if(args.length != 1) {
					sender.sendMessage("Usage: /parse <file name>");
					return true;
				}
				if(!isValid(args[0])) {
					sender.sendMessage("File name can only contains english letters.");
					return true;
				}
				if(!((Entity)player).isOnGround()) {
					sender.sendMessage("You must stand on marked bedrock block");
					return true;
				}
				Location location = player.getLocation();
				location.setY(location.getY()-0.8);
				if(location.getBlock().getType() != Material.BEDROCK) {
					sender.sendMessage("You must stand on marked bedrock block");
					return true;
				}
				location.setX(location.getX() + 1);
				location.setZ(location.getZ() + 1);
				Block beg = location.getBlock();
				int bx = beg.getX();
				int by = beg.getY();
				int bz = beg.getZ();
				World w = player.getWorld();
				short[] size = new short[]{0,0,0};
				while(true) {
					Block b = w.getBlockAt(bx+size[0], by, bz);
					if(b.getType() != Material.BEDROCK) break;
					size[0]++;
				}
				while(true) {
					Block b = w.getBlockAt(bx, by+size[1], bz);
					if(b.getType() != Material.BEDROCK) break;
					size[1]++;
				}
				while(true) {
					Block b = w.getBlockAt(bx, by, bz+size[2]);
					if(b.getType() != Material.BEDROCK) break;
					size[2]++;
				}
				for(byte i = 0; i < 3; i++) size[i]-=2;
				bx++;
				by++;
				bz++;
				byte[][][] map = new byte[size[2]][size[1]][size[0]];
				ArrayList<SpawnPoint> spawns = new ArrayList<SpawnPoint>();
				/*
				00 - air
				01 - glowstone
				02 - gold block
				03 - polihed granite
				04 - orange wool (lava)
				05 - stone brick
				06 - slab
				07 - slab invert
				08 - stair r1
				09 - stair r2
				10 - stair r3
			 	11 - stair r4
				12 - inv stair r1
				13 - inv stair r2
				14 - inv stair r3
				15 - inv stair r4
				*/
				for(int z = 0; z < size[2]; z++) {
					for(int y = 0; y < size[1]; y++) {
						for(int x = 0; x < size[0]; x++) {
							Block block = w.getBlockAt(bx+x, by+y, bz+z);
							switch(block.getType()) {
							case GLOWSTONE:
								map[z][y][x] = 1;
								break;
							case GOLD_BLOCK:
								map[z][y][x] = 2;
								break;
							case STONE:
								if(block.getData() != 2) map[z][y][x] = 0;
								else map[z][y][x] = 3;
								break;
							case WOOL:
								Wool wool = (Wool) block.getState().getData();
								if(wool.getColor() != DyeColor.ORANGE) map[z][y][x] = 0;
								else map[z][y][x] = 4;
								break;
							case SMOOTH_BRICK:
								map[z][y][x] = 5;
								break;
							case STEP:
								Step step = (Step) block.getState().getData();
								map[z][y][x] = (byte)(step.isInverted()?7:6);
								break;
							case SMOOTH_STAIRS:
								Stairs stairs = (Stairs) block.getState().getData();
								map[z][y][x] = (byte)(8+dirToByte(stairs.getFacing())+(stairs.isInverted()?4:0));
								break;
							case SIGN:
							case SIGN_POST:
							case WALL_SIGN:
								Sign sign = (Sign) block.getState();
								if(sign.getLine(0).equals("spawn")) {
									int rotation;
									try { rotation = Integer.parseInt(sign.getLine(2)); }
									catch(Exception e) { rotation = 0; }
									spawns.add(new SpawnPoint(x, y, z, rotation, sign.getLine(1).equals("b")));
								}
								break;
							default:
								map[z][y][x] = 0;
							}
						}
					}
				}

				int offsetX = 0;
				int offsetY = 0;
				int offsetZ = 0;
				boolean found = false;
				for(;offsetX < size[0]; offsetX++) {
					for(int z = 0; z < size[2] && !found; z++) {
						for(int y = 0; y < size[1] && !found; y++) {
							if(map[z][y][offsetX] != 0) found = true;
						}
					}
					if(found) break;
				}
				found = false;
				for(;offsetY < size[1]; offsetY++) {
					for(int z = 0; z < size[2] && !found; z++) {
						for(int x = 0; x < size[0] && !found; x++) {
							if(map[z][offsetY][x] != 0) found = true;
						}
					}
					if(found) break;
				}
				found = false;
				for(;offsetZ < size[2]; offsetZ++) {
					for(int y = 0; y < size[1] && !found; y++) {
						for(int x = 0; x < size[0] && !found; x++) {
							if(map[offsetZ][y][x] != 0) found = true;
						}
					}
					if(found) break;
				}
				int borderX = size[0];
				int borderY = size[1];
				int borderZ = size[2];
				found = false;
				for(;borderX > offsetX; borderX--) {
					for(int z = 0; z < size[2] && !found; z++) {
						for(int y = 0; y < size[1] && !found; y++) {
							if(map[z][y][borderX-1] != 0) found = true;
						}
					}
					if(found) break;
				}
				found = false;
				for(;borderY > offsetY; borderY--) {
					for(int z = 0; z < size[2] && !found; z++) {
						for(int x = 0; x < size[0] && !found; x++) {
							if(map[z][borderY-1][x] != 0) found = true;
						}
					}
					if(found) break;
				}
				found = false;
				for(;borderZ > offsetZ; borderZ--) {
					for(int y = 0; y < size[1] && !found; y++) {
						for(int x = 0; x < size[0] && !found; x++) {
							if(map[borderZ-1][y][x] != 0) found = true;
						}
					}
					if(found) break;
				}
				size = new short[]{(short)(borderX-offsetX),(short)(borderY-offsetY),(short)(borderZ-offsetZ)};
				byte[][][] newMap = new byte[size[2]][size[1]][size[0]];
				for(int z = 0; z < size[2]; z++) {
					for(int y = 0; y < size[1]; y++) {
						for(int x = 0; x < size[0]; x++) {
							newMap[z][y][x] = map[offsetZ+z][offsetY+y][offsetX+x];
						}
					}
				}
				map = null;
				for(SpawnPoint spawn : spawns) {
					spawn.position[0] -= offsetX;
					spawn.position[1] -= offsetY;
					spawn.position[2] -= offsetZ;
				}
				File file = new File("maps/");
				if(!file.isDirectory()) file.delete();
				if(!file.exists()) file.mkdir();
				file = new File("maps/"+args[0]+".map");
				if(file.exists()) file.delete();
				try { file.createNewFile();
				} catch (IOException e) { 
					sender.sendMessage("Can't create file " + file.getAbsolutePath());
					return true;
				}
				DataOutputStream dos = null;
				try { dos = new DataOutputStream(new FileOutputStream(file));
				} catch (FileNotFoundException e) {
					sender.sendMessage("Can't write to file " + file.getAbsolutePath());
					return true;
				}
				try {
					for(byte i = 0; i < 3; i++) dos.writeShort(size[i]);
					for(int z = 0; z < size[2]; z++) {
						for(int y = 0; y < size[1]; y++) {
							for(int x = 0; x < size[0]; x++) {
								dos.writeByte(newMap[z][y][x]);
							}
						}
					}
					dos.writeByte(spawns.size());
					while(!spawns.isEmpty()) spawns.remove(0).write(dos);
				} catch (IOException e) {
					try {dos.close();} catch (IOException e1) {}
					sender.sendMessage("Can't write to file " + file.getAbsolutePath());
					return true;
				}
				try {dos.close();} catch (IOException e) {}
				sender.sendMessage("Map saved.");
				return true;
			}
			if(command.getName().equals("load")) {
				if(args.length != 1) {
					sender.sendMessage("Usage: /load <file name>");
					return true;
				}
				File file = new File("maps/"+args[0]+".map");
				if(!file.exists()) {
					sender.sendMessage("Map "+args[0]+" was not found.");
					return true;
				}
				if(!((Entity)player).isOnGround()) {
					sender.sendMessage("You must stand on marked bedrock block");
					return true;
				}
				Location location = player.getLocation();
				location.setY(location.getY()-0.8);
				if(location.getBlock().getType() != Material.BEDROCK) {
					sender.sendMessage("You must stand on marked bedrock block");
					return true;
				}
				location.setX(location.getX() + 1);
				location.setZ(location.getZ() + 1);
				Block beg = location.getBlock();
				int bx = beg.getX();
				int by = beg.getY();
				int bz = beg.getZ();
				World w = player.getWorld();
				short[] bsize = new short[]{0,0,0};
				while(true) {
					Block b = w.getBlockAt(bx+bsize[0], by, bz);
					if(b.getType() != Material.BEDROCK) break;
					bsize[0]++;
				}
				while(true) {
					Block b = w.getBlockAt(bx, by+bsize[1], bz);
					if(b.getType() != Material.BEDROCK) break;
					bsize[1]++;
				}
				while(true) {
					Block b = w.getBlockAt(bx, by, bz+bsize[2]);
					if(b.getType() != Material.BEDROCK) break;
					bsize[2]++;
				}
				for(byte i = 0; i < 3; i++) bsize[i]-=2;
				bx++;
				by++;
				bz++;
				
				DataInputStream dis = null;
				try { dis = new DataInputStream(new FileInputStream(file));
				} catch (FileNotFoundException e) {
					sender.sendMessage("Can't read from file " + file.getAbsolutePath());
					return true;
				}
				short[] size = new short[3];
				byte[][][] map = null;
				ArrayList<SpawnPoint> spawns = new ArrayList<SpawnPoint>();
				try {
					for(byte i = 0; i < 3; i++) size[i] = dis.readShort();
					map = new byte[size[2]][size[1]][size[0]];
					for(int z = 0; z < size[2]; z++) {
						for(int y = 0; y < size[1]; y++) {
							for(int x = 0; x < size[0]; x++) {
								map[z][y][x] = dis.readByte();
							}
						}
					}
					for(int i = dis.readByte(); i != 0; i--) spawns.add(new SpawnPoint(dis));
				} catch (IOException e) {
					try {dis.close();} catch (IOException e1) {}
					sender.sendMessage("Can't read from file " + file.getAbsolutePath());
					return true;
				}
				try {dis.close();} catch (IOException e1) {}

				/*
				00 - air
				01 - glowstone
				02 - gold block
				03 - polihed granite
				04 - orange wool (lava)
				05 - stone brick
				06 - slab
				07 - slab invert
				08 - stair r1
				09 - stair r2
				10 - stair r3
			 	11 - stair r4
				12 - inv stair r1
				13 - inv stair r2
				14 - inv stair r3
				15 - inv stair r4
				*/
				
				for(int z = 0; z < size[2]; z++) {
					for(int y = 0; y < size[1]; y++) {
						for(int x = 0; x < size[0]; x++) {
							Block block = w.getBlockAt(bx+x, by+y, bz+z);
							if(map[z][y][x] == 1) {
								block.setType(Material.GLOWSTONE);
							} else if(map[z][y][x] == 2) {
								block.setType(Material.GOLD_BLOCK);
							} else if(map[z][y][x] == 3) {
								block.setType(Material.STONE);
								block.setData((byte)2);
							} else if(map[z][y][x] == 4) {
								block.setType(Material.WOOL);
								block.setData(new Wool(DyeColor.ORANGE).getData());
							} else if(map[z][y][x] == 5) {
								block.setType(Material.SMOOTH_BRICK);
								block.setData((byte)0);
							} else if(map[z][y][x] == 6 || map[z][y][x] == 7) {
								block.setType(Material.STEP);
								BlockState state = block.getState();
								Step step = new Step(Material.SMOOTH_BRICK);
								step.setInverted(map[z][y][x] == 7);
								state.setData(step);
								state.update();
							} else if(map[z][y][x] >= 8) {
								block.setType(Material.SMOOTH_STAIRS);
								BlockState state = block.getState();
								Stairs stairs = new Stairs(Material.SMOOTH_BRICK);
								stairs.setInverted(map[z][y][x]-8 >= 4);
								stairs.setFacingDirection(byteToDir((byte)((map[z][y][x]-8)%4)));
								state.setData(stairs);
								state.update();
							} else {
								block.setType(Material.AIR);
							}
						}
					}
				}
				while(!spawns.isEmpty()) {
					SpawnPoint spawn = spawns.remove(0);
					Block block = w.getBlockAt(bx+spawn.position[0], by+spawn.position[1], bz+spawn.position[2]);
					block.setType(Material.SIGN_POST);
					Sign sign = (Sign) block.getState();
					sign.setLine(0, "spawn");
					sign.setLine(1, spawn.isBlue?"b":"r");
					sign.setLine(2, ""+spawn.getRotation());
					sign.update();
				}
				
				sender.sendMessage("Map loaded.");
				return true;
			}
			if(command.getName().equals("buildkit")) {
				if(args.length != 0) {
					sender.sendMessage("Usage: /buildkit");
					return true;
				}
				player.setGameMode(GameMode.CREATIVE);
				Inventory inventory = player.getInventory();
				inventory.clear();
				for(int i = 0; i < BUILD_KIT_ITEMS.length; i++) inventory.addItem(BUILD_KIT_ITEMS[i].clone());
				player.updateInventory();
				return true;
			}
			if(command.getName().equals("skull")) {
				if(args.length != 1) {
					sender.sendMessage("Usage: /skull <skinname>");
					return true;
				}
				Inventory inventory = player.getInventory();
				
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
		        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
		        meta.setOwner(args[0]);
		        meta.setDisplayName(args[0]);
		        skull.setItemMeta(meta);
		        
				inventory.addItem(skull);
				player.updateInventory();
				return true;
			}
		}
		if(command.getName().equals("day")) {
			List<World> worlds = sender.getServer().getWorlds();
			for(World world : worlds) world.setFullTime(1000);
			return true;
		}
		return false;
	}
	
	private static byte dirToByte(BlockFace face) {
		switch(face) {
		case NORTH: return 1;
		case SOUTH: return 0;
		case WEST: return 3;
		default: return 2;
		}
	}
	
	private static BlockFace byteToDir(byte b) {
		switch(b) {
		case 0: return BlockFace.NORTH;
		case 1: return BlockFace.SOUTH;
		case 2: return BlockFace.WEST;
		default: return BlockFace.EAST;
		}
	}
	
	private static boolean isValid(String string) {
		for(int i = 0; i < string.length(); i++) if(!validLetters.contains(""+string.charAt(i))) return false;
		return true;
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if(event.toWeatherState()){
			event.getWorld().setStorm(false);
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onThunerChange(ThunderChangeEvent event) {
		if(event.toThunderState()){
			event.getWorld().setStorm(false);
			event.setCancelled(true);
		}
	}
}
