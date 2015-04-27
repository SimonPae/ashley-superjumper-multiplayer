package multiplayer;

public class JsonPlattform {

	private Integer type;
	private double y;
	private double x;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	@Override
	public String toString(){
		return "type:"+ type + ",y:"+y+",x:"+x;
	}
	
}
