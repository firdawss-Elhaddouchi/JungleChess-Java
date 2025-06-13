package jungleGame.model;

import java.util.List;

public class Tiger extends Animal {
	public Tiger(int player, Position position) {
        super(6, player, position,"T");
    }

    @Override
    
    public boolean canMoveTo(Position to, Arena arena, List<Animal> animals) {
        Position from = getPosition();

        if (!arena.isValidPosition(to)) return false; // Ensure target is valid

        // Check if target position is occupied by another animal of the same player
        for (Animal a : animals) {
            if (a.getPosition().equals(to) && a.getPlayer() == this.getPlayer()) {
                return false; // Cannot move to a spot occupied by own animal
            }
        }

        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());

        // Normal one-step move (horizontal or vertical)
        if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
            return !arena.isLake(to); // Cannot move into a lake normally
        }

        // Jump over lake logic (similar to Lion)
        if (from.getX() == to.getX()) { // Horizontal jump
            if (dy == 3 || dy == 4) { // Typical lake jump distance (adjust if lake sizes differ)
                 if (arena.isLakeBetween(from, to) && !arena.hasRatInLakeBetween(from, to, animals)) {
                    return true;
                }
            }
        } else if (from.getY() == to.getY()) { // Vertical jump
            if (dx == 3 || dx == 4) { // Typical lake jump distance
                 if (arena.isLakeBetween(from, to) && !arena.hasRatInLakeBetween(from, to, animals)) {
                    return true;
                }
            }
        }
        return false;
    }
}
