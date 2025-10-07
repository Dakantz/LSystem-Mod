package at.tugraz.lsysmode.lsysvar;

import at.tugraz.lsysmode.LSysFractals;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LSysVarBlockEntity extends BlockEntity {

    private static int SEARCH_SIZE = 16;
    private static Map<BlockPos, LSysVarBlockEntity> placed_entities = new HashMap<>();

    private String variableName;
    private boolean isUsed;


    private Map<BlockPos, BlockState> neighbours;

    public LSysVarBlockEntity(BlockPos pos, BlockState blockState, String variableName, boolean isUsed) {
        super(LSysVarRegistry.LSYS_VAR_BLOCK_ENTITY.get(), pos, blockState);
        this.variableName = variableName;
        this.isUsed = isUsed;
        neighbours = new HashMap<>();
        placed_entities.put(pos, this);
    }

    public LSysVarBlockEntity(BlockPos pos, BlockState blockState) {
        this(pos, blockState, "L", false);
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setUsed() {
        isUsed = true;
    }

    public void reset() {
        isUsed = false;
    }

    public boolean isAlreadyUsed() {
        return isUsed;
    }

    public String increaseVarName() {
        this.setVariableName(new String(new byte[]{(byte) (variableName.charAt(0) + 1)}) + variableName.substring(1));
        return this.variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void refillNeighbours(Level lvl) {
        this.neighbours.clear();
        this.neighbours.put(this.getBlockPos(), this.getBlockState());
        this.fillNeighbours(lvl, this.getBlockPos(), SEARCH_SIZE);
    }

    private void fillNeighbours(Level lvl, BlockPos pos, int stack) {
        if (stack <= 0) {
            return;
        }
        var neighbour_postions = new BlockPos[]{
                pos.above(),
                pos.below(),
                pos.east(),
                pos.west(),
                pos.north(),
                pos.south(),
        };
        for (var nghbr_pos : neighbour_postions) {
            var state = lvl.getBlockState(nghbr_pos);
            if (state == null || state.getBlock() == Blocks.AIR) {
                continue;
            }
            if (this.neighbours.containsKey(nghbr_pos)) {
                continue;
            }
            this.neighbours.put(nghbr_pos, state);
            fillNeighbours(lvl, nghbr_pos, stack - 1);
        }
    }

    public LSysVarBlockEntity findVariable(String variableName) {
        for (var placed : placed_entities.values()) {
            if (placed.getVariableName().equals(variableName)
                    && !neighbours.containsKey(placed.getBlockPos())
                    && placed.getBlockPos() != this.getBlockPos()) {
                return placed;
            }
        }
        return null;

    }

    public void placeNeighboursAt(Level lvl, BlockPos pos) {
        var this_pos = this.getBlockPos();
        for (var neighbour_pos : neighbours.keySet()) {
            var neighbour_state = neighbours.get(neighbour_pos);
            var relative_pos = neighbour_pos.subtract(this_pos).offset(pos);
            lvl.setBlock(relative_pos, neighbour_state, lvl.isClientSide() ? Block.UPDATE_CLIENTS : Block.UPDATE_ALL);
            var be_set = lvl.getBlockEntity(relative_pos);
            LSysFractals.LOGGER.info("Set relative pos, Neighbor pos+state: " + neighbour_pos + neighbour_state + "Relative pos+state" + relative_pos + lvl.getBlockState(relative_pos));
            if (be_set instanceof LSysVarBlockEntity) {
                var lvys_be = (LSysVarBlockEntity) be_set;
                var neighbour_be = lvl.getBlockEntity(neighbour_pos);
                var lsys_neighbour_be = (LSysVarBlockEntity) neighbour_be;
                if (lsys_neighbour_be != null) {
                    lvys_be.setVariableName(lsys_neighbour_be.getVariableName());
                    LSysFractals.LOGGER.warn("lsys_neighbour_be is null should NOT happen! Neighbor pos+state" + neighbour_pos + neighbour_state + "Relative pos+state" + relative_pos + lvl.getBlockState(relative_pos));
                }
                lvys_be.reset();
            }
        }
    }

    public void updatePlacedEntities(Level lvl) {
        var to_keep = new HashMap<BlockPos, LSysVarBlockEntity>();
        for (var placed_pos : placed_entities.keySet()) {
            if (lvl.getBlockState(placed_pos).equals(placed_entities.get(placed_pos).getBlockState())) {
                to_keep.put(placed_pos, (LSysVarBlockEntity) lvl.getBlockEntity(placed_pos));
            }
        }
        LSysVarBlockEntity.placed_entities = to_keep;
    }

    public void triggerReplacement(Level lvl) {
        this.updatePlacedEntities(lvl);
        for (var be : placed_entities.values()) {
            if (!be.isAlreadyUsed()) {
                be.refillNeighbours(lvl);
            }
        }
        for (var neighbour_pos : neighbours.keySet()) {
            var be = lvl.getBlockEntity(neighbour_pos);
            if (be != null && be instanceof LSysVarBlockEntity) {
                var lsys_be = (LSysVarBlockEntity) be;
                if (lsys_be.isAlreadyUsed()) {
                    continue;
                }
                var lsys_rule_be = findVariable(lsys_be.getVariableName());
                if (lsys_rule_be != null) {
                    lsys_rule_be.placeNeighboursAt(lvl, neighbour_pos);
                }
                lsys_be.setUsed();
            }
        }


    }

    // Read values from the passed ValueInput here.
    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        // Will default to 0 if absent. See the ValueIO article for more information.
        this.variableName = input.getStringOr("varname", "L");
        this.isUsed = input.getBooleanOr("used", false);
    }

    // Save values into the passed ValueOutput here.
    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("varname", this.variableName);
        output.putBoolean("used", this.isUsed);
    }

}
