# Blue Night — Milestone 1: Register and summon (Minecraft 1.16.5 / RotP)

This is a TEST FOUNDATION ONLY. No weapons, finger-ring layer, Insomnia mechanics, or ultimate yet.

Repository: https://github.com/elrobino422/RotP-Alcor-Addon

## Files that are NEW
Copy the three `.java` files from this kit into the same paths in your IntelliJ project's `src/main/java/` tree:
- `com/rotpaddon/exampleaddon/entity/BlueNightStandEntity.java`
- `com/rotpaddon/exampleaddon/client/render/BlueNightStandModel.java`
- `com/rotpaddon/exampleaddon/client/render/BlueNightStandRenderer.java`

## Asset copy (new file, no existing original removed)
In your project, duplicate:
`src/main/resources/assets/rotpalcoraddon/geo/sbh.geo.json`
to:
`src/main/resources/assets/rotpalcoraddon/geo/blue_night.geo.json`
Do NOT move or delete `sbh.geo.json`.

No new texture is required in this milestone: BlueNightStandRenderer temporarily points at existing `textures/entity/stand/example_stand.png`.

## Edit existing `InitStands.java` (do not replace it)
Add this import:
`import com.rotpaddon.exampleaddon.entity.BlueNightStandEntity;`

Immediately before the FINAL closing brace of class InitStands (after STAND_SBH) paste:

```java
    // Blue Night: minimal TEST Stand — ring and weapons come later.
    public static final EntityStandRegistryObject<EntityStandType<StandStats>, StandEntityType<BlueNightStandEntity>> STAND_BLUE_NIGHT =
            new EntityStandRegistryObject<>("blue_night",
                    STANDS,
                    () -> new EntityStandType.Builder<StandStats>()
                            .color(0x4752A9)
                            .storyPartName(new TranslationTextComponent("story_part.rotpalcoraddon.alcor"))
                            .disableManualControl()
                            .disableStandLeap()
                            .setSurvivalGameplayPool(StandType.StandSurvivalGameplayPool.NON_ARROW)
                            .defaultStats(StandStats.class, new StandStats.Builder()
                                    .tier(5)
                                    .power(10)
                                    .speed(12)
                                    .range(10, 20)
                                    .durability(10)
                                    .precision(15)
                                    .build())
                            .build(),
                    InitEntities.ENTITIES,
                    () -> new StandEntityType<BlueNightStandEntity>(BlueNightStandEntity::new, 0.7F, 2.1F))
                    .withDefaultStandAttributes();
```

The stats are temporary testing defaults, not final Blue Night balancing.
No actions yet: both action hotbars will be empty for this milestone.
`NON_ARROW` keeps this unfinished Stand out of the random Stand Arrow pool. You can still give it with the `/stand give` command.

## Edit existing `ClientInit.java` (do not replace it)
Add import:
`import com.rotpaddon.exampleaddon.client.render.BlueNightStandRenderer;`

Add inside `onFMLClientSetup(FMLClientSetupEvent event)` alongside the other renderer registrations:
```java
        RenderingRegistry.registerEntityRenderingHandler(
                InitStands.STAND_BLUE_NIGHT.getEntityType(), BlueNightStandRenderer::new);
```

## Edit existing `en_us.json` (do not replace it)
Inside the existing JSON object, insert (with commas according to placement):
```json
  "stand.rotpalcoraddon.blue_night": "Blue Night",
  "entity.rotpalcoraddon.blue_night": "Blue Night",
```

## Test
1. Save changes, reload Gradle if necessary, run Gradle `build` or IntelliJ runClient.
2. Open world with commands/cheats and try `/stand give @s rotpalcoraddon:blue_night true` (RotP supports a final `replace` Boolean).
3. Summon Stand with the usual RotP keybind.
4. EXPECT: Blue Night named correctly; for now it LOOKS like a placeholder humanoid copied from SBH.
5. EXPECT: NO ring worn on finger and NO weapon moves yet.
6. Check existing SBH still summons normally.
7. Make a Git commit once it works. If build errors occur, send the FIRST error and the file/line.

No existing file or old code should be unloaded/removed. Do not overwrite any SBH classes, geometry or animation.
