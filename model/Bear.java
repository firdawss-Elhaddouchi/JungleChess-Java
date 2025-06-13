package jungleGame.model;

import java.util.List;

public class Bear extends Animal {
    public Bear(int player, Position position) {
        super(6, player, position,"B");
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

        boolean isOneStepMove = ((dx == 1 && dy == 0) || (dx == 0 && dy == 1));
        if (isOneStepMove) {
            // Leopard cannot enter lakes
            return !arena.isLake(to);
        }
        return false;
    }
}
