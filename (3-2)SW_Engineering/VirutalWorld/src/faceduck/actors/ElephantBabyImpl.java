package faceduck.actors;

import faceduck.ai.ElephantAI;
import faceduck.ai.ElephantBabyAI;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Elephant;
import faceduck.skeleton.interfaces.ElephantBaby;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

public class ElephantBabyImpl implements ElephantBaby {
	private static int ELEPHANTBABY_CURRENT_ENERGY = 0;// current energy of ElephantBaby.
	private static final int ELEPHANTBABY_MAX_ENERGY = 300;// ElephantBaby's max energy.
	private static final int ELEPHANTBABY_VIEW_RANGE = 5;// ElephantBaby's view range.
	private static final int ELEPHANTBABY_BREED_LIMIT = 0;// ElephantBaby's breed limit.
	private static final int ELEPHANTBABY_COOL_DOWN = 10;// ElephantBaby's cool down.
	private static final int ELEPHANTBABY_INITIAL_ENERGY = ELEPHANTBABY_MAX_ENERGY * 1 / 2;// ElephantBaby's initial energy.
	
	private static ElephantBabyAI ai = null;// ElephantBabyAI for action.
	private static Command com = null;// Command object to be returned in AI's act method.
	private static Location loc = null;// a Location where an actor is located.
	private static Direction dir = null;// a Direction where an actor do act toward.

	/**
	 * constructor for {@link ElephantBabyImpl}
	 * 
	 */
	public ElephantBabyImpl() {
		return ;
	}
	
	/**
	 * constructor for {@link ElephantImpl}
	 * 
	 * In case of {@link BreedCommand}, It's current energy is initialized to max
	 * energy/2. and the ElephantAI is declared.
	 * 
	 */
	public ElephantBabyImpl(int ELEPHANTBABY_PARENT_ENERGY) {
		this.ELEPHANTBABY_CURRENT_ENERGY = ELEPHANTBABY_PARENT_ENERGY/2;
		this.ai = new ElephantBabyAI();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEnergy() {
		return this.ELEPHANTBABY_CURRENT_ENERGY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxEnergy() {
		return this.ELEPHANTBABY_MAX_ENERGY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBreedLimit() {
		return this.ELEPHANTBABY_BREED_LIMIT;
	}
	
	
	/**
	 * {@inheritDoc}
	 * 
	 * a Location is declared which indicate a {@link Direction} to eat
	 * and because {@link ElephantBabyImpl} can only eat {@link Grass}, declare new grass which is located in location above.
	 * finally, a ElephantBabyImpl eat grass by removing the grass in the {@link World}.(by calling remove method).
	 * and ElephantBabyImpl got grass's energy value.
	 * If ElephantBabyImpl energy exceed Its max energy, Its current energy will be same as Its max energy.
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
		this.ELEPHANTBABY_CURRENT_ENERGY += eaten.getEnergyValue();
		if (this.ELEPHANTBABY_CURRENT_ENERGY > this.ELEPHANTBABY_MAX_ENERGY)
			this.ELEPHANTBABY_CURRENT_ENERGY = this.ELEPHANTBABY_MAX_ENERGY;
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
	 * {@link ElephantBabyImpl} can not do breed.
	 * so return ;
	 * 
	 */
	@Override
	public void breed(World world, Direction dir) {
		return ;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * {@link ElephantBabyImpl} do action in the {@link world}.
	 *
	 * Especially ElephantBabyImpl can do eat, move.
	 * If {@link ElephantBabyImpl}'s act method is called, that return a {@link Command}.
	 * 
	 * If ElephantBabyImpl has not enough energy to action, it dies by calling remove method in {@link World}.
	 * It not, ElephantBabyImpl energy decrement by 1.
	 * 
	 * If the command is null, FoxImpl do nothing.
	 * If the command is instance of {@link EatCommand}, it calls eat method toward all adjacent direction. 
	 * If the command is instance of {@link MoveCommand}, it calls move method toward that direction. 
	 * If the command is instance of {@link BreedCommand}, it return.
	 * If the command is not in above case, return.
	 * 
	 * If ElephantBabyImpl has not enough energy just after action, it dies by calling remove method in {@link World}.
	 * If ElephantBabyImpl has enough energy to growth, It grows as an adult elephant.
	 * It is implemented by adding new {@link ElephantImpl} in same {@link Location} just after removing this ElephantImpl.
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
		if(this.ELEPHANTBABY_CURRENT_ENERGY <= 0) {
			world.remove(this);
			return ;
		}
		com = ai.act(world, this);
		this.ELEPHANTBABY_CURRENT_ENERGY--;
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
			return ;
		} else {
			throw new NullPointerException("Rabbit must do at least one action.");
		}
		if(this.ELEPHANTBABY_CURRENT_ENERGY <= 0) {
			world.remove(this);
			return ;
		}
		if(this.ELEPHANTBABY_CURRENT_ENERGY >= (this.ELEPHANTBABY_INITIAL_ENERGY*3)/4) {
			Location temp = loc;
			world.remove(this);
			world.add(new ElephantImpl(), temp);
			return ;
		}
		return ;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getViewRange() {
		return this.ELEPHANTBABY_VIEW_RANGE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCoolDown() {
		return this.ELEPHANTBABY_COOL_DOWN;
	}
}
