package at.tugraz.lsysmode.lsysvar;

import at.tugraz.lsysmode.LSysFractals;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

public class LSysVarBlock extends Block implements EntityBlock {

    public static final BooleanProperty UNUSED = BooleanProperty.create("unused");

    public LSysVarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(UNUSED, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        var lsys_be = new LSysVarBlockEntity(pos, state);
        return lsys_be;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        LSysFractals.LOGGER.info("Used item: " + stack);
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.FAIL;
        }
        var item = stack.getItem();
        if (item == Items.AIR) {
            if (level.getBlockEntity(pos) instanceof LSysVarBlockEntity) {
                LSysVarBlockEntity be = (LSysVarBlockEntity) level.getBlockEntity(pos);
                be.increaseVarName();
                LSysFractals.LOGGER.info("Changed Variable Name: " + be.getVariableName());
                return InteractionResult.SUCCESS;
            }
        } else if (item == LSysVarRegistry.LSYS_VAR_ACTIVATOR_ITEM.get()) {
            LSysVarBlockEntity be = (LSysVarBlockEntity) level.getBlockEntity(pos);
            if (be.isAlreadyUsed()) {
                be.reset();
            } else {
                be.triggerReplacement(level);
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNUSED);
    }

}
