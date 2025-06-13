package jungleGame.model;

import java.util.List;

public abstract class Animal {
	private int rank; 
    private int player; 
    private Position position;
    private String name;

    public Animal(int rank, int player, Position position, String name) {
        this.rank = rank;
        this.player = player;
        this.position = position;
        this.name = name;
    }
    
    public String getName() {
		return name;
	}
    
    public int getRank() {
    	return rank; 
    }
    public int getPlayer(){
    	return player; 
    }
    public Position getPosition(){
    	return position; 
    }
    public void setPosition(Position position){
    	this.position = position; 
    
    }

    public abstract boolean canMoveTo(Position to, Arena arena, List<Animal> animals);

    public boolean canCapture(Animal target){
        if (target == null) return false;
        return this.rank <= target.rank || (this.rank == 8 && target.rank == 1);
    }
    

    public boolean canCapture(Animal target, Arena arena) {
        if (target == null) return false;

        if (this.getPlayer() == target.getPlayer()) {
            return false;
        }

        int thisEffectiveRank = this.rank;
        int targetEffectiveRank = target.rank;

        if (arena.isTrap(target.getPosition())) {
            targetEffectiveRank = 0;
        }

        if (this.rank == 1 && target.rank == 8) {
            return true;
        }

        if (this.rank == 8 && target.rank == 1) { // assuming Elephant is rank 8, Rat is rank 1
            return false;
        }
        
        if (arena.isLake(this.getPosition())) {
            if (this.rank != 1) { 
                return false; 
            }
            return thisEffectiveRank >= targetEffectiveRank;
        }

        if (arena.isLake(target.getPosition()) && !(this instanceof Rat)) {
            return false;
        }

        return thisEffectiveRank >= targetEffectiveRank;
    }

    @Override
    public String toString() {
        return "Animal{rank=" + rank + ", player=" + player + ", position=" + position + "}";
    }
    
    protected boolean canCaptureOrMove(Position to, Arena arena, List<Animal> animals) {
        Animal target = arena.getAnimalAt(to, animals);
        if (target == null) {
            return true; 
        } else if (target.getPlayer() == this.getPlayer()) {
            return false; 
        } else {
            return canCapture(target, arena); 

        }
    }
    
	
}
