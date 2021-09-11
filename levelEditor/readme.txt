/ This is an automated content generation.
 
/ Entities that can walk, or run, must be required to have movements for the game
/ to feel lively.
 
/ You can either use the corresponding symbols or tag names when adding new scripts.
 
/ More commands will be added.
 
/ /: Comments. Gets ignored.
/ _: Whitespaces.
/ @: Trigger name.
 
/ Tag names are wrapped inside < > angle brackets. Similar tags are comma-separated.
 
/ ^ <PathData>: [Direction, Steps]. Can be chained for delaying scripted movements.
/ $ <BeginScript>: Start of script. Always appear at beginning of script. Range [1~65535]
/ & <NpcScript>: Start of NPC script. Appears at the beginning of script. Range [1~255]
/ % <EndScript>: Script delimiter. Always appear at end of script.
/ # <Speech>: Speech Dialogue.
/ ? <Question>: Question Dialogue.
/ + <Affirm>: Affirmative dialogue.
/ - <Reject>: Negative dialogue
/ [ <Confirm>: Affirmative Action
/ ] <Deny>: Negative Action
/ ; <Repeat, Repeatable>: Repeat Flag. If contains ';', it means it's enabled by default.
 
/ Example Legacy Format:
$0
@Eraser
%
 
/ Example JSON Format:
{
   "data": [
      {
         "BEGIN": "1",
         "CONTENT": [
            {"1": "#First speech begins."},
            {"2": "?First question?"},
            {"3": "#Second speech begins."},
            {"4": "#Explaining stuffs."},
            {"5": "#Ending first trigger."}
         ],
         "NAME": "Sample trigger #1"
      },
      {
         "NPC": "2",
         "CONTENT": [
            {"1": "#First speech."},
            {"2": "#NPC talks second dialogue."},
            {"3": "#Nothing else."},
            {"4": "#Ending NPC speech."}
         ],
         "NAME": "Sample NPC trigger #2"
      }
   ],
   "CHKSUM": "ce42c3f6a6f16392"
}
