package me.quickscythe.quipt.utils.teleportation;

import me.quickscythe.quipt.api.QuiptIntegration;
import me.quickscythe.quipt.api.config.ConfigManager;
import me.quickscythe.quipt.files.TeleportationConfig;
import me.quickscythe.quipt.utils.CoreUtils;
import me.quickscythe.quipt.utils.PaperIntegration;
import me.quickscythe.quipt.utils.chat.MessageUtils;
import me.quickscythe.quipt.utils.heartbeat.Flutter;
import me.quickscythe.quipt.utils.heartbeat.HeartbeatUtils;
import me.quickscythe.quipt.utils.teleportation.points.TeleportationPoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LocationUtils {

    private static final List<TeleportRequest> requests = new ArrayList<>();
    private static final List<TeleportRequest> requestsAddQueue = new ArrayList<>();
    private static final List<TeleportRequest> requestsRemoveQueue = new ArrayList<>();
    static TeleportationConfig config;

    public static void start(PaperIntegration integration) {
        if(integration.plugin().isEmpty()) throw new IllegalStateException("Integration plugin is not present");
        config = ConfigManager.registerConfig(CoreUtils.integration(), TeleportationConfig.class);
        Flutter flutter = () -> {
            requests.addAll(requestsAddQueue);
            requestsAddQueue.clear();
            requests.removeAll(requestsRemoveQueue);
            requestsRemoveQueue.clear();
            requests.forEach(request -> {
                if (System.currentTimeMillis() - request.created() > 30000) {
                    request.deny();
                    requestsRemoveQueue.add(request);
                }
            });
            return true;
        };
        HeartbeatUtils.heartbeat(integration).addFlutter(flutter);
    }

    public static void put(TeleportationPoint point) {
        config.locations.put(point.name(), point.serialize());
    }

    public static TeleportationPoint get(String name) {
        return config.locations.has(name) ? new TeleportationPoint(config.locations.getJSONObject(name)) : null;
    }

    public static void remove(String name) {
        config.locations.remove(name);
    }

    public static Collection<TeleportationPoint> getAll() {
        List<TeleportationPoint> points = new ArrayList<>();
        for (String key : config.locations.keySet()) {
            points.add(new TeleportationPoint(config.locations.getJSONObject(key)));
        }
        return points;
    }

    public static Collection<TeleportationPoint> getAll(TeleportationPoint.Type type) {
        List<TeleportationPoint> points = new ArrayList<>();
        for (String key : config.locations.keySet()) {
            TeleportationPoint point = new TeleportationPoint(config.locations.getJSONObject(key));
            if (point.type() == type) points.add(point);
        }
        return points;
    }

    public static void save() {
        config.save();
    }

    public static String serialize(Location loc) {
        String r = loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getPitch() + ":" + loc.getYaw();
        r = r.replaceAll("\\.", ",");
        r = "location:" + r;
        return r;
    }

    public static Location deserialize(String s) {
        if (s.startsWith("location:")) s = s.replaceAll("location:", "");

        if (s.contains(",")) s = s.replaceAll(",", ".");
        String[] args = s.split(":");
        Location r = new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
        if (args.length >= 5) {
            r.setPitch(Float.parseFloat(args[4]));
            r.setYaw(Float.parseFloat(args[5]));
        }
        return r;
    }

    public static TeleportRequest request(Player requester, Player target) {
        TeleportRequest request = new TeleportRequest(requester, target);
        request.send();
        requestsAddQueue.add(request);
        return request;
    }

    public static List<TeleportRequest> requests() {
        return requests;
    }

    public static class TeleportRequest {

        private final Player requester;
        private final Player target;
        private final long created;

        public TeleportRequest(Player requester, Player target) {
            this.requester = requester;
            this.target = target;
            this.created = System.currentTimeMillis();
        }

        public Player requester() {
            return requester;
        }

        public Player target() {
            return target;
        }

        public long created() {
            return created;
        }


        public void send() {
            requester.sendMessage(MessageUtils.getMessage("quipt.tpr.sent.requester", target.getName()));
            target.sendMessage(MessageUtils.getMessage("quipt.tpr.sent.target", requester.getName()));
        }

        public void accept() {
            requester.sendMessage(MessageUtils.getMessage("quipt.tpr.accepted.requester", target.getName()));
            target.sendMessage(MessageUtils.getMessage("quipt.tpr.accepted.target", requester.getName()));
            requester.teleport(target);
            LocationUtils.requestsRemoveQueue.add(this);
        }

        public void deny() {
            requester.sendMessage(MessageUtils.getMessage("quipt.tpr.denied.requester", target.getName()));
            target.sendMessage(MessageUtils.getMessage("quipt.tpr.denied.target", requester.getName()));
            LocationUtils.requestsRemoveQueue.add(this);
        }
    }
}
