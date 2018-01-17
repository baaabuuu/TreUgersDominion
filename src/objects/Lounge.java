package objects;

public class Lounge {
	private int[] lobbies;
	private int[] playerCount;
	public Lounge(int[] lobbies, int[] playerCount) {
		this.lobbies = lobbies;
		this.playerCount = playerCount;
	}
	public int[] getLobbies() {
		return lobbies;
	}
	public int[] getplayerCount() {
		return playerCount;
	}
	public void setLobbies(int[] lobbies) {
		this.lobbies = lobbies;
	}
	public void setplayerCount(int[] playerCount) {
		this.playerCount = playerCount;
	}
}
