package at.tugraz.lsysmode.lsysvar;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;
import java.util.function.Supplier;

import static at.tugraz.lsysmode.LSysFractals.MODID;
import static net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion.MOD_ID;

public class LSysVarRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "lsysfractals" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "lsysfractals" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> LSYS_VAR_BLOCK = BLOCKS.registerBlock("lsysvar_block",
            registryName -> new LSysVarBlock(BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "lsysvar_block")))
                    .sound(SoundType.WOOD)
                    .lightLevel(state -> 7)
            ));
    // Creates a new BlockItem with the id "lsysfractals:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> LSYS_VAR_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("lsysvar_block", LSYS_VAR_BLOCK, new Item.Properties());
    public static final DeferredItem<Item> LSYS_VAR_ACTIVATOR_ITEM = ITEMS.registerItem("lsysactivator_item", LSysActivatorItem::new, new Item.Properties().overrideDescription("Right Click to expand system!"));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final Supplier<BlockEntityType<LSysVarBlockEntity>> LSYS_VAR_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "lsysvar_be",
            // The block entity type.
            () -> new BlockEntityType<>(
                    // The supplier to use for constructing the block entity instances.
                    LSysVarBlockEntity::new,
                    // An optional value that, when true, only allows players with OP permissions
                    // to load NBT data (e.g. placing a block item)
                    false,
                    // A vararg of blocks that can have this block entity.
                    // This assumes the existence of the referenced blocks as DeferredBlock<Block>s.
                    LSYS_VAR_BLOCK.get()
            )
    );

    public static void register(IEventBus modEventBus) {

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        BLOCK_ENTITIES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
    }

}
