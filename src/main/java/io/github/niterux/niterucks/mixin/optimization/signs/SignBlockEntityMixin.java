package io.github.niterux.niterucks.mixin.optimization.signs;

import com.mojang.blaze3d.platform.MemoryTracker;
import io.github.niterux.niterucks.niterucksfeatures.SignBlockEntityInterface;
import net.minecraft.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin implements SignBlockEntityInterface {
	@Unique
	private int glCallList = -1;
	@Unique
	private String[] linesUpdateChecker = new String[4];

	@Override
	public int niterucks$getGlCallList() {
		return glCallList;
	}

	@Override
	public void niterucks$setGlCallList(int num) {
		glCallList = num;
	}


	@Override
	public String[] niterucks$getLinesUpdateChecker() {
		return linesUpdateChecker;
	}

	@Override
	public void niterucks$copyToUpdateChecker(String[] lines) {
		linesUpdateChecker = new String[lines.length];
		System.arraycopy(lines, 0, linesUpdateChecker, 0, lines.length);
	}

	@Override
	public void niterucks$releaseList() {
		if (glCallList != -1)
			MemoryTracker.m_1450705(glCallList);
		glCallList = -1;
	}
}
