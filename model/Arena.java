package jungleGame.model;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class Arena {
	private static final int ROWS = 9;
    private static final int COLS = 7;
    private Set<Position> lakes;
    private Set<Position> traps;
    private Set<Position> sanctuaries;

    public Arena() {
        lakes = new HashSet<>();
        traps = new HashSet<>();
        sanctuaries = new HashSet<>();
        initializeArena();
    }

    private void initializeArena() {
        lakes.add(new Position(3, 1));
        lakes.add(new Position(3, 2));
        lakes.add(new Position(4, 1));
        lakes.add(new Position(4, 2));
        lakes.add(new Position(5, 1));
        lakes.add(new Position(5, 2));

        lakes.add(new Position(3, 4));
        lakes.add(new Position(3, 5));
        lakes.add(new Position(4, 4));
        lakes.add(new Position(4, 5));
        lakes.add(new Position(5, 4));
        lakes.add(new Position(5, 5));

        sanctuaries.add(new Position(0, 3));
        sanctuaries.add(new Position(8, 3)); 

        traps.add(new Position(0, 2));
        traps.add(new Position(0, 4));
        traps.add(new Position(1, 3));

        traps.add(new Position(7, 3)); 
        traps.add(new Position(8, 2));
        traps.add(new Position(8, 4));
    }

    public boolean isLake(Position pos) {
        return lakes.contains(pos);
    }

    public boolean isTrap(Position pos) {
        return traps.contains(pos);
    }

    public boolean isSanctuary(Position pos) {
        return sanctuaries.contains(pos);
    }

    public boolean isValidPosition(Position pos) {
        return pos.getX() >= 0 && pos.getX() < ROWS &&
               pos.getY() >= 0 && pos.getY() < COLS;
    }
    
    public boolean isSpecialPosition(Position pos) {
        return isLake(pos) || isTrap(pos) || isSanctuary(pos);
    }
    
    public Animal getAnimalAt(Position pos, List<Animal> animals) {
        for (Animal animal : animals) {
            if (animal.getPosition().equals(pos)) {
                return animal;
            }
        }
        return null;
    }

    public boolean hasRatInLakeBetween(Position from, Position to, List<Animal> animals) {
        if (from.getX() != to.getX() && from.getY() != to.getY()) {
            return false; 
        }

        if (from.getX() == to.getX()) { 
            int minY = Math.min(from.getY(), to.getY());
            int maxY = Math.max(from.getY(), to.getY());
            for (int y = minY + 1; y < maxY; y++) {
                Position current = new Position(from.getX(), y);
                if (isLake(current)) {
                    Animal animalAtPos = getAnimalAt(current, animals);
                    if (animalAtPos != null && animalAtPos instanceof Rat) {
                        return true;
                    }
                }
            }
        } else { 
            int minX = Math.min(from.getX(), to.getX());
            int maxX = Math.max(from.getX(), to.getX());
            for (int x = minX + 1; x < maxX; x++) {
                Position current = new Position(x, from.getY());
                if (isLake(current)) {
                    Animal animalAtPos = getAnimalAt(current, animals);
                    if (animalAtPos != null && animalAtPos instanceof Rat) {
                        return true; 
                    }
                }
            }
        }
        return false;
    }

   
    public boolean isLakeBetween(Position from, Position to) {
        if (from.getX() != to.getX() && from.getY() != to.getY()) {
            return false; // ليس حركة أفقية أو عمودية
        }

        if (Math.abs(from.getX() - to.getX()) <= 1 && Math.abs(from.getY() - to.getY()) <= 1) {
            return false; 
        }

        if (from.getX() == to.getX()) {
            int minY = Math.min(from.getY(), to.getY());
            int maxY = Math.max(from.getY(), to.getY());
            for (int y = minY + 1; y < maxY; y++) {
                if (!isLake(new Position(from.getX(), y))) {
                    return false; 
                }
            }
            return true;
        } else { 
            int minX = Math.min(from.getX(), to.getX());
            int maxX = Math.max(from.getX(), to.getX());
            for (int x = minX + 1; x < maxX; x++) {
                if (!isLake(new Position(x, from.getY()))) {
                    return false; 
                }
            }
            return true;
        }
    }
    
    
    public boolean isOccupied(Position pos, List<Animal> animals) {
        return getAnimalAt(pos, animals) != null;
    }
    public int getSanctuaryOwner(Position pos) {
        if (pos.equals(new Position(0, 3))) {
            return 2; 
        }
        if (pos.equals(new Position(8, 3))) {
            return 1; 
        }
        return 0; 
    }

}
