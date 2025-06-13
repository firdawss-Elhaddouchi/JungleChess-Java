package jungleGame.model;

import java.util.List;

public class Rat extends Animal{
	public Rat(int player, Position position) {
        super(1, player, position,"R"); 
    }

    @Override
    public boolean canMoveTo(Position to, Arena arena,  List<Animal> animals) {
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

        // Rat can move one step horizontally or vertically
        boolean isOneStepMove = ((dx == 1 && dy == 0) || (dx == 0 && dy == 1));

        if (isOneStepMove) {
            // Rat can move into a lake.
            // Rat cannot move into a trap or sanctuary unless it's a lake (which it isn't, based on the map).
            // So, a rat cannot move into a trap or sanctuary.
            return !arena.isTrap(to) && !arena.isSanctuary(to);
        }
        return false;
    }
}
