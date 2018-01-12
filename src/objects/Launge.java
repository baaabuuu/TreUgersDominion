package objects;

public class Launge {
	private int[] lobbies;
	private int[] playerCount;
	public Launge(int[] lobbies, int[] playerCount) {
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
