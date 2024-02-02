package io.github.niterux.niterucks.bevofeatures;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.github.niterux.niterucks.NiteLogger;
import io.github.niterux.niterucks.Niterucks;
import io.github.niterux.niterucks.mixin.accessors.MinecraftInstanceAccessor;

public class BetaEVOPacketDecoder {
	private static final NiteLogger logger = Niterucks.LOGGER;
	private static final Gson gson = new Gson();

	public static void decode(String PacketData) {
		try {
			BetaEVOPacketPOJO[] data = gson.fromJson(PacketData, BetaEVOPacketPOJO[].class);
			for (BetaEVOPacketPOJO datum : data) {
				decodeUpdate(datum);
			}
		} catch (JsonSyntaxException e) {
			logger.warn("Invalid BetaEvo packet data recieved! Outdated client?");
		}
	}

	private static void decodeUpdate(BetaEVOPacketPOJO data) {
		switch (data.updateType) {
			case "handshake":
				if (data.protocol == 2)
					MinecraftInstanceAccessor.getMinecraft().getNetworkHandler().sendPacket(new BetaEVOPacket("[{\"protocol\":2,\"updateType\":\"handshake\"}]"));
				break;
			case "fly":
				if (data.protocol == 1)
					BetaEVOFlyHelper.flyAllowed = data.enabled;
				break;
			case "complexNameTagUpdate":
			case "mouseEars":
				//todo
				break;
			case "basicNameTagUpdate":
				basicNameTagUpdate(data);
				break;
			case "none":
				logger.warn("No update type provided in betaevo packet! Outdated client?");
				break;
			default:
				logger.warn("Unexpected updateType: " + data.updateType);
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

	private static void addNameTagData(String name, int color, boolean rainbow) {
		if (BetaEVO.playerList.containsKey(name)) {
			PlayerNameStatus currPlayer = BetaEVO.playerList.get(name);
			currPlayer.color = color;
			currPlayer.isRainbow = rainbow;
			BetaEVO.playerList.put(name, currPlayer);
		} else {
			BetaEVO.playerList.put(name, new PlayerNameStatus(name, color, rainbow));
		}
	}
}
