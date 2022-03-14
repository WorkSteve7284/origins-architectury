package io.github.apace100.origins.screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.origins.Origins;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record Badge(ResourceLocation spriteLocation, String hoverText) {
	public static final Codec<Badge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SerializableDataTypes.IDENTIFIER.fieldOf("sprite").forGetter(Badge::getSpriteLocation),
			Codec.STRING.fieldOf("text").forGetter(Badge::getHoverText)
	).apply(instance, Badge::new));

	public static final SerializableData DATA = new SerializableData()
			.add("sprite", SerializableDataTypes.IDENTIFIER)
			.add("text", SerializableDataTypes.STRING);

	public static final Badge ACTIVE = new Badge(
			Origins.identifier("textures/gui/badge/active.png"),
			"origins.gui.badge.active");

	public static final Badge TOGGLE = new Badge(
			Origins.identifier("textures/gui/badge/toggle.png"),
			"origins.gui.badge.toggle");

    public String getHoverText() {
		return hoverText;
	}

	public ResourceLocation getSpriteLocation() {
		return spriteLocation;
	}

	public SerializableData.Instance getData() {
		SerializableData.Instance data = DATA.new Instance();
		data.set("sprite", spriteLocation);
		data.set("text", hoverText);
		return data;
	}

	public static Badge fromData(SerializableData.Instance data) {
		return new Badge(data.getId("sprite"), data.getString("text"));
	}

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeWithCodec(CODEC, this);
    }

    public JsonElement toJson() {
        return CODEC.encodeStart(JsonOps.INSTANCE, this).result().get();
    }

    public static Badge fromNetwork(FriendlyByteBuf buf) {
        return buf.readWithCodec(CODEC);
    }

    public static Badge fromJson(JsonElement element) throws JsonParseException {
        DataResult<Badge> badge = CODEC.decode(JsonOps.INSTANCE, element).map(Pair::getFirst);
        if (badge.error().isPresent())
            throw new JsonParseException("Error parsing badge: " + badge.error().get().message());
        if (badge.result().isPresent())
            return badge.result().get();
        throw new JsonParseException("If you see this message, someone did something REALLY wrong.");
    }
}
