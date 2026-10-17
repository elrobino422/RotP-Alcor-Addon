# Blue Night — Milestone 3F: Insomnia HUD

## What's new

A compact moon-themed overlay in the **top-left** of the screen appears whenever the user has **Blue Night summoned**, regardless of whether the current form is Bracelet or Scythe.

- **INSOMNIA N/100** with a 10-segment blue/cyan resource bar
- The moon brightens as stacks increase and sparkles at 100/100
- Filled segments animate smoothly rather than jumping instantly
- F1 / Hide HUD and unsummoning Blue Night hide the display
- Insomnia gameplay and Reap of Insomnia's damage/cooldown are unchanged

## Java architecture (read this to learn how it works!)

| Class | Responsibility |
| --- | --- |
| `BlueNightInsomnia` | Server-owned persistent NBT counter; sends a snapshot immediately after gains |
| `BlueNightInsomniaSyncEvents` | Sync on summon/login/respawn/dimension change and 5-second fallback heartbeat |
| `BlueNightInsomniaSyncPacket` | One-way network packet; updates ONLY the HUD on the client |
| `AlcorNetwork` | Registers packet ID 1 in the addon's Forge channel (protocol 2) |
| `BlueNightInsomniaHud` | Draws a client-only overlay on `RenderGameOverlayEvent.Post / ElementType.ALL` |

**Security/design:** The client never tells the server how much Insomnia it has. Attacks and the counter stay server-authoritative. No custom textures are necessary — the HUD is drawn using native `AbstractGui.fill` operations.

## Test checklist

1. Checkout branch `feature/blue-night-insomnia-hud` in IntelliJ, then `runClient`.
2. Summon Blue Night in Bracelet form: the panel should appear in the upper left with the current server value (0/100 if fresh).
3. Select Scythe, hit a mob, and cast Reap of Insomnia: numeric and segmented values should grow together. A full meter should glow.
4. Return to Bracelet: HUD **stays** because Blue Night remains active. Unsummon or change Stand: HUD disappears.
5. Press F1 to hide the vanilla HUD; Insomnia display also disappears.
6. Log out and rejoin: the HUD should refresh from the server when Blue Night is summoned again.
7. If using multiplayer, install **the same updated addon version on both server and client** (the addon channel protocol changed to 2); confirm each player sees their own counter.
8. Test dimension travel and respawn for stale HUD values.

## Known scope limits

- This is HUD display only: it does not spend Insomnia, alter Reap damage, grant new weapon forms or add a configurable position.
- A bar at 0/100 is intentional so the player learns the resource exists.
- Requires Minecraft/Forge 1.16.5 with the existing RotP addon dependencies.
- Compile and in-game testing must be completed before merging.
