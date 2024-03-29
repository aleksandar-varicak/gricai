package old.game.server;

import old.game.Player;
import old.messages.Message;

public class User implements ConnectionUser {
	private Connection conn;
	private Player player;
	private int number;

	public User(Connection c, MessageReceiver mrec) {
		this(c);
	}

	public User(Connection c) {
		conn = c;
		conn.attach(this);
		player = new Player();
		player.setUser(this);
	}

	// public void receive(Message msg) {
	// if(App.getInstance().getServer() != null)
	// App.getInstance().getServer().receive(this, msg);
	// }

	public void send(Message msg) {
		conn.send(msg);
	}

	public void stateChange(int state) {
		// TODO Auto-generated method stub

	}

	public Player getPlayer() {
		return player;
	}

	public int getNumber() {
		return number;
	}

	@Override
	public void receive(Message msg) {
		// TODO Auto-generated method stub

	}
}
