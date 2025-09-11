package not.savage.airheads.utility;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

/**
 * Utility for editing metadata of heads based on base64 strings.
 * This is a copy-paste class I, @Savagelife, have on hand for editing heads of different game versions.
 * So some methods are unused in this project & could be removed. /shrug.
 * @see SkullMeta The metadata of a skull can be a pain!
 */
@UtilityClass
public class Heads {

    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time

    /**
     * Set the texture of a given SkullMeta to a base64 string.
     * @param base64 The base64 string of the texture.
     * @param meta The SkullMeta to set the texture to.
     */
    public static void setBase64ToSkullMeta(String base64, SkullMeta meta) {
        PlayerProfile profile = getProfileBase64(base64);
        meta.setPlayerProfile(profile);
    }

    public static void setReflectionSkin(Player player, SkullMeta meta) {
        PlayerProfile profile = player.getPlayerProfile();
        meta.setPlayerProfile(profile);
    }

    private static PlayerProfile getProfileBase64(String base64) {
        PlayerProfile profile = Bukkit.createProfile(RANDOM_UUID); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = getUrlFromBase64(base64);
        } catch (Exception exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

    public static URL getUrlFromBase64(String base64) throws MalformedURLException, IllegalArgumentException {
        URL url;
        String skin = "";
        try {
            String decoded = new String(Base64.getDecoder().decode(base64));
            JsonObject json;
            try {
                json = JsonParser.parseString(decoded + "}").getAsJsonObject();
            } catch (JsonSyntaxException e) {
                json = JsonParser.parseString(decoded).getAsJsonObject();
            }

            if (!json.has("textures")) {
                throw new IllegalArgumentException("Base64 Missing Textures: " + json);
            }

            JsonObject textures = json.get("textures").getAsJsonObject();

            if (!textures.has("SKIN")) {
                throw new IllegalArgumentException("Textures missing SKIN field: " + textures);
            }

            skin = textures.get("SKIN").getAsJsonObject().get("url").getAsString();
            url = URI.create(skin).toURL();
        } catch (IllegalArgumentException t) {
            throw new IllegalArgumentException("Invalid base64 string: " + base64);
        } catch (JsonSyntaxException t) {
            throw new IllegalArgumentException("Invalid base64 encoded json: " + base64);
        } catch (MalformedURLException t) {
            throw new MalformedURLException("Invalid URL: " + skin);
        }

        return url;
    }
}
