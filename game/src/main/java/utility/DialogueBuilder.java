package utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import dialogue.Dialogue;
import dialogue.Dialogue.Type;
import entity.Player;

public class DialogueBuilder {
	public static Dialogue createText(String dialogue, Type type) {
		return DialogueBuilder.createText(dialogue, dialogue.length(), type, true);
	}

	public static Dialogue createText(String dialogue, int length, Type type, boolean lock) {
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

	public static ArrayList<Map.Entry<Dialogue, Integer>> loadDialogues(String filename) {
		ArrayList<Map.Entry<Dialogue, Integer>> result = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(Dialogue.class.getClassLoader().getResourceAsStream(filename))
			);
			String line;
			String[] tokens;
			int dialogueID = 0;
			Dialogue temp = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					// Dialogue ID
					tokens = line.split("#");
					dialogueID = Integer.valueOf(tokens[1]);
				}
				else if (line.startsWith("@")) {
					// Dialogue message
					tokens = line.split("@");
					temp = DialogueBuilder.createText(
						tokens[1], Dialogue.MAX_STRING_LENGTH, Type.DIALOGUE_SPEECH,
						false
					);
				}
				else if (line.startsWith("-")) {
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

	public static ArrayList<Map.Entry<String, Boolean>> toLines(String all, final int regex) {
		ArrayList<Map.Entry<String, Boolean>> lines = new ArrayList<>();
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
