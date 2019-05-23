<style>
	p {
		display: block;
	}

	span[mod] {
		padding: 0 5px;
		display: inline-block;
		background-color: #fefefe;
		color: #444;
		border: 1px solid #444;
		border-radius: 4px;
	}
</style>

### AUTOMATION SCRIPT TUTORIAL

<b>DESCRIPTION:</b> Entities that can walk, or run, must be required to have movements for the game
to feel lively. 

<b>FUTURE:</b> More commands to come.  
 
<span mod>_</span>: Whitespaces.  
<span mod>@</span>: Trigger name.  
<span mod>^</span>: [Direction, Steps]. Can be chained for delaying scripted movements.  
<span mod>$</span>: Start of script. Always appear at beginning of script.  
<span mod>%</span>: Script delimiter. Always appear at end of script.  
<span mod>\#</span>: Speech Dialogue.  
<span mod>\/</span>: Comments. Gets ignored.  
<span mod>?</span>: Question Dialogue.  
<span mod>+</span>: Affirmative dialogue.  
<span mod>-</span>: Negative dialogue  
<span mod>[</span>: Affirmative Action  
<span mod>]</span>: Negative Action  
<span mod>;</span>: Repeat Flag. If contains ';', it means it's enabled by default.  
  
DO NOT CHANGE\/REMOVE THIS TRIGGER SCRIPT. THIS IS RESERVED ONLY. FOLLOW THIS FORMAT.  
  
<b>TODO:</b> Remove the dependency of this Eraser trigger from the Level Editor.

<pre>
$0
@Eraser
%
</pre>
