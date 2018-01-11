package objects;

public class BoardState {
	private int[] shopArea, handCount, deckCount, discardCount, vpCount;
	private int trashCount;
	private String[] names;
	public BoardState(int[] a, int[] b, int[] c, int[] d, int e, int[] f, String[] g) {
		this.shopArea = a;
		this.handCount = b;
		this.deckCount = c;
		this.discardCount = d;
		this.trashCount = e;
		this.vpCount = f;
		this.names = g;
	}
	public int[] getShopArea(){
		return shopArea;
	}
	public int[] getHandCount(){
		return handCount;
	}
	public int[] getDeckCount(){
		return deckCount;
	}
	public int[] getDiscardCount(){
		return discardCount;
	}
	public int getTrashCount(){
		return trashCount;
	}
	public int[] getVpCount(){
		return vpCount;
	}
	public String[] getNames(){
		return names;
	}
	public void setShopArea(int[] shopArea){
		this.shopArea = shopArea;
	}
	public void setHandCount(int[] handCount){
		this.handCount = handCount;
	}
	public void setDeckCount(int[] deckCount){
		this.deckCount = deckCount;
	}
	public void setDiscardCount(int[] discardCount){
		this.discardCount = discardCount;
	}
	public void setTrashCount(int trashCount){
		this.trashCount = trashCount;
	}
	public void setVpCount(int[] vpCount){
		this.vpCount = vpCount;
	}
	public void setNames(String[] names){
		this.names = names;
	}
}
