package faceduck.actors;

import faceduck.ai.FoxAI;
import faceduck.ai.RabbitAI;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Fox;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

public class FoxImpl implements Fox {
	private static int FOX_CURRENT_ENERGY = 0;// current energy of Fox.
	private static final int FOX_MAX_ENERGY = 160;// Fox's max energy.
	private static final int FOX_VIEW_RANGE = 5;// Fox's view range.
	private static final int FOX_BREED_LIMIT = FOX_MAX_ENERGY * 1 / 4;// Fox's breed limit.
	private static final int FOX_COOL_DOWN = 10;// Fox's cool down.
	private static final int FOX_INITIAL_ENERGY = FOX_MAX_ENERGY * 1 / 2;// Fox's initial energy.
	
	private static FoxAI ai = null;// FoxAI for action.
	private static Command com = null;// Command object to be returned in AI's act method.
	private static Location loc = null;// a Location where an actor is located.
	private static Direction dir = null;// a Direction where an actor do act toward.

	/**
	 * constructor for {@link FoxImpl}
	 * 
	 * It's current energy is initialized to max energy. and the FoxAI is
	 * declared.
	 * 
	 */
	public FoxImpl() {
		this.FOX_CURRENT_ENERGY = this.FOX_INITIAL_ENERGY;
		this.ai= new FoxAI();
	}
	
	/**
	 * constructor for {@link FoxImpl}
	 * 
	 * In case of {@link BreedCommand}, It's current energy is initialized to max
	 * energy/2. and the FoxAI is declared.
	 * 
	 */
	public FoxImpl(int FOX_PARENT_ENERGY) {
		this.FOX_CURRENT_ENERGY = FOX_PARENT_ENERGY/2;
		this.ai = new FoxAI();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEnergy() {
		return this.FOX_CURRENT_ENERGY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxEnergy() {
		return this.FOX_MAX_ENERGY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBreedLimit() {
		return this.FOX_BREED_LIMIT;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * a Location is declared which indicate a {@link Direction} to eat.
	 * and because {@link FoxImpl} can only eat {@link Grass} or {@link RabbitImpl},
	 * It should be check type of {@link Edible}.
	 * 
	 * If the edible object which is located in that location is Grass,
	 * declare new grass which is located in location above.
	 * finally, a FoxImpl eat grass by removing the grass in the {@link World}.(by calling remove method).
	 * and FoxImpl got grass's energy value.
	 * 
	 * And if the edible object which is located in that location is RabbitImpl,
	 * declare new RabbitImpl which is located in location above.
	 * finally, a FoxImpl eat RabbitImpl by removing the RabbitImpl in the {@link World}.(by calling remove method).
	 * and FoxImpl got RabbitImpl's energy value.
	 * 
	 * If FoxImpl's energy exceed Its max energy, Its current energy will be same as Its max energy.
	 * 
	 */
	@Override
	public void eat(World world, Direction dir) {
		Location toeat = new Location(loc,dir);
		if(world.getThing(toeat) instanceof Grass) {
			Grass eaten = (Grass) world.getThing(toeat);
			this.FOX_CURRENT_ENERGY += eaten.getEnergyValue();
			world.remove(eaten);	
		} else if(world.getThing(toeat) instanceof RabbitImpl) {
			RabbitImpl eaten = (RabbitImpl)world.getThing(toeat);	
			this.FOX_CURRENT_ENERGY += eaten.getEnergyValue();
			world.remove(eaten);	
		}
		if(this.FOX_CURRENT_ENERGY > this.FOX_MAX_ENERGY)
			this.FOX_CURRENT_ENERGY = this.FOX_MAX_ENERGY;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * a Location is declared which indicate a {@link Direction} to move.
	 * then, call remove method to delete {@link Animal}.
	 * and call add method to add same animal into new location.
	 * 
	 */
	@Override
	public void move(World world, Direction dir) {
		Location togo = new Location(loc,dir);
		world.remove(this);
		world.add(this,togo);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * a Location is declared which indicate a {@link Direction} to breed.
	 * then, call add method in {@link World} to add new {@link FoxImpl}.
	 * the new FoxImpl is created by calling constructor of FoxImpl with parent's current energy.
	 * then parent FoxImpl's energy decrease in half.
	 * 
	 */
	@Override
	public void breed(World world, Direction dir) {
		Location tobreed = new Location(loc,dir);
		world.add(new FoxImpl(this.FOX_CURRENT_ENERGY), tobreed);
		this.FOX_CURRENT_ENERGY /= 2;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * {@link FoxImpl} do action in the {@link world}.
	 *
	 * Especially FoxImpl can do eat, move, breed.
	 * If {@link FoxImplAI}'s act method is called, that return a {@link Command}.
	 * 
	 * If FoxImpl has not enough energy to action, it dies by calling remove method in {@link World}.
	 * It not, FoxImpl's energy decrement by 1.
	 * 
	 * If the command is null, FoxImpl do nothing.
	 * If the command is instance of {@link EatCommand}, it calls eat method toward that direction. 
	 * If the command is instance of {@link MoveCommand}, it calls move method toward that direction. 
	 * If the command is instance of {@link BreedCommand}, it calls breed method toward that direction. 
	 * If the command is not in above case, return null.
	 * 
	 * If FoxImpl has not enough energy just after action, it dies by calling remove method in {@link World}.
	 * 
	 * @throws throw NullPointerException
	 * 			world must not be null.
	 * 
	 * @throws NullPointerException 
	 * 			actor cannot be located in null and invalid.
	 */
	@Override
	public void act(World world) {
		if (world == null) {
			throw new NullPointerException("World must not be null.");
		}
		loc = world.getLocation(this);
		if(loc == null || !world.isValidLocation(loc)) {
			throw new NullPointerException("Location is not valid.");
		}
		if(this.FOX_CURRENT_ENERGY <= 0) {
			world.remove(this);
			return ;
		}
		com = ai.act(world, this);
		this.FOX_CURRENT_ENERGY--;
		if(com == null)
			return ;
		dir = com.getDirection();
		if(com instanceof EatCommand) {
			eat(world,dir);
		} else if(com instanceof MoveCommand) {
			move(world,dir);
		} else if(com instanceof BreedCommand) {
			breed(world,dir);
		} else {
			throw new NullPointerException("Rabbit must do at least one action.");
		}
		if(this.FOX_CURRENT_ENERGY <= 0) {
			world.remove(this);
			return ;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getViewRange() {
		return this.FOX_VIEW_RANGE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCoolDown() {
		return this.FOX_COOL_DOWN;
	}
}
