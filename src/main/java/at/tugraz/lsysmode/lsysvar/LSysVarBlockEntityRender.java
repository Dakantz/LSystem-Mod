package at.tugraz.lsysmode.lsysvar;

import at.tugraz.lsysmode.LSysFractals;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.generators.template.RotationBuilder;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.awt.*;

public class LSysVarBlockEntityRender implements BlockEntityRenderer<LSysVarBlockEntity> {
    private BlockEntityRendererProvider.Context context;

    public LSysVarBlockEntityRender(BlockEntityRendererProvider.Context context) {
        super();
        this.context = context;
    }

    @Override
    public void render(LSysVarBlockEntity entity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
//https://forums.minecraftforge.net/topic/115346-drawing-text-ingame-using-fontdraw/
        Minecraft instance = Minecraft.getInstance();
        String text_str = entity.getVariableName();
        int color = entity.isAlreadyUsed() ? 0xffa00000 : 0xff0000a0;
//        Component text = new TextComponent(text_str);

//y up!
        poseStack.pushPose();
        poseStack.translate(0.5, 1.01, 0.5);
        poseStack.mulPose(new Quaternionf().rotationX(Math.toRadians(90)));
        poseStack.scale(1 / 18f, 1 / 18f, 1 / 18f);
        Font font = instance.font;
        float f2 = (float) (-font.width(text_str) / 2);
        instance.font.drawInBatch(text_str,
                f2,
                0f,
                color,
                false,
                poseStack.last().pose(),
                multiBufferSource,
                Font.DisplayMode.NORMAL,
                0,
                packedLight);
        poseStack.popPose();


        for (int i = 0; i < 4; i++) {
            Vec3 translation = new Vec3(0, 0, 0);
            if (i % 2 == 0) {
                translation = translation.add(0.51, 0, 0);
            } else {
                translation = translation.add(0, 0, -0.51);
            }
            float mult = 1;
            if (i >= 2) {
                mult = -1;
            }
            translation = translation.scale(mult);
            translation = translation.add(0.5, 0.5, 0.5);
            poseStack.pushPose();
            poseStack.translate(translation);
            poseStack.mulPose(new Quaternionf().rotationY(Math.toRadians((i + 1) * 90)));
            poseStack.mulPose(new Quaternionf().rotationX(Math.toRadians(180)));
            poseStack.scale(1 / 18f, 1 / 18f, 1 / 18f);
            instance.font.drawInBatch(text_str,
                    f2,
                    0f,
                    color,
                    false,
                    poseStack.last().pose(),
                    multiBufferSource,
                    Font.DisplayMode.NORMAL,
                    0,
                    packedLight);
            poseStack.popPose();
        }

    }
}
