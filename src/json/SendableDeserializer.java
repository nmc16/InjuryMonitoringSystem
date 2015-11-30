package json;

import java.lang.reflect.Type;

import sendable.DataType;
import sendable.Sendable;
import sendable.alarm.Alarm;
import sendable.alarm.Cause;
import sendable.data.Acceleration;
import sendable.data.Position;
import sendable.data.Request;
import sendable.data.Service;
import json.ServiceDeserializer;
import json.CauseDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;


public class SendableDeserializer implements JsonDeserializer<Sendable> {

    @Override
    public Sendable deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Gson gson = new GsonBuilder().registerTypeAdapter(Cause.class, new CauseDeserializer())
                                     .registerTypeAdapter(Service.class, new ServiceDeserializer()).create();

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int dataType = jsonObject.get("type").getAsInt();

        if (dataType == DataType.POS) {
            return gson.fromJson(jsonElement, Position.class);
        } else if (dataType == DataType.ACCEL) {
            return gson.fromJson(jsonElement, Acceleration.class);
        } else if (dataType == DataType.ALARM) {
            return gson.fromJson(jsonElement, Alarm.class);
        } else if (dataType == DataType.REQUEST) {
            return gson.fromJson(jsonElement, Request.class);
        } else if (dataType == DataType.SERVICE) {
            return gson.fromJson(jsonElement, Service.class);
        } else {
            throw new JsonParseException("Could not find object where data type is " + dataType);
        }
    }
}
