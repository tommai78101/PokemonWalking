package utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dialogue.Dialogue;
import dialogue.Dialogue.DialogueType;
import entity.Player;
import enums.ScriptTags;

public class DialogueBuilder {
	public static Dialogue createText(String dialogue, DialogueType type) {
		return DialogueBuilder.createText(dialogue, Dialogue.MAX_STRING_LENGTH, type, true);
	}

	/**
	 * 
	 * @param dialogue
	 * @param length
	 *            - The max length of 1 line in the in-game dialogue box.
	 * @param type
	 * @param lock
	 * @return
	 */
	public static Dialogue createText(String dialogue, int length, DialogueType type, boolean lock) {
		Dialogue dialogues = new Dialogue();
		dialogues.setLines(DialogueBuilder.toLines(dialogue, length));
		dialogues.setLineLength(length);
		dialogues.setTotalDialogueLength(dialogue.length());
		dialogues.setType(type);
		dialogues.setShowDialog(true);
		if (lock) {
			if (!Player.isMovementsLocked())
				Player.lockMovements();
		}
		return dialogues;
	}

	public static Dialogue createText(String dialogue, int length, DialogueType type, boolean lock, boolean ignoreInputs) {
		Dialogue dialogues = DialogueBuilder.createText(dialogue, length, type, lock);
		dialogues.setIgnoreInputs(ignoreInputs);
		return dialogues;
	}

	public static List<Map.Entry<Dialogue, Integer>> loadDialogues(String filename) {
		List<Map.Entry<Dialogue, Integer>> result = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(Dialogue.class.getClassLoader().getResourceAsStream(filename))
			);
			String line;
			String[] tokens;
			int dialogueID = 0;
			Dialogue temp = null;
			while ((line = reader.readLine()) != null) {
				if (ScriptTags.Speech.beginsAt(line)) {
					// Dialogue ID
					dialogueID = Integer.valueOf(ScriptTags.Speech.removeScriptTag(line));
				}
				else if (ScriptTags.ScriptName.beginsAt(line)) {
					// Dialogue message
					temp = DialogueBuilder.createText(
						ScriptTags.ScriptName.removeScriptTag(line),
						Dialogue.MAX_STRING_LENGTH, DialogueType.SPEECH,
						false
					);
				}
				else if (ScriptTags.Reject.beginsAt(line)) {
					// Dialogue delimiter
					Map.Entry<Dialogue, Integer> entry = new AbstractMap.SimpleEntry<>(
						temp,
						dialogueID
					);
					result.add(entry);
				}
			}
		}
		catch (Exception e) {
			return null;
		}
		return result;
	}

	public static List<Map.Entry<String, Boolean>> toLines(String all, final int regex) {
		List<Map.Entry<String, Boolean>> lines = new ArrayList<>();
		String[] words = all.split("\\s");
		String line = "";
		int length = 0;
		for (String w : words) {
			if (length + w.length() + 1 > regex) {
				if (w.length() >= regex) {
					line += w;
					lines.add(new AbstractMap.SimpleEntry<>(line, false));
					line = "";
					continue;
				}
				lines.add(new AbstractMap.SimpleEntry<>(line, false));
				line = "";
				length = 0;
			}
			if (length > 0) {
				line += " ";
				length += 1;
			}
			line += w;
			length += w.length();
		}
		if (line.length() > 0)
			lines.add(new AbstractMap.SimpleEntry<>(line, false));
		return lines;
	}
}
