import java.util.Random;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Assig6 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

/*
 * Timer
 */

class ClockTimer
{
	
}

/*
 * Class GameModel
 */

class GameModel 
{
	
}

/*
 * Class GameView
 */

class GameView 
{
	
}

/*
 * Class GameModel
 */

class GameControl 
{
	
}

/*
 * Class CardGameFramework
 */

class CardGameFramework
{
   private static final int MAX_PLAYERS = 50;


   private int numPlayers;
   private int numPacks;            // # standard 52-card packs per deck
                                    // ignoring jokers or unused cards
   private int numJokersPerPack;    // if 2 per pack & 3 packs per deck, get 6
   private int numUnusedCardsPerPack;  // # cards removed from each pack
   private int numCardsPerHand;        // # cards to deal each player
   private Deck deck;               // holds the initial full deck and gets
                                    // smaller (usually) during play
   private Hand[] hand;             // one Hand for each player
   private Card[] unusedCardsPerPack;   // an array holding the cards not used
                                        // in the game.  e.g. pinochle does not
                                        // use cards 2-8 of any suit


   public CardGameFramework( int numPacks, int numJokersPerPack,
         int numUnusedCardsPerPack,  Card[] unusedCardsPerPack,
         int numPlayers, int numCardsPerHand)
   {
      int k;


      // filter bad values
      if (numPacks < 1 || numPacks > 6)
         numPacks = 1;
      if (numJokersPerPack < 0 || numJokersPerPack > 4)
         numJokersPerPack = 0;
      if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) //  > 1 card
         numUnusedCardsPerPack = 0;
      if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
         numPlayers = 4;
      // one of many ways to assure at least one full deal to all players
      if  (numCardsPerHand < 1 ||
            numCardsPerHand >  numPacks * (52 - numUnusedCardsPerPack)
            / numPlayers )
         numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;


      // allocate
      this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
      this.hand = new Hand[numPlayers];
      for (k = 0; k < numPlayers; k++)
         this.hand[k] = new Hand();
      deck = new Deck(numPacks);


      // assign to members
      this.numPacks = numPacks;
      this.numJokersPerPack = numJokersPerPack;
      this.numUnusedCardsPerPack = numUnusedCardsPerPack;
      this.numPlayers = numPlayers;
      this.numCardsPerHand = numCardsPerHand;
      for (k = 0; k < numUnusedCardsPerPack; k++)
         this.unusedCardsPerPack[k] = unusedCardsPerPack[k];


      // prepare deck and shuffle
      newGame();
   }


   // constructor overload/default for game like bridge
   public CardGameFramework()
   {
      this(1, 0, 0, null, 4, 13);
   }


   public Hand getHand(int k)
   {
      // hands start from 0 like arrays


      // on error return automatic empty hand
      if (k < 0 || k >= numPlayers)
         return new Hand();


      return hand[k];
   }


   public Card getCardFromDeck() { return deck.dealCard(); }


   public int getNumCardsRemainingInDeck() { return deck.getNumCards(); }


   public void newGame()
   {
      int k, j;


      // clear the hands
      for (k = 0; k < numPlayers; k++)
         hand[k].resetHand();


      // restock the deck
      deck.init(numPacks);


      // remove unused cards
      for (k = 0; k < numUnusedCardsPerPack; k++)
         deck.removeCard( unusedCardsPerPack[k] );


      // add jokers
      for (k = 0; k < numPacks; k++)
         for ( j = 0; j < numJokersPerPack; j++)
            deck.addCard( new Card('X', Card.Suit.values()[j]) );


      // shuffle the cards
      deck.shuffle();
   }


   public boolean deal()
   {
      // returns false if not enough cards, but deals what it can
      int k, j;
      boolean enoughCards;


      // clear all hands
      for (j = 0; j < numPlayers; j++)
         hand[j].resetHand();


      enoughCards = true;
      for (k = 0; k < numCardsPerHand && enoughCards ; k++)
      {
         for (j = 0; j < numPlayers; j++)
            if (deck.getNumCards() > 0)
               hand[j].takeCard( deck.dealCard() );
            else
            {
               enoughCards = false;
               break;
            }
      }


      return enoughCards;
   }


   void sortHands()
   {
      int k;


      for (k = 0; k < numPlayers; k++)
         hand[k].sort();
   }


   Card playCard(int playerIndex, int cardIndex)
   {
      // returns bad card if either argument is bad
      if (playerIndex < 0 ||  playerIndex > numPlayers - 1 ||
          cardIndex < 0 || cardIndex > numCardsPerHand - 1)
      {
         //Creates a card that does not work
         return new Card('M', Card.Suit.spades);      
      }
   
      // return the card played
      return hand[playerIndex].playCard(cardIndex);
   
   }

   boolean takeCard(int playerIndex)
   {
      // returns false if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1)
         return false;
     
       // Are there enough Cards?
       if (deck.getNumCards() <= 0)
          return false;


       return hand[playerIndex].takeCard(deck.dealCard());
   }
}

/*
 * class GUIcard
 */

class GUICard
{
   private static Icon[][] iconCards = new ImageIcon[14][4]; // 14 = A thru K + joker
   private static Icon iconBack;
   static boolean iconsLoaded = false;


   public GUICard()
   {
      //only proceed if iconsloaded is false
      if(iconsLoaded == false)
      {
         loadCardIcons();
         iconBack = new ImageIcon("images/BK.gif");
         iconsLoaded = true;
      }           
   }


   static void loadCardIcons()
   {
      //create and array of values and an array of suits
      String numLetter[] = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "X"};
      String suit[] = {"C", "D", "H", "S"};
      //use the two arrays with a nested loop to initialize the iconCards[][] 2D array
       for(int row = 0;row <  4;row++)
       {
           for(int col = 0;col < 14;col++)
           {
               iconCards[col][row] = new ImageIcon("images/" + numLetter[col] + suit[row] + ".gif");
           }
       }
   }
   public static int valueToInt(Card card)
   {
      return Card.valueOfCard(card);
   }
   public static int suitToNum(Card card)
   {
      Card.Suit cardSuit = card.getSuit();


      switch (cardSuit)
      {
         case clubs:
            return 0;
         case diamonds:
            return 1;
         case hearts:
            return 2;
         case spades:
            return 3;
         default:
            return -1;
      }
   }
   public static Icon getIcon(Card card)
   {
      return iconCards[valueToInt(card)][suitToNum(card)];
   }


   static public Icon getBackCardIcon()
   {
      return iconBack;     
   }


}

//class Card  ---------------------------------------------------------------------
class Card
{
//Declare our variables
public enum Suit {clubs, diamonds, hearts, spades};
//NEWLY ADDED - to account for joker (X)
//public static char[] valuRanks = {'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'X'};
public static String valuRanks = "A23456789TJQKX";
private char value;
private Suit suit;
private boolean errorFlag;
//Default constructor 
public Card()
{
  value = 'A';
  suit = Card.Suit.spades;
  errorFlag = false;
}
//Overloaded constructor
public Card(char value, Suit suit)
{
  set(value, suit);
}
//NEWLY ADDED - to sort the cards using bubblesort
static void arraySort(Card[] cards, int arraySize)
 {
    // Bubble sort cards using a temp variable and nested for loops
    int length = cards.length;
    Card temp;
    for(int i = 0; i < length; i++) 
    {
       for(int j = 1; j < (length-i); j++) 
       {
          if(cards[j-1].getValue() > cards[j].getValue()) 
          {
             temp = cards[j-1];
             cards[j-1] = cards[j];
             cards[j] = temp;
          }
       }
    }
 }

static int valueOfCard(Card card)
{


   // It traverses the valuRanks and check which matches the card value
   // Then it returns the index position as the value
   //System.out.print("Card Value: " + card.getValue() + "\n");
    if(valuRanks.indexOf(card.getValue()) > -1)
        return valuRanks.indexOf(card.getValue());
     else
        return -11;




}
//Returns true or false ( depending on validity of values )
private boolean isValid(char value, Suit suit)
{ //check that value is one of these values, its not necessary to check for suit
  //because the program will NOT run without a valid suit
  if(value == 'A' || value == '2' || value == '3' || value == '4' || value == '5' ||
     value == '6' || value == '7' || value == '8' || value == '9' || value == 'T' ||
     value == 'J' || value == 'Q' || value == 'K' || value == 'X')
  {
     return true;
  }
  else
  {
    return false;
  }
}
//Creates a card if isValid() method is true, otherwise it does not
public boolean set(char value, Suit suit)
{
  if(isValid(value, suit))
  {
    this.value = value;
    this.suit = suit;
    errorFlag = false;
    return true;
  }
  else
  {
    errorFlag = true;
    return false;
  }
}
//Outputs the card if it's valid or "[ invalid ]"" if its not valid
public String toString()
{
  StringBuilder sb = new StringBuilder();
  if(getErrorFlag())
  {
    sb.append("[ invalid ]");
    return sb.toString();
  }
  else
  {
    sb.append(this.value).append(" of ").append(this.suit);
    return sb.toString();
  }
}
//Accessor for suit 
public Suit getSuit()
{
    return suit;
}
//Accessor for value
public char getValue()
{
  return value;
}
//Accesor for errorFlag
public boolean getErrorFlag()
{
  return errorFlag;
}
public boolean equals(Card card)
 {
    // Compare value and suit of this card to the other one
    if (card.getValue() == this.value && card.getSuit() == this.suit && card.getErrorFlag() == this.errorFlag)
    {
       return true;
    }
    else
    {
       return false;
    }
 }
}
//class Hand  --------------------------------------------------------------------
class Hand
{
//Declare our variables
public int MAX_CARDS = 56;
private Card[] myCards;
private int numCards;
//Default constructor
public Hand()
{
  resetHand();
}
//NEWLY ADDED - will sort the hand by calling the arraySort() method in the Card class
void sort()
{
    Card.arraySort(myCards, numCards);
}
//NEWLY ADDED - will remove the card at a location and slide all of the cards down
//one spot in the myCards array
public Card playCard(int cardIndex)
{
  if ( numCards == 0 ) //error
  {
     //Creates a card that does not work
     return new Card('M', Card.Suit.spades);
  }
  //Decreases numCards.
  Card card = myCards[cardIndex];
    
  numCards--;
  for(int i = cardIndex; i < numCards; i++)
  {
     myCards[i] = myCards[i+1];
  }
   
  myCards[numCards] = null;
    
  return card;
}
//Resets (deletes current contents) of myCards array 
public void resetHand()
{
  myCards = new Card[MAX_CARDS];
  numCards = 0;
}
//Adds a card to the next available position in the myCards array
public boolean takeCard(Card card)
{
  myCards[numCards] = card;
  numCards++;
  return true;
}
//Removes and returns the top card of the myCards array
public Card playCard()
{
  numCards--;
  Card topCard = myCards[numCards];
  return topCard;
}
//A stringizer that the client can use to display the entire hand
public String toString()
{
  String handString = "Hand = (";
  for(int i = 0;i < numCards;i++)
  {
    if (i != 0) handString += ", ";
    handString += myCards[i].getValue() + " of " + myCards[i].getSuit();
  }
  return handString + ")";
}
//Accessor for numCards
public int getNumCards()
{
  return numCards;
}
public Card inspectCard(int k) 
{
   // Deliberately create an invalid card so we can have one with an error flag set
   Card card = new Card('b', Card.Suit.clubs);
   if (k <= numCards)
   {
      // Set the card to the one at k if there is a card at that location
      if (myCards[k] != null)
      {
        card = myCards[k];
      }
   }
    return card;
 }
}
//class Deck  ---------------------------------------------------------------------
class Deck
{ //Declare variables
private static boolean alreadyExecuted = false;
public final int MAX_CARDS = 336;
private static Card[] masterPack;
private Card[] cards;
private int topCard;
//Default constructor
public Deck()
{
  //Create a new empty deck of cards
  cards = new Card[56];
  //Method that initializes masterPack[] with all possible cards
  allocateMasterPack();
  //initialize cards[] with init(numPacks) method
  init(1);
}
//Overloaded constructor
public Deck(int numPacks)
{
  //Create a new empty deck of cards
  cards = new Card[56 * numPacks];
  //Method that initializes masterPack[] with all possible cards
  allocateMasterPack();
  //initialize cards[] with init(numPacks) method
  init(numPacks);         
}
//NEWLY ADDED - 
boolean addCard(Card card)
{
  // We only allow a card to be added if we will not exceed the maximum number of cards
  if (topCard < cards.length)
  {
    // Iterate and look for an open spot
    for (Card c : cards)
    {
    // We have found a spot.
      if (c == null)
      {
          c = card;
          topCard += 1; // Increment since we added a card
          // Successfully added the card so return true
          return true;
      }
    }
  }
    // We have maxed out our cards or failed to add a card and so cannot add another one.
    return false;
 }
 //NEWLY ADDED 
 boolean removeCard(Card card)
 {  
   // Search for the card
   for (Card c: cards)
   {
     // We have found the card to remove
     if (c.getValue() == card.getValue() && c.getSuit() == card.getSuit())
     {
        // Temp for swapping the top card into this one's location
        Card temp = cards[topCard];
          
        // Swap topcard temp into the spot
        c = temp;
          
        // Don't want duplicate top card
        cards[topCard] = null;
        topCard -= 1; // Decrement since we removed a card
        return true; // Successfully removed the card
     }
    }
    // Did not find the card to remove so return false
    return false;
 }
 //NEWLY ADDED
 void sort() 
 {
    Card.arraySort(cards, topCard);
 }
 //NEWLY ADDED
 int getNumCards()
 {
    int cardCount = 0;
    for (Card card : cards)
    {
       if (card != null)
       {
          cardCount += 1;
       }
       else
       {
          // If we hit an empty slot all subsequent slots are empty, so save cycles
          break;
       }
    }
    return cardCount;
 }
//Initializes cards[] with the use of masterPack[]
public void init(int numPacks) 
{
  int numberOfCards = numPacks * 56;
  if(numberOfCards <= MAX_CARDS)
  {
    int count = 0;
    topCard = 56 * numPacks;


    for(int i = 0;i < numPacks;i++)
    {
      for(int j = 0;j < 56;j++)
      {
        cards[count] = masterPack[j];
        count++;
      }
    }
  }
}
//Shuffles the contents of cards[]
public void shuffle()
{
  Random rgen = new Random();     

  for (int i = 0; i < cards.length; i++) {
    int randomPosition = rgen.nextInt(cards.length);
    Card temp = cards[i];
    cards[i] = cards[randomPosition];
    cards[randomPosition] = temp;
  }
}
//Returns and removes the card in the top occupied position of cards[]
public Card dealCard()
{
  topCard--;
  Card topC = cards[topCard];
  return topC;
}
//Accessor for topCard
public int getTopCard()
{
  return topCard;
}


public Card inspectCard(int k)
{
  // Use a bad card as a default
  Card card = new Card('R', Card.Suit.spades);


  if (k < topCard)
  {
    card = cards[k];
  }
  return card;
}
//Initializes masterPack[] with all the possible cards
private static void allocateMasterPack()
{
  //This must only be executed once
  if(!alreadyExecuted)
  {
    alreadyExecuted = true;


    masterPack = new Card[56];


    char[] number = {'A','2','3','4','5','6','7','8','9','T','J','Q','K','X'};
    int count  = 0;


    for(int i = 0;i < 4;i++)
    {
      for(int j = 0;j < 14;j++)
      {
        if(i == 0) masterPack[count] = new Card(number[j], Card.Suit.clubs);
        if(i == 1) masterPack[count] = new Card(number[j], Card.Suit.diamonds);
        if(i == 2) masterPack[count] = new Card(number[j], Card.Suit.hearts);
        if(i == 3) masterPack[count] = new Card(number[j], Card.Suit.spades);
        count++;
      }
    }
  }
}   
}
