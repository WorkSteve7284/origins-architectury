package io.github.edwinmindcraft.origins.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class OriginsConfigs {
	public static class Common {
		public Common(ForgeConfigSpec.Builder builder) {}
	}

	public static class Client {
		public Client(ForgeConfigSpec.Builder builder) {}
	}

	public static class Server {
		public Server(ForgeConfigSpec.Builder builder) {}
	}

	public static final ForgeConfigSpec COMMON_SPECS;
	public static final ForgeConfigSpec CLIENT_SPECS;
	public static final ForgeConfigSpec SERVER_SPECS;

	public static final Common COMMON;
	public static final Client CLIENT;
	public static final Server SERVER;

	static {
		Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
		Pair<Client, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(Client::new);
		Pair<Server, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(Server::new);
		COMMON_SPECS = common.getRight();
		CLIENT_SPECS = client.getRight();
		SERVER_SPECS = server.getRight();
		COMMON = common.getLeft();
		CLIENT = client.getLeft();
		SERVER = server.getLeft();
	}
}
