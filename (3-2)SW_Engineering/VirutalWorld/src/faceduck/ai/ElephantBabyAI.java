package faceduck.ai;

import java.util.Random;

import faceduck.actors.Grass;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

public class ElephantBabyAI extends AbstractAI implements AI{
	// a Random object to choose random number.
	private static Random generator = null;
	// a Location where an actor is located.
	private static Location loc = null;
	// a Direction where an actor do act toward.
	private static Direction dir = null;

	/**
	 * constructor for ElephantBabyAI
	 */
	public ElephantBabyAI()  {
	}

	/**
	 * {@inheritDoc}
	 * 
	 * {@link ElephantBaby} do action in the {@link world}.
	 * 
	 * Especially ElephantBaby do move, eat.
	 * 
	 * In case of move, It choose random adjacent direction and if there are no
	 * object(empty) in that direction, return {@link MoveCommand} toward that
	 * direction. If there are no empty adjacent {@link Location}, then return null.
	 * 
	 * In case of eat, It checks all adjacent direction and if there are
	 * {@link Grass} in adjacent direction, return {@link EatCommand} toward that
	 * direction. If there are no empty adjacent {@link Grass} then return null.
	 * 
	 * @throws NullPointerException
	 *             actor cannot be located in null.
	 */
	@Override
	public Command act(World world, Actor actor) {
		loc = world.getLocation(actor);
		if (loc == null)
			throw new NullPointerException("Actor cannot be located in null");
		generator = new Random();
		int choose = generator.nextInt(25);
		if (choose < 5) { // move
			for (int i = 0; i < 4; i++) {
				dir = Util.randomDir();
				if (world.isValidLocation(new Location(loc, dir))) {
					Location tmp = new Location(loc, dir);
					if (world.getThing(tmp) == null)
						return new MoveCommand(dir);
				}
			}
		} else { // eat
			for (int i = 0; i < 4; i++) {
				dir = Util.randomDir();
				if (world.isValidLocation(new Location(loc, dir))) {
					Location tmp = new Location(loc, dir);
					if (world.getThing(tmp) instanceof Grass)
						return new EatCommand(dir);
				}
			}
		} 
		// if not thing to do, then return null.
		return null;
	}
}
