# Blue Night — Milestone 3F: Insomnia HUD

## Updated design — follows the RotP Bars position button

The earlier 190×46 standalone Blue Night panel was too large and disconnected
from RotP's native stamina/resolve UI. It has been replaced by a compact
moon-themed meter drawn directly beside RotP's bar group.

The addon uses RotP's **public**
`ClientModSettings.getSettingsReadOnly().barsPosition` value:

| RotP setting | Insomnia placement |
| --- | --- |
| **Bars position: Left** | Slim 8×100 vertical Insomnia bar next to RotP's vertical bars on the left |
| **Bars position: Right** | Same slim bar mirrored to the right |
| **Bars position: Top Left** | Small horizontal meter beside the horizontal RotP bars |
| **Bars position: Top Right** | Same small meter mirrored on the right |

Changing that setting in RotP's own HUD options immediately repositions
Insomnia without changing an addon config file. No separate 190-pixel panel.

**Important technical distinction:** RotP does not expose an API for
registering additional meter types directly in its internal BarsRenderer.
This addon mirrors the built-in UI's layout and follows the **same position
setting**, without modifying RotP core classes or using fragile reflection.
The meter is rendered as its own addon overlay alongside the RotP bars.

- Small waxing moon icon as the meter fills
- Blue → cyan → icy white fill, numeric amount below/near the bar
- Active for Blue Night in Bracelet **or** Scythe form
- F1 hides it with the rest of RotP/Minecraft's HUD
- Original Reap damage, cooldown, particles and Insomnia gains unchanged

## Networking and Java architecture

| Class | Responsibility |
| --- | --- |
| `BlueNightInsomnia` | Server NBT; publishes Insomnia changes |
| `BlueNightInsomniaSyncEvents` | Resync on summon, respawn, dimension change and heartbeat |
| `BlueNightInsomniaSyncPacket` | Server-to-client value snapshot |
| `AlcorNetwork` | Registered Forge packet channel, protocol 2 |
| `BlueNightInsomniaHud` | Small addon HUD that reads RotP's existing bar position setting |

## In-game test checklist

1. In IntelliJ, checkout branch `feature/blue-night-insomnia-hud` and run `runClient`.
2. In RotP settings -> HUD, set **Bars position: Left**. Summon Blue Night. The compact moon bar should appear alongside RotP's vertical bars.
3. Change **Bars position: Right** and return to game. The Insomnia meter should move to the opposite side, **without restarting Minecraft**.
4. Try **Top Left** and **Top Right** for the horizontal bar layouts.
5. Hit an enemy with Moon Reaper and cast Reap: the small meter should fill; at 100/100 its moon brightens.
6. Switch to Bracelet: the bar should remain. Unsummon Blue Night or change Stand: the bar should hide.
7. Press F1: the Insomnia bar should hide with the rest of the HUD.
8. Test UI scale changes and, if available, dedicated multiplayer.

## Testing / scope

- The new layout is untested in the Minecraft runtime, so test before merging.
- Server and client must both use the new addon build (network protocol 2).
- Insomnia is still a resource display only; no spending mechanics yet.
- The additive bar's footprint matches RotP's known 1.16.5 bar sizes;
  minor pixel-spacing adjustments can be made after an in-game screenshot.
