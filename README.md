Durak
=========================

This is a scala implementation of the card game Durak. 

What
---------------------------
This project is a playable game of Durak [Wikipedia](https://en.wikipedia.org/wiki/Durak). The rules are simplified for this version.

Why
-------------
The project was developed for the
class Software Engineering at the University of Applied Science HTWG Konstanz.

This implementation has a MVC architecture and a GUI and a TUI

Documentation
-------------
See the Scaladoc

How
---------------
You can find the rules in this [Wikipedia article](https://en.wikipedia.org/wiki/Durak).

At the beginning of the game, enter the names of the players, separated by comma. After pressing enter, the GUI will start.

To play using the GUI:

As an attacker: 
- To play a card click on it
- To finish a turn click on "Finish turn"

As a defender:
- To play a card click on the attack you want to defend and then on the card you want to defend that attack with.
- The turn is finished automatically once all attacks are defended.
- To finish a turn click on "Finish turn". This will mean the defender loses that round.

To undo or redo the last card, click on the menu "Spielzug" and then on undo or redo.

To play using the TUI:

As an attacker:
- To play a card enter: suit,rank
- To finish a turn enter: e

As a defender:
- To play a card enter: suit,rank,numberOfAttack
- To finish a turn enter: e

You can switch between the TUI and the GUI at any time.
