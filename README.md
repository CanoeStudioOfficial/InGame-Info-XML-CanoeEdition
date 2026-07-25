# InGame Info XML CanoeEdition

InGame Info XML CanoeEdition is a maintained Minecraft 1.12.2 fork/edition of InGame Info XML. It keeps the original configuration style while integrating several commonly used IGI addon features directly into this mod, so modpacks can use fewer extra addon jars.

This edition also includes the required LunatriusCore functionality internally. A separate LunatriusCore jar is not required for CanoeEdition.

## Requirements

- Minecraft `1.12.2`
- Forge `14.23.5.x`
- Java 8 runtime
- No separate LunatriusCore installation is required

The development workspace uses Gradle 9.2.1 and RetroFuturaGradle.

## Documentation

The legacy official wiki is unavailable, so this repository includes a rebuilt wiki based on the current source code.

- English wiki: [Home-English](../InGame-Info-XML-CanoeEdition.wiki/Home-English.md)
- 中文 wiki: [Home](../InGame-Info-XML-CanoeEdition.wiki/Home.md)
- English tag/method reference: [Main Methods](../InGame-Info-XML-CanoeEdition.wiki/Main-Methods-English.md), [Main Tags](../InGame-Info-XML-CanoeEdition.wiki/Main-Tags-English.md), [Mod Methods](../InGame-Info-XML-CanoeEdition.wiki/Mod-Methods-English.md)
- 中文标签/方法参考: [主用方法](../InGame-Info-XML-CanoeEdition.wiki/Main-Methods.md), [主标签](../InGame-Info-XML-CanoeEdition.wiki/Main-Tags.md), [Mod 兼容方法](../InGame-Info-XML-CanoeEdition.wiki/Mod-Methods.md)

## Config Files

Config files are stored in the Minecraft `config` directory:

- `InGameInfo.xml`
- `InGameInfo.json`
- `InGameInfo.txt`

The default startup config is `InGameInfo.xml`. If the selected config file does not exist, the mod loads the built-in default XML config from the jar.

Useful commands:

- `/igi reload` reloads the current config.
- `/igi load <filename>` loads another config file and saves it as the startup config.
- `/igi save <filename>` saves the current config as XML, JSON, or TXT.
- `/igi enable` enables the HUD.
- `/igi disable` disables the HUD.
- `/igi taglist` opens the tag list.
- `/igi edit` opens the position editor.
- `/igi alignment` opens the alignment editor.

## Migration from Legacy InGame Info XML

Most legacy InGame Info XML configs can be reused directly.

Recommended migration steps:

1. Back up your old `config/InGameInfo.xml`, `config/InGameInfo.json`, and/or `config/InGameInfo.txt`.
2. Install `InGame Info XML CanoeEdition`.
3. Put your old config file back into the new instance's `config` directory.
4. Start the game and run `/igi reload`, or restart the game.
5. If your config file is not `InGameInfo.xml`, run `/igi load <filename>` once to select it.

XML, JSON, and TXT formats are still supported. Built-in legacy tags such as `{fps}`, `{day}`, `{biome}`, `{mainhandname}`, `{potionduration0}`, and `{worldname}` keep their names.

If your old instance installed LunatriusCore only for InGame Info XML, you can remove that separate LunatriusCore jar after switching to CanoeEdition. Keep LunatriusCore only if another mod in the same pack still requires it.

## Merged IGI Addons

The following IGI addon functionality is now built into CanoeEdition. After migration, remove the old addon jar from the `mods` folder to avoid duplicate tag registration or unnecessary extra mods.

LunatriusCore is also bundled internally for this mod's needs. It is not an IGI addon, but it was a common legacy dependency and no longer needs to be installed separately for CanoeEdition.

### IGI Extended

Former addon: `IGI-Extended`

Built-in tags:

| Tag | Description |
| --- | --- |
| `{tps}` | Server TPS, formatted with two decimals and capped at `20.00`. |
| `{mspt}` | Server average milliseconds per tick, formatted with two decimals. |

Old configs using `{tps}` or `{mspt}` do not need changes.

### IGI Addon Deep Resonance

Former addon: `IGI-Addon-DeepResonance`

Built-in tag:

| Tag | Description |
| --- | --- |
| `{drradiation}` | Deep Resonance radiation level at the player's current position. |

This tag is registered only when `Deep Resonance` itself is installed. Old configs using `{drradiation}` can keep using it.

## Built-In Mod Compatibility

These integrations are built into CanoeEdition, but they are conditional. The tags are registered only when the corresponding external mod is loaded.

| Mod | Loader condition | Tags |
| --- | --- | --- |
| Thaumcraft | `thaumcraft` | `{thaumaura}`, `{thaumflux}`, `{thaumwarpperm}`, `{thaumwarpnormal}`, `{thaumwarptemp}` |
| Tough As Nails | `toughasnails` | `{tancurrtemp}`, `{tantargettemp}`, `{tanseason}` |
| Blood Magic | `bloodmagic` | `{bmcurrentlp}`, `{bmorbtier}` |
| Deep Resonance | `deepresonance` | `{drradiation}` |
| RFTools Dimensions | `rftools` | `{rftdimension}`, `{rftdimensionname}`, `{rftdimensionpower}`, `{rftdimensioncost}` |
| Serene Seasons | `sereneseasons` | `{sereneseasonsdayduration}`, `{sereneseasonssubseasonduration}`, `{sereneseasonsseasonduration}`, `{sereneseasonscycleduration}`, `{sereneseasonsseasoncycleticks}`, `{sereneseasonsday}`, `{sereneseasonscurrentseason}`, `{sereneseasonscurrentsubseason}`, `{sereneseasonscurrenttropicalseason}`, `{sereneseasonscurrentseasonord}`, `{sereneseasonscurrentsubseasonord}`, `{sereneseasonsdayofseason}` |

## Migration Notes for Modpacks

- Remove the standalone LunatriusCore jar if it was installed only for InGame Info XML. Keep it if another mod still depends on it.
- Remove `IGI-Extended` after installing CanoeEdition. `{tps}` and `{mspt}` are provided by this mod.
- Remove `IGI-Addon-DeepResonance` after installing CanoeEdition. `{drradiation}` is provided by this mod.
- Do not remove the actual content mods. CanoeEdition only integrates IGI display tags; it does not replace Thaumcraft, Blood Magic, Deep Resonance, RFTools Dimensions, Serene Seasons, or Tough As Nails.
- Thaumcraft tags require Thaumcraft to be loaded. If Thaumcraft is absent, `{thaumaura}`, `{thaumflux}`, and warp tags are not registered.
- Blood Magic tags require Blood Magic to be loaded. If Blood Magic is absent, `{bmcurrentlp}` and `{bmorbtier}` are not registered.
- RFTools tags require RFTools Dimensions to be loaded under the `rftools` mod id used by the current source. If your pack uses a different fork or mod id, verify the tag list in game.
- Serene Seasons tags require Serene Seasons. `Tough As Nails`'s `{tanseason}` also reads season data from the Serene Seasons API, so packs using TAN without season data should test that tag before shipping a shared config.
- Deep Resonance radiation display requires Deep Resonance itself. The merged addon only removes the need for the separate IGI addon jar.
- If a shared config references tags for mods that are not always installed, maintain separate `InGameInfo.xml` variants per pack/profile, or keep mod-specific lines out of the common config.
- Use `/igi taglist` in game to confirm which tags are actually registered in the current modpack.
- If a tag appears missing, check the external mod is installed, loaded, and using the expected mod id.

## Development

Common Gradle tasks:

```powershell
.\gradlew.bat compileJava
.\gradlew.bat processResources
.\gradlew.bat build
```

Dependency declarations are in `gradle/scripts/dependencies.gradle`.
