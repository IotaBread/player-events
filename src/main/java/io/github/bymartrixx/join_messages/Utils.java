package io.github.bymartrixx.join_messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class Utils {
    public static String[] stringsOnStrings(String[] array1, String[] array2) {
        try {
            String newArray[] = new String[array1.length + array2.length];
        
            for(int i = 0; i < newArray.length; i++) {
                if (i % 2 == 1 && i / 2 < array2.length) {
                    newArray[i] = array2[i / 2];
                } else if (i % 2 == 0) {
                    newArray[i] = array1[i / 2];
                } else {
                    for (String s : array1) {
                        newArray[i] = s;
                    }
                }
            }
            return newArray;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: array2 length mus be less or equal than array1 length");
            e.printStackTrace();
            return new String[0];
        }
    }

    public static String messageAsString(String message, PlayerEntity player) {
        String[] messagePieces = message.split("%s");
        String separator = player.getName().asString();

        return String.join(separator, messagePieces);
    }

    public static MutableText messageAsText(String message, PlayerEntity player) {
        //TODO: FORMATTING
        String[] messagePieces = message.split("%s");
        MutableText result = new LiteralText("");
        MutableText[] pieces = new MutableText[messagePieces.length];

        for (int i = 0; i < pieces.length; i++) {
            pieces[i] = new LiteralText(messagePieces[i]).formatted(Formatting.YELLOW);
        }

        for (int i = 0; i < pieces.length + (pieces.length - 1); i++) {
            if (i == 0 && !messagePieces[i].startsWith(" ")) {
                result = pieces[i];
            } else {
                if (i % 2 == 1 || i == 0 && messagePieces[i].startsWith(" ")) {
                    result.append(player.getDisplayName());
                } else {
                    result.append(pieces[i / 2]);
                }
            }
        }

        return result;
    }
}

