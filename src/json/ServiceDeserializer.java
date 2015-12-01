package json;

import java.lang.reflect.Type;

import sendable.DataType;
import sendable.alarm.Alarm;
import sendable.data.Acceleration;
import sendable.data.Position;
import sendable.data.Service;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class ServiceDeserializer implements JsonDeserializer<Service> {

    @Override
    public Service deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Gson gson = new Gson();

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int dataType = jsonObject.get("dataType").getAsInt();

        if (dataType == DataType.ACCEL) {
            Type t = new TypeToken<Service<Acceleration>>(){}.getType();
            return gson.fromJson(jsonElement, t);
        } else if (dataType == DataType.POS) {
            Type t = new TypeToken<Service<Position>>(){}.getType();
            return gson.fromJson(jsonElement, t);
        } else if (dataType == DataType.ALARM) {
            Type t = new TypeToken<Service<Alarm>>(){}.getType();
            return gson.fromJson(jsonElement, t);
        } else {
            throw new JsonParseException("Could not find object where data type is " + dataType);
        }
    }
}
