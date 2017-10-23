package SocialNetwork;

public class SocialNewwork {
	public static void main(String[] args) {
		FriendGraph graph = new FriendGraph(); 
		Person john = new Person("John"); 
		Person tom = new Person("Tom"); 
		Person jane = new Person("Jane"); 
		Person marry = new Person("Marry"); 
		graph.addPerson(john); 
		graph.addPerson(tom);
		graph.addPerson(jane);
		graph.addPerson(marry);
		graph.addFriendship("John", "Tom");
		graph.addFriendship("Tom", "Jane"); 
		System.out.println(graph.getDistance("John", "Tom"));// should print 1 
		System.out.println(graph.getDistance("John", "Jane")); // should print 2 
		System.out.println(graph.getDistance("John", "John")); // should print 0 
		System.out.println(graph.getDistance("Marry", "Marry")); // should print -1
		
	}
}
