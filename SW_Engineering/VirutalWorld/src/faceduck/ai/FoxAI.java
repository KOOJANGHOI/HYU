package faceduck.ai;

import java.util.Random;

import faceduck.actors.Grass;
import faceduck.actors.RabbitImpl;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Animal;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.Rabbit;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

public class FoxAI extends AbstractAI implements AI {
	// a Random object to choose random number.
	private static Random generator = null;
	// a Location where an actor is located.
	private static Location loc = null;
	// a Direction where an actor do act toward.
	private static Direction dir = null;

	/**
	 * constructor for FoxAI
	 */
	public FoxAI() {
	}

	/**
	 * {@inheritDoc}
	 * 
	 * {@link Fox} do action in the {@link world}.
	 * 
	 * Especially Fox do move, eat, breed.
	 * 
	 * In case of move, It choose random adjacent direction and if there are no
	 * object(empty) in that direction, return {@link MoveCommand} toward that
	 * direction. If there are no empty adjacent {@link Location}, then return null.
	 * 
	 * In case of eat, It choose random adjacent direction and if there are
	 * {@link Grass} or {@link Rabbit} in that direction, return {@link EatCommand}
	 * toward that direction. If there are no empty adjacent {@link Grass} or
	 * {@link Rabbit}, then return null.
	 * 
	 * In case of breed, It choose random adjacent direction and if there are no
	 * object(empty) in that direction and Rabbit has sufficient energy, return
	 * {@link BreedCommand} toward that direction If there are no empty adjacent
	 * {@link Location} or Rabbit has insufficient energy, then return null.
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
		int choose = generator.nextInt(22);
		if (choose < 6) { // move
			for (int i = 0; i < 4; i++) {
				dir = Util.randomDir();
				if (world.isValidLocation(new Location(loc, dir))) {
					Location tmp = new Location(loc, dir);
					if (world.getThing(tmp) == null)
						return new MoveCommand(dir);
				}
			}
		} else if (choose < 18) { // eat
			for (int i = 0; i < 4; i++) {
				dir = Util.randomDir();
				if (world.isValidLocation(new Location(loc, dir))) {
					Location tmp = new Location(loc, dir);
					if (world.getThing(tmp) instanceof Grass || world.getThing(tmp) instanceof RabbitImpl)
						return new EatCommand(dir);
				}
			}
		} else {
			for (int i = 0; i < 4; i++) { // breed
				dir = Util.randomDir();
				if (world.isValidLocation(new Location(loc, dir))) {
					Location tmp = new Location(loc, dir);
					Animal ani = (Animal) actor;
					if (world.getThing(tmp) == null && ani.getEnergy() >= ani.getBreedLimit())
						return new BreedCommand(dir);
				}
			}
		}
		// if not thing to do, then return null.
		return null;
	}
}
