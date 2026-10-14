package com.rotpaddon.exampleaddon.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.rotpaddon.exampleaddon.entity.SBHVoidOrbEntity;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

/**
 * Blockbench 5.2.2 Void Orb geometry, adapted from 1.16 MCP names to
 * this Forge 1.16.5 project's official Mojang mappings.
 *
 * The supplied 1.19+ AnimationDefinition export is implemented in setupAnim
 * without any unsupported 1.19 animation classes.
 */
public class SBHVoidOrbModel extends EntityModel<SBHVoidOrbEntity> {
	private final ModelRenderer root;
	private final ModelRenderer singularity;
	private final ModelRenderer accretion_disk;
	private final ModelRenderer accretion_disk_seg_01;
	private final ModelRenderer accretion_disk_seg_02;
	private final ModelRenderer accretion_disk_seg_03;
	private final ModelRenderer accretion_disk_seg_04;
	private final ModelRenderer accretion_disk_seg_05;
	private final ModelRenderer accretion_disk_seg_06;
	private final ModelRenderer accretion_disk_seg_07;
	private final ModelRenderer accretion_disk_seg_08;
	private final ModelRenderer accretion_disk_seg_09;
	private final ModelRenderer accretion_disk_seg_10;
	private final ModelRenderer accretion_disk_seg_11;
	private final ModelRenderer accretion_disk_seg_12;
	private final ModelRenderer accretion_disk_seg_13;
	private final ModelRenderer accretion_disk_seg_14;
	private final ModelRenderer accretion_disk_seg_15;
	private final ModelRenderer accretion_disk_seg_16;
	private final ModelRenderer orbit_fragments;

	public SBHVoidOrbModel() {
		texWidth = 128;
		texHeight = 128;

		root = new ModelRenderer(this);
		// Origin at the projectile center, not the Blockbench floor anchor.
        root.setPos(0.0F, 0.0F, 0.0F);
		

		singularity = new ModelRenderer(this);
		singularity.setPos(0.0F, 0.0F, 0.0F);
		root.addChild(singularity);
		singularity.texOffs(2, 2).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
		singularity.texOffs(2, 20).addBox(-5.0F, -3.0F, -3.0F, 10.0F, 6.0F, 6.0F, 0.0F, false);
		singularity.texOffs(36, 2).addBox(-3.0F, -5.0F, -3.0F, 6.0F, 10.0F, 6.0F, 0.0F, false);
		singularity.texOffs(62, 2).addBox(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 10.0F, 0.0F, false);

		accretion_disk = new ModelRenderer(this);
		accretion_disk.setPos(0.0F, 0.0F, 0.0F);
		root.addChild(accretion_disk);
		setRotationAngle(accretion_disk, -0.3142F, 0.0F, -0.2094F);
		

		accretion_disk_seg_01 = new ModelRenderer(this);
		accretion_disk_seg_01.setPos(-8.0F, 0.0F, 0.0F);
		accretion_disk.addChild(accretion_disk_seg_01);
		accretion_disk_seg_01.texOffs(36, 20).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_02 = new ModelRenderer(this);
		accretion_disk_seg_02.setPos(-7.391F, 0.0F, 3.0615F);
		accretion_disk.addChild(accretion_disk_seg_02);
		setRotationAngle(accretion_disk_seg_02, 0.0F, 0.3927F, 0.0F);
		accretion_disk_seg_02.texOffs(46, 20).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_03 = new ModelRenderer(this);
		accretion_disk_seg_03.setPos(-5.6569F, 0.0F, 5.6569F);
		accretion_disk.addChild(accretion_disk_seg_03);
		setRotationAngle(accretion_disk_seg_03, 0.0F, 0.7854F, 0.0F);
		accretion_disk_seg_03.texOffs(56, 20).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_04 = new ModelRenderer(this);
		accretion_disk_seg_04.setPos(-3.0615F, 0.0F, 7.391F);
		accretion_disk.addChild(accretion_disk_seg_04);
		setRotationAngle(accretion_disk_seg_04, 0.0F, 1.1781F, 0.0F);
		accretion_disk_seg_04.texOffs(66, 20).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_05 = new ModelRenderer(this);
		accretion_disk_seg_05.setPos(0.0F, 0.0F, 8.0F);
		accretion_disk.addChild(accretion_disk_seg_05);
		setRotationAngle(accretion_disk_seg_05, 0.0F, 1.5708F, 0.0F);
		accretion_disk_seg_05.texOffs(76, 20).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_06 = new ModelRenderer(this);
		accretion_disk_seg_06.setPos(3.0615F, 0.0F, 7.391F);
		accretion_disk.addChild(accretion_disk_seg_06);
		setRotationAngle(accretion_disk_seg_06, 0.0F, 1.9635F, 0.0F);
		accretion_disk_seg_06.texOffs(86, 20).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_07 = new ModelRenderer(this);
		accretion_disk_seg_07.setPos(5.6569F, 0.0F, 5.6569F);
		accretion_disk.addChild(accretion_disk_seg_07);
		setRotationAngle(accretion_disk_seg_07, 0.0F, 2.3562F, 0.0F);
		accretion_disk_seg_07.texOffs(96, 20).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_08 = new ModelRenderer(this);
		accretion_disk_seg_08.setPos(7.391F, 0.0F, 3.0615F);
		accretion_disk.addChild(accretion_disk_seg_08);
		setRotationAngle(accretion_disk_seg_08, 0.0F, 2.7489F, 0.0F);
		accretion_disk_seg_08.texOffs(106, 20).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_09 = new ModelRenderer(this);
		accretion_disk_seg_09.setPos(8.0F, 0.0F, 0.0F);
		accretion_disk.addChild(accretion_disk_seg_09);
		setRotationAngle(accretion_disk_seg_09, 0.0F, 3.1416F, 0.0F);
		accretion_disk_seg_09.texOffs(116, 20).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_10 = new ModelRenderer(this);
		accretion_disk_seg_10.setPos(7.391F, 0.0F, -3.0615F);
		accretion_disk.addChild(accretion_disk_seg_10);
		setRotationAngle(accretion_disk_seg_10, 0.0F, 3.5343F, 0.0F);
		accretion_disk_seg_10.texOffs(2, 34).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_11 = new ModelRenderer(this);
		accretion_disk_seg_11.setPos(5.6569F, 0.0F, -5.6569F);
		accretion_disk.addChild(accretion_disk_seg_11);
		setRotationAngle(accretion_disk_seg_11, 0.0F, 3.927F, 0.0F);
		accretion_disk_seg_11.texOffs(12, 34).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_12 = new ModelRenderer(this);
		accretion_disk_seg_12.setPos(3.0615F, 0.0F, -7.391F);
		accretion_disk.addChild(accretion_disk_seg_12);
		setRotationAngle(accretion_disk_seg_12, 0.0F, 4.3197F, 0.0F);
		accretion_disk_seg_12.texOffs(22, 34).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_13 = new ModelRenderer(this);
		accretion_disk_seg_13.setPos(0.0F, 0.0F, -8.0F);
		accretion_disk.addChild(accretion_disk_seg_13);
		setRotationAngle(accretion_disk_seg_13, 0.0F, 4.7124F, 0.0F);
		accretion_disk_seg_13.texOffs(32, 34).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_14 = new ModelRenderer(this);
		accretion_disk_seg_14.setPos(-3.0615F, 0.0F, -7.391F);
		accretion_disk.addChild(accretion_disk_seg_14);
		setRotationAngle(accretion_disk_seg_14, 0.0F, 5.1051F, 0.0F);
		accretion_disk_seg_14.texOffs(42, 34).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_15 = new ModelRenderer(this);
		accretion_disk_seg_15.setPos(-5.6569F, 0.0F, -5.6569F);
		accretion_disk.addChild(accretion_disk_seg_15);
		setRotationAngle(accretion_disk_seg_15, 0.0F, 5.4978F, 0.0F);
		accretion_disk_seg_15.texOffs(52, 34).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		accretion_disk_seg_16 = new ModelRenderer(this);
		accretion_disk_seg_16.setPos(-7.391F, 0.0F, -3.0615F);
		accretion_disk.addChild(accretion_disk_seg_16);
		setRotationAngle(accretion_disk_seg_16, 0.0F, 5.8905F, 0.0F);
		accretion_disk_seg_16.texOffs(62, 34).addBox(-0.5F, -1.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);

		orbit_fragments = new ModelRenderer(this);
		orbit_fragments.setPos(0.0F, 0.0F, 0.0F);
		root.addChild(orbit_fragments);
		orbit_fragments.texOffs(72, 34).addBox(-11.0F, -3.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		orbit_fragments.texOffs(82, 34).addBox(9.0F, 1.0F, 1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		orbit_fragments.texOffs(92, 34).addBox(-4.5F, -6.0F, -6.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		orbit_fragments.texOffs(98, 34).addBox(2.5F, 3.5F, 7.0F, 1.0F, 1.0F, 2.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(SBHVoidOrbEntity entity, float limbSwing,
                          float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        // Blockbench keyframe export: 4 singularity turns and 5 disk turns
        // in each 0.5-second interval. Maintain continuous rotation rather
        // than reversing back toward 0 degrees in the second half.
        float seconds = ageInTicks / 20.0F;
        singularity.yRot = (float) Math.toRadians(-2880.0D * seconds);
        accretion_disk.yRot = (float) Math.toRadians(-3600.0D * seconds);

        // Keyframed fragment position: 0px -> +1px at 0.5s -> 0px at 1s.
        float phase = seconds - (float) Math.floor(seconds);
        orbit_fragments.y = phase <= 0.5F ? phase * 2.0F
                : (1.0F - phase) * 2.0F;
    }

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}
}