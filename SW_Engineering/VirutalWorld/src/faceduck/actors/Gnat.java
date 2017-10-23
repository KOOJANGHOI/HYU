package faceduck.actors;

import faceduck.ai.GnatAI;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.skeleton.interfaces.World;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;

/**
 * This is a simple implementation of a Gnat. It never loses energy and moves in
 * random directions.
 */
public class Gnat implements Animal {
	private static int GNAT_CURRENT_ENERGY; // current energy of Gnat.
	private static GnatAI ai = null; // GnatAI for action.
	private static Command com = null; // Command object to be returned in AI's act method.
	private static Location loc = null;// a Location where an actor is located.
	private static Direction dir = null;// a Direction where an actor do act toward.
	private static final int MAX_ENERGY = 10; // Gnat's max energy.
	private static final int VIEW_RANGE = 1;// Gnat's view_range.
	private static final int BREED_LIMIT = 0;// Gnat's breed_limit .
	private static final int COOL_DOWN = 0;// Gnat's cool_down.

	/**
	 * constructor for {@link Gnat}
	 * 
	 * It's current energy is initialized to max energy. and the GantAI is declared.
	 * 
	 */
	public Gnat(int i) {
		this.GNAT_CURRENT_ENERGY = MAX_ENERGY;
		this.ai = new GnatAI();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * {@link Gnat} do action in the {@link world}.
	 * 
	 * Especially Gnat can only do move.
	 * If {@link GnatAI}'s act method is called, that return a {@link Command}.
	 * If the command is null, Gnat do nothing.
	 * If the command is instance of {@link MoveCommand}, it calls move method toward that direction. 
	 * 
	 * @throws throw NullPointerException
	 * 			world must not be null.
	 * 
	 * @throws NullPointerException 
	 * 			actor cannot be located in null and invalid.
	 */
	@Override
	public void act(World world) {
		if (world == null)
			throw new NullPointerException("World must not be null.");
		loc = world.getLocation(this);
		if (loc == null || !world.isValidLocation(loc)) {
			throw new NullPointerException("Location is not valid.");
		}
		com = ai.act(world, this);
		if (com == null)
			return;
		if (com instanceof MoveCommand) {
			dir = com.getDirection();
			move(world, dir);
			return;
		}
		return;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getViewRange() {
		return Gnat.VIEW_RANGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCoolDown() {
		return Gnat.COOL_DOWN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEnergy() {
		return Gnat.GNAT_CURRENT_ENERGY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxEnergy() {
		return Gnat.MAX_ENERGY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getBreedLimit() {
		return Gnat.BREED_LIMIT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void eat(World world, Direction dir) {
		// Gnat cannot to eat
		return;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * a Location is declared which indicate a {@link Direction} to move.
	 * then, call remove method in {@link World} to delete {@link Animal}.
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
	 */
	@Override
	public void breed(World world, Direction dir) {
		// Gnat cannot to eat
		return;
	}

}
