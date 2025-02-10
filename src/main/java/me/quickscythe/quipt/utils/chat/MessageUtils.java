package me.quickscythe.quipt.utils.chat;

import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.api.registries.Registries;
import me.quickscythe.quipt.api.registries.Registry;
import me.quickscythe.quipt.files.MessagesConfig;
import me.quickscythe.quipt.utils.CoreUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.json.JSONObject;


import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;


public class MessageUtils {

    private static final Registry<Component> registry = Registries.add("messages", new Registry<>());
    private static MessagesConfig config;

    public static void start() {
        config = ConfigManager.getConfig(CoreUtils.integration(), MessagesConfig.class);
        createDefaultMessages();
        for (String key : config.messages.keySet()) {
            if (registry.get(key).isEmpty()) {
                registry.register(key, deserialize(config.messages.getString(key)));
            }
        }
    }

    public static void register(String key, String serializedComponent) {
        if (!config.messages.has(key)) {
            registry.register(key, deserialize(serializedComponent));
            config.messages.put(key, serializedComponent);
        }
    }

    public static void register(String key, Component deserializedComponent) {
        register(key, serialize(deserializedComponent));
    }

    private static void createDefaultMessages() {
        register("cmd.error.no_perm", "{\"text\":\"Sorry, you don't have the permission to run that command.\",\"color\":\"red\"}");
        register("cmd.error.no_command", "{\"text\":\"Sorry, couldn't find the command \\\"[0]\\\". Please check your spelling and try again.\",\"color\":\"red\"}");
        register("cmd.error.no_console", "{\"text\":\"Sorry, this command can only be run by players.\",\"color\":\"red\"}");
        register("cmd.session.start", "{\"text\":\"Session started\",\"color\":\"green\"}");
        register("cmd.session.end", "{\"text\":\"Session ended\",\"color\":\"green\"}");
        register("cmd.session.reward", "{\"text\":\"You have been rewarded with [0]\",\"color\":\"green\"}");
        register("cmd.session.task", "{\"text\":\"You have been assigned the task [0]\",\"color\":\"green\"}");
        register("quipt.tpr.sent.requester", text().append(text("Teleport request sent to ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());
        register("quipt.tpr.sent.target", text().append(text("You have a teleport request from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(". You have ", TextColor.color(325234)).append(text("30", NamedTextColor.RED).append(text(" seconds to respond.", TextColor.color(325234))))).build());
        register("quipt.tpr.accepted.requester", text().append(text("Teleport request accepted from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());
        register("quipt.tpr.accepted.target", text().append(text("Teleport request accepted from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());
        register("quipt.tpr.denied.requester", text().append(text("Teleport request denied or timed-out from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());
        register("quipt.tpr.denied.target", text().append(text("Teleport request denied or timed-out from ", TextColor.color(325234))).append(text("[0]", TextColor.color(23434234))).append(text(".", TextColor.color(325234))).build());
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

    public static Component get(String key, Object... replacements) {
        Component a = get(key);
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

    private static Component get(String key) {
        return registry.getOrDefault(key, translatable(key));
    }
}
