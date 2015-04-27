package multiplayer;

import java.util.ArrayList;

public class JsonPlattformArray {

	private ArrayList<JsonPlattform> list = new ArrayList<JsonPlattform>();

	public ArrayList<JsonPlattform> getList() {
		return list;
	}

	public int length() {
		return list.size();
	}

	public void setList(ArrayList<JsonPlattform> list) {
		this.list = list;
	}

	public JsonPlattform getJsonPlattform(int i) {
		return list.get(i);
	}

}
