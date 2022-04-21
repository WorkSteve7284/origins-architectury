package io.github.edwinmindcraft.origins.api.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JsonUtils {
	public static <T> Optional<T> getOptional(JsonObject root, String name, BiFunction<JsonObject, String, T> function) {
		if (root.has(name))
			return Optional.of(function.apply(root, name));
		return Optional.empty();
	}

	public static <T> List<T> getOptionalList(JsonObject root, String name, BiFunction<JsonElement, String, T> conversion) {
		Optional<JsonArray> optional = getOptional(root, name, GsonHelper::getAsJsonArray);
		if (optional.isEmpty())
			return ImmutableList.of();
		ImmutableList.Builder<T> builder = ImmutableList.builder();
		JsonArray arr = optional.get();
		for (int i = 0; i < arr.size(); i++)
			builder.add(conversion.apply(arr.get(i), "%s[%d]".formatted(name, i)));
		return builder.build();
	}

	public static List<ResourceLocation> getIdentifierList(JsonObject root, String name) {
		return getOptionalList(root, name, (jsonElement, s) -> {
			String s1 = GsonHelper.convertToString(jsonElement, s);
			try {
				return new ResourceLocation(s1);
			} catch (ResourceLocationException t) {
				throw new JsonParseException(s1 + " isn't a valid identifier at: " + s, t);
			}
		});
	}

	public static <T extends JsonElement> Collector<T, JsonArray, JsonArray> toJsonArray() {
		return Collector.of(JsonArray::new, JsonArray::add, (array, array2) -> {
			array.addAll(array2);
			return array;
		});
	}

	public static Stream<JsonElement> stream(JsonArray array) {
		return IntStream.range(0, array.size()).mapToObj(array::get);
	}

	public static <T, V> Function<T, V> rethrow(Function<T, V> input, String message) {
		return t -> {
			try {
				return input.apply(t);
			} catch (Throwable th) {
				throw new JsonParseException(message, th);
			}
		};
	}
}
