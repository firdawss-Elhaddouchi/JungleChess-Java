package jungleGame.model;

import java.util.Objects;

public class Position {
	private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		return x == other.x && y == other.y;
	}

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
