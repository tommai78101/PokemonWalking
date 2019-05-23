### AUTOMATION SCRIPT TUTORIAL

<b>DESCRIPTION:</b> Entities that can walk, or run, must be required to have movements for the game
to feel lively. 

<b>FUTURE:</b> More commands to come.  
 
<span mod>_</span>&nbsp;&nbsp;: Whitespaces.  
<span mod>@</span>&nbsp;&nbsp;: Trigger name.  
<span mod>^</span>&nbsp;&nbsp;: [Direction, Steps]. Can be chained for delaying scripted movements.  
<span mod>$</span>&nbsp;&nbsp;: Start of script. Always appear at beginning of script.  
<span mod>%</span>&nbsp;&nbsp;: Script delimiter. Always appear at end of script.  
<span mod>\#</span>&nbsp;&nbsp;: Speech Dialogue.  
<span mod>\/</span>&nbsp;&nbsp;: Comments. Gets ignored.  
<span mod>?</span>&nbsp;&nbsp;: Question Dialogue.  
<span mod>+</span>&nbsp;&nbsp;: Affirmative dialogue.  
<span mod>-</span>&nbsp;&nbsp;: Negative dialogue  
<span mod>[</span>&nbsp;&nbsp;: Affirmative Action  
<span mod>]</span>&nbsp;&nbsp;: Negative Action  
<span mod>;</span>&nbsp;&nbsp;: Repeat Flag. If contains ';', it means it's enabled by default.  
  
DO NOT CHANGE\/REMOVE THIS TRIGGER SCRIPT. THIS IS RESERVED ONLY. FOLLOW THIS FORMAT.  
  
<b>TODO:</b> Remove the dependency of this Eraser trigger from the Level Editor.

<pre>
$0
@Eraser
%
</pre>
