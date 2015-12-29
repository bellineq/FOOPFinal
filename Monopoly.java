import java.util.*;

public class Monopoly{

	public static void theGame(){
		Computer pc = new Computer(10,1,3,22000);
		pc.play();
	
	}
	
	public static void main(String[] argv){
		
		theGame();
	}

}


class Computer{
	private Map map;
	private int mapSize;
	private ArrayList<Character> chars;
	private int charNum;
	private int initialMoney;
	
	Computer(int newMapSize, int playerNum, int AINum, int newInitialMoney){
		mapSize = newMapSize;
		charNum = playerNum + AINum;
		initialMoney = newInitialMoney;
		chars = new ArrayList<Character> ();

		createMap(mapSize);
		createAI(AINum);
		createPlayer(playerNum);

			
	}
		
	public void createMap(int num){
		for(int i=0;i<num;i++){
			String name = "Property_";
			name.concat(Integer.toString(i));
			int price = (i*500)%5000;
			int rent = (i*200)%2000;
			Property p = new Property(name,i,price,rent);
			map.addSpace(p);
		}
	}
	
	public void createAI(int num){
		for(int i=0;i<num;i++){
			String name = "Computer_";
			name.concat(Integer.toString(i));
			AI ai = new AI(name,initialMoney,mapSize);
			chars.add(ai);
		}
	}

	public void createPlayer(int num){
		for(int i=0;i<num;i++){
			String name = DisplayCenter.askToNamePlayer();
			Player p = new Player(name,initialMoney,mapSize);
			chars.add(p);
		}
	}

	public void round(Character c){
	// what happen in one round with some character
		Dice dice = new Dice();
		int step = dice.roll();
		c.move(step);
		int position = c.getPosition();
		Space s = map.getSpaces().get(position);
		DisplayCenter.showMove(c,step,position);
		DisplayCenter.showSpaceStatus(s);
		s.trigger(c);
	}
	
	public ArrayList<Character> findBrokeChar(){
		ArrayList<Character> brokeChar = new ArrayList<Character> ();
		for(int i=0; i<chars.size(); i++){
			Character c = chars.get(i);
			if (c.getMoney()<0){
				brokeChar.add(c);
			}
		}
		return brokeChar;
	}

	
	public void play(){
		boolean someOneBroke = false;
		while(!someOneBroke){
			for(int i =0;i<chars.size();i++){
				Character c = chars.get(i);
				round(c);
				ArrayList<Character> brokeChar = findBrokeChar();
				if (brokeChar.size()>0){
					someOneBroke = true;
					DisplayCenter.showLoser(brokeChar);
					break;
				}						
			}
		}
		DisplayCenter.showEndGame();
	}



}// End Computer Class


class Map{
	private ArrayList<Space> spaces;
	private int size;
	
	Map(){
		spaces = new ArrayList<Space>();
		size = 0;
	}
	
	public void addSpace(Space s){
		spaces.add(s);
		size = spaces.size();
	}
	
	public ArrayList<Space> getSpaces(){
		return spaces;
	}
	
	public int getSize(){
		return size;
	}
	
}// End Map class

abstract class Space{
	private String name;
	private int position;

	Space(String spaceName, int spacePosition){
		name = spaceName;
		position = spacePosition;
	}
	
	public String getName(){
		return name;
	}

	public int getPosition(){
		return position;
	}
	
	public abstract void description();

	public abstract void trigger(Character c);


}// End Space class


class Property extends Space{
	private Character owner;
	private int price;
	private int rent;
	
	Property(String newName, int newPosition, int newPrice, int newRent){
		super(newName, newPosition);
		price = newPrice;
		rent = newRent;
		owner = null;
	}

	public void trigger(Character c) {
		if (owner.equals(null)){
			boolean decision = c.decideToBuy(this);
			if(decision){
				c.buy(this);
				DisplayCenter.showBuyProperty(c,this);
			}
		}
		else{
			c.pay(rent);
			owner.earn(rent);
			DisplayCenter.showPayRent(c,owner,rent);
		}
	}

	
	public void setOwner(Character c){
		owner = c;
	}

	public void setRent(int r){
		rent = r;	
	}
	
	public int getPrice(){
		return price;
	}

	public Character getOwner(){
		return owner;
	}

	public int getRent(){
		return rent;
	}

	public void description(){
		DisplayCenter.showPropertyStatus(this);
	}

}// End Property class


abstract class Character{
	private String name;
	private int money;
	private int position = 0;
	private int mapSize = 0;
	private ArrayList <Property> deeds;

	Character(){
		deeds = new ArrayList <Property>();
	};
	
	Character(String newCharName){
		name = newCharName;
		deeds = new ArrayList <Property>();
	}
	
	Character(String newCharName, int newMoney, int newMapSize){
		name = newCharName;
		money = newMoney;
		mapSize = newMapSize;
		deeds = new ArrayList <Property>();
	}

	public void move(int step){
		position = (position+step)%mapSize;
	}

	public void buy(Property p) {
		int price = p.getPrice();
		if (money >= price){
			money -= price;
			deeds.add(p);
			p.setOwner(this);
		}			
		else{
			DisplayCenter.showNoMoneyError();
		}
	}

	public void earn(int amount){
		money += amount;
	}
	
	public void pay(int amount){
		money -= amount;
	}

	public boolean isBroke(){
		if (money < 0){
			return true;
		}
		else return false;
	}

	public void setName(String newName){
		name = newName;
	}


	public String getName(){
		return name;
	}

	public int getMoney(){
		return money;
	}

	public int getPosition(){
		return position;
	}

	public ArrayList <Property> getDeeds(){
		return deeds;
	}


	public abstract boolean decideToBuy(Property p);


}// end Character class


class Player extends Character{
	Player(String name, int money, int mapSize){
		super(name,money,mapSize);
	}

	public boolean decideToBuy(Property p){
		return DisplayCenter.askToBuy(this,p);
	}

}


class AI extends Character{
	AI(String name, int money, int mapSize){
		super(name,money,mapSize);
	}

	public boolean decideToBuy(Property p){
		if (p.getPrice() <= getMoney()){
			return true;
		}
		return false;
	}
}


class Dice{
	java.util.Random rnd = new java.util.Random();
	public int roll(){
		return rnd.nextInt(6)+1;
	}
}


class DisplayCenter{
	public static void showCharStatus(Character c){	
		System.out.println("Player name is" + c.getName());
		System.out.println("Player has " + c.getMoney() + " dollars");
		System.out.println("Player has the following lands");
		ArrayList<Property> deeds = c.getDeeds();
		for (int i = 0; i < deeds.size(); i++){
			System.out.println(deeds.get(i).getName());
		}
	}

	public static boolean askToBuy(Character c, Property p){
		System.out.println("Do you want to buy" + p.getName());
		System.out.println("The price is " + p.getPrice());
		System.out.println("Please answer 0(No) or 1(Yes)");
		Scanner scanner= new Scanner(System.in);
		int res = scanner.nextInt();

		if (res == 0){return false;}
		return true;
	}

	public static void showNoMoneyError(){
		System.out.println("You don't have enough money");	
	}

	public static String askToNamePlayer(){
		System.out.println("Please enter player's name");
		Scanner scanner= new Scanner(System.in);
		String res = scanner.next();
		System.out.println("Your player's name is "+res);
		return res;
	}

	public static void showMove(Character c, int step, int finalPos){	
		System.out.println(c.getName()+" rolls "+step+"!");
		System.out.println("moves to position "+finalPos);
	}

	public static void showSpaceStatus(Space s){	
		s.description();
	}

	public static void showLoser(ArrayList<Character> chars){
		System.out.print("The Loser: ");
		for (int i=0; i<chars.size(); i++){
			System.out.println(chars.get(i).getName());
			
		}
	}

	public static void showPropertyStatus(Property p){
		System.out.print("Type: Property");
		System.out.print("Position: "+Integer.toString(p.getPosition()));
		System.out.print("Name: "+p.getName());
		System.out.print("Price: "+Integer.toString(p.getPrice()));
		System.out.print("Rent: "+Integer.toString(p.getRent()));
		Character owner = p.getOwner();
		if (owner.equals(null)){
			System.out.print("This property is owned by no one");
		}
		else{
			System.out.print("This property is owned by "+owner.getName());	
		}	
	}

	public static void showBuyProperty(Character c, Property p){
		System.out.println(c.getName()+" bought "+p.getName()+" with "+p.getPrice());
	};

	public static void showPayRent(Character payer, Character owner, int rent){
		System.out.println(payer.getName()+" pays "+rent+" to "+owner.getName());	
	};

	public static void showEndGame(){
		System.out.print("Game Ends");
	}
}









