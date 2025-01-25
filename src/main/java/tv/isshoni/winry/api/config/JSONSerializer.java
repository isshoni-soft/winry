package tv.isshoni.winry.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import institute.isshoni.araragi.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONSerializer implements IConfigSerializer<JsonObject> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public JsonObject convert(Object value) {
        return GSON.toJsonTree(value).getAsJsonObject();
    }

    @Override
    public void serialize(String path, JsonObject object) throws IOException {
        Files.writeString(Paths.get(path), GSON.toJson(object));
    }

    @Override
    public <R> R deserialize(String path, Class<R> target, boolean internal) throws IOException {
        InputStream stream;
        if (internal) {
            stream = FileUtil.getResource(path);
        } else {
            Path file = Paths.get(path);
            if (Files.exists(file) && !Files.isDirectory(file)) {
                stream = Files.newInputStream(file);
            } else {
                throw new IOException("Cannot open config file at path: " + file.toRealPath());
            }
        }

        return GSON.fromJson(new InputStreamReader(stream), target);
    }

    @Override
    public Class<? extends JsonObject> getType() {
        return JsonObject.class;
    }
}
