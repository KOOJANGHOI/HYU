package faceduck.actors;

import faceduck.ai.GnatAI;
import faceduck.ai.RabbitAI;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Edible;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

public class RabbitImpl implements Rabbit {
	private int RABBIT_CURRENT_ENERGY = 0;// current energy of Rabbit.
	private static final int RABBIT_MAX_ENERGY = 40;// Rabbit's max energy.
	private static final int RABBIT_VIEW_RANGE = 3;// Rabbit's view range.
	private static final int RABBIT_BREED_LIMIT = RABBIT_MAX_ENERGY * 1 / 2;// Rabbit's breed limit.
	private static final int RABBIT_ENERGY_VALUE = 20;// Rabbit's energy value.
	private static final int RABBIT_COOL_DOWN = 10; // Rabbit's cool down.
	private static final int RABBIT_INITIAL_ENERGY = RABBIT_MAX_ENERGY * 1 / 2; // Rabbit's initial energy.

	private RabbitAI ai = null; // RabbitAI for action.
	private static Command com = null;// Command object to be returned in AI's act method.
	private static Location loc = null;// a Location where an actor is located.
	private static Direction dir = null;// a Direction where an actor do act toward.

	/**
	 * constructor for {@link RabbitImpl}
	 * 
	 * It's current energy is initialized to max energy. and the RabbitAI is
	 * declared.
	 * 
	 */
	public RabbitImpl() {
		this.RABBIT_CURRENT_ENERGY = this.RABBIT_INITIAL_ENERGY;
		this.ai = new RabbitAI();
	}

	/**
	 * constructor for {@link RabbitImpl}
	 * 
	 * In case of {@link BreedCommand}, It's current energy is initialized to max
	 * energy/2. and the RabbitAI is declared.
	 * 
	 */
	public RabbitImpl(int RABBIT_PARENT_ENERGY) {
		this.RABBIT_CURRENT_ENERGY = RABBIT_PARENT_ENERGY / 2;
		this.ai = new RabbitAI();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEnergy() {
		return this.RABBIT_CURRENT_ENERGY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxEnergy() {
		return this.RABBIT_MAX_ENERGY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBreedLimit() {
		return this.RABBIT_BREED_LIMIT;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * a Location is declared which indicate a {@link Direction} to eat
	 * and because {@link RabbitImpl} can only eat {@link Grass}, declare new grass which is located in location above.
	 * finally, a RabbitImpl eat grass by removing the grass in the {@link World}.(by calling remove method).
	 * and RabbitImpl got grass's energy value.
	 * If RabbitImpl's energy exceed Its max energy, Its current energy will be same as Its max energy.
	 * 
	 */
	@Override
	public void eat(World world, Direction dir) {
		Location toeat = new Location(loc, dir);
		Grass eaten = (Grass) world.getThing(toeat);
		this.RABBIT_CURRENT_ENERGY += eaten.getEnergyValue();
		if (this.RABBIT_CURRENT_ENERGY > this.RABBIT_MAX_ENERGY)
			this.RABBIT_CURRENT_ENERGY = this.RABBIT_MAX_ENERGY;
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
		Location togo = new Location(loc, dir);
		world.remove(this);
		world.add(this, togo);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * a Location is declared which indicate a {@link Direction} to breed.
	 * then, call add method in {@link World} to add new {@link RabbitImpl}.
	 * the new RabbitImpl is created by calling constructor of RabbitImpl with parent's current energy.
	 * then parent RabbitImpl's energy decrease in half.
	 * 
	 */
	@Override
	public void breed(World world, Direction dir) {
		Location tobreed = new Location(loc, dir);
		world.add(new RabbitImpl(this.RABBIT_CURRENT_ENERGY), tobreed);
		this.RABBIT_CURRENT_ENERGY /= 2;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * {@link RabbitImpl} do action in the {@link world}.
	 *
	 * Especially RabbitImpl can do eat, move, breed.
	 * If {@link RabbitAI}'s act method is called, that return a {@link Command}.
	 * 
	 * If RabbitImpl has not enough energy to action, it dies by calling remove method in {@link World}.
	 * It not, RabbitImpl's energy decrement by 1.
	 * 
	 * If the command is null, RabbitImpl do nothing.
	 * If the command is instance of {@link EatCommand}, it calls eat method toward that direction. 
	 * If the command is instance of {@link MoveCommand}, it calls move method toward that direction. 
	 * If the command is instance of {@link BreedCommand}, it calls breed method toward that direction. 
	 * If the command is not in above case, return null.
	 * 
	 * If RabbitImpl has not enough energy just after action, it dies by calling remove method in {@link World}.
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
		if (loc == null || !world.isValidLocation(loc)) {
			throw new NullPointerException("Location is not valid.");
		}
		if (this.RABBIT_CURRENT_ENERGY <= 0) {
			world.remove(this);
			return;
		}
		com = ai.act(world, this);
		this.RABBIT_CURRENT_ENERGY--;
		if (com == null)
			return;
		dir = com.getDirection();
		if (com instanceof EatCommand) {
			eat(world, dir);
		} else if (com instanceof MoveCommand) {
			move(world, dir);
		} else if (com instanceof BreedCommand) {
			breed(world, dir);
		} else {
			throw new NullPointerException("Rabbit must do at least one action.");
		}
		if (this.RABBIT_CURRENT_ENERGY <= 0) {
			world.remove(this);
			return;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getViewRange() {
		return this.RABBIT_VIEW_RANGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCoolDown() {
		return this.RABBIT_COOL_DOWN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEnergyValue() {
		return this.RABBIT_ENERGY_VALUE;
	}

}
