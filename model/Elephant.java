package jungleGame.model;

import java.util.List;

public class Elephant extends Animal{
	public Elephant(int player, Position position) {
        super(8, player, position,"E"); 
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

        // Elephant moves one square horizontally or vertically
        boolean isOneStepMove = ((dx == 1 && dy == 0) || (dx == 0 && dy == 1));

        if (isOneStepMove) {
            // Elephant can move into a lake only if the destination is a lake and no other animal is there
            // However, the standard rule is that only rats can be in lakes. Elephants cannot enter lakes.
            return !arena.isLake(to);
        }
        return false;
    }

    @Override
    public boolean canCapture(Animal target, Arena arena) {
        // Special rule: Elephant cannot capture Rat
        if (target instanceof Rat) {
            return false;
        }
        return super.canCapture(target, arena); // Use base animal capture logic for others
    }
}
