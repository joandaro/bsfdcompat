# 蔚蓝乐事 / Blue Skies Delight

- Mod ID / 构建产物名：`bsfdcompat`
- 主类：`com.jos.bsfdcompat.BSFDCompat`
- Minecraft 1.20.1；Forge 开发版本 47.4.0；Java 17。
- 入口保留空的物品注册器 `BSFDCompat.ITEMS`。
- 项目目前仅依赖 Forge 和 Minecraft。Blue Skies / Farmer's Delight 的开发依赖及加载依赖尚未添加；后续接入时再指定版本。
- 此前的食物标签数据包保持独立，尚未合并进这个空项目。

用 IDE 作为 Gradle 项目打开。Windows 下生成 IntelliJ 运行配置用 `gradlew.bat genIntellijRuns`，构建用 `gradlew.bat build`；Linux/macOS 使用对应的 `./gradlew` 命令。

Mod 信息在 `gradle.properties` 修改。许可暂时沿用 MDK 默认的 `All Rights Reserved`；随附的 `LICENSE.txt` 和 `CREDITS.txt` 是原始 Forge MDK 的说明。

最低 Forge 版本设为 47.3.10，以支持原 MDK 使用的 `FMLJavaModLoadingContext` 构造器注入（此功能的引入版本见原 MDK changelog）。
