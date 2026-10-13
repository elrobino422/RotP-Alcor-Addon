package com.rotpaddon.exampleaddon.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Custom-textured luminous vortex particles, sent by the gravity core.
 * The texture comes from assets/rotpalcoraddon/particles/gravity_well_spark.json.
 */
@OnlyIn(Dist.CLIENT)
public class SBHGravityWellParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;

    private SBHGravityWellParticle(ClientWorld world, double x, double y, double z,
                                   double xd, double yd, double zd, IAnimatedSprite sprites) {
        super(world, x, y, z, xd, yd, zd);
        this.sprites = sprites;
        this.lifetime = 14 + this.random.nextInt(10);
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.quadSize = 0.18F + this.random.nextFloat() * 0.23F;
        this.alpha = 0.95F;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
        this.alpha = 0.95F * Math.max(0.0F, 1.0F - ((float) age / lifetime));
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;

        public Factory(IAnimatedSprite sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world,
                double x, double y, double z, double xd, double yd, double zd) {
            return new SBHGravityWellParticle(world, x, y, z, xd, yd, zd, sprites);
        }
    }
}
