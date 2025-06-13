package jungleGame.model;

import java.util.List;

public class Leopard extends Animal {
	public Leopard(int player, Position position) {
        super(5, player, position,"P");
    }

    @Override
    /****** 
     * public boolean canMoveTo(Position to, Arena arena, Animal[][] board) {
        Position from = getPosition();
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());

        return ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) &&
               !arena.isLake(to);
    }****/
    
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

        // Leopard moves one square horizontally or vertically
        boolean isOneStepMove = ((dx == 1 && dy == 0) || (dx == 0 && dy == 1));
        if (isOneStepMove) {
            // Leopard cannot enter lakes
            return !arena.isLake(to);
        }
        return false;
    }

}
