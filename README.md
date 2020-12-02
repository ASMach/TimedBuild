# TimedBuild

Phase 1: Refactor to Model-View-Controller paradigm.

* Model - Model represents an object carrying data. It can also have logic to update controller if its data changes.
* View - View represents the visualization of the data that model contains.
* Controller - Controller acts on both model and view. It controls the data flow into model object and updates the view whenever data changes. It keeps view and model separate.

Model Classes
-Card
-Hand
-Deck
-CardGameFramework

View Classes
-GUICard
-CardTable

Controller Classes must be implemented that separate out interaction logic from the above classes.

Phase 2: Multithreading

Multithreading must be implemented to handle the timer and the game. The timer must be on its own thread. Additional threads must be used for the game itself.

Phase 3: Build Game

Make a new card game

1. Take turns with the computer putting a card on one of three stacks in the middle of the table.  
2. You can put on a card that is one value higher or one value lower.  (6 on a 5 OR 4 on a 5, Q on a J OR T on a J, etc.) 
3. After you play, you get another card from the deck in your hand.
4. The number of cards remaining in the deck should be displayed on the screen.
5. Keep going until the single deck is out of cards.
6. If you cannot play, click a button that says "I cannot play".  The the computer gets a second turn.  Same for you, a second turn if the computer cannot play.  If neither of you can play, then the deck puts a new card on each of the three stacks in the middle of the table.
7. Display on the screen the number of "cannot plays" on the screen for both the player and the computer.
8. Whoever has the least number of "cannot plays", is the winner.  Declare this at the end, when the deck is exhausted even though you will still have cards in your hand.
9. First use playCard() to get the card you want to play from playerIndex and at the cardIndex location.  Then use the takeCard() to get a new card from end of the array.  You will then need to reorder the labels by using setIcon().
10. The playCard() method that we added to the Hand class last week will now be used to remove the card at a location and slide all of the cards down one spot in the myCards array.  