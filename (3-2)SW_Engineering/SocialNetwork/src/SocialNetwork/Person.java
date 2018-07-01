package SocialNetwork;

public class Person {
	/**
	 * This class is for Person object
	 * It has only one value(name)
	 * 
	 * @version 1.00. September 2017
	 * @author KOOJANGHOI
	 * @see https://github.com/KOOJANGHOI
	 * @email kjanghoi@gmail.com
	 */
	
	private String name;
	
	// Constructor
	public Person(String name) {
		this.name  = name;
	}
	
	// getter for name
	public String getName() {
		return this.name;
	}
}
