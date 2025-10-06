package io.github.niterux.niterucks.niterucksfeatures.debugmenu;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class DebugHotKeyRegistry {
	public static ObjectArrayList<KeyCombo> HotKeyRegistry = new ObjectArrayList<>();

	static {
		HotKeyRegistry.add(new ReloadChunks());
		HotKeyRegistry.add(new ShowHitboxes());
		HotKeyRegistry.add(new ClearChat());
		HotKeyRegistry.add(new HideChat());
		HotKeyRegistry.add(new ChunkBoundaries());
		HotKeyRegistry.add(new ShowHelp());
		HotKeyRegistry.add(new ReloadAssets());
		HotKeyRegistry.add(new SnapCamera());
	}
}
