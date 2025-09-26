package io.github.niterux.niterucks.niterucksfeatures;


public interface SignBlockEntityInterface {
	boolean niterucks$getRenderCheck();

	void niterucks$setRenderCheck(boolean renderCheck);

	int niterucks$getGlCallList();

	void niterucks$setGlCallList(int num);

	String[] niterucks$getLinesUpdateChecker();

	void niterucks$copyToUpdateChecker(String[] lines);

	void niterucks$releaseList();
}
