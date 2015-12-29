import java.util.*;

public class Monopoly{

	public static void theGame(){
	
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

		createMap(mapSize);
		createAI(AINum);
		createPlayer(playerNum);

			
	}
		
	public void createMap(int num){
		for(int i=0;i<num;i++){
			String name = "Property_";
			name.concat(Integer.toString(i));
			int price = (i*500)%5000;
			int rent = (i*10)%1000;
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




}// End Computer Class


class Map{
	private ArrayList<Space> spaces;
	private int size = 0;
	
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
			}
		}
		else{
			c.pay(rent);
			owner.earn(rent);
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

	public String getOwner(){
		return owner.getName();
	}

	public int getRent(){
		return rent;
	}

}// End Property class


abstract class Character{
	private String name;
	private int money;
	private int position = 0;
	private int mapSize = 0;
	private ArrayList <Property> deeds;

	Character(){};
	
	Character(String newCharName){
		name = newCharName;
	}
	
	Character(String newCharName, int newMoney, int newMapSize){
		name = newCharName;
		money = newMoney;
		mapSize = newMapSize;
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
	public static void showStatus(Character c){	
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

}









