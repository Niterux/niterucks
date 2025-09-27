package io.github.niterux.niterucks.bevofeatures;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;

public class BetaEVOPacketDecoder {
	private static final Logger logger = Niterucks.LOGGER;
	private static final Gson gson = Niterucks.GSON;

	public static void decode(String PacketData) {
		try {
			BetaEVOPacketPOJO[] data = gson.fromJson(PacketData, BetaEVOPacketPOJO[].class);
			for (BetaEVOPacketPOJO datum : data)
				decodeUpdate(datum);
		} catch (JsonSyntaxException e) {
			logger.warn("Invalid BetaEvo packet data received! Outdated client?");
		}
	}

	private static void decodeUpdate(BetaEVOPacketPOJO data) {
		Minecraft minecraft = MinecraftInstanceAccessor.getMinecraft();
		switch (data.updateType) {
			case "handshake":
				if (data.protocol >= 2) {
					var responsePacket = new BetaEVOPacket(gson.toJson(new HandshakeResponse[]{new HandshakeResponse()}));
					minecraft.getNetworkHandler().sendPacket(responsePacket);
				}
				break;
			case "fly":
				if (data.protocol >= 1)
					BetaEVOFlyHelper.flyAllowed = data.enabled;
				break;
			case "complexNameTagUpdate":
			case "mouseEars":
				//todo
				break;
			case "basicNameTagUpdate":
				basicNameTagUpdate(data);
				break;
			case "playerList":
				playerListUpdate(data.players);
				break;
			case "privileges":
				if (data.protocol >= 3)
					BetaEVOFlyHelper.flyAllowed = data.privileges.fly;
				break;
			case "none":
				logger.info("No update type provided in betaevo packet! Outdated client?");
				break;
			default:
				logger.info("Unexpected updateType: {}", data.updateType);
		}
	}

	private static void basicNameTagUpdate(BetaEVOPacketPOJO data) {
		int realColor;
		boolean isRainbow = false;
		switch (data.color) {
			case "RED":
				realColor = 0xFF0000;
				break;
			case "YELLOW":
				realColor = 0xFFFF00;
				break;
			case "BLUE":
				realColor = 0x0000FF;
				break;
			case "GREEN":
				realColor = 0x008000;
				break;
			case "CYAN":
				realColor = 0x00FFFF;
				break;
			case "ORANGE":
				realColor = 0xFFA500;
				break;
			case "PURPLE":
				realColor = 0x6A0DAD;
				break;
			case "RAINBOW":
				isRainbow = true;
			default:
				realColor = 0xFFFFFF;
		}
		addNameTagData(data.player, realColor, isRainbow);
	}

	private static void playerListUpdate(PlayerPOJO[] players) {
		BetaEVO.playerListPacketReceived = true;
		ObjectArrayList<String> playersNames = new ObjectArrayList<>(players.length);
		for (PlayerPOJO player : players)
			playersNames.add(player.name);
		var iterator = BetaEVO.playerList.object2ObjectEntrySet().fastIterator();
		while (iterator.hasNext()) {
			var entry = iterator.next();
			var name = entry.getKey();
			if (!playersNames.contains(name)) {
				iterator.remove();
				continue;
			}
			PlayerPOJO player = players[playersNames.indexOf(name)];
			entry.setValue(new PlayerNameStatus(player.name, player.displayName, player.prefix));
		}
	}

	private static void addNameTagData(String name, int color, boolean rainbow) {
		if (!BetaEVO.playerList.containsKey(name)) {
			BetaEVO.playerList.put(name, new PlayerNameStatus(name, color, rainbow));
			return;
		}
		PlayerNameStatus currPlayer = BetaEVO.playerList.get(name);
		currPlayer.color = color;
		currPlayer.isRainbow = rainbow;
		BetaEVO.playerList.put(name, currPlayer);
	}
}
