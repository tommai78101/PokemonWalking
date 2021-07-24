### DISCLAIMER:

**Download links** are located near the bottom of this README.

**FUTURE:** Look into GitHub releases now that Java 12 is being used.

|<b>Table of Contents</b>|
|:---|
|<a href="https://github.com/tommai78101/PokemonWalking#pok%C3%A9mon-walking-algorithm-working-title">Pokémon Walking Algorithm</a>|
|<a href="https://github.com/tommai78101/PokemonWalking#level-editor">Level Editor</a>| 
|<a href="https://github.com/tommai78101/PokemonWalking#script-editor">Scripting Editor</a>| 

<b>System Requirements:</b>
<ul>
	<li>OpenJDK 14.0.2 or newer</li>
	<li>Visual Studio Code v1.58.0 or newer</li>
	<li>Java Extension Pack</li>
	<li>Gradle 6.7 or newer</li>
	<li>JUnit Jupiter 5.4.2 or newer</li>
</ul>

===

![Imgur](http://i.imgur.com/rwkiHwC.png)

![Imgur](http://i.imgur.com/P56aP0J.png)

### Pokémon Walking Algorithm (Working Title) 

===

**Abstract:**

Implements the walking algorithm used in classic Pokémon G/S/C games. Also includes a level editor to create your own game areas.

**MILESTONE HAS REACHED**, *ALL PRIORITIES ARE SHIFTED TO LOW!* That means I won't be working on this as active as I used to be, but it is still in development. Note that it does not mimic the walking algorithm used in Pokémon R/B/Y games (1st generation games). I also no longer put new releases here at GitHub, due to the lack of showing downloads count. For more information, please scroll to the bottom.

**NEW UPDATE**: School life has now taken up 90% of my entire free time. As far as I know, my courses are time-consuming, I feared I can no longer update this project as I wanted. Sucks though, because this is a wonderful project I couldn't just put it down. Apparently, Java 2D isn't popular and welcomed at my school, so it's now disheartening for me to sponsor this and let everyone else know. But, I'll do my best to continue efforts on completing it. **/UPDATE**

===

**How to Build:**

1. Navigate to `PokemonWalking/` directory after cloning the repository.
1. Run `gradle clean jar`.
	* On Windows, replace `gradle` with `.\gradlew.bat`.
2. To run the game, run the command: `java -jar game/build/libs/game.jar`
3. To run the level editor, run the command: `java -jar levelEditor/build/libs/editor.jar`

===

**Description:**

This is a project I am currently working on while I'm doing conscription service. The aim of this project is to port the walking algorithm used in the 2nd generation games of the official Pokémon game series. At the moment, I'm leaving out the 1st generation walking algorithm, as the 2nd generation games introduced "turning on the spot".

If all bodes well, it's possible that this will become a Java port of the Pokémon 2nd generation games. Highly likely, but I don't want to get my hopes up too soon.

It's still in very early stages of development. Possibly even earlier than "alpha". *Everything is done from scratch/by hand. Only the character design is mostly mimicked from the original Pokémon games.*

===

**Demo:**

http://www.gfycat.com/EachIlliterateHyrax#

http://www.gfycat.com/ShabbySoupyDobermanpinscher

===

**Controls:**

*Inputs:*

* Use WASD keys or the arrow keys to navigate around. Tapping the keys will "turn on the spot".

* Just walk into the water to surf around.

* Use Z, X to interact. / and . can also be used. ZX and arrow keys are for right hand users, WASD and ./ are for left hand users.

* Press Enter to open start menu.


*Save/Load:*

* To save your game, use the in-game menu option called "Save".

* To load your game, just start up the game. Loading is done automatically upon start up.

* To delete your saved data, just delete the "data.sav" file that was generated when you save your game.

===

![Imgur](http://i.imgur.com/u8xYVVc.png)

### Level Editor

===

**Abstract:**

A level editor used to create custom maps that can be loaded into the game by anyone.

===

**Description:**

This level editor is designed with the intention of easily create maps and allow better focus on development. Since the game loads the area maps from PNG bitmap files, I thought that it would be much nicer if there's a program that allows me to draw area maps more quicker and easier.

It used to be painstakingly laborious to draw area maps. I had to open up Paint.NET, a graphics editor that I used, and follow the rules of drawing tiles set forth by the documentations that I wrote for this game, and have to use an Eyedropper tool to look up the pixel colors in Paint.NET to check for consistencies and errors. And once that was done, I would had to redo everything if the area map loaded up by the game turns out to be wrong or a tile was drawn incorrectly. Everything about it was just tedious to begin with.

With this level editor, I was able to speed up development, and easily draw out test areas for debugging new game mechanics. Just fire up this program, quickly draw something, save it, let the game load it up, and then finally test everything out. It's quick, simple, and gets the job done.

This level editor is released to the public to allow players to create their own custom areas and play them from the game.

===

**Demo and Usage:**

*How to use the Level Editor, part 1:*

http://www.gfycat.com/NextNeighboringFluke

*How to use the Level Editor, part 2:*

http://www.gfycat.com/FrighteningGargantuanDonkey

*How to use the Level Editor, part 3:*

http://www.gfycat.com/SneakyTalkativeAcaciarat

*You Should Know when starting the game:*

http://www.gfycat.com/CloudyBonyGlowworm

*Adding your Custom Maps:*

http://www.gfycat.com/WeepyUnsightlyFairybluebird

*Loading your Custom Maps:*

http://www.gfycat.com/FamiliarUncommonIndianabat


===

![Imgur](http://i.imgur.com/6Tcdi5a.png)

### Script Editor

===

**Abstract:**

A script editor. Users can create custom scripts that controls the aspects in a map.

===

**Description:**

This script editor is part of the level editor that comes with the game. 

Its purposes is used for simple script creations, allowing the users to create custom scripts for players to interact in their custom maps. The user can save, load, and create custom scripts with the help of its graphical user interface. The interface is designed intuitively for users to quickly create scripts.

Scripts are what will become Triggers and Events that the game will have in the maps. Currently, Triggers and Events are not yet completed. Stay tuned.

-------

<b>Automation Script Guide</b>

-------

<b>DESCRIPTION:</b> Entities that can walk, or run, must be required to have movements for the game
to feel lively. 

<b>FUTURE:</b> More commands to come.  
 
<span mod>_</span>&nbsp;&nbsp;&nbsp;&nbsp;: Whitespaces.  
<span mod>@</span>&nbsp;&nbsp;&nbsp;&nbsp;: Trigger name.  
<span mod>^</span>&nbsp;&nbsp;&nbsp;&nbsp;: [Direction, Steps]. Can be chained for delaying scripted movements.  
<span mod>$</span>&nbsp;&nbsp;&nbsp;&nbsp;: Start of script. Always appear at beginning of script.  
<span mod>%</span>&nbsp;&nbsp;&nbsp;&nbsp;: Script delimiter. Always appear at end of script.  
<span mod>\#</span>&nbsp;&nbsp;&nbsp;&nbsp;: Speech Dialogue.  
<span mod>\/</span>&nbsp;&nbsp;&nbsp;&nbsp;: Comments. Gets ignored.  
<span mod>?</span>&nbsp;&nbsp;&nbsp;&nbsp;: Question Dialogue.  
<span mod>+</span>&nbsp;&nbsp;&nbsp;&nbsp;: Affirmative dialogue.  
<span mod>-</span>&nbsp;&nbsp;&nbsp;&nbsp;: Negative dialogue  
<span mod>[</span>&nbsp;&nbsp;&nbsp;&nbsp;: Affirmative Action  
<span mod>]</span>&nbsp;&nbsp;&nbsp;&nbsp;: Negative Action  
<span mod>;</span>&nbsp;&nbsp;&nbsp;&nbsp;: Repeat Flag. If contains ';', it means it's enabled by default.  
  
DO NOT CHANGE\/REMOVE THIS TRIGGER SCRIPT. THIS IS RESERVED ONLY. FOLLOW THIS FORMAT.  
  
<b>TODO:</b> Remove the dependency of this Eraser trigger from the Level Editor.

<pre><code>
$0
@Eraser
%
</code></pre>

===

**Features:**

| In development | Beta (Close to completion) | Complete |
|:---:|:---:|:---:|
| Triggers/Events | Dialogues | All Movements* |
| Script Editor | Level Editor | Ledges |
| ??? | ??? | Area Warping |
| | | Stairs |
| | | Water |
| | | Bitmap Animation |
| | | Item Balls |
| | | Inventory |
| | | Saving/Loading |

\*All movements: Walking, Facing/Turning, Surfing, Bicycling, Jumping, etc.

===

**Known Issues:**

* There may be issues with the tiles-to-tiles interaction, causing unwanted bugs.
* There may be hidden issues I haven't seen/know of yet.
* All items will exit out of inventory.
* I may not have enough time to put time and effort on this project.

===

**Plans:**

* ~~Implement all walking situations from the original games. Walking, surfing, jumping over ledges, riding bicycle, etc. (MAIN GOAL)~~  (MILESTONE COMPLETE!)
* Create a game completely different from Pokémon games. (Extended Plan, low priority, low activity, won't guarantee completion.)

===

| Downloads | Latest Stable Version | Link |
|:---:|:---:|:---:|
| Walking | v20 (6/18/2015) | http://www.thehelper.net/attachments/walking_v20-zip.18776/ |
| Editor | v6 (6/18/2015) | http://www.thehelper.net/attachments/editor_v6-zip.18777/ |

Extract the JAR file from the ZIP file after you have downloaded it. Then double-click on it to run.

===

**Wanna chat?**

You may head over to The Helper Forums, or Java-Gaming.org to post your feedback and/or comments. Much appreciated.

| Discussion Site | Link to Discussion Thread (Including latest release info) |
|:---:|:---:|
| The Helper Forums | http://www.thehelper.net/threads/java-pok%C3%A9mon-walking-algorithm.159287/ |
| Java-Gaming.org | http://www.java-gaming.org/topics/pok-mon-walking/32546/view.html |
