package model.config;

import com.google.gson.annotations.SerializedName;
import tv.isshoni.winry.api.annotation.Config;

@Config("test.json")
public class TestConfig {

    @SerializedName("name")
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
