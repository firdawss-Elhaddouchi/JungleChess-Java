package jungleGame.model;

import java.util.List;

public class Wolf extends Animal {
	
    public Wolf(int player, Position position) {
        super(3, player, position, "Wolf"); // Rank 3 for Wolf
    }

    @Override
    public boolean canMoveTo(Position to, Arena arena, List<Animal> animals) {
        Position from = getPosition();

        if (!arena.isValidPosition(to)) return false;

        for (Animal a : animals) {
            if (a.getPosition().equals(to) && a.getPlayer() == this.getPlayer()) {
                return false;
            }
        }

        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());

        return ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) &&
               !arena.isLake(to); // Cannot move into lakes
    }
}
