package io.github.apace100.origins.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.OriginsClient;
import io.github.apace100.origins.origin.Impact;
import io.github.edwinmindcraft.apoli.api.ApoliAPI;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public abstract class OriginDisplayScreen extends Screen {
	private static final ResourceLocation WINDOW = new ResourceLocation(Origins.MODID, "textures/gui/choose_origin.png");

	protected static final int windowWidth = 176;
	protected static final int windowHeight = 182;
	protected static final int border = 13;

	protected int guiLeft;
	protected int guiTop;
	protected int currentMaxScroll;
	protected int scrollPos;

	protected OriginDisplayScreen(Component name) {
		super(name);
	}

	protected abstract Origin getCurrentOrigin();

	protected abstract OriginLayer getCurrentLayer();

	protected void renderOriginWindow(PoseStack matrices, int mouseX, int mouseY) {
		RenderSystem.enableBlend();
		if(this.getCurrentLayer() != null) {
			this.renderWindowBackground(matrices, 16, 0);
			this.renderOriginContent(matrices);
			RenderSystem.setShaderTexture(0, WINDOW);
			this.blit(matrices, this.guiLeft, this.guiTop, 0, 0, windowWidth, windowHeight);
			this.renderOriginName(matrices);
			RenderSystem.setShaderTexture(0, WINDOW);
			this.renderOriginImpact(matrices, mouseX, mouseY);
			Component title = new TranslatableComponent(Origins.MODID + ".gui.view_origin.title", this.getCurrentLayer().name());
			drawCenteredString(matrices, this.font, title.getString(), this.width / 2, this.guiTop - 15, 0xFFFFFF);
		} else {
			drawCenteredString(matrices, this.font, new TranslatableComponent(Origins.MODID + ".gui.view_origin.empty").getString(), this.width / 2, this.guiTop + 15, 0xFFFFFF);
		}
		RenderSystem.disableBlend();
	}

	private void renderOriginName(PoseStack matrices) {
		Origin origin = this.getCurrentOrigin();
		FormattedText originName = this.font.substrByWidth(origin == Origin.EMPTY ? this.getCurrentLayer().missingDescription() : origin.getName(), windowWidth - 36);
		drawString(matrices, this.font, originName.getString(), this.guiLeft + 39, this.guiTop + 19, 0xFFFFFF);
		ItemStack is = origin.getIcon();
		this.itemRenderer.renderAndDecorateFakeItem(is, this.guiLeft + 15, this.guiTop + 15);
	}

	private void renderWindowBackground(PoseStack matrices, int offsetYStart, int offsetYEnd) {
		int endX = this.guiLeft + windowWidth - border;
		int endY = this.guiTop + windowHeight - border;
		RenderSystem.setShaderTexture(0, WINDOW);
		for (int x = this.guiLeft; x < endX; x += 16) {
			for (int y = this.guiTop + offsetYStart; y < endY + offsetYEnd; y += 16) {
				this.blit(matrices, x, y, windowWidth, 0, Math.max(16, endX - x), Math.max(16, endY + offsetYEnd - y));
			}
		}
	}

	private void renderOriginImpact(PoseStack matrices, int mouseX, int mouseY) {
		Impact impact = this.getCurrentOrigin().getImpact();
		int impactValue = impact.getImpactValue();
		int wOffset = impactValue * 8;
		for(int i = 0; i < 3; i++) {
			if(i < impactValue) {
				this.blit(matrices, this.guiLeft + 128 + i * 10, this.guiTop + 19, windowWidth + wOffset, 16, 8, 8);
			} else {
				this.blit(matrices, this.guiLeft + 128 + i * 10, this.guiTop + 19, windowWidth, 16, 8, 8);
			}
		}
		if(mouseX >= this.guiLeft + 128 && mouseX <= this.guiLeft + 158 && mouseY >= this.guiTop + 19 && mouseY <= this.guiTop + 27) {
			TranslatableComponent ttc = (TranslatableComponent) new TranslatableComponent(Origins.MODID + ".gui.impact.impact").append(": ").append(impact.getTextComponent());
			this.renderTooltip(matrices, ttc, mouseX, mouseY);
		}
	}

	private void renderOriginContent(PoseStack matrices) {
		Origin origin = this.getCurrentOrigin();
		int x = this.guiLeft + 18;
		int y = this.guiTop + 50;
		int startY = y;
		int endY = y - 72 + windowHeight;
		y -= this.scrollPos;

		Component orgDesc = origin.getDescription();
		if(origin == Origin.EMPTY) {
			orgDesc = this.getCurrentLayer().missingDescription().copy();
		}
		List<FormattedCharSequence> descLines = this.font.split(orgDesc, windowWidth - 36);
		for (FormattedCharSequence line : descLines) {
			if (y >= startY - 18 && y <= endY + 12) {
				this.font.draw(matrices, line, x + 2, y - 6, 0xCCCCCC);
			}
			y += 12;
		}
		Registry<ConfiguredPower<?, ?>> powers = ApoliAPI.getPowers();
		for (ConfiguredPower<?, ?> power : origin.getPowers().stream().map(powers::get).filter(Objects::nonNull).toList()) {
			if (power.getData().hidden())
				continue;
			FormattedCharSequence name = Language.getInstance().getVisualOrder(this.font.substrByWidth(power.getData().getName().withStyle(ChatFormatting.UNDERLINE), windowWidth - 36));
			Component desc = power.getData().getDescription().copy();
			List<FormattedCharSequence> drawLines = this.font.split(desc, windowWidth - 36);
			if (y >= startY - 24 && y <= endY + 12) {
				this.font.draw(matrices, name, x, y, 0xFFFFFF);
			}
			for (FormattedCharSequence line : drawLines) {
				y += 12;
				if (y >= startY - 24 && y <= endY + 12) {
					this.font.draw(matrices, line, x + 2, y, 0xCCCCCC);
				}
			}

			y += 14;
		}
		y += this.scrollPos;
		this.currentMaxScroll = y - windowHeight - 15;
		if (this.currentMaxScroll < 0) {
			this.currentMaxScroll = 0;
		}
	}
}
