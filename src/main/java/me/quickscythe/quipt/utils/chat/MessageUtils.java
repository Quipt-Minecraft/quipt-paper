package me.quickscythe.quipt.utils.chat;

import me.quickscythe.quipt.utils.CoreUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;


public class MessageUtils {

    private static final File file = new File(CoreUtils.plugin().getDataFolder() + "/messages.json");
    private static JSONObject messages = new JSONObject();

    public static void start() {
        createDefaultMessages();
        StringBuilder data = new StringBuilder();
        try {
            if (!file.exists())
                CoreUtils.logger().log(Logger.LogLevel.INFO, "MessageUtils", "Creating file: " + file.createNewFile());
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            CoreUtils.logger().error("MessageUtils", e);
        }
        JSONObject loaded = data.toString().isEmpty() ? new JSONObject() : new JSONObject(data.toString());
        //messages = what is in memory
        //need to check what is in memory and hasn't been loaded then add it to file
        boolean discrepency = false;
        for (Map.Entry<String, Object> entry : messages.toMap().entrySet()) {
            String key = entry.getKey();
            String text = (String) entry.getValue();
            if (!loaded.has(key)) {
                discrepency = true;
                loaded.put(key, text);
            }
        }
        messages = loaded;
        if (discrepency) loadChangesToFile();
    }

    public static void loadChangesToFile() {
        try {
            FileWriter f2 = new FileWriter(file, false);
            f2.write(messages.toString(2));
            f2.close();
        } catch (IOException e) {
            CoreUtils.logger().error("MessageUtils", e);
        }
    }

    public static void addMessage(String key, String serializedComponent) {
        if (!messages.has(key)) messages.put(key, serializedComponent);
    }

    public static void addMessage(String key, Component deserializedComponent) {
        addMessage(key, serialize(deserializedComponent));
    }

    private static void createDefaultMessages() {
        addMessage("cmd.error.no_player", "{\"text\":\"Sorry \\\"[0]\\\" couldn't be found. If the player is offline their username must be typed exactly.\",\"color\":\"red\"}");
        addMessage("cmd.error.no_perm", "{\"text\":\"Sorry, you don't have the permission to run that command.\",\"color\":\"red\"}");
        addMessage("cmd.error.no_command", "{\"text\":\"Sorry, couldn't find the command \\\"[0]\\\". Please check your spelling and try again.\",\"color\":\"red\"}");
        addMessage("cmd.error.no_console", "{\"text\":\"Sorry, this command can only be run by players.\",\"color\":\"red\"}");
        addMessage("cmd.session.start", "{\"text\":\"Session started\",\"color\":\"green\"}");
        addMessage("cmd.session.end", "{\"text\":\"Session ended\",\"color\":\"green\"}");
        addMessage("cmd.session.reward", "{\"text\":\"You have been rewarded with [0]\",\"color\":\"green\"}");
        addMessage("cmd.session.task", "{\"text\":\"You have been assigned the task [0]\",\"color\":\"green\"}");
        addMessage("quipt.tpr.sent.requester", text().append(text("Teleport request sent to ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());
        addMessage("quipt.tpr.sent.target", text().append(text("You have a teleport request from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(". You have ", TextColor.color(325234)).append(text("30", NamedTextColor.RED).append(text(" seconds to respond.", TextColor.color(325234))))).build());
        addMessage("quipt.tpr.accepted.requester", text().append(text("Teleport request accepted from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());
        addMessage("quipt.tpr.accepted.target", text().append(text("Teleport request accepted from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());
        addMessage("quipt.tpr.denied.requester", text().append(text("Teleport request denied or timed-out from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());
        addMessage("quipt.tpr.denied.target", text().append(text("Teleport request denied or timed-out from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());

    }

    public static Component deserialize(JSONObject json) {
        return deserialize(json.toString());
    }

    public static Component deserialize(String json) {
        return GsonComponentSerializer.gson().deserialize(json);
    }

    public static String plainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String serialize(Component component) {
        String a = GsonComponentSerializer.gson().serialize(component);
        if (!a.startsWith("{")) a = "{\"text\":" + a + "}";
        return a;
    }

    public static Component getMessage(String key, Object... replacements) {
        Component a = getMessageRaw(key);
        for (int i = 0; i != replacements.length; i++) {
            int finalI = i;
            a = a.replaceText(builder -> {
                Component replacement;
                if (replacements[finalI] instanceof Component) replacement = (Component) replacements[finalI];
                else replacement = text(replacements[finalI].toString());
                builder.match("\\[" + finalI + "\\]").replacement(replacement);
            });
        }
        return a;
    }

    private static Component getMessageRaw(String key) {
        return messages.has(key) ? deserialize(messages.getString(key)) : translatable(key);
    }
}
