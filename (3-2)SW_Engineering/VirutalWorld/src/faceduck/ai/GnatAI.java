package faceduck.ai;

import java.util.Random;

import faceduck.actors.Grass;
import faceduck.commands.BreedCommand;
import faceduck.commands.EatCommand;
import faceduck.commands.MoveCommand;
import faceduck.skeleton.interfaces.AI;
import faceduck.skeleton.interfaces.Actor;
import faceduck.skeleton.interfaces.Command;
import faceduck.skeleton.interfaces.World;
import faceduck.skeleton.util.Direction;
import faceduck.skeleton.util.Location;
import faceduck.skeleton.util.Util;

/**
 * The AI for a Gnat. This AI will pick a random direction and then return a
 * command which moves in that direction.
 *
 * This class serves as a simple example for how other AIs should be
 * implemented.
 */
public class GnatAI implements AI {
	// a Location where an actor is located.
	private static Location loc = null;
	// a Direction where an actor do act toward.
	private static Direction dir = null;

	/**
	 * constructor for RabbitAI
	 */
	public GnatAI() {
	}

	/**
	 * {@inheritDoc}
	 * 
	 * {@link Gnat} do action in the {@link world}.
	 * 
	 * Especially Gnat can only do move.
	 * 
	 * In case of move, It choose random adjacent direction and if there are no
	 * object(empty) in that direction, return {@link MoveCommand} toward that
	 * direction. If there are no empty adjacent {@link Location}, then return null.
	 * 
	 * @throws NullPointerException
	 *             actor cannot be located in null.
	 */
	@Override
	public Command act(World world, Actor actor) {
		loc = world.getLocation(actor);
		if (loc == null)
			throw new NullPointerException("Actor cannot be located in null");
		for (int i = 0; i < 10; i++) { // move
			dir = Util.randomDir();
			if (world.isValidLocation(new Location(loc, dir))) {
				Location tmp = new Location(loc, dir);
				if (world.getThing(tmp) == null)
					return new MoveCommand(dir);
			}
		}
		// if not thing to do, then return null.
		return null;
	}
}
