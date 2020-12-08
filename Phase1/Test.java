package Phase1;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * 
 * Test Class
 * @author Alonzo Machiraju
 */

public class Test
{
   static int NUM_CARDS_PER_HAND = 7;
   static int  NUM_PLAYERS = 2;

   static CardTableController cardTableController;

   static GameModel gameModel;

   public static void main(String[] args)
   {
      // Instantiate model first but it must be checked
      if (gameModel == null)
      {
         gameModel = new GameModel();
      }
      // Now instantiate the controller
      if (cardTableController == null)
      {
         CardTableController cardTableController = new CardTableController(gameModel, NUM_CARDS_PER_HAND, NUM_PLAYERS); 
      }
   }
}


/**
 * 
 * CardGameFramework Class
 * @author Jesse Cecil
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

/**
 * CardGameModel Class
 * @author Alonzo Machiraju
 */

class GameModel 
{
   int playerScore = 0, computerScore = 0, count = 0;

   final static int MAX_CARDS_PER_HAND = 56;
   final static int MAX_PLAYERS = 2;  // for now, we only allow 2 person games

   int numCardsPerHand = MAX_CARDS_PER_HAND;
   int numPlayers = MAX_PLAYERS;

   final static int NUM_BACK_CARDS = 9;

   /**
    * Increase player score by 1
    */
   public void incrementPlayerScore()
   {
      playerScore++;
   }

   /**
    * Increase computer score by 1
    */
   public void incrementComputerScore()
   {
      computerScore++;
   }
   
   /**
    * Increase count of rounds by 1
    */
   public void incrementCount()
   {
      count++;
   }

   /**
    * Reset scores to 0
    */
   public void resetScores()
   {
      playerScore = 0;
      computerScore = 0;
   }
   
   /**
    * Reset round counter
    */
   public void resetCount()
   {
      count = 0;
   }

   /**
    * Mutators
    */

   /**
    * 
    * @param numCardsPerHand How many cards to allow per hand
    * @return Was the number of cards per had set succesfully?
    */
   public boolean setNumCardsPerHand(int numCardsPerHand)
   {
      if (numCardsPerHand <= MAX_CARDS_PER_HAND && numCardsPerHand > 0)
      {
         this.numCardsPerHand = numCardsPerHand;
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * 
    * @param numPlayers How many players this game has
    * @return Was the number of players successfully set?
    */
   public boolean setNumPlayers(int numPlayers)
   {
      if (numPlayers <= MAX_PLAYERS && numPlayers > 0)
      {
         this.numPlayers = numPlayers;
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * Accessors
    */

   /**
    * 
    * @return Number of cards allowed per hand
    */
   public int getNumCardsPerHand()
   {
      return numCardsPerHand;
   }

   /**
    * 
    * @return Number of players in the game
    */
   public int getNumPlayers()
   {
      return numPlayers;
   }

   /**
    * 
    * @return Player's score
    */
   public int getPlayerScore()
   {
      return playerScore;
   }

   /**
    * 
    * @return Computer's score
    */
   public int getComputerScore()
   {
      return computerScore;
   }
   
   /**
    * 
    * @return
    */
   public int getCount()
   {
      return count;
   }
}

/**
 * 
 * CardTableController Class
 * @author Alonzo Machiraju
 */

class CardTableController
{   
   static CardGameFramework lowCardGame;

   static CardTable cardTable;

   GameModel gameModel = new GameModel();

   /**
    * Constructors
    */

   /**
    * Default Constructor
    */

   public CardTableController()
   {
      init();
   }

   /**
    * @param gameModel Data model for the game
    * @param numCardsPerHand Cards dealt to each hand
    * @param numPlayers Number of people playing this round
    */

   public CardTableController(GameModel gameModel, int numCardsPerHand, int numPlayers) 
   {
      this.gameModel = gameModel;

      //check that the data coming in is valid before proceeding, otherwise use default values
      if((numCardsPerHand <= GameModel.MAX_CARDS_PER_HAND && numCardsPerHand > 0) && (numPlayers <= GameModel.MAX_PLAYERS && numPlayers > 0))
      {
         this.gameModel.setNumCardsPerHand(numCardsPerHand);
         this.gameModel.setNumPlayers(numPlayers);
      }
      init();
   }

   /**
    * Initialize our basic data
    */
   public void init()
   {
      int numPacksPerDeck = 1;
      int numJokersPerPack = 2;
      int numUnusedCardsPerPack = 0;
      Card[] unusedCardsPerPack = null;

      gameModel.resetScores();

      lowCardGame = new CardGameFramework( 
            numPacksPerDeck, numJokersPerPack,  
            numUnusedCardsPerPack, unusedCardsPerPack, 
            gameModel.getNumPlayers(), gameModel.getNumCardsPerHand());

      lowCardGame.deal();

      // Create and setup the CardTable
      cardTable = new CardTable("CardTable by Team POSIXOtters", gameModel, lowCardGame, GameModel.MAX_CARDS_PER_HAND);

      cardTable.setSize(800, 600);
      cardTable.setLocationRelativeTo(null);
      cardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // show everything to the user
      cardTable.setVisible(true);
   }

   /**
    * Accessors
    */

   /**
    * 
    * @return How many cards are in each hand
    */

   public int getNumCardsPerHand()
   {
      return gameModel.getNumCardsPerHand();
   }

   /**
    * 
    * @return How many players are playing
    */
   public int getNumPlayers()
   {
      return gameModel.getNumPlayers();
   }
}

/**
 * 
 * CardTable Class
 * @author Miguel Nunez
 * This class is the view of the MVC paradigm
 */

class CardTable extends JFrame {
   static JLabel[] computerLabels;
   static JLabel[] computerBackCardLabels;
   static JLabel[] humanLabels;  
   static JLabel[] playedCardLabels;
   static JLabel[] playLabelText;
   static JLabel[] scores;

   GameModel gameModel;
   CardGameFramework cardGameFramework;

   public JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea;

   public CardTable(String title, GameModel gameModel, CardGameFramework cardGameFramework, int numCardsPerHand) 
   {
      this.gameModel = gameModel;
      this.cardGameFramework = cardGameFramework;

      //check that the data coming in is valid before proceeding 

      //give the JFrame a title and set the gridLayout
      setTitle(title);
      //getRootPane().setBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, Color.decode("#47d147")));
      setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints(); 
      c.fill = GridBagConstraints.BOTH;
      c.weightx = 1.0;
      c.weighty = 1.0;

      //initialize the pnlComputerHand JPanel and add it to the JFrame
      pnlComputerHand = new JPanel();
      pnlComputerHand.setLayout(new GridLayout(0,numCardsPerHand, 10, 0));
      pnlComputerHand.setBackground(Color.WHITE);
      //Border line1 = BorderFactory.createTitledBorder(null, "Computer Hand", TitledBorder.LEFT, TitledBorder.TOP, new Font("sans",Font.BOLD,12), Color.BLACK);
      //pnlComputerHand.setBorder(line1);
      pnlComputerHand.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(Color.WHITE, Color.WHITE), "Computer Hand", TitledBorder.LEFT, TitledBorder.TOP));
      c.gridx = 0;
      c.gridy = 0;
      add(pnlComputerHand, c);
      //initialize the pnlPlayArea JPanel and add it to the JFrame
      pnlPlayArea = new JPanel();
      pnlPlayArea.setLayout(new GridLayout(2, 3, 20, -50));
      pnlPlayArea.setBackground(Color.decode("#47d147"));
      Border blackline = BorderFactory.createLineBorder(Color.decode("#e6ac00"), 5);
      pnlPlayArea.setBorder(blackline);
      //Border line2 = BorderFactory.createTitledBorder(null, "Play Area", TitledBorder.LEFT, TitledBorder.TOP, new Font("sans",Font.BOLD,12), Color.BLACK);
      //pnlPlayArea.setBorder(line2);
      c.gridx = 0;
      c.gridy = 2;
      add(pnlPlayArea, c);
      //initialize the pnlHumanHand JPanel and add it to the JFrame
      pnlHumanHand = new JPanel();
      pnlHumanHand.setLayout(new GridLayout(0,numCardsPerHand, 10, 0));
      pnlHumanHand.setBackground(Color.WHITE);
      //Border line3 = BorderFactory.createTitledBorder(null, "Your Hand", TitledBorder.LEFT, TitledBorder.TOP, new Font("sans",Font.BOLD,12), Color.BLACK);
      //pnlHumanHand.setBorder(line3);
      pnlHumanHand.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(Color.WHITE, Color.WHITE), "Your Hand", TitledBorder.LEFT, TitledBorder.TOP));            
      c.gridx = 0;
      c.gridy = 3;
      add(pnlHumanHand, c);    

      // CREATE LABELS ----------------------------------------------------
      //create a GUICard object so we can have access to methods getIcon() and getBackCardIcon()
      GUICard guiC = new GUICard();

      createJLabels();
      fillJLabels();

      int k = 0;

      addEventListeners();

      // ADD LABELS TO PANELS -----------------------------------------
      //add each Jlabel to its respective JPanel (pnlComputerHand or pnlHumanHand) from myCardTable 

      for (k = 0; k < numCardsPerHand; k++)
      {
         if (k < computerBackCardLabels.length && computerBackCardLabels[k] != null)
            this.getPnlComputerHand().add(computerBackCardLabels[k]);
         if (k < humanLabels.length && humanLabels[k] != null)
            this.getPnlHumanHand().add(humanLabels[k]);
      }
      //this is the set up our play area is going to have as soon as we run the program
      this.getPnlPlayArea().add(computerBackCardLabels[7]);
      this.getPnlPlayArea().add(scores[0]);
      this.getPnlPlayArea().add(computerBackCardLabels[8]);
      this.getPnlPlayArea().add(playLabelText[0]);
      this.getPnlPlayArea().add(scores[1]);
      this.getPnlPlayArea().add(playLabelText[1]);
   }

   /**
    * Helpers
    */

   void createJLabels()
   {
      computerLabels = new JLabel[gameModel.getNumCardsPerHand()];
      computerBackCardLabels = new JLabel[GameModel.NUM_BACK_CARDS];
      humanLabels = new JLabel[gameModel.getNumCardsPerHand()];    
      playedCardLabels  = new JLabel[gameModel.getNumPlayers()]; 
      playLabelText  = new JLabel[gameModel.getNumPlayers()];
      scores = new JLabel[gameModel.getNumPlayers()];
   }

   void fillJLabels()
   {
      final int numCardsPerHand = gameModel.getNumCardsPerHand();
      final int numPlayers = gameModel.getNumPlayers();

      int k;

      //create JLabels for computer and human cards, we need 7 for each
      for(k = 0; k < numCardsPerHand; k++)
      {
         computerLabels[k] = new JLabel(GUICard.getIcon(cardGameFramework.getHand(0).inspectCard(k)));
         humanLabels[k] = new JLabel(GUICard.getIcon(cardGameFramework.getHand(1).inspectCard(k)));
      }
      //create JLabels for the back cards, we need 9
      for(k = 0; k < GameModel.NUM_BACK_CARDS; k++)
      {
         computerBackCardLabels[k] = new JLabel(GUICard.getBackCardIcon());
      }

      //create JLabels for the text and store them in appropriate array
      for(k = 0; k < numPlayers; k++)
      {
         if(k == 0) playLabelText[k] = new JLabel("Computer", JLabel.CENTER);
         if(k == 1) playLabelText[k] = new JLabel("You", JLabel.CENTER);
      }
      //create JLabels for the score
      for(k = 0; k < numPlayers; k++)
      {
         if(k == 0) scores[k] = new JLabel("SCORE", JLabel.CENTER);
         if(k == 1) scores[k] = new JLabel("0 - 0", JLabel.CENTER);
      }
   }

   // add events listeners and check to see who wins
   void addEventListeners()
   {
      for (int k = 0; k < gameModel.getNumCardsPerHand(); k++)
      {
         final int index = k;

         humanLabels[index].addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e, int k){
               //remove any card labels and text that are in the play area each time we play a card
               pnlPlayArea.remove(computerBackCardLabels[7]);
               pnlPlayArea.remove(computerBackCardLabels[8]);
               pnlPlayArea.remove(scores[0]);
               pnlPlayArea.remove(scores[1]);
               for(int i = 0; i < gameModel.getNumCardsPerHand(); i++)
               {
                  pnlPlayArea.remove(computerLabels[i]);
                  pnlPlayArea.remove(humanLabels[i]);
               }
               //remove a card from the hand once its clicked and post it in the play area
               computerBackCardLabels[k].setVisible(false);//make this computer back card invisible
               humanLabels[k].setVisible(false);//make this human card invisible
               pnlPlayArea.add(computerLabels[k]);//add this card to play area
               //check to see who won the game
               int computerCardValue = Card.valueOfCard(cardGameFramework.getHand(0).inspectCard(k));
               int humanCardValue = Card.valueOfCard(cardGameFramework.getHand(1).inspectCard(k));

               //computer wins
               if (computerCardValue < humanCardValue )
               {
                  gameModel.incrementComputerScore();
                  scores[0] = new JLabel("SCORE", JLabel.CENTER);
                  scores[1] = new JLabel(Integer.toString(gameModel.getComputerScore()) +" - "+Integer.toString(gameModel.getPlayerScore()), JLabel.CENTER);
                  pnlPlayArea.add(scores[0]);
                  pnlPlayArea.add(humanLabels[k]);
                  pnlPlayArea.add(playLabelText[0]);
                  pnlPlayArea.add(scores[1]);
                  pnlPlayArea.add(playLabelText[1]);
                  humanLabels[k].setVisible(true);//make this human card re-visible on play area
                  updateGame();
               }
               //you win
               if (humanCardValue < computerCardValue)
               {
                  gameModel.incrementPlayerScore();
                  scores[0] = new JLabel("SCORE", JLabel.CENTER);
                  scores[1] = new JLabel(Integer.toString(gameModel.getComputerScore()) +" - "+Integer.toString(gameModel.getPlayerScore()), JLabel.CENTER);
                  pnlPlayArea.add(scores[0]);
                  pnlPlayArea.add(humanLabels[k]);//add this card to play area
                  pnlPlayArea.add(playLabelText[0]);
                  pnlPlayArea.add(scores[1]);
                  pnlPlayArea.add(playLabelText[1]);
                  humanLabels[k].setVisible(true);//make this human card re-visible on play area
                  updateGame();
               }
               //draw
               if (humanCardValue == computerCardValue)
               {
                  scores[0] = new JLabel("SCORE", JLabel.CENTER);
                  scores[1] = new JLabel(Integer.toString(gameModel.getComputerScore()) +" - "+Integer.toString(gameModel.getPlayerScore()), JLabel.CENTER);
                  pnlPlayArea.add(scores[0]);
                  pnlPlayArea.add(humanLabels[k]);//add this card to play area
                  pnlPlayArea.add(playLabelText[0]);
                  pnlPlayArea.add(scores[1]);
                  pnlPlayArea.add(playLabelText[1]);
                  humanLabels[k].setVisible(true);//make this human card re-visible on play area
                  updateGame();
               }

            }
            
            private void updateGame()
            {
               final int computerScore = gameModel.getComputerScore();
               final int playerScore = gameModel.getPlayerScore();
               
               gameModel.incrementCount();
               if(computerScore + playerScore == gameModel.getNumCardsPerHand() || gameModel.getCount() == 7)
               {
                  if (computerScore > playerScore)
                  {
                     JOptionPane.showMessageDialog(pnlPlayArea, "Computer Wins");
                  }
                  else if(computerScore < playerScore)
                  {
                     JOptionPane.showMessageDialog(pnlPlayArea, "You Win!");
                  }
                  else
                  {
                     JOptionPane.showMessageDialog(pnlPlayArea, "Draw!");
                  }
               }
            }
         });
      }
   }

   /**
    * Accessors
    */

   /**
    * 
    * @return Panel used for computer player's hand
    */

   public JPanel getPnlComputerHand()
   {
      return pnlComputerHand;
   }


   /**
    * 
    * @return Panel used for human player's hand
    */
   public JPanel getPnlHumanHand()
   {
      return pnlHumanHand;
   }


   /**
    * 
    * @return Panel used for play area
    */
   public JPanel getPnlPlayArea()
   {
      return pnlPlayArea;
   }
}

/**
 * 
 * Deck Class
 * @author Alonzo
 */

class Deck
{ 
   //Declare variables
   public final int MAX_CARDS = 336;
   private static Card[] masterPack;
   private Card[] cards = new Card[56];
   private int topCard;

   /**    
    * Constructors      
    */      

   /**
    * Default Constructor
    */
   public Deck()
   {
      //Initialize masterPack[] with all possible cards
      allocateMasterPack();
      //initialize cards[] with init(numPacks) method
      init(1);
   }

   /**      
    *
    * @param numPacks How many packs make up this deck
    */
   public Deck(int numPacks)
   {
      //Create a new empty deck of cards
      cards = new Card[56 * numPacks];
      //Initialize masterPack[] with all possible cards
      allocateMasterPack();
      //initialize cards[] with init(numPacks) method
      init(numPacks);         
   }

   /**         for (Card c : cards)
    * Initialize this deck with a number of packs  
    * @param numPacks How many packs this deck contains  
    */
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

   /**   
    * Add a card to the top of the decl   
    * @param card Card to add 
    * @return Was the card successfully added?  
    */
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

   /**   
    * Remove a card from the deck if it is present 
    * @param card 
    * @return Was the card successfully removed?   
    */
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

   /**   
    * Sort all the cards in this deck  
    */
   void sort() 
   {
      Card.arraySort(cards, topCard);
   }

   /**     
    * Shuffle the cards    
    */
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

   /**      
    * Deal a card from the top of the deck   
    * @return This card is dealt 
    */
   public Card dealCard()
   {
      topCard--;
      Card topC = cards[topCard];
      return topC;
   }

   /**     
    * Sets up all card values for a deck of the maximum pack size, and does so only once 
    */
   private static void allocateMasterPack()
   {
      if (masterPack != null)
      {
         // We cannot allocate the master pack more than once
         return;
      }

      masterPack = new Card[56];

      char[] number = {'A','2','3','4','5','6','7','8','9','T','J','Q','K','X'};
      int count  = 0;

      for(int i = 0; i < 4; i++)
      {
         for(int j = 0; j < 14; j++)
         {
            if(i == 0) masterPack[count] = new Card(number[j], Card.Suit.clubs);
            if(i == 1) masterPack[count] = new Card(number[j], Card.Suit.diamonds);
            if(i == 2) masterPack[count] = new Card(number[j], Card.Suit.hearts);
            if(i == 3) masterPack[count] = new Card(number[j], Card.Suit.spades);
            count++;
         }
      }
   }

   /**  
    * Accessors   
    */   

   /**   
    * Get the topmost card in the deck 
    * @return Topmost card 
    */
   public int getTopCard()
   {
      return topCard;
   }

   /**   
    *    
    * @return The number of cards in this deck  
    */
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

   /**   
    * Peek at a card in the deck 
    *    
    * @param k Location in the deck 
    * @return Card at location k 
    */
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
}


/**
 * 
 * GUICard Class
 *
 */

class GUICard
{
   private static Icon[][] iconCards = new ImageIcon[14][4]; // 14 = A thru K + joker
   private static Icon iconBack;
   static boolean iconsLoaded = false;

   /**   
    * Constructor 
    */
   public GUICard()
   {
      //only proceed if iconsLoaded is false
      if(iconsLoaded == false)
      {
         loadCardIcons();
         iconBack = new ImageIcon("images/BK.gif");
         iconsLoaded = true;
      }           
   }

   /**
    * Load all the icon images 
    */
   static void loadCardIcons()
   {
      // build the file names ("AC.gif", "2C.gif", "3C.gif", "TC.gif", etc.)
      // in a SHORT loop.  For each file name, read it in and use it to
      // instantiate each of the 57 Icons in the icon[] array.
      // Outer loop for card values
      // Inner loop for suites   
      for(int col = 0; col <= 3; col++)
      {
         for(int row = 0; row < 13; row++)
         {
            iconCards[row][col] = new ImageIcon(turnIntIntoCardValue(row) + turnIntIntoCardSuit(col));
            // Now load the back 
            iconCards[13][col] = new ImageIcon("images/BK.gif");
         }
      }
   }

   /**   
    * Helpers  
    */

   /**   
    * Turns 0 - 13 into "A", "2", "3", ... "Q", "K", "X" 
    * @param k 
    * @return Card value string representation inside images directory  
    */
   static String turnIntIntoCardValue(int k)
   {
      // an idea for a helper method (do it differently if you wish)
      String numLetter[] = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "X"};
      return "images/" + numLetter[k];
   }

   /**   
    * turns 0 - 3 into "C", "D", "H", "S" 
    * @param j Input integer  
    * @return String representation of suit with GIF file extension  
    */   
   static String turnIntIntoCardSuit(int j)
   {
      // an idea for another helper method (do it differently if you wish) 
      String suit[] = {"C", "D", "H", "S"};  
      return suit[j] + ".gif";
   }

   /**
    * Get the value of a particular card
    * @param card Card whose value we want to get
    * @return numeric value of card
    */
   public static int valueToInt(Card card)
   {
      return Card.valueOfCard(card);
   }

   /**
    * Get a numeric representation of a suit
    * @param card Card whose suit we want to get
    * @return numeric representation of suit as int
    */
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

   /**   
    *    
    * Accessors   
    */

   /**      
    *    
    * @param card The card whose icon we need to get  
    * @return The icon of the card  
    */
   public static Icon getIcon(Card card)
   {
      return iconCards[valueToInt(card)][suitToNum(card)];
   }

   /**   
    * @return Back image of card 
    */
   static public Icon getBackCardIcon()
   {
      if(!iconsLoaded)
      {  
         loadCardIcons();  
         iconsLoaded = true;  
      }  
      return iconBack;
   }
}

/**
 * 
 * Card Class
 *
 */

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

   /**    
    * Constructors     
    */

   /**   
    * Default constructor  
    */ 
   public Card()
   {
      value = 'A';
      suit = Card.Suit.spades;
      errorFlag = false;
   }

   /**       
    * @param value face value of a card, A, 2-9, T, J, Q, or K      
    * @param suit one of the four suits of playing cards      
    */
   public Card(char value, Suit suit)
   {
      set(value, suit);
   }

   /**
    * 
    * @param cards Cards to sort
    * @param arraySize size of set to sort
    */
   static void arraySort(Card[] cards, int arraySize)
   {
      // Bubble sort cards using a temp variable and nested for loops
      Card temp;
      for(int i = 0; i < arraySize; i++) 
      {
         for(int j = 1; j < (arraySize-i); j++) 
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

   /**
    * Get rank order value of card
    * @param card Card whose value rank must be checked
    * @return Priority order of this card for ranking purposes
    */
   static int valueOfCard(Card card)
   {
      // It traverses the valuRanks and check which matches the card value
      // Then it returns the index position as the value
      //System.out.print("Card Value: " + card.getValue() + "\n");
      if(valuRanks.indexOf(card.getValue()) > -1)
      {
         return valuRanks.indexOf(card.getValue());
      }
      else
      {
         return -11;
      }
   }

   /**   
    * Helper   
    */   

   /**   
    *    
    * @param value Make sure this is one of the allowed card values  
    * @param suit This is always valid due to being an enum 
    * @return Does this card have a valid value?   
    */
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

   /**
    * String Representation
    */
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

   /**   
    *    
    * @param card compare value and suit with this card  
    * @return do both cards have the same value and suit?   
    */
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

   /**
    * Mutators
    */

   /**    
    *      
    * @param value Change the value of this card       
    * @param suit Change the suit of this card    
    * @return Was the change successful?   
    */
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

   /**
    * 
    * Accessors
    */

   /**   
    *    
    * @return value of this card 
    */
   public char getValue()
   {
      return value;
   }

   /**   
    *    
    * @return suit of this card  
    */
   public Suit getSuit()
   {
      return suit;
   }

   /**   
    * Is this a bad card with bad values in it? If so, it's 
    * not a legitimate card to play.   
    * @return errorFlag 
    */
   public boolean getErrorFlag()
   {
      return errorFlag;
   }
}

/**
 * 
 * Hand Class
 *
 */

class Hand
{
   //Declare our variables
   public int MAX_CARDS = 56;

   private Card[] myCards;
   private int numCards;

   /**   
    * Constructor   
    */
   public Hand()
   {
      // This also initializes all the cards in the hand
      resetHand();
   }

   /**   
    * Sorts all cards in the hand   
    */
   void sort()
   {
      Card.arraySort(myCards, numCards);
   }

   /**   
    * Play the card at the given location 
    * @param cardIndex  
    * @return The card at the index given 
    */
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

   /**   
    * Clears all cards in the hand  
    */
   public void resetHand()
   {
      // Remove all cards from hand by setting it to a null array
      myCards = new Card[MAX_CARDS];
      numCards = 0; // We don't have any cards now so we reset this.
   }

   /**   
    * Take a card 
    * @param card The card that goes to this hand  
    * @return Was the card taken?   
    */
   public boolean takeCard(Card card)
   {
      // Don't add a bad card to our hand!   
      if (card.getErrorFlag())   
      {  
         return false;  
      }  
      else  
      {  
         // Make a copy if the card to avoid passing a reference  
         Card newCard = new Card(card.getValue(), card.getSuit());   
         // Search for an open slot and insert the new card 
         for (int i = 0; i < numCards; i++)  
         {  
            if (myCards[i] == null) 
            {  
               myCards[i] = newCard;   
               numCards += 1; // Increment our count  
               return true;   
            }  
         }  
         myCards[numCards] = newCard; // Insert at end   
         numCards += 1; // Increment our count  
         return true;   
      }
   }

   /**     
    * Play the last card added and still present    
    * @return The card that is played   
    */
   public Card playCard()
   {
      numCards--; // Decrement since we are playing a card
      Card topCard = myCards[numCards];
      return topCard;
   }

   /**   
    * Convenient string representation
    */
   public String toString()
   {
      String handString = "Hand = (";

      for (Card card : myCards)
      {     
         // Guard against crashes when we have an empty slot
         if (card != null)      
         {      
            handString = handString + card.toString() + ", ";
         }          
      }
      return handString + ")";
   }


   /**   
    * Accessors   
    */   

   /**   
    * How many cards are in this hand? 
    * @return The number of cards in this hand  
    */
   public int getNumCards()
   {
      return numCards;
   }

   /**   
    * Peek at a card in this hand   
    * @param k Location of a card in the hand   
    * @return The card at location K   
    */
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