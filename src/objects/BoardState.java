package objects;

public class BoardState {
	private Integer[] shopArea, handCount, deckCount, discardCount, vpCount;
	private int trashCount;
	public BoardState(Integer[] a, Integer[] b, Integer[] c, Integer[] d, int e, Integer[] f) {
		this.shopArea = a;
		this.handCount = b;
		this.deckCount = c;
		this.discardCount = d;
		this.trashCount = e;
		this.vpCount = f;
	}
	public Integer[] getShopArea(){
		return shopArea;
	}
	public Integer[] getHandCount(){
		return handCount;
	}
	public Integer[] getDeckCount(){
		return deckCount;
	}
	public Integer[] getDiscardCount(){
		return discardCount;
	}
	public int getTrashCount(){
		return trashCount;
	}
	public Integer[] getVpCount(){
		return vpCount;
	}
	public void setShopArea(Integer[] shopArea){
		this.shopArea = shopArea;
	}
	public void setHandCount(Integer[] handCount){
		this.handCount = handCount;
	}
	public void setDeckCount(Integer[] deckCount){
		this.deckCount = deckCount;
	}
	public void setDiscardCount(Integer[] discardCount){
		this.discardCount = discardCount;
	}
	public void setTrashCount(int trashCount){
		this.trashCount = trashCount;
	}
	public void setVpCount(Integer[] vpCount){
		this.vpCount = vpCount;
	}
}
