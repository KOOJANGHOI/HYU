package faceduck.actors;

import faceduck.ai.ElephantAI;
import faceduck.ai.FoxAI;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Elephant;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

public class ElephantImpl implements Elephant {
	private static int ELEPHANT_CURRENT_ENERGY = 0;// current energy of Elephant.
	private static final int ELEPHANT_MAX_ENERGY = 500;// Elephant's max energy.
	private static final int ELEPHANT_VIEW_RANGE = 5;// Elephant's view range.
	private static final int ELEPHANT_BREED_LIMIT = ELEPHANT_MAX_ENERGY * 1 / 5;// Elephant's breed limit.
	private static final int ELEPHANT_COOL_DOWN = 10;// Elephant's cool down.
	private static final int ELEPHANT_INITIAL_ENERGY = ELEPHANT_MAX_ENERGY * 1 / 2;// Elephant's initial energy.
	
	private static ElephantAI ai = null;// ElephantAI for action.
	private static Command com = null;// Command object to be returned in AI's act method.
	private static Location loc = null;// a Location where an actor is located.
	private static Direction dir = null;// a Direction where an actor do act toward.

	/**
	 * constructor for {@link ElephantImpl}
	 * 
	 * It's current energy is initialized to max energy. and the ElephantAI is
	 * declared.
	 * 
	 */
	public ElephantImpl() {
		this.ELEPHANT_CURRENT_ENERGY = this.ELEPHANT_INITIAL_ENERGY;
		this.ai= new ElephantAI();
	}
	
	/**
	 * constructor for {@link ElephantImpl}
	 * 
	 * In case of {@link BreedCommand}, It's current energy is initialized to max
	 * energy/2. and the ElephantAI is declared.
	 * 
	 */
	public ElephantImpl(int ELEPHANT_PARENT_ENERGY) {
		this.ELEPHANT_CURRENT_ENERGY = ELEPHANT_PARENT_ENERGY/2;
		this.ai = new ElephantAI();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEnergy() {
		return this.ELEPHANT_CURRENT_ENERGY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxEnergy() {
		return this.ELEPHANT_MAX_ENERGY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBreedLimit() {
		return this.ELEPHANT_BREED_LIMIT;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * a Location is declared which indicate a {@link Direction} to eat
	 * and because {@link ElephantImpl} can only eat {@link Grass}, declare new grass which is located in location above.
	 * finally, a ElephantImpl eat grass by removing the grass in the {@link World}.(by calling remove method).
	 * and ElephantImpl got grass's energy value.
	 * If ElephantImpl energy exceed Its max energy, Its current energy will be same as Its max energy.
	 * 
	 */
	@Override
	public void eat(World world, Direction dir) {
		if (!world.isValidLocation(new Location(loc, dir)))
			return ;
		Location toeat = new Location(loc, dir);
		if(world.getThing(toeat) == null || !(world.getThing(toeat) instanceof Grass))
			return ;
		Grass eaten = (Grass) world.getThing(toeat);
		this.ELEPHANT_CURRENT_ENERGY += eaten.getEnergyValue();
		if (this.ELEPHANT_CURRENT_ENERGY > this.ELEPHANT_MAX_ENERGY)
			this.ELEPHANT_CURRENT_ENERGY = this.ELEPHANT_MAX_ENERGY;
		world.remove(eaten);
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
	 * then, call add method in {@link World} to add new {@link ElephantBabyImpl}.
	 * the new ElephantBabyImpl is created by calling constructor of ElephantBabyImpl with parent's current energy.
	 * then parent ElephantImpl energy decrease in half.
	 * 
	 */
	@Override
	public void breed(World world, Direction dir) {
		Location tobreed = new Location(loc,dir);
		world.add(new ElephantBabyImpl(this.ELEPHANT_CURRENT_ENERGY), tobreed);
		this.ELEPHANT_CURRENT_ENERGY /= 2;
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * {@link ElephantImpl} do action in the {@link world}.
	 *
	 * Especially ElephantImpl can do eat, move, breed.
	 * If {@link ElephantImplAI}'s act method is called, that return a {@link Command}.
	 * 
	 * If ElephantImpl has not enough energy to action, it dies by calling remove method in {@link World}.
	 * It not, ElephantImpl energy decrement by 1.
	 * 
	 * If the command is null, FoxImpl do nothing.
	 * If the command is instance of {@link EatCommand}, it calls eat method toward all adjacent direction. 
	 * If the command is instance of {@link MoveCommand}, it calls move method toward that direction. 
	 * If the command is instance of {@link BreedCommand}, it calls breed method toward that direction. 
	 * If the command is not in above case, return null.
	 * 
	 * If ElephantImpl has not enough energy just after action, it dies by calling remove method in {@link World}.
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
		if(this.ELEPHANT_CURRENT_ENERGY <= 0) {
			world.remove(this);
			return ;
		}
		com = ai.act(world, this);
		this.ELEPHANT_CURRENT_ENERGY--;
		if(com == null)
			return ;
		dir = com.getDirection();
		if(com instanceof EatCommand) {
			eat(world,Direction.NORTH);
			eat(world,Direction.WEST);
			eat(world,Direction.SOUTH);
			eat(world,Direction.EAST);
		} else if(com instanceof MoveCommand) {
			move(world,dir);
		} else if(com instanceof BreedCommand) {
			breed(world,dir);
		} else {
			throw new NullPointerException("Rabbit must do at least one action.");
		}
		if(this.ELEPHANT_CURRENT_ENERGY <= 0) {
			world.remove(this);
			return ;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getViewRange() {
		return this.ELEPHANT_VIEW_RANGE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCoolDown() {
		return this.ELEPHANT_COOL_DOWN;
	}
}
